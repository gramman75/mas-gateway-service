FROM openjdk:11.0-jdk-slim-buster

VOLUME /temp

COPY target/gateway-service-1.0.jar gateway-service.jar

ENTRYPOINT ["java","-jar","gateway-service.jar"]
