proxy_set_header Host $host;
proxy_set_header X-Real-IP $remote_addr;
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto $scheme;
# 弱口令清单-跨境登录：启用 GeoIP2 后改为 $geoip2_country_code
# proxy_set_header X-Country-Code $geoip2_country_code;
proxy_http_version 1.1;
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
client_max_body_size 1000m;
proxy_connect_timeout 600;
proxy_send_timeout 600;
proxy_read_timeout 600;
send_timeout 600;
access_log /www/wwwlogs/xgszp-boot.access.log combined;
