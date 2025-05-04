#!/bin/bash

allowBuildBranchs=("dev" "dev-1" "dev-2" "release" "pre-master" "master")
dockerRegistryUrl="repositories.stringee.com:8842/repository/cogover"
dockerRegistryUsername="cogover"



#Lay thong tin git
gitCommitter=$(git log -1 --pretty=format:'%an')
gitCommitHash=$(git rev-parse --short HEAD)
gitRepoUrl=$(git config --get remote.origin.url)
gitRepoName=$(basename "${gitRepoUrl}" .git)
gitCleanRepoUrl=$(echo ${gitRepoUrl} | sed 's/https:\/\/[^@]*@/https:\/\//')
gitBranch=$(git rev-parse --abbrev-ref HEAD)


#Check xem co build docker cho nhanh nay hay ko ==>
canBuild=false
for branch in "${allowBuildBranchs[@]}"; do
    if [ "$branch" = "$gitBranch" ]; then
        canBuild=true
    fi
done
if [ "$canBuild" = false ]; then
    echo "********* Do not build for branch: $gitBranch *********"
    exit 0
fi
#Check xem co build docker cho nhanh nay hay ko <==

echo "gitRepoName: $gitRepoName"
echo "gitBranch: $gitBranch"
echo "gitCommitHash: $gitCommitHash"


#clean & build
#./gradlew clean
#./gradlew build


#Build docker
cd _deploy || exit 1




#docker login --username "$dockerRegistryUsername" --password "$dockerRegistryPassword" "$dockerRegistryUrl"
docker login --username "$dockerRegistryUsername" "$dockerRegistryUrl"

docker build -t "$dockerRegistryUrl"/"$gitRepoName":"$gitCommitHash" .
docker push "$dockerRegistryUrl"/"$gitRepoName":"$gitCommitHash"

docker build -t "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch" .
docker push "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch"

docker build -t "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch"-"$gitCommitHash" .
docker push "$dockerRegistryUrl"/"$gitRepoName":"$gitBranch"-"$gitCommitHash"
