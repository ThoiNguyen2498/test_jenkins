#!/bin/bash

K8S_MASTER_USER=stringee
K8S_MASTER_HOST=sm-master


#Lay thong tin git
gitRepoUrl=$(git config --get remote.origin.url)
gitRepoName=$(basename "${gitRepoUrl}" .git)
gitCommitHash=$(git rev-parse --short HEAD)
gitBranch=$(git rev-parse --abbrev-ref HEAD)


#Lay thong tin de deploy
DEPLOY_MODULE=$gitRepoName
DEPLOY_COMMIT=$gitCommitHash
DEPLOY_BRANCH=$gitBranch


echo "Deploy git repo name: $DEPLOY_MODULE"
echo "Deploy git repo branch: $DEPLOY_BRANCH"
echo "Deploy git repo commit: $DEPLOY_COMMIT"

if [ ! -e "./deployment/k8s-master-ssh.private.key" ]; then
    echo "ERROR: File ./k8s-master-ssh.private.key không tồn tại."
    exit 1
fi

chmod 400 ./deployment/k8s-master-ssh.private.key

# Helm deploy
IMAGE_TAG=$gitBranch
ssh -o StrictHostKeyChecking=no -i ./deployment/k8s-master-ssh.private.key ${K8S_MASTER_USER}@${K8S_MASTER_HOST} -T "cd /data/deploy/nbt-${DEPLOY_BRANCH}/nbt-helm-chart && git fetch && git stash && git pull && ./deploy-module.sh $DEPLOY_MODULE $IMAGE_TAG $DEPLOY_COMMIT nbt-$DEPLOY_BRANCH"


