#!/bin/bash

export ENV_HASHICORP_VAULT_ADDRESS=http://localhost:8200/
export ENV_HASHICORP_VAULT_TOKEN=123456
export ENV_HASHICORP_VAULT_VERSION=2
export ENV_VAULT_TYPE=0
export VAULT_KV_MODULE_ADDR=kv/module_addr/test

java -jar App.jar
