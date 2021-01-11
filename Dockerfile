FROM gradle:6.7.1-jdk8 as builder
USER root
WORKDIR /builder
ADD . /builder
# Skip '-x test' task because it requires a working db connection, wich cannot be established at this point, because
# the postgres container has not started yet
RUN gradle build -x test --stacktrace

FROM openjdk:8-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=builder /builder/build/libs/spring_mvc_ohm-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "spring_mvc_ohm-0.0.1-SNAPSHOT.jar"]