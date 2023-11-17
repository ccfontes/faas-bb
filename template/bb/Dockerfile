FROM ghcr.io/openfaas/of-watchdog:0.9.12 AS watchdog
FROM babashka/babashka:1.3.182 AS babashka
FROM clojure:tools-deps-1.11.1.1347 AS build

RUN set -e

ENV HOME /home/app
ENV TEST_DIR $HOME/function/test

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
COPY --from=babashka /usr/local/bin/bb /usr/local/bin/bb

RUN addgroup --system app && adduser --system --ingroup app app && \
    mkdir -p $HOME/.deps.clj/1.11.1.1347/ClojureTools && \
    mv /root/.m2 $HOME && \
    chown app:app -R $HOME && \
    mv /usr/local/lib/clojure/libexec/clojure-tools-1.11.1.1347.jar $HOME/.deps.clj/1.11.1.1347/ClojureTools

USER app
WORKDIR $HOME

COPY --chown=app:app function/bb.edn ./
RUN bb prepare && bb print-deps


FROM build AS test

ARG TEST=true

COPY --chown=app:app function/test/bb.edn* function/test/bb.edn

USER app

RUN if [ "$TEST" != false ] && [ -d "$TEST_DIR" ]; then \
     /bin/sh -c "cd $TEST_DIR && bb prepare && bb print-deps"; \
    fi


FROM build AS build_function

COPY --chown=app:app function function
COPY --chown=app:app index.clj lib ./


FROM test

COPY --chown=app:app function function
COPY --chown=app:app index.clj lib ./

USER app

RUN if [ "$TEST" != false ] && [ -d "$TEST_DIR" ]; then \
      /bin/sh -c "cd $TEST_DIR && bb run-tests.clj"; \
    fi


FROM build_function AS build_no_tests

USER app
WORKDIR $HOME

RUN rm -rf function/bb.edn "$TEST_DIR"


FROM build_no_tests AS ship

USER app
WORKDIR $HOME

ENV mode="http"
ENV upstream_url="http://127.0.0.1:8082"

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
