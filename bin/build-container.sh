#!/bin/sh

#git submodule update --init --recursive
#git submodule update --recursive --remote

source build-jars.sh

# we are in the project root folder ...
docker build . -t kstb:latest


###
### TODO: use my own Dockerhub account
###
#
#docker login --username=yourhubusername --email=youremail@company.com
#docker build . -t kamir/kstb:latest
#docker push yourhubusername/verse_gapminder
#
