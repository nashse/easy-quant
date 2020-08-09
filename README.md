# easy-quant

easy-quant是一个轻量级数字货币量化框架，目的是简化开发人员开发策略的成本，使开发人员专注于策略逻辑开发。
为什么不引用Spring框架？因为Spring引入了很多不需要的功能和代码，会导致jar包变大；在开发调试阶段，需要经常在ide上重启服务，Spring的启动流程会比较慢，导致浪费调试时间。

<!-- PROJECT LOGO -->
<br />

<p align="center">
  <a href="https://github.com/nashse/easy-quant/">
    <img src="logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">easy-quant</h3>
  <p align="center">
   轻量级数字货币量化框架
</p>

 
## 目录

- [上手指南](#上手指南)
  - [开发前的配置要求](#开发前的配置要求)
- [文件目录说明](#文件目录说明)
- [模块说明](#模块说明)
- [demo使用](#demo使用)
- [框架原理](#框架原理)
- [配置文件说明](#配置文件说明)
- [贡献者](#贡献者)
  - [如何参与开源项目](#如何参与开源项目)
- [版本控制](#版本控制)
- [作者](#作者)
- [鸣谢](#鸣谢)

### 上手指南

###### 开发前的配置要求

1. 开发工具：IntelliJ IDEA
2. JDK1.8

### 框架原理
- 框架入口：com.zyf.strategy.Main，通过给String[] args赋值，分别为策略包名和配置名，用于指定策略和策略使用的配置文件。
```
public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        EasyQuantyApplication.run(Class.forName(args[0]), args[1]);
    }
}
```
- 框架启动原理借鉴Spring的启动流程，包含功能：
读取配置文件
```
// 预处理
this.prepareRefresh();
```
通过反射实例化策略，并将配置信息加载到策略实例中
```
// 将配置信息加载到策略实例
this.invokeProcessors();
```
通过反射实例化策略，并将配置信息加载到策略实例中
```
// 将配置信息加载到策略实例
this.invokeProcessors();
```
处理自定义注解
```
// 处理注解
this.handleAnnotation();
```
通过IOC容器调用策略函数
```
// 初始化策略
this.prepareStrategy();
```
启动策略
```
// 启动定时任务
this.startUpScheduler();
```

### 配置文件说明
```
{
  "configs" : [
    {
	  // 策略实例id
      "id" : "1",
	  // 目标交易所名
      "exchangeName" : "deribit",
	  // 目标币对（合约）
      "symbol" : "BTC-PERPETUAL",
	  // 行情接口实例
      "mdE" : { "exchangeName" : "deribit", "securitiesType" : "future" },
	  // 私有接口实例
      "tradeE" : { "exchangeName" : "deribit","securitiesType" : "future", "accessKey" : "accessKey/12312", "secretKey" : "secretKey" },
      // 自定义行情接口实例
	  "huobiproE" : { "exchangeName" : "huobipro" }
    }
  ]
}
```

### 模块说明 
- baseservice：业务公共模块
- common：通用公共模块
- framework：量化框架模块
- marketdata：交易所公共接口模块（盘口等）
- strategy：策略模块
- trade：交易所私有接口模块（下单等）

### demo使用
- 启动demo程序命令
java -jar 包名.jar 策略包名 --config-name:配置名
- 例子
java -jar OkexGridStrategy-1.0-jar-with-dependencies.jar com.zyf.strategy.Strategy --config-name:grid.json
- 程序会输出deribit交易所的BTC-PERPETUAL合约的Detph数据（为什么demo用deribit交易所，因为不需要翻墙）

### 文件目录说明
eg:

```
filetree 
├─.idea
│  └─inspectionProfiles
├─baseservice
│  └─src
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─zyf
│      │  │          └─baseservice
│      │  │              ├─baseexchange
│      │  │              │  ├─future
│      │  │              │  ├─option
│      │  │              │  └─stock
│      │  │              ├─model
│      │  │              │  └─enums
│      │  │              └─util
│      │  │                  ├─deribit
│      │  │                  ├─huobipro
│      │  │                  └─okex
│      │  └─resources
│      └─test
│          └─java
├─common
│  └─src
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─zyf
│      │  │          └─common
│      │  │              ├─crypto
│      │  │              ├─exceptions
│      │  │              ├─http
│      │  │              ├─model
│      │  │              │  ├─enums
│      │  │              │  ├─future
│      │  │              │  └─http
│      │  │              ├─okhttp
│      │  │              └─util
│      │  └─resources
│      └─test
│          └─java
├─framework
│  └─src
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─zyf
│      │  │          └─framework
│      │  │              ├─boot
│      │  │              ├─scheduling
│      │  │              │  └─interfaces
│      │  │              ├─service
│      │  │              │  └─impl
│      │  │              └─util
│      │  └─resources
│      └─test
│          └─java
│              └─com
│                  └─zyf
│                      └─framework
├─logs
├─marketdata
│  └─src
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─zyf
│      │  │          └─marketdata
│      │  │              ├─factory
│      │  │              ├─http
│      │  │              │  ├─future
│      │  │              │  ├─option
│      │  │              │  │  └─deribit
│      │  │              │  └─stock
│      │  │              └─proxy
│      │  └─resources
│      └─test
│          └─java
├─strategy
│  └─src
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─zyf
│      │  │          └─strategy
│      │  └─resources
│      └─test
│          └─java
└─trade
    └─src
        ├─main
        │  ├─java
        │  │  └─com
        │  │      └─zyf
        │  │          └─trade
        │  │              ├─factory
        │  │              ├─http
        │  │              │  ├─future
        │  │              │  └─stock
        │  │              └─proxy
        │  └─resources
        └─test
            └─java
```

#### 如何参与开源项目

贡献使开源社区成为一个学习、激励和创造的绝佳场所。你所作的任何贡献都是**非常感谢**的。


1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



### 版本控制

该项目使用Git进行版本管理。您可以在repository参看当前可用版本。

### 作者

yuanfeng.z

邮箱:sourcez@foxmail.com &ensp; qq:1004283115    

 *您也可以在贡献者名单中参看所有参与该项目的开发者。*

### 版权说明

该项目签署了MIT 授权许可，详情请参阅 [LICENSE.txt](https://github.com/nashse/easyquant/blob/master/LICENSE.txt)

### 鸣谢


- [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
- [Img Shields](https://shields.io)
- [Choose an Open Source License](https://choosealicense.com)
- [GitHub Pages](https://pages.github.com)
- [Animate.css](https://daneden.github.io/animate.css)
- [xxxxxxxxxxxxxx](https://connoratherton.com/loaders)






