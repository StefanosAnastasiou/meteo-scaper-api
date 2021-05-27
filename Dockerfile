FROM alpine:latest

RUN apk update && \
    apk upgrade && \
    apk add --no-cache bash maven openjdk8

RUN mkdir /app

WORKDIR /app

COPY /target/Meteo-Scraper-API.jar /app

EXPOSE 8087

CMD ["java", "-Duser.timezone=Europe/Athens", "-jar", "Meteo-Scraper-API.jar"]