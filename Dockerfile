FROM openjdk:11
VOLUME /tmp
EXPOSE 8015
ADD ./target/fixedtaccount-0.0.1-SNAPSHOT.jar fixedtaccount.jar
ENTRYPOINT ["java","-jar","/fixedtaccount.jar"]