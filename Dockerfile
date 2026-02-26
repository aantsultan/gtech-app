FROM ubuntu:jammy
COPY target/gtech-app /gtech-app-docker
CMD ["/gtech-app-docker"]