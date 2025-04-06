FROM openjdk:17-oracle
COPY ./ImpliedVolatilityMicroService/build/libs/ImpliedVolatilityMicroService-0.0.1-SNAPSHOT.jar ImpliedVolatilityMicroService-0.0.1-SNAPSHOT.jar
WORKDIR /tmp
ENTRYPOINT ["java","-jar","/ImpliedVolatilityMicroService-0.0.1-SNAPSHOT.jar"]