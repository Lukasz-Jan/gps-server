FROM eclipse-temurin:21-jre-alpine-3.23

RUN mkdir -p /usr/app/gps/register-server

COPY target/register-server-1.0.jar /usr/app/gps/register-server

CMD ["java", "-jar", "/usr/app/gps/register-server/register-server-1.0.jar"]
