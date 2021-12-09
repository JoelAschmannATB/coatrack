FROM alpine:edge
LABEL maintainer=coatrack.eu

RUN apk add --no-cache openjdk11

ARG MODULE_VERSION
ARG MODULE_NAME
ARG MODULE_DIR

COPY ${MODULE_DIR}/target/${MODULE_NAME}-${MODULE_VERSION}.jar /opt/coatrack/lib/${MODULE_NAME}.jar
COPY spring-boot/proxy/target/coatrack-proxy-${MODULE_VERSION}.jar /opt/coatrack/lib/coatrack-proxy-${MODULE_VERSION}.jar
RUN if [ ${MODULE_NAME} != "coatrack-admin" ]; then rm /opt/coatrack/lib/coatrack-proxy-${MODULE_VERSION}.jar; fi
ENV JAVA_OPTS=""
ENV MODULE_NAME=${MODULE_NAME}
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /opt/coatrack/lib/$MODULE_NAME.jar" ]
