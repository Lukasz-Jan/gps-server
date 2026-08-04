FROM eclipse-temurin@sha256:5583e3116cba3393e885b70a4498fc961556fa42afa2d892ecffcd4f689e2107

ARG VERSION
ARG APP_NAME="position-forwarder"
ARG WORKDIR=/usr/app/gps/${APP_NAME}
ENV APP_JAR=${APP_NAME}.jar

RUN addgroup --system admgroup \
    && adduser --system deployer -G admgroup \
    && mkdir -p ${WORKDIR} \
    && chown -R deployer:admgroup ${WORKDIR} \
    && test -n "$VERSION"

WORKDIR ${WORKDIR}

USER deployer

COPY --chown=deployer:admgroup target/${APP_NAME}-${VERSION}.jar ${APP_JAR}

EXPOSE 9000

CMD ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -jar \"${APP_JAR}\""]
