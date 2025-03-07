FROM gradle:jdk21
# RUN apk add curl
# RUN apk add busybox-extras
VOLUME /data/db
COPY build/libs/miniinsta-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/home/gradle/app.jar"]