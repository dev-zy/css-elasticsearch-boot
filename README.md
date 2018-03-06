# css-elasticsearch-boot
Elasticsearch与spring-boot集成，解决jar依赖与业务项目jar包的冲突，并扩展HTTP、Rest、Transport接口，支持压力测试

## 一、jar包冲突
```
1.log4j2
2.jackson
3.io.netty以及jboss.netty
4.fastjson
```
## 二、相关配置及说明

1. Maven配置
------------
```xml
<dependency>
    <groupId>com.ucloudlink.css</groupId>
    <artifactId>css-elasticsearch</artifactId>
    <version>5.x.x</version>
</dependency>
```

2.压测参数
```properties
es.thread=1	//线程数
es.opt=w	//压测方式:w(写入[默认])/(读取)
es.type=0	//访问方式:0.HTTP[标准HTTP方式],1.Rest[内置HTTP方式],2.HighRest[内置HTTP方式],3.Transport方式[内置接口],4.Spring方式[内置接口]
es.loop=1	//压测次数(<1:无限次,默认:1)
es.data.gt1k=false//压测数据是否大于1KB
```
或
```properties
elasticsearch.thread=1	//线程数
elasticsearch.opt=w	//压测方式:w(写入[默认])/(读取)
elasticsearch.type=0	//访问方式:0.HTTP[标准HTTP方式],1.Rest[内置HTTP方式],2.HighRest[内置HTTP方式],3.Transport方式[内置接口],4.Spring方式[内置接口]
elasticsearch.loop=1	//压测次数(<1:无限次,默认:1)
elasticsearch.data.gt1k=false//压测数据是否大于1KB
```
默认使用采用jdk线程方式，若使用线程池方式增加参数：es.threadpool=executor或elasticsearch.threadpool=executor

其他参数为springboot参数
-------------------------------------
3.ES Version VS Log4j Vesion
----------------------------

ES version | Log4j version
-----------|-----------
5.6.x | 2.9.0
5.5.x | 2.8.2
5.4.x | 2.8.2
5.3.x | 2.7
