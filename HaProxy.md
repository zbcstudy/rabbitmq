HaProxy搭建主备模式的集群架构

haproxy配置：
listen rabbitmq_cluster
bind 0.0.0.0:5672  #配置tcp模式
mode tcp  #简单的轮询
balance roundrobin #主节点
server bhz128 192.168.115.128:5672 check inter 5000 rise 2 fall 2
server bhz129 192.168.115.129:5672 backup check inter 5000 rise 2 fall 2 #备用节点
