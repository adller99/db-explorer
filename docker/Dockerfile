FROM gradle:6.8.3-jdk11 AS build
WORKDIR ../
COPY --chown=gradle:gradle . /home/gradle/src
VOLUME /tmp
ADD build/libs/dbexplorer-0.0.1-SNAPSHOT.jar dbexplorer-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","dbexplorer-0.0.1-SNAPSHOT.jar"]


