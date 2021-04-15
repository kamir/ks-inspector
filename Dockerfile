FROM gradle:jdk11

RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y maven

### Get source from git repo https://github.com/christophschubert/kafka-clusterstate-tools.git
#RUN git clone -b ks-inspector-linking https://github.com/nbchn/kafka-clusterstate-tools.git /app/kafka-clusterstate-tools
### Get source from git repo https://github.com/kamir/ks-inspector
#RUN git clone https://github.com/kamir/ks-inspector /app/ks-inspector
#
#RUN cd /app/kafka-clusterstate-tools && sh /app/kafka-clusterstate-tools/gradlew build install
#RUN cd /app/ks-inspector && mvn package

#
# We copy the final JAR files into the image to save some time ...
#

RUN mkdir -p /app/kafka-clusterstate-tools
COPY kafka-clusterstate-tools/build/libs/kafka-clusterstate-tools-1.0.1-SNAPSHOT.jar /app/kafka-clusterstate-tools/kafka-clusterstate-tools-1.0.1-SNAPSHOT.jar

RUN mkdir -p /app/ks-inspector
COPY target/ks-inspector-1.0-SNAPSHOT.jar /app/ks-inspector/ks-inspector-1.0-SNAPSHOT.jar
