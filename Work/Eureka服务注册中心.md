# Eureka服务注册中心



　　服务注册中心是服务实现服务化管理的核心组件，类似于目录服务的作用，主要用来存储服务信息，譬如提供者 url 串、路由信息等。服务注册中心是微服务架构中最基础的设施之一。

　　在微服务架构流行之前，注册中心就已经开始出现在分布式架构的系统中。比如 Dubbo 是一个在国内比较流行的分布式框架，被大量的中小型互联网公司所采用，它提供了比较完善的服务治理功能，而服务治理的实现主要依靠的就是注册中心。



## 1.学习目标

![image-20211018171408727](E:/java49/java3/0905Eureka注册中心/006_document/Eureka服务注册中心.assets/image-20211018171408727.png)



## 2.什么是注册中心



　　注册中心可以说是微服务架构中的“通讯录”，它记录了服务和服务地址的映射关系。在分布式架构中，服务会注册到这里，当服务需要调用其它服务时，就到这里找到服务的地址，进行调用。

　　简单理解就是：在没有注册中心时候，服务间调用需要知道被当服务调方的具体地址（写死的 ip:port）。更换部署地址，就不得不修改调用当中指定的地址。而有了注册中心之后，每个服务在调用别人的时候只需要知道服务名称（软编码）就好，地址都会通过注册中心根据服务名称获取到具体的服务地址进行调用。

　　举个现实生活中的例子，比如说，我们手机中的通讯录的两个使用场景：

> 当我想给张三打电话时，那我需要在通讯录中按照名字找到张三，然后就可以找到他的手机号拨打电话。—— 服务发现
>
> 李四办了手机号并把手机号告诉了我，我把李四的号码存进通讯录，后续，我就可以从通讯录找到他。—— 服务注册
>
> 通讯录 —— ？什么角色（服务注册中心）

　　总结：服务注册中心的作用就是**服务的注册**和**服务的发现**。



## 常见的注册中心



- Netflix Eureka
- Alibaba Nacos
- HashiCorp Consul
- Apache ZooKeeper
- CoreOS Etcd
- CNCF CoreDNS



| 特性            | Eureka      | Nacos                      | Consul            | Zookeeper  |
| :-------------- | :---------- | :------------------------- | :---------------- | :--------- |
| CAP             | AP          | CP + AP                    | CP                | CP         |
| 健康检查        | Client Beat | TCP/HTTP/MYSQL/Client Beat | TCP/HTTP/gRPC/Cmd | Keep Alive |
| 雪崩保护        | 有          | 有                         | 无                | 无         |
| 自动注销实例    | 支持        | 支持                       | 不支持            | 支持       |
| 访问协议        | HTTP        | HTTP/DNS                   | HTTP/DNS          | TCP        |
| 监听支持        | 支持        | 支持                       | 支持              | 支持       |
| 多数据中心      | 支持        | 支持                       | 支持              | 不支持     |
| 跨注册中心同步  | 不支持      | 支持                       | 支持              | 不支持     |
| SpringCloud集成 | 支持        | 支持                       | 支持              | 支持       |



## CAP 原则与 BASE 理论



### CAP 原则



　　CAP 原则又称 CAP 定理，指的是在一个分布式系统中， Consistency（一致性）、 Availability（可用性）、Partition tolerance（分区容错性），三者不可得兼。

　　CAP 由 Eric Brewer 在 2000 年 PODC 会议上提出。该猜想在提出两年后被证明成立，成为我们熟知的 CAP 定理。CAP 三者不可兼得。

| 特性                | 定理                                                         |
| ------------------- | ------------------------------------------------------------ |
| Consistency         | **一致性**，也叫做数据原子性，系统在执行某项操作后仍然处于一致的状态。在分布式系统中，更新操作执行成功后所有的用户都应该读到最新的值，这样的系统被认为是具有强一致性的。等同于所有节点访问同一份最新的数据副本。 |
| Availability        | **可用性**，每一个操作总是能够在一定的时间内返回结果，这里需要注意的是"一定时间内"和"返回结果"。一定时间内指的是在可以容忍的范围内返回结果，结果可以是成功或者是失败，且不保证获取的数据为最新数据。 |
| Partition tolerance | **分区容错性**，分布式系统在遇到任何网络分区故障的时候，仍然能够对外提供满足一致性和可用性的服务，除非整个网络环境都发生了故障。 |

 

#### 取舍策略



　　CAP 三个特性只能满足其中两个，那么取舍的策略就共有三种：

- **CA without P**：如果不要求P（不允许分区），则C（强一致性）和A（可用性）是可以保证的。但放弃 P 的同时也就意味着放弃了系统的扩展性，也就是分布式节点受限，没办法部署子节点，这是违背分布式系统设计的初衷的。
- **CP without A**：如果不要求A（可用），相当于每个请求都需要在服务器之间保持强一致，而P（分区）会导致同步时间无限延长（也就是等待数据同步完才能正常访问服务），一旦发生网络故障或者消息丢失等情况，就要牺牲用户的体验，等待所有数据全部一致了之后再让用户访问系统。设计成 CP 的系统其实不少，最典型的就是分布式数据库，如 Redis、HBase 等。对于这些分布式数据库来说，数据的一致性是最基本的要求，因为如果连这个标准都达不到，那么直接采用关系型数据库就好，没必要再浪费资源来部署分布式数据库。
- **AP without C**：要高可用并允许分区，则需放弃一致性。一旦分区发生，节点之间可能会失去联系，为了高可用，每个节点只能用本地数据提供服务，而这样会导致全局数据的不一致性。典型的应用就如某米的抢购手机场景，可能前几秒你浏览商品的时候页面提示是有库存的，当你选择完商品准备下单的时候，系统提示你下单失败，商品已售完。这其实就是先在 A（可用性）方面保证系统可以正常的服务，然后在数据的一致性方面做了些牺牲，虽然多少会影响一些用户体验，但也不至于造成用户购物流程的严重阻塞。



#### 总结



　　现如今，对于多数大型互联网应用的场景，主机众多、部署分散，而且现在的集群规模越来越大，节点只会越来越多，所以节点故障、网络故障是常态，因此**分区容错性**也就成为了一个分布式系统必然要面对的问题。那么就只能在 C 和 A 之间进行取舍。但对于传统的项目就可能有所不同，拿银行的转账系统来说，涉及到金钱的对于数据一致性不能做出一丝的让步，C 必须保证，出现网络故障的话，宁可停止服务，可以在 A 和 P 之间做取舍。而互联网非金融项目普遍都是基于 AP 模式。

　　总而言之，没有最好的策略，好的系统应该是根据业务场景来进行架构设计的，只有适合的才是最好的。



### BASE 理论



　　CAP 理论已经提出好多年了，难道真的没有办法解决这个问题吗？也许可以做些改变。比如 C 不必使用那么强的一致性，可以先将数据存起来，稍后再更新，实现所谓的 “最终一致性”。

　　这个思路又是一个庞大的问题，同时也引出了第二个理论 BASE 理论。

> BASE：全称 Basically Available（基本可用），Soft state（软状态），和 Eventually consistent（最终一致性）三个短语的缩写，来自 ebay 的架构师提出。

　　BASE 理论是对 CAP 中一致性和可用性权衡的结果，其来源于对大型互联网分布式实践的总结，是基于 CAP 定理逐步演化而来的。其核心思想是：

> 既然无法做到强一致性（Strong consistency），但每个应用都可以根据自身的业务特点，采用适当的方式来使系统达到最终一致性（Eventual consistency）。



#### Basically Available（基本可用）



　　基本可用是指分布式系统在出现故障的时候，允许损失部分可用性（例如响应时间、功能上的可用性）。需要注意的是，基本可用绝不等价于系统不可用。

- 响应时间上的损失：正常情况下搜索引擎需要在 0.5 秒之内返回给用户相应的查询结果，但由于出现故障（比如系统部分机房发生断电或断网故障），查询结果的响应时间增加到了 1~2 秒。
- 功能上的损失：购物网站在购物高峰（如双十一）时，为了保护系统的稳定性，部分消费者可能会被引导到一个降级页面。



#### Soft state（软状态）



　　什么是软状态呢？相对于原子性而言，要求多个节点的数据副本都是一致的，这是一种 “硬状态”。

　　软状态是指允许系统存在中间状态，而该中间状态不会影响系统整体可用性。分布式存储中一般一份数据会有多个副本，允许不同副本数据同步的延时就是软状态的体现。



#### Eventually consistent（最终一致性）



　　系统不可能一直是软状态，必须有个时间期限。在期限过后，应当保证所有副本保持数据一致性。从而达到数据的最终一致性。这个时间期限取决于网络延时，系统负载，数据复制方案设计等等因素。

　　实际上，不只是分布式系统使用最终一致性，关系型数据库在某个功能上，也是使用最终一致性的，比如备份，数据库的复制都是需要时间的，这个复制过程中，业务读取到的值就是旧值。当然，最终还是达成了数据一致性。这也算是一个最终一致性的经典案例。



#### 总结



　　总的来说，BASE 理论面向的是大型高可用可扩展的分布式系统，和传统事务的 ACID 是相反的，它完全不同于 ACID 的强一致性模型，而是通过牺牲强一致性来获得可用性，并允许数据在一段时间是不一致的。



## 为什么需要注册中心



　　了解了什么是注册中心，那么我们继续谈谈，为什么需要注册中心。在分布式系统中，我们不仅仅是需要在注册中心找到服务和服务地址的映射关系这么简单，我们还需要考虑更多更复杂的问题：

- 服务注册后，如何被及时发现
- 服务宕机后，如何及时下线
- 服务如何有效的水平扩展
- 服务发现时，如何进行路由
- 服务异常时，如何进行降级
- 注册中心如何实现自身的高可用

　　这些问题的解决都依赖于注册中心。简单看，注册中心的功能有点类似于 DNS 服务器或者负载均衡器，而实际上，注册中心作为微服务的基础组件，可能要更加复杂，也需要更多的灵活性和时效性。所以我们还需要学习更多 Spring Cloud 微服务组件协同完成应用开发。



　　注册中心解决了以下问题：

- 服务管理
- 服务之间的自动发现
- 服务的依赖关系管理



## Eureka 介绍



　　Eureka 是 Netflix 开发的服务发现组件，本身是一个基于 REST 的服务。Spring Cloud 将它集成在其子项目 Spring Cloud Netflix  中，实现 Spring Cloud 的服务注册与发现，同时还提供了负载均衡、故障转移等能力。



## Eureka 注册中心三种角色（两种角色）



![image-20211018171505157](E:/java49/java3/0905Eureka注册中心/006_document/Eureka服务注册中心.assets/image-20211018171505157.png)



### Eureka Server



　　通过 Register、Get、Renew 等接口提供服务的注册和发现。



### Service Provider（Eureka Client）



　　服务提供方，把自身的服务实例注册到 Eureka Server 中。



### Service Consumer（Eureka Client）



　　服务调用方，通过 Eureka Server 获取服务列表，消费服务。

![](Eureka服务注册中心.assets/1578648274465.png)

## Eureka 入门案例



　　`eureka-demo` 聚合工程。`SpringBoot 2.2.4.RELEASE`、`Spring Cloud Hoxton.SR1`。



### 创建项目



　　我们创建聚合项目来讲解 Eureka，首先创建一个 pom 父工程。

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113319718.png)

![](Eureka服务注册中心.assets/1578822985788.png)

![](Eureka服务注册中心.assets/1578823080703.png)



### 添加依赖



　　pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 项目坐标地址 -->
    <groupId>com.xxxx</groupId>
    <!-- 项目模块名称 -->
    <artifactId>eureka-demo</artifactId>
    <!-- 项目版本名称 快照版本SNAPSHOT、正式版本RELEASE -->
    <version>1.0-SNAPSHOT</version>

    <!-- 继承 spring-boot-starter-parent 依赖 -->
    <!-- 使用继承方式，实现复用，符合继承的都可以被使用 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.4.RELEASE</version>
    </parent>

    <!--
        集中定义依赖组件版本号，但不引入，
        在子工程中用到声明的依赖时，可以不加依赖的版本号，
        这样可以统一管理工程中用到的依赖版本
     -->
    <properties>
        <!-- Spring Cloud Hoxton.SR1 依赖 -->
        <spring-cloud.version>Hoxton.SR1</spring-cloud.version>
    </properties>

    <!-- 项目依赖管理 父项目只是声明依赖，子项目需要写明需要的依赖(可以省略版本信息) -->
    <dependencyManagement>
        <dependencies>
            <!-- spring cloud 依赖 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>
```



### 注册中心 eureka-server



　　在刚才的父工程下创建 `eureka-server` 注册中心的项目。



#### 创建项目



![](Eureka服务注册中心.assets/1578836332989.png)

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113447822.png)

![](Eureka服务注册中心.assets/1578239500169.png)

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113547468.png)

![](Eureka服务注册中心.assets/1578823384080.png)



#### 添加依赖



　　pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxxx</groupId>
    <artifactId>eureka-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- 继承父依赖 -->
    <parent>
        <groupId>com.xxxx</groupId>
        <artifactId>eureka-demo</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <!-- 项目依赖 -->
    <dependencies>
        <!-- netflix eureka server 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

        <!-- spring boot test 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

</project>
```



#### 配置文件



　　application.yml

```yml
server:
  port: 8761 # 端口

spring:
  application:
    name: eureka-server # 应用名称

# 配置 Eureka Server 注册中心
eureka:
  instance:
    hostname: localhost			  # 主机名，不配置的时候将根据操作系统的主机名来获取
  client:
    register-with-eureka: false   # 是否将自己注册到注册中心，默认为 true
    fetch-registry: false         # 是否从注册中心获取服务注册信息，默认为 true
    service-url:                  # 注册中心对外暴露的注册地址
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```



#### 启动类



　　EurekaServerApplication.java

```java
package com.xxxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// 开启 EurekaServer 注解
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}
```



#### 访问



　　访问：http://localhost:8761/

![](Eureka服务注册中心.assets/1578243640844.png)



## 高可用 Eureka 注册中心



### 注册中心 eureka-server



#### 创建项目



　　在刚才的父工程下再创建一个 `eureka-server02` 注册中心的项目，如果是多机器部署不用修改端口，通过 IP 区分服务，如果在一台机器上演示需要修改端口区分服务。



#### 添加依赖



　　pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxxx</groupId>
    <artifactId>eureka-server02</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- 继承父依赖 -->
    <parent>
        <groupId>com.xxxx</groupId>
        <artifactId>eureka-demo</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <!-- 项目依赖 -->
    <dependencies>
        <!-- netflix eureka server 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

        <!-- spring boot test 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

</project>
```



#### 配置文件



　　集群配置下，注册中心需要相互注册实现信息的同步。

　　eureka-server 的 application.yml

```yml
server:
  port: 8761 # 端口

spring:
  application:
    name: eureka-server # 应用名称(集群下相同)

# 配置 Eureka Server 注册中心
eureka:
  instance:
    hostname: eureka01            # 主机名，不配置的时候将根据操作系统的主机名来获取
  client:
    # 设置服务注册中心地址，指向另一个注册中心
    service-url:                  # 注册中心对外暴露的注册地址
      defaultZone: http://localhost:8762/eureka/
```



　　eureka-server02 的 application.yml

```yml
server:
  port: 8762 # 端口

spring:
  application:
    name: eureka-server # 应用名称(集群下相同)

# 配置 Eureka Server 注册中心
eureka:
  instance:
    hostname: eureka02            # 主机名，不配置的时候将根据操作系统的主机名来获取
  client:
    # 设置服务注册中心地址，指向另一个注册中心
    service-url:                  # 注册中心对外暴露的注册地址
      defaultZone: http://localhost:8761/eureka/
```



#### 启动类



　　启动类不变，启动两个 server。



#### 访问



　　访问：http://localhost:8761/ 或者 http://localhost:8762/ 都出现如下图说明互相注册成功。

![](Eureka服务注册中心.assets/1578311267949.png)

　　`Status` 显示方式为默认值，如果想要清晰可见每个服务的 IP + 端口需要通过以下配置来实现。



#### 显示 IP + 端口



　　一个普通的 Netflix Eureka 实例注册的 ID 等于其主机名（即，每个主机仅提供一项服务）。 Spring Cloud Eureka 提供了合理的默认值，定义如下：

`${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}}`，也就是：主机名：应用名：应用端口。

　　我们也可以可以自定义进行修改：

```yml
eureka:
  instance:
    prefer-ip-address: true       # 是否使用 ip 地址注册
    instance-id: ${spring.cloud.client.ip-address}:${server.port} # ip:port
```

![](Eureka服务注册中心.assets/1578311606022.png)



### 服务提供者 service-provider



#### 创建项目



　　在刚才的父工程下创建一个 `service-provider` 服务提供者的项目。

![](Eureka服务注册中心.assets/1578836246536.png)

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113447822.png)

![](Eureka服务注册中心.assets/1578288878277.png)

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113547468.png)

![](Eureka服务注册中心.assets/1578824040960.png)



#### 添加依赖



　　pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxxx</groupId>
    <artifactId>service-provider</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- 继承父依赖 -->
    <parent>
        <groupId>com.xxxx</groupId>
        <artifactId>eureka-demo</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <!-- 项目依赖 -->
    <dependencies>
        <!-- netflix eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- spring boot web 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- spring boot test 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
  
</project>
```



#### 配置文件



　　application.yml

```yml
server:
  port: 7070 # 端口

spring:
  application:
    name: service-provider # 应用名称(集群下相同)

# 配置 Eureka Server 注册中心
eureka:
  instance:
    prefer-ip-address: true       # 是否使用 ip 地址注册
    instance-id: ${spring.cloud.client.ip-address}:${server.port} # ip:port
  client:
    service-url:                  # 设置服务注册中心地址
      defaultZone: http://localhost:8761/eureka/,http://localhost:8762/eureka/
```



#### 实体类



　　Product.java

```java
package com.xxxx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private Integer id;
    private String productName;
    private Integer productNum;
    private Double productPrice;

}
```



#### 编写服务



　　ProductService.java

```java
package com.xxxx.service;

import com.xxxx.pojo.Product;

import java.util.List;

/**
 * 商品服务
 */
public interface ProductService {

    /**
     * 查询商品列表
     *
     * @return
     */
    List<Product> selectProductList();

}
```

　　ProductServiceImpl.java

```java
package com.xxxx.service.impl;

import com.xxxx.pojo.Product;
import com.xxxx.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 商品服务
 */
@Service
public class ProductServiceImpl implements ProductService {

    /**
     * 查询商品列表
     *
     * @return
     */
    @Override
    public List<Product> selectProductList() {
        return Arrays.asList(
                new Product(1, "华为手机", 2, 5888D),
                new Product(2, "联想笔记本", 1, 6888D),
                new Product(3, "小米平板", 5, 2666D)
        );
    }

}
```



#### 控制层



　　ProductController.java

```java
package com.xxxx.controller;

import com.xxxx.pojo.Product;
import com.xxxx.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查询商品列表
     *
     * @return
     */
    @GetMapping("/list")
    public List<Product> selectProductList() {
        return productService.selectProductList();
    }

}
```

> 该项目我们可以通过单元测试进行测试，也可以直接通过 url 使用 postman 或者浏览器来进行测试。



#### 启动类



　　ServiceProviderApplication.java

```java
package com.xxxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 开启 EurekaClient 注解，目前版本如果配置了 Eureka 注册中心，默认会开启该注解
//@EnableEurekaClient
public class ServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProviderApplication.class, args);
    }

}
```



#### 注册中心



　　访问注册中心，可以看到用户服务已经注册至注册中心。

![](Eureka服务注册中心.assets/1578292644366.png)



### 服务消费者 service-consumer



#### 创建项目



　　在刚才的父工程下创建一个 service-consumer 服务消费者的项目。

![](Eureka服务注册中心.assets/1578836165343.png)

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113447822.png)

![](Eureka服务注册中心.assets/1578292823725.png)

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213113547468.png)

![](Eureka服务注册中心.assets/1578824558814.png)



#### 添加依赖



　　pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxxx</groupId>
    <artifactId>service-consumer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- 继承父依赖 -->
    <parent>
        <groupId>com.xxxx</groupId>
        <artifactId>eureka-demo</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <!-- 项目依赖 -->
    <dependencies>
        <!-- netflix eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- spring boot web 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- spring boot test 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
  
</project>
```



#### 配置文件



　　application.yml

```yml
server:
  port: 9090 # 端口

spring:
  application:
    name: service-consumer # 应用名称

# 配置 Eureka Server 注册中心
eureka:
  client:
    register-with-eureka: false         # 是否将自己注册到注册中心，默认为 true
    registry-fetch-interval-seconds: 10 # 表示 Eureka Client 间隔多久去服务器拉取注册信息，默认为 30 秒
    service-url:                        # 设置服务注册中心地址
      defaultZone: http://localhost:8761/eureka/,http://localhost:8762/eureka/
```



#### 实体类



 　　Product.java

```java
package com.xxxx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private Integer id;
    private String productName;
    private Integer productNum;
    private Double productPrice;

}
```



　　Order.java

```java
package com.xxxx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private Integer id;
    private String orderNo;
    private String orderAddress;
    private Double totalPrice;
    private List<Product> productList;

}
```



#### 消费服务



　　OrderService.java

```java
package com.xxxx.service;

import com.xxxx.pojo.Order;

public interface OrderService {

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    Order selectOrderById(Integer id);

}
```



　　对于服务的消费我们这里讲三种实现方式：

- DiscoveryClient：通过元数据获取服务信息
- LoadBalancerClient：Ribbon 的负载均衡器
- @LoadBalanced：通过注解开启 Ribbon 的负载均衡器



##### DiscoveryClient



　　Spring Boot 不提供任何自动配置的`RestTemplate` bean，所以需要在启动类中注入 `RestTemplate`。

```java
package com.xxxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// 开启 Eureka Client 注解，目前版本如果配置了 Eureka 注册中心，默认会开启该注解
//@EnableEurekaClient
public class ServiceConsumerApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceConsumerApplication.class, args);
    }

}
```



　　OrderServiceImpl.java

```java
package com.xxxx.service.impl;

import com.xxxx.pojo.Order;
import com.xxxx.pojo.Product;
import com.xxxx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id, "order-001", "中国", 31994D,
                selectProductListByDiscoveryClient());
    }

    private List<Product> selectProductListByDiscoveryClient() {
        StringBuffer sb = null;

        // 获取服务列表
        List<String> serviceIds = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(serviceIds)){
            return null;
        }

        // 根据服务名称获取服务
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("service-provider");
        if (CollectionUtils.isEmpty(serviceInstances)){
            return null;
        }

        ServiceInstance si = serviceInstances.get(0);
        sb = new StringBuffer();
        sb.append("http://" + si.getHost() + ":" + si.getPort() + "/product/list");

        // ResponseEntity: 封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                sb.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {});
        return response.getBody();
    }

}
```



##### LoadBalancerClient



　　OrderServiceImpl.java

```java
package com.xxxx.service.impl;

import com.xxxx.pojo.Order;
import com.xxxx.pojo.Product;
import com.xxxx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient; // Ribbon 负载均衡器

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id, "order-001", "中国", 31994D,
                selectProductListByLoadBalancerClient());
    }

    private List<Product> selectProductListByLoadBalancerClient() {
        StringBuffer sb = null;

        // 根据服务名称获取服务
        ServiceInstance si = loadBalancerClient.choose("service-provider");
        if (null == si){
            return null;
        }

        sb = new StringBuffer();
        sb.append("http://" + si.getHost() + ":" + si.getPort() + "/product/list");

        // ResponseEntity: 封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                sb.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {});
        return response.getBody();
    }

}
```



##### @LoadBalanced



　　启动类注入 `RestTemplate` 时添加 `@LoadBalanced` 负载均衡注解，表示这个 `RestTemplate` 在请求时拥有客户端负载均衡的能力。

```java
package com.xxxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// 开启 Eureka Client 注解，目前版本如果配置了 Eureka 注册中心，默认会开启该注解
//@EnableEurekaClient
public class ServiceConsumerApplication {

    @Bean
    @LoadBalanced // 负载均衡注解
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceConsumerApplication.class, args);
    }

}
```



　　OrderServiceImpl.java

```java
package com.xxxx.service.impl;

import com.xxxx.pojo.Order;
import com.xxxx.pojo.Product;
import com.xxxx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id, "order-001", "中国", 31994D,
                selectProductListByLoadBalancerAnnotation());
    }

    private List<Product> selectProductListByLoadBalancerAnnotation() {
        // ResponseEntity: 封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                "http://service-provider/product/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {});
        return response.getBody();
    }

}
```



#### 控制层



　　OrderController.java

```java
package com.xxxx.controller;

import com.xxxx.pojo.Order;
import com.xxxx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Order selectOrderById(@PathVariable("id") Integer id) {
        return orderService.selectOrderById(id);
    }

}
```



#### 访问



　　访问：http://localhost:9090/order/1

![](Eureka%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83.assets/image-20200213125511377.png)



## Eureka 架构原理



![](Eureka服务注册中心.assets/timg-1578298168361.jfif)



- Register(服务注册)：把自己的 IP 和端口注册给 Eureka。
- Renew(服务续约)：发送心跳包，每 30 秒发送一次，告诉 Eureka 自己还活着。如果 90 秒还未发送心跳，宕机。
- Cancel(服务下线)：当 Provider 关闭时会向 Eureka 发送消息，把自己从服务列表中删除。防止 Consumer 调用到不存在的服务。
- Get Registry(获取服务注册列表)：获取其他服务列表。
- Replicate(集群中数据同步)：Eureka 集群中的数据复制与同步。
- Make Remote Call(远程调用)：完成服务的远程调用。



![](Eureka服务注册中心.assets/u=516778926,3804820192&fm=26&gp=0.jpg)



## Eureka 自我保护



### 启动自我保护条件



　　一般情况下，服务在 Eureka 上注册后，会每 30 秒发送心跳包，Eureka 通过心跳来判断服务是否健康，同时会定期删除超过 90 秒没有发送心跳的服务。

![](Eureka服务注册中心.assets/1578312338213.png)



**有两种情况会导致 Eureka Server 收不到微服务的心跳**



- 微服务自身的原因
- 微服务与 Eureka 之间的网络故障



**自我保护模式**



　　Eureka Server 在运行期间会去统计心跳失败比例在 15 分钟之内是否低于 85%，如果低于 85%，Eureka Server 会将这些实例保护起来，让这些实例不会过期，同时提示一个警告。这种算法叫做 Eureka Server 的自我保护模式。



### 为什么要启动自我保护



- 因为同时保留"好数据"与"坏数据"总比丢掉任何数据要更好，当网络故障恢复后，这个 Eureka 节点会退出"自我保护模式"。
- Eureka 还有客户端缓存功能（也就是微服务的缓存功能）。即使 Eureka 集群中所有节点都宕机失效，微服务的 Provider 和 Consumer 都能正常通信。
- 微服务的负载均衡策略会自动剔除死亡的微服务节点。



### 如何关闭自我保护



　　注册中心配置自我保护

```yml
eureka:
  server:
    enable-self-preservation: false # true：开启自我保护模式，false：关闭自我保护模式
    eviction-interval-timer-in-ms: 60000 # 清理间隔（单位：毫秒，默认是 60*1000）
```



## Eureka 优雅停服



　　配置了优雅停服以后，将不需要 Eureka Server 中配置关闭自我保护。本文使用 actuator 实现。



### 添加依赖



　　服务提供者添加 actuator 依赖

```xml
<!-- spring boot actuator 依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```



### 配置文件



　　服务提供者配置度量指标监控与健康检查

```yml
# 度量指标监控与健康检查
management:
  endpoints:
    web:
      exposure:
        include: shutdown         # 开启 shutdown 端点访问
  endpoint:
    shutdown:
      enabled: true               # 开启 shutdown 实现优雅停服
```



### 优雅停服



　　使用 POST 请求访问：http://localhost:7070/actuator/shutdown 效果如下

![](Eureka服务注册中心.assets/1578311606022.png)



## Eureka 安全认证



### 添加依赖



　　注册中心添加 security 依赖

```xml
<!-- spring boot security 依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```



### 配置文件



　　注册中心配置安全认证

```yml
spring:
  # 安全认证
  security:
    user:
      name: root
      password: 123456
```



### 修改访问集群节点的 url



　　注册中心的配置文件

```yml
# 配置 Eureka Server 注册中心
eureka:
  instance:
    hostname: eureka01            # 主机名，不配置的时候将根据操作系统的主机名来获取
    prefer-ip-address: true       # 是否使用 ip 地址注册
    instance-id: ${spring.cloud.client.ip-address}:${server.port} # ip:port
  client:
    # 设置服务注册中心地址，指向另一个注册中心
    service-url:                  # 注册中心对外暴露的注册地址
      defaultZone: http://root:123456@localhost:8762/eureka/
```

　　服务提供者的配置文件

```yml
# 配置 Eureka Server 注册中心
eureka:
  instance:
    prefer-ip-address: true       # 是否使用 ip 地址注册
    instance-id: ${spring.cloud.client.ip-address}:${server.port} # ip:port
  client:
    service-url:                  # 设置服务注册中心地址
      defaultZone: http://root:123456@localhost:8761/eureka/,http://root:123456@localhost:8762/eureka/
```

　　服务消费者的配置文件

```yml
# 配置 Eureka Server 注册中心
eureka:
  client:
    register-with-eureka: false         # 是否将自己注册到注册中心，默认为 true
    registry-fetch-interval-seconds: 10 # 表示 Eureka Client 间隔多久去服务器拉取注册信息，默认为 30 秒
    service-url:                        # 设置服务注册中心地址
      defaultZone: http://root:123456@localhost:8761/eureka/,http://root:123456@localhost:8762/eureka/
```



### 过滤 CSRF



　　Eureka 会自动化配置 CSRF 防御机制，Spring Security 认为 POST, PUT, and DELETE http methods 都是有风险的，如果这些 method 发送过程中没有带上 CSRF token 的话，会被直接拦截并返回 403 forbidden。

　　官方给出了解决的方法，具体可以参考 [spring cloud issue 2754](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fspring-cloud%2Fspring-cloud-netflix%2Fissues%2F2754)，里面有大量的讨论，这里提供两种解决方案。

　　首先注册中心配置一个 `@EnableWebSecurity` 配置类，继承 `org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter`，然后重写 `configure` 方法。



#### 方案一



　　使 CSRF 忽略 `/eureka/**` 的所有请求

```java
package com.xxxx.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全认证配置类
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http); // 加这句是为了访问 eureka 控制台和 /actuator 时能做安全控制
        http.csrf().ignoringAntMatchers("/eureka/**"); // 忽略 /eureka/** 的所有请求
    }

}
```



#### 方案二



　　保持密码验证的同时禁用 CSRF 防御机制

```java
package com.xxxx.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全认证配置类
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 注意，如果直接 disable 的话会把安全验证也禁用掉
        http.csrf().disable().authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

}
```



### 访问



![](Eureka服务注册中心.assets/1578362263209.png)



　　使用配置好的用户名和密码登录以后可看到注册中心界面，启动服务提供者和服务消费者，功能正常使用，至此 Eureka 注册中心所有的知识点就讲解结束了。非常感谢你可以认真学习至此，加油少年~

