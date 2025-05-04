
### 1. Nếu cấu hình enable Vault, App đọc các biến môi trường sau để lấy Vault addr, info, kvPath
```
ENV_HASHICORP_VAULT_ADDRESS=http://localhost:8200/
ENV_HASHICORP_VAULT_TOKEN=123456
ENV_HASHICORP_VAULT_VERSION=2
ENV_VAULT_TYPE=0
VAULT_KV_MODULE_ADDR=kv/module_addr/test
```

### 2. Credentials

      
+ rpcHttpServerPrivatePath: kv/authorization-server/rpcHttpServerPrivate
```
{
  "password": "...",
  "username": "..."
}
```


### 3. Module addrs
Chứa địa chỉ các module (bao gồm DB và các module khác của hệ thống)

+ kv/module_addr/system: dùng để load khi chạy thật (các địa chỉ dạng nội bộ, sử dụng trong K8S)
```
{
  "MYSQL_APPLE": "{\"ro\":\"127.0.0.1:3306\", \"rw\":\"127.0.0.1:3306\"}",
  "MYSQL_ORANGE": "{\"ro\":\"127.0.0.1:3306\", \"rw\":\"127.0.0.1:3306\"}",
  "auth-server-rpc": "{\"http\":\"authorization-server-svc-rpc-http-private:7181\", \"tcp\":\"authorization-server-svc-rpc-tcp-private:7180\", \"httpPublic\":\"authorization-server-svc-rpc-http-public:10100\"}",
  "mysql-common": "{\"ro\":\"127.0.0.1:3306\", \"rw\":\"127.0.0.1:3306\"}"
}
```

+ kv/module_addr/test: dùng để load khi chạy test (các địa chỉ được public ra ngoài cho Build-server kết nối đến)
```
{
  "MYSQL_APPLE": "{\"ro\":\"mysql-apple-slave-svc:3306\", \"rw\":\"mysql-apple-master-svc:3306\"}",
  "MYSQL_ORANGE": "{\"ro\":\"mysql-orange-slave-svc:3306\", \"rw\":\"mysql-orange-master-svc:3306\"}",
  "auth-server-rpc": "{\"http\":\"authorization-server-svc-rpc-http-private:7181\", \"tcp\":\"authorization-server-svc-rpc-tcp-private:7180\", \"httpPublic\":\"authorization-server-svc-rpc-http-public:10100\"}",
  "mysql-common": "{\"ro\":\"mysql-slave-svc:3306\", \"rw\":\"mysql-master-svc:3306\"}"
}
```




