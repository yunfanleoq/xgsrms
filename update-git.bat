@echo off
chcp 936 > nul
echo Starting code update process...

echo Saving local changes...
git stash save "Local changes before update"

echo Pulling latest code...
git pull

echo Restoring local changes...
git stash pop

echo Code update complete!
pause 