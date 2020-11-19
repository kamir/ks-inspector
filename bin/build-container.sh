#!/bin/sh

source build-jars.sh

# we are in the project root folder ...
docker build . -t kstb:latest

#docker login --username=yourhubusername --email=youremail@company.com

#docker buikd . -t kamir/kstb:latest
#docker push yourhubusername/verse_gapminder

