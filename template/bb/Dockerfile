FROM ghcr.io/openfaas/of-watchdog:0.9.12 AS watchdog
FROM babashka/babashka:1.3.182 AS babashka
FROM clojure:tools-deps-1.11.1.1347 AS build

RUN set -e

ARG TEST=true

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
COPY --from=babashka /usr/local/bin/bb /usr/local/bin/bb

ENV HOME /home/app

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

ENV TEST_DIR $HOME/function/test

COPY --chown=app:app function/test/bb.edn* function/test/bb.edn

RUN if [ $TEST != false ] && [ -d $TEST_DIR ]; then \
     /bin/sh -c "cd $TEST_DIR && bb prepare && bb print-deps"; \
    fi

FROM build

COPY --chown=app:app function function
COPY --chown=app:app index.clj lib ./

FROM test

COPY --chown=app:app function function
COPY --chown=app:app index.clj lib ./

RUN if [ $TEST != false ] && [ -d $TEST_DIR ]; then \
      /bin/sh -c "cd $TEST_DIR && bb run-tests.clj"; \
    fi

FROM build

RUN rm -rf function/bb.edn $TEST_DIR

FROM build AS ship

ENV mode="http"
ENV upstream_url="http://127.0.0.1:8082"

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
