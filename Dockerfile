FROM eclipse-temurin:21-alpine as build

COPY . .
RUN ./gradlew clean build

FROM eclipse-temurin:21-alpine as runtime

WORKDIR /app

COPY --from=build /build/libs/namingway-*-all.jar bot.jar

ENTRYPOINT ["java", "-Dlog4j.configurationFile=config/log4j2.xml", "-jar" , "bot.jar"]
