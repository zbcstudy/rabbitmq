准备：
yum install 
build-essential openssl openssl-devel unixODBC unixODBC-devel 
make gcc gcc-c++ kernel-devel m4 ncurses-devel tk tc xz

下载：
wget www.rabbitmq.com/releases/erlang/erlang-18.3-1.el7.centos.x86_64.rpm
wget http://repo.iotti.biz/CentOS/7/x86_64/socat-1.7.3.2-5.el7.lux.x86_64.rpm
wget www.rabbitmq.com/releases/rabbitmq-server/v3.6.5/rabbitmq-server-3.6.5-1.noarch.rpm

配置文件：
vim /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app
比如修改密码、配置等等，例如：loopback_users 中的 <<"guest">>,只保留guest
服务启动和停止：
启动 rabbitmq-server start &
停止 rabbitmqctl app_stop// rabbitmq-server stop

查看状态：rabbitmqctl status

查看是否已经启动：lsof -i:5672
查看插件列表： rabbitmq-plugins list

管理插件：rabbitmq-plugins enable rabbitmq_management
访问地址：http://192.168.11.76:15672/


rabbitmq 集群架构模式
主备模式 并发和数据量不高的情况下

远程模式 ：shovel
    启用插件：rabbitmq-plugins enable amqp-client
             rabbitmq-plugins enable rabbitmq_shovel
             
镜像模式

多活模式：插件--federation
