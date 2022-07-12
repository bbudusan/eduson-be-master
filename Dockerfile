FROM maven:3.6-jdk-11 as build

# copy pom and download dependencies
COPY pom.xml .
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

# dependencies already downloaded so just create the jar
COPY src/ src/
RUN ["mvn", "package", "-DskipTests"]

FROM openjdk:11-jre-slim

ENV DB=eduson-db
RUN apt-get update; apt-get install -y fontconfig libfreetype6; apt-get -y install ksh; apt-get -y install zsh; apt-get -y install inotify-tools; apt-get -y install curl;

EXPOSE 8080

WORKDIR /app
COPY --from=build /target/eduson-0.0.1-SNAPSHOT.jar ./app.jar

# this variable is looked for by spring
ENV SPRING_PROFILES_ACTIVE=cloud
ENTRYPOINT ["java","-jar","app.jar"]
