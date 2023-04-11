FROM ghcr.io/openfaas/of-watchdog:0.9.11 AS watchdog

FROM babashka/babashka:1.3.176-alpine

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
EXPOSE 8080
HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1

RUN addgroup -S app && adduser -S app -G app

USER app
WORKDIR /home/app

COPY index.clj function/bb.edn ./
COPY function function

CMD ["fwatchdog"]
