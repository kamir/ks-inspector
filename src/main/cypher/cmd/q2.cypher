#---------------------------------------------------------------
# Find all topics with two registered subjects
#---------------------------------------------------------------

MATCH p=()<-[r1:hasSubject]-(n)-[r2:hasSubject]->()
RETURN distinct n.name as topicname
