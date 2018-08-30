FROM openjdk:8-jre-alpine

ARG JAR_NAME
ENV JAR_NAME=${JAR_NAME}

COPY ${JAR_NAME} /opt/app.jar

ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
