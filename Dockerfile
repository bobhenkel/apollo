FROM nginx:1.10.3-alpine
MAINTAINER Roi Rav-Hon <roi@logz.io>
RUN apk add --update \
    openjdk8-jre \
    bash \
  && rm -rf /var/cache/apk/*

COPY docker/nginx.conf /etc/nginx/nginx.conf
COPY docker/go.sh /go.sh
EXPOSE 8080:80

COPY ui/dist /usr/share/nginx/html
COPY apollo-backend/target/apollo-backend-jar-with-dependencies.jar /apollo-backend-jar-with-dependencies.jar
CMD /go.sh
