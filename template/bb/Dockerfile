FROM ghcr.io/openfaas/of-watchdog:0.9.13 AS watchdog
FROM babashka/babashka:1.3.186 AS babashka
FROM eclipse-temurin:8u392-b08-jdk AS build

RUN set -e

ARG TEST=true
ENV HOME /home/app
ENV TEST_DIR $HOME/function/test
ENV PATH="$PATH:/usr/local/bin"

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
COPY --from=babashka /usr/local/bin/bb /usr/local/bin/bb

RUN addgroup --system app && adduser --system --ingroup app app

USER app
WORKDIR $HOME

COPY --chown=app:app index.clj bb.edn lib merge_config.clj ./

RUN bb prepare

COPY --chown=app:app function/bb.edn* function/bb.edn
RUN bb merge-config --src "function/bb.edn" --out "ship.edn" && \
    bb --config ship.edn prepare && \
    bb --config ship.edn print-deps && \
    cp -r .m2 .m2-ship

COPY --chown=app:app function/test/bb.edn* function/test/bb.edn
RUN if [ "$TEST" != false ]; then \
      bb merge-config --src "function/bb.edn" --test "function/test/bb.edn" --out "test.edn" && \
      bb --config test.edn prepare && \
      bb --config test.edn print-deps; \
    fi

COPY --chown=app:app function function
RUN if [ "$TEST" != false ]; then \
      bb merge-config --src "function/bb.edn" --test "function/test/bb.edn" --out "test.edn" && \
      bb --config test.edn test; \
    fi && \
    rm -r .m2 && mv .m2-ship .m2 && \
    rm -rf merge_config.clj function/bb.edn "$TEST_DIR"

FROM build AS ship

USER app
WORKDIR $HOME

ENV mode="http"
ENV fprocess="bb --config ship.edn run-function"
ENV upstream_url="http://127.0.0.1:8082"

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
