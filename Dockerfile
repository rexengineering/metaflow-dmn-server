FROM openjdk:11 AS base
# install any other useful apps here
RUN apt-get update 
RUN apt-get install -y vim curl 

COPY ./src /usr/src/feel/src
COPY ./lib /usr/src/feel/lib
WORKDIR /usr/src/feel/src
ENV CLASSPATH=/usr/src/feel/src/.:/usr/src/feel/lib/*
ENV DMN_HOST=0.0.0.0
ENV DMN_PORT=8001
EXPOSE 8001

RUN javac DmnServer.java

FROM base AS test
CMD [ "true" ]

FROM base AS container

CMD ["java", "DmnServer"]

