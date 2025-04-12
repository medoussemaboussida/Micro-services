FROM openjdk:17
EXPOSE 8094
ADD target/Gateway-0.0.1-SNAPSHOT.jar Gateway.jar
ENTRYPOINT ["java","-jar","Gateway.jar"]