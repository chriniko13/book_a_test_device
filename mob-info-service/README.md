
## Mob Info Service

### Description

Because FonoApi (`https://fonoapi.freshpixl.com/`) was down, this service has been developed in order to act as
mobile phone information provider.


### Build and Run
* Execute: `mvn clean install -pl mob-info-service/`
* Then run: ` java -jar mob-info-service/target/mob-info-service-1.0-SNAPSHOT-jar-with-dependencies.jar`
* You should see output like the following:
```text
14:40:30,824 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
14:40:30,825 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
14:40:30,907 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - debug attribute not set
14:40:30,908 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
14:40:30,913 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [CONSOLE]
14:40:30,958 |-WARN in ch.qos.logback.core.ConsoleAppender[CONSOLE] - This appender no longer admits a layout as a sub-component, set an encoder instead.
14:40:30,958 |-WARN in ch.qos.logback.core.ConsoleAppender[CONSOLE] - To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.
14:40:30,958 |-WARN in ch.qos.logback.core.ConsoleAppender[CONSOLE] - See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details
14:40:30,959 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [com.chriniko.mob.info.service] to DEBUG
14:40:30,959 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting additivity of logger [com.chriniko.mob.info.service] to false
14:40:30,959 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [CONSOLE] to Logger[com.chriniko.mob.info.service]
14:40:30,959 |-INFO in ch.qos.logback.classic.joran.action.RootLoggerAction - Setting level of ROOT logger to ERROR
14:40:30,959 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [CONSOLE] to Logger[ROOT]
14:40:30,959 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
14:40:30,960 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@4e1d422d - Registering current configuration as safe fallback point

```