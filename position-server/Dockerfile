FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position-server

COPY target/position-server-1.0.jar /usr/app/gps/position-server

CMD ["java", "-jar", "/usr/app/gps/position-server/position-server-1.0.jar"]
