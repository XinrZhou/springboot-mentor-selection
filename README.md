# SpringBoot + Webflux
### 项目介绍

2022年暑假学习了 Vue 和 docker，九月份在老师的指导下完成了[“毕设选导师”项目](https://github.com/XinrZhou/vue3-vite-project)的前端，前后端分离，后端是老师写的。  
十月份开始学习 SpringBoot，学了一段时间，发现自己有很多盲区，又去学了 Spring。放寒假前和老师聊了寒假的安排，把“毕设选导师”的后端用 Reactive 做了。最近花时间学了 Webflux，并对之前 Spring 的[笔记]((https://github.com/XinrZhou/spring-learning))做了补充。

### 初始化
不太会写，参考了老师的代码。使用@EventListener注解，查询数据库中记录数，若为0，初始化数据库
``` 
@Transactional
    @EventListener(classes = ApplicationReadyEvent.class)
    public Mono<Void> onApplicationEvent() {
        String number = "2046";
        var pwd = "2046";
        return userRepository.count().flatMap(r -> {
            if (r == 0) {
                User admin = User.builder()
                        .name(number)
                        .number(number)
                        .password(encoder.encode(pwd))
                        .role(User.ADMIN)
                        .insertTime(LocalDateTime.now())
                        .selectTime(LocalDateTime.now().plusMonths(5))
                        .build();
                return userRepository.save(admin).then();
            }
            return userRepository.findByNumber(number).doOnSuccess(user -> {
                startTime.setStartTime(user.getSelectTime());
            }).then();
        });
    }
```

## 数据处理
### id策略：雪花算法
SnowFlake：Twitter的分布式自增ID算法，按照时间有序生成，分布式系统不会产生ID碰撞并且效率较高 
该项目使用雪花算法作为ID，由于雪花算法长度为19位，而前端能处理的最大长度为16位，会产生精度丢失。使用消息转换器可解决这一问题。(jackson)  
id自动填充
   * User id属性加上注解
   ``` 
    @Id
    @Column("id")
    @CreatedBy
    private Long id; 
   ```
   * SnowFlake类加上注解：@EnableR2dbcAuditing
### 密码
Spring Security 中有一个加密的类BCryptPasswordEncoder，内部实现了随机加盐处理，每次加密的值都不同  
PasswordEncoder接口中有三个方法
   * String encode(CharSequence rawPassword)：加密原始密码
   * boolean matches(CharSequence rawPassword, String encodedPassword)：对加密的密码和传入的原始密码进行验证，但存储的密码本身永远不会被解码
   * boolean upgradeEncoding(String encodedPassword)：如果加密后的密码需要重新加密以提高安全性，则返回true，否则返回false
   ``` 
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-crypto</artifactId>
    </dependency>  
   ```
### 返回数据
使用jackson进行数据处理  
字段不返回：@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  
字段为空时不返回：@JsonInclude(JsonInclude.Include.NON_NULL)  
封装ResultVO类，定义数据的返回格式
### 异常
自定义异常类 
全局异常处理：@ControllerAdvice + @ExceptionHandler
``` 
@RestControllerAdvice
public class GlobalExceptinHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResultVO> handleException(Exception e) {
        return Mono.just(ResultVO.error(400, e.getMessage()));
    }
    
    @ExceptionHandler(XException.class)
    public Mono<ResultVO> handleXException(XException e) {
        return Mono.just(ResultVO.error(e.getCode(), e.getMessage()));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResultVO> handelDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return Mono.just(ResultVO.error(409, "唯一约束冲突" + exception.getMessage()));
    }
}
```

## 所有角色
### 登录
没想到在登陆这就卡了，写了两天。花时间学了Spring Security，感觉Spring Security的配置比较麻烦，后来换了其他思路
封装JwtUtil工具类：提供生成、解析JWT的方法  
创建LoginCheckFilter过滤器  
测接口时遇到了一个bug：【java.lang.NoClassDefFoundError: javax/xml/bind/DatatypeConverter】，降低jdk版本(11 -> 8)或者手动导入需要的依赖
### 获取用户信息
看了老师的代码，基于用户身份返回不同的数据，我在这块没有做处理，直接在实体类上加了注解
``` 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer total;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
```
### 修改密码

## 管理员
### 重置开始时间、密码
初始化时设置了一个开始时间  
重置之后，修改管理员的select_time字段，并将其作为开始时间  
根据学工号重置密码，默认密码为学工号  
### 添加用户
添加的用户角色包括老师和学生
数据层用了同一个方法，业务层基于不同身份写了不同的方法
### 重置数据
九月份写前端的时候没有这个接口，后来加的  
老师角色，将count字段设为0  
学生角色，若已选择导师，将teacher_name、teacher_id、select_time置空

## 老师
### 该老师的学生信息、未选学生信息、所有学生选导师信息
这两天学了点Spring Data相关的知识，这三个接口都比较简单，在数据层按照Spring Data命名规则写对应的方法，业务层调用即可   
命名规则：以findBy开头，涉及条件查询时，将对应的属性用关键字连接，属性首字母大写
``` 
List<Article> findByTitle(String title);
List<Article> findByTitleContains(String title);
List<Article> findByTitleAndContentContains(String title, String content);
List<Article> findByIdBetween(Integer startId, Integer endId);
Page<Article> findByIdBetween(Integer startId, Integer endId, Pageable pageable);
```

## 学生
### 获取导师列表
若当前时间早于开始时间，将开始时间返给前端，并对时间数据进行格式处理。否则，直接返回导师列表
``` 
DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startTime.getStartTime()))
```
### 选导师
这块业务逻辑比较复杂
  * 基于id查询学生信息，若查询结果为空，抛异常，学生不存在；若该学生已选择导师，抛异常，导师不可重复选
  * 基于id查询老师信息，若查询结果为空，抛异常，老师不存在，若该老师名额已满，抛异常，导师名额已满，选择失败
开始时间返回给前端时做了格式处理

### 总结
一直接触的是SSM，这次用的是Spring WebFlux，和传统的SpringMvc方式有很大的差别。  
刚开始写项目的时候比较困难，没什么思路，遇到了很多坑。理论和实践还是有区别，概念学会了也不一定会用。  
这个项目做完，最大的感受就是会的东西太少了，导致遇到问题时不知道从哪块分析。之前没学过Reactive相关的知识，写这个项目之前也只学了SpringBoot、SSM和MP。   
通过这次项目，逐渐了解了响应式编程相关的概念，中途还学到了Spring Security和Spring Data的一些知识。
这两次的前后端项目开发经历，让我找到了更适合自己的学习方式。从2022年暑假到现在，在老师的指导下成长了很多，感谢我的老师，在我最迷茫的时候给了我支持和鼓励。


