git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch graphdb/neo4j/data/transactions/neo4j/neostore.transaction.db.0' --prune-empty --tag-name-filter cat -- --all
