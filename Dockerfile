FROM scratch
WORKDIR /files

COPY target/classes/logging.properties ./conf/
COPY target/carts.jar target/libs/* ./lib/
