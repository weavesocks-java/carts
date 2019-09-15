FROM scratch
WORKDIR /files

COPY target/classes/logging.properties ./conf/
COPY target/classes/fluentd-cloud.conf ./conf/
COPY target/carts.jar target/libs/* ./lib/
