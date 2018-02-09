# Middleware
##### 中间件是基础软件，处于操作系统（或网络协议）与分布式应用之间。

## 分类：

消息中间件（MOM：Message-Oriented Middleware）

数据中间件（Database Middleware）

远程过程调用中间件（RPC：Remote Process Call）

对象请求代理中间件（ORB：Object Request Broker）

事务处理中间件（TP Monitor：Transaction Process Monitor）

J2EE中间件

## Kafka

官网地址：http://kafka.apache.org/

Kafka是一种高吞吐量的分布式发布订阅消息系统，它可以处理消费者规模的网站中的所有动作流数据

    1）、以时间复杂度为O(1)的方式提供消息持久化能力，即使对TB级以上数据也能保证常数时间复杂度的访问性能。高吞吐率。

    2）、即使在非常廉价的商用机器上也能做到单机支持每秒100K条以上消息的传输。

    3）、支持Kafka Server间的消息分区，及分布式消费，同时保证每个Partition内的消息顺序传输。

    4）、同时支持离线数据处理和实时数据处理。

    5）、Scale out：支持在线水平扩展。

## Redis
官网地址：http://www.redis.cn/

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。
