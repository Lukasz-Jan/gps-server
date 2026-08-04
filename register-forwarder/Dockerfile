FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/forwarder

COPY target/register-forwarder-1.0.jar /usr/app/gps/forwarder

CMD ["java", "-jar", "/usr/app/gps/forwarder/register-forwarder-1.0.jar"]
