cd ..
git add .
echo "Clean the repo: $1"
git commit -m "prep clean $1"
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch $1" --prune-empty --tag-name-filter cat -- --all
