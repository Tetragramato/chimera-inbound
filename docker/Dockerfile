FROM openjdk:8-jdk-alpine
ARG DIRECTORY=build-docker
COPY application-docker.yml /app/
COPY ${DIRECTORY}/*.jar /app/application.jar
RUN chmod +x /app/application.jar
ENTRYPOINT java -jar /app/application.jar --spring.config.additional-location=/app/application-docker.yml
