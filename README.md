rest-nettyserver
===============

rest-nettyserver是基于netty通信框架的轻量级、高性能RESTful服务，用于构建分布式系统。<br/>
集成了netty、spring、fastjson、mybatis、h2、BrekeleyDB、javamelody及memcached通用缓存模块

![性能测试结果](https://raw.github.com/langke93/rest-nettyserver/master/doc/img/performance.jpg)

上图是性能测试结果：在c10k环境下，平均每秒处理2w个请求，前面一部分是<a href="https://github.com/langke93/rest-testscript">rest-testscript</a>测试结果，后面是webbench测试结果
　
测试环境：<br/>
CPU:2*E5620  @ 2.40GHz <br/>
内存：16GB <br/>
JDK:Java HotSpot(TM) 64-Bit Server VM 1.6.0_33