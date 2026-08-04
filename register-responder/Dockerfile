FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position/forwarder \
&&  addgroup -S appgroup \
    && adduser -S appuser -G appgroup

USER appuser

COPY target/register-responder-*.jar /usr/app/gps/register-responder/register-responder.jar

CMD ["java", "-jar", "/usr/app/gps/register-responder/register-responder.jar"]
