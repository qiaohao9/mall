drop database if exists mall;
drop user if exists 'mall'@'localhost';
# 支持emoji：需要mysql数据库参数： character_set_server=utf8mb4
create database mall default character set utf8mb4 collate utf8mb4_unicode_ci;
use mall;
create user 'mall'@'localhost' identified by '123456';
grant all privileges on mall.* to 'mall'@'localhost';
flush privileges;