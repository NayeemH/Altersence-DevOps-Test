#Build Stage
FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
RUN mvn -f /app/pom.xml clean package

RUN ls -al


#package stage
FROM openjdk:20-ea-14-jdk-oraclelinux8
COPY --from=build /app/target/*SNAPSHOT.jar .
COPY --from=build /app/input.txt .
COPY --from=build /app/out/artifacts/DevOpsTest1_jar/*.jar .
EXPOSE 80
ENTRYPOINT exec java $JAVA_OPTS -jar DevOpsTest1.jar