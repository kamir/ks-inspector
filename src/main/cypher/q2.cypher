#
# Find all topics with two registered subjects
#
MATCH p=()<-[r1:hasRegisteredSchema]-(n)-[r2:hasRegisteredSchema]->()
RETURN n
