FROM eclipse-temurin:21.0.1_12-jre-alpine
ARG UNAME=java
ARG UID=101
ARG GID=101

WORKDIR /app
COPY target/worklist*.jar .
RUN mv worklist* worklist.jar

RUN set -x \
 && addgroup -g $GID $UNAME \
 && adduser -u $UID -G $UNAME -s /bin/bash -D $UNAME \
 && chown -R 101 /app
USER $UNAME

EXPOSE 8082
ENTRYPOINT ["java", "-Dlogging.config=file:/app/logback.xml", "-jar", "worklist.jar"]
