FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/register-responder

COPY target/register-responder-1.0.jar /usr/app/gps/register-responder

CMD ["java", "-jar", "/usr/app/gps/register-responder/register-responder-1.0.jar"]
