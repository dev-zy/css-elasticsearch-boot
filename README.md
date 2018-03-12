# css-elasticsearch-boot
Elasticsearch\u4e0espring-boot\u96c6\u6210\uff0c\u89e3\u51b3jar\u4f9d\u8d56\u4e0e\u4e1a\u52a1\u9879\u76eejar\u5305\u7684\u51b2\u7a81\uff0c\u5e76\u6269\u5c55HTTP\u3001Rest\u3001Transport\u63a5\u53e3\uff0c\u652f\u6301\u538b\u529b\u6d4b\u8bd5
## \u4e00\u3001jar\u5305\u51b2\u7a81
```
1.log4j2
2.jackson
3.io.netty\u4ee5\u53cajboss.netty
4.fastjson
```
## \u4e8c\u3001\u76f8\u5173\u914d\u7f6e\u53ca\u8bf4\u660e
### 1. Maven\u914d\u7f6e
```xml
<dependency>
    <groupId>com.ucloudlink.css</groupId>
    <artifactId>css-elasticsearch-boot</artifactId>
    <version>5.x.x</version>
</dependency>
```
### 2.\u538b\u6d4b\u53c2\u6570
```properties
es.thread=1	//\u7ebf\u7a0b\u6570(<1:\u7531\u7cfb\u7edfCPU\u6838\u6570\u51b3\u5b9a)
es.opt=w	//\u538b\u6d4b\u65b9\u5f0f:w(\u5199\u5165[\u9ed8\u8ba4])/(\u8bfb\u53d6)
es.type=0	//\u8bbf\u95ee\u65b9\u5f0f:0.HTTP[\u6807\u51c6HTTP\u65b9\u5f0f],1.Rest[\u5185\u7f6eHTTP\u65b9\u5f0f],2.HighRest[\u5185\u7f6eHTTP\u65b9\u5f0f],3.Transport\u65b9\u5f0f[\u5185\u7f6e\u63a5\u53e3],4.Spring\u65b9\u5f0f[\u5185\u7f6e\u63a5\u53e3]
es.loop=1	//\u538b\u6d4b\u6b21\u6570(<1:\u65e0\u9650\u6b21,\u9ed8\u8ba4:1)
es.data.gt1k=false//\u538b\u6d4b\u6570\u636e\u662f\u5426\u5927\u4e8e1KB
```
\u6216
```properties
elasticsearch.thread=1	//\u7ebf\u7a0b\u6570(<1:\u7531\u7cfb\u7edfCPU\u6838\u6570\u51b3\u5b9a)
elasticsearch.opt=w	//\u538b\u6d4b\u65b9\u5f0f:w(\u5199\u5165[\u9ed8\u8ba4])/(\u8bfb\u53d6)
elasticsearch.type=0	//\u8bbf\u95ee\u65b9\u5f0f:0.HTTP[\u6807\u51c6HTTP\u65b9\u5f0f],1.Rest[\u5185\u7f6eHTTP\u65b9\u5f0f],2.HighRest[\u5185\u7f6eHTTP\u65b9\u5f0f],3.Transport\u65b9\u5f0f[\u5185\u7f6e\u63a5\u53e3],4.Spring\u65b9\u5f0f[\u5185\u7f6e\u63a5\u53e3]
elasticsearch.loop=1	//\u538b\u6d4b\u6b21\u6570(<1:\u65e0\u9650\u6b21,\u9ed8\u8ba4:1)
elasticsearch.data.gt1k=false//\u538b\u6d4b\u6570\u636e\u662f\u5426\u5927\u4e8e1KB
```
\u9ed8\u8ba4\u4f7f\u7528\u91c7\u7528jdk\u7ebf\u7a0b\u65b9\u5f0f\uff0c\u82e5\u4f7f\u7528\u7ebf\u7a0b\u6c60\u65b9\u5f0f\u589e\u52a0\u53c2\u6570\uff1aes.threadpool=executor\u6216elasticsearch.threadpool=executor

\u5176\u4ed6\u53c2\u6570\u53c2\u8003springboot\u53c2\u6570
### 3.ES Version VS Log4j Vesion
ES version | Log4j version
-----------|-----------
5.6.x | 2.9.0
5.5.x | 2.8.2
5.4.x | 2.8.2
5.3.x | 2.7
