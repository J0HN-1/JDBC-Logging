FROM openjdk:8-jre-alpine

ARG JAR_NAME
ENV JAR_NAME=${JAR_NAME}

COPY ${JAR_NAME} /opt/${JAR_NAME}

ENTRYPOINT ["java", "-jar", "echo /opt/${JAR_NAME}"]
