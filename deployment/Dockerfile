FROM bitnami/java:21.0.3-10-debian-12-r0

RUN /bin/bash -c "apt update"
RUN /bin/bash -c "apt install -y iproute2 iputils-ping telnet"

WORKDIR app/

COPY App.jar ./App.jar
COPY lib ./lib
COPY config ./config
COPY exporter ./exporter

CMD ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-javaagent:./exporter/jmx_prometheus_javaagent-0.19.0.jar=20004:./exporter/config.yaml", "-Dfile.encoding=UTF-8", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8", "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector", "-jar", "App.jar"]
