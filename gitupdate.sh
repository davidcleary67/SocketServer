echo "Message: $1"
git status
git add -A
git commit -m "$1"
git push origin main
git status
