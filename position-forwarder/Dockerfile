FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/position/forwarder

COPY target/position-forwarder-1.0.jar /usr/app/gps/position/forwarder

CMD ["java", "-jar", "/usr/app/gps/position/forwarder/position-forwarder-1.0.jar"]
