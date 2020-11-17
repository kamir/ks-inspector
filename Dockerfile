FROM gradle:jdk11

RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y maven

## Get source from git repo https://github.com/christophschubert/kafka-clusterstate-tools.git
RUN git clone -b ks-inspector-linking https://github.com/nbchn/kafka-clusterstate-tools.git /app/kafka-clusterstate-tools
## Get source from git repo https://github.com/kamir/ks-inspector
RUN git clone https://github.com/kamir/ks-inspector /app/ks-inspector

RUN cd /app/kafka-clusterstate-tools && sh /app/kafka-clusterstate-tools/gradlew build install
RUN cd /app/ks-inspector && mvn package