FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position-responder \
&&  addgroup -S appgroup \
    && adduser -S appuser -G appgroup

USER appuser

COPY target/position-responder-*.jar /usr/app/gps/position-responder/position-responder.jar

CMD ["java", "-jar", "/usr/app/gps/position-responder/position-responder.jar"]
