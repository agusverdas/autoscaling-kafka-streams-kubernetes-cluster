FROM openjdk:11

ARG JMX_PORT="5555"
ENV JMX_JAVA_OPTS="-Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

COPY ./build/libs/kafka-streams-scaling-all.jar /usr/src/myapp/kafka-streams-scaling-all.jar

CMD ["sh", "-c", "java $JMX_JAVA_OPTS -jar /usr/src/myapp/kafka-streams-scaling-all.jar"]