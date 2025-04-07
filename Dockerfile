FROM eclipse-temurin:17-jre-alpine

COPY buildSrc/build/libs/buildSrc.jar /opt/app/buildSrc.jar

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

CMD ["java", "-jar", "/opt/app/buildSrc.jar"]