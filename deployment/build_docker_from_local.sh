#!/bin/bash

allowBuildBranches=("dev" "dev-1" "dev-2" "release" "pre-master" "master")
dockerRegistryUrl="repositories.stringee.com:8842/repository/cogover"
dockerRegistryUsername="cogover"


#Lay thong tin git
gitCommitHash=$(git rev-parse --short HEAD)
gitRepoUrl=$(git config --get remote.origin.url)
gitRepoName=$(basename "${gitRepoUrl}" .git)
gitBranch=$(git rev-parse --abbrev-ref HEAD)


#Check xem co build docker cho nhanh nay hay ko ==>
canBuild=false
for branch in "${allowBuildBranches[@]}"; do
    if [ "$branch" = "$gitBranch" ]; then
        canBuild=true
    fi
done
if [ "$canBuild" = false ]; then
    echo "********* Do not build git branch: $gitBranch *********"
    exit 0
fi


echo "Build git repo name: $gitRepoName"
echo "Build git repo branch: $gitBranch"
echo "Build git repo commit: $gitCommitHash"


#Build docker
if [ ! -e "./_deploy/App.jar" ]; then
    echo "ERROR: File ./_deploy/App.jar không tồn tại. Bạn cần build trước khi chạy deploy!"
    exit 1
fi
cd _deploy || exit 1

# Kiểm tra xem file có tồn tại hay không
if [ ! -e "../deployment/dockerRegistryPassword.txt" ]; then
    echo "ERROR: File ../deployment/dockerRegistryPassword.txt không tồn tại."
    exit 1
fi

dockerRegistryPassword=$(head -n 1 ../deployment/dockerRegistryPassword.txt)
docker login --username "$dockerRegistryUsername" --password "$dockerRegistryPassword" "$dockerRegistryUrl"

docker build --platform linux/amd64 -t "$dockerRegistryUrl"/"$gitRepoName":"$gitCommitHash" .
docker push "$dockerRegistryUrl"/"$gitRepoName":"$gitCommitHash"

docker build --platform linux/amd64 -t "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch" .
docker push "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch"

docker build --platform linux/amd64 -t "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch"-"$gitCommitHash" .
docker push "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch"-"$gitCommitHash"
