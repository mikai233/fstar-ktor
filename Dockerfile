FROM openjdk:11
COPY build/libs/fstar-ktor-0.0.1-all.jar fstar-ktor.jar
EXPOSE 9009
ENTRYPOINT ["java","-jar","-Ddruid.mysql.usePingMethod=false","/fstar-ktor.jar"]