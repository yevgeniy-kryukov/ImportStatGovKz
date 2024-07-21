# ImportStatGovKz

Загрузчик данных по юридическим лицам и ИП с сервиса stat.gov.kz

Для работы создайте следующие файлы настроек:

app.properties:

    downloadDir = c:\\temp

    useProxy = false

    proxyHost =

    proxyPort =

    countLoadThreads = 5

    logPath = d:/logs

database.properties:

    url = jdbc:edb://localhost:5445/portal

    username = enterprisedb

    password = Welc0me

lo4j.properties:

    log4j.rootLogger=DEBUG, A1
    log4j.appender.A1=org.apache.log4j.ConsoleAppender
    log4j.appender.A1.layout=org.apache.log4j.PatternLayout
    log4j.appender.A1.layout.ConversionPattern=%d{ISO8601} [%c] %p: %m%n
    log4j.category.com.monitorjbl=DEBUG
