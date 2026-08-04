FROM eclipse-temurin:21-jre-alpine-3.23

ARG VERSION
ARG APP_NAME
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

EXPOSE 8090

CMD ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -jar \"${APP_JAR}\""]