FROM ghcr.io/openfaas/of-watchdog:0.9.11 AS watchdog
FROM babashka/babashka:1.3.176 AS babashka
FROM clojure:tools-deps-1.11.1.1257

RUN set -e

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
COPY --from=babashka /usr/local/bin/bb /usr/local/bin/bb

ENV HOME /home/app

RUN addgroup --system app && adduser --system --ingroup app app && \
    mkdir -p $HOME/.deps.clj/1.11.1.1257/ClojureTools && \
    mv /root/.m2 $HOME && \
    chown app:app -R $HOME/.m2 && \
    mv /usr/local/lib/clojure/libexec/clojure-tools-1.11.1.1257.jar $HOME/.deps.clj/1.11.1.1257/ClojureTools

USER app
WORKDIR $HOME

COPY index.clj function/bb.edn ./
COPY function function

RUN bb prepare

EXPOSE 8080
HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
