#---------------------------------------------------------------
# Find Kafka-Streams-Applications which process classified data
#---------------------------------------------------------------

# match (a:KSApp)-[*..8]-(b:ClassifiedField) return distinct a, b;

match p=(a:KSApp)-[*..8]-(b:ClassifiedField) return p, a, b;
