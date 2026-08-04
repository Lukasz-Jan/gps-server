FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position-responder

COPY target/position-responder-1.0.jar /usr/app/gps/position-responder

CMD ["java", "-jar", "/usr/app/gps/position-responder/position-responder-1.0.jar"]
