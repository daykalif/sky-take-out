nginx安装目录：/opt/homebrew/Cellar/nginx/1.27.3
nginx配置文件：/opt/homebrew/etc/nginx/nginx.conf
替换nginx的前端html页面：/opt/homebrew/var/www
nginx日志：/opt/homebrew/var/log/nginx
新增的nginx日志：/opt/homebrew/Cellar/nginx/1.27.3/logs

进入nginx配置目录：cd /opt/homebrew/etc/nginx
重新加载nginx配置文件：nginx -s reload
停止nginx：sudo nginx -s stop
启动nginx：sudo nginx
【重启电脑后运行这行】运行nginx：open /opt/homebrew/bin/nginx

tomcat安装地址：cd /opt/homebrew/etc/tomcat@9

该目录下的www文件夹就是nginx替换前端html页面的www文件夹：/opt/homebrew/var/www



前端访问地址：http://localhost/#/login
