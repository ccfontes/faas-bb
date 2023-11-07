FROM ghcr.io/openfaas/of-watchdog:0.9.12 AS watchdog
FROM babashka/babashka:1.3.182 AS babashka
FROM clojure:tools-deps-1.11.1.1347

RUN set -e

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
COPY --from=babashka /usr/local/bin/bb /usr/local/bin/bb

ENV HOME /home/app

RUN addgroup --system app && adduser --system --ingroup app app && \
    mkdir -p $HOME/.deps.clj/1.11.1.1347/ClojureTools && \
    mv /root/.m2 $HOME && \
    chown app:app -R $HOME/.m2 && \
    mv /usr/local/lib/clojure/libexec/clojure-tools-1.11.1.1347.jar $HOME/.deps.clj/1.11.1.1347/ClojureTools

USER app
WORKDIR $HOME

COPY --chown=app:app function/bb.edn ./
RUN bb prepare && bb print-deps

COPY --chown=app:app function function
RUN rm function/bb.edn

COPY --chown=app:app index.clj ./
COPY --chown=app:app lib ./

ENV mode="http"
ENV upstream_url="http://127.0.0.1:8082"

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
