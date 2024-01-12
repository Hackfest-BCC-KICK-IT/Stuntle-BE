FROM openjdk:17 AS build
COPY . .
RUN ./mvnw clean package

FROM eclipse-temurin:17.0.8.1_1-jre AS run
COPY --from=build /target/sipas-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "sipas-0.0.1-SNAPSHOT.jar"]