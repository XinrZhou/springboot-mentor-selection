## SpringBoot + Webflux
### 项目介绍
2022年暑假学习了 Vue 和 docker，九月份在老师的指导下完成了[“毕设选导师”项目](https://github.com/XinrZhou/vue3-vite-project)的前端，前后端分离，后端是老师写的。  
十月份开始学习 SpringBoot，学了一段时间，发现自己有很多盲区，又去学了 Spring。放寒假前和老师聊了寒假的安排，把“毕设选导师”的后端用 Reactive 做了。最近花时间学了 Webflux，并对之前 Spring 的[笔记]((https://github.com/XinrZhou/spring-learning))做了补充。
### 准备工作
1. 创建工程，导入项目相关的依赖
2. 配置数据库连接信息
``` 
spring:
  r2dbc:
    url: r2dbcs:mysql://122.9.33.79:3308/mentorselection?serverTimezone=Asia/Shanghai
    username: root
    password: 123456
server:
  port: 8855
```