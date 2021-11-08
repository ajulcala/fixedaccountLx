FROM openjdk:11
VOLUME /tmp
EXPOSE 8015
ADD ./target/fixedaccount-0.0.1-SNAPSHOT.jar fixedaccount.jar
ENTRYPOINT ["java","-jar","/fixedaccount.jar"]