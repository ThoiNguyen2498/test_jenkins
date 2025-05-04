#!/bin/bash

sh ./deployment/build_docker_from_local.sh

sh ./deployment/deploy_k8s.sh


