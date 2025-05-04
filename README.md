

### Cách chạy test trên máy Local
+ Bật VPN => bật Lens để kết nối đến K8s
+ Bật fw port Vault hệ thống nbt-dev về máy local port: 8210
+ Tạo 1 key-value: kv/module_addr/YOUR_USER_NAME
+ Sửa thông tin Vault (addr, token) ở file .env.dev, lưu ý biến "VAULT_KV_MODULE_ADDR" sửa đúng bằng kvPath đã tạo ở trên



### Deploy

```
build

sh ./deployment/one_click_deploy.sh
```


