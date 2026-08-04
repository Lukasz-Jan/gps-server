FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position-server \
&&  addgroup -S appgroup \
    && adduser -S appuser -G appgroup

USER appuser

COPY target/position-server-*.jar /usr/app/gps/position-server/position-server.jar

CMD ["java", "-jar", "/usr/app/gps/position-server/position-server.jar"]


