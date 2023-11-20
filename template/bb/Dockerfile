FROM ghcr.io/openfaas/of-watchdog:0.9.12 AS watchdog
FROM babashka/babashka:1.3.182 AS babashka
FROM clojure:tools-deps-1.11.1.1347 AS build

RUN set -e

ARG TEST=true
ENV HOME /home/app
ENV TEST_DIR $HOME/function/test
ENV PATH="$PATH:/usr/local/bin"

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
COPY --from=babashka /usr/local/bin/bb /usr/local/bin/bb

RUN addgroup --system app && adduser --system --ingroup app app && \
    mkdir -p $HOME/.deps.clj/1.11.1.1347/ClojureTools && \
    mv /root/.m2 $HOME && \
    chown app:app -R $HOME && \
    mv /usr/local/lib/clojure/libexec/clojure-tools-1.11.1.1347.jar $HOME/.deps.clj/1.11.1.1347/ClojureTools

USER app
WORKDIR $HOME

COPY --chown=app:app index.clj bb.edn lib with-bb-deps.clj ./

COPY --chown=app:app function/bb.edn* function/bb.edn
RUN ./with-bb-deps.clj --src "function/bb.edn" --out "ship-deps.edn" && \
    SHIP_DEPS=$(cat ship-deps.edn) && \
    bb -Sdeps "$SHIP_DEPS" prepare && \
    bb -Sdeps "$SHIP_DEPS" print-deps && \
    cp -rv .m2 .m2-ship

COPY --chown=app:app function/test/bb.edn* function/test/bb.edn
RUN if [ "$TEST" != false ]; then \
      ./with-bb-deps.clj --src "function/bb.edn" --test "function/test/bb.edn" --out "test-deps.edn" && \
      TEST_DEPS=$(cat test-deps.edn) && \
      bb -Sdeps "$TEST_DEPS" prepare && \
      bb -Sdeps "$TEST_DEPS" print-deps; \
    fi

COPY --chown=app:app function function

RUN if [ "$TEST" != false ] && [ -d "$TEST_DIR" ]; then \
      TEST_DEPS=$(cat test-deps.edn) && \
      bb -Sdeps "$TEST_DEPS" test; \
    fi && \
    rm -rv $HOME/.m2 && \
    mv .m2-ship .m2 && \
    rm -rf with-bb-deps.clj function/bb.edn "$TEST_DIR"

FROM build AS ship

USER app
WORKDIR $HOME

ENV mode="http"
ENV fprocess="bb run-function"
ENV upstream_url="http://127.0.0.1:8082"

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
