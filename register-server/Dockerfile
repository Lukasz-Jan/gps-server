FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position/forwarder \
&&  addgroup -S appgroup \
    && adduser -S appuser -G appgroup

USER appuser

COPY target/register-server-*.jar /usr/app/gps/register-server/register-server.jar

CMD ["java", "-jar", "/usr/app/gps/register-server/register-server.jar"]
