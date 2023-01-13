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
### 数据处理
#### id策略：雪花算法
1. SnowFlake：Twitter的分布式自增ID算法，按照时间有序生成，分布式系统不会产生ID碰撞并且效率较高
2. 该项目使用雪花算法作为ID，由于雪花算法长度为19位，而前端能处理的最大长度为16位，会产生精度丢失。使用消息转换器可解决这一问题。(jackson)
3. id自动填充
   * User id属性加上注解
   ``` 
    @Id
    @Column("id")
    @CreatedBy
    private Long id; 
   ```
   * SnowFlake类加上注解：@EnableR2dbcAuditing
#### 密码
1. Spring Security 中有一个加密的类BCryptPasswordEncoder，内部实现了随机加盐处理，每次加密的值都不同
2. PasswordEncoder接口中有三个方法
   * String encode(CharSequence rawPassword)：加密原始密码
   * boolean matches(CharSequence rawPassword, String encodedPassword)：对加密的密码和传入的原始密码进行验证，但存储的密码本身永远不会被解码
   * boolean upgradeEncoding(String encodedPassword)：如果加密后的密码需要重新加密以提高安全性，则返回true，否则返回false
   ``` 
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-crypto</artifactId>
    </dependency>  
   ```
#### 返回数据
使用jackson进行数据处理
1. 字段不返回：@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
2. 字段为空时不返回：@JsonInclude(JsonInclude.Include.NON_NULL)
#### 异常
1. 自定义异常类 
2. 全局异常处理：@ControllerAdvice + @ExceptionHandler
``` 
@RestControllerAdvice
public class GlobalExceptinHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResultVO> handleException(Exception e) {
        return Mono.just(ResultVO.error( e.getMessage()));
    }

    @ExceptionHandler(XException.class)
    public Mono<ResultVO> handleXException(XException e) {
        return Mono.just(ResultVO.error(e.getMessage()));
    }

} 
```
### 登录
写了两天，没想到在登陆这就卡了。花时间学了Spring Security，感觉配置很麻烦，试了很多种方法项目也没跑起来
1. 封装JwtUtil工具类：提供生成、解析JWT的方法
2. 创建LoginCheckFilter过滤器
3. 测接口时遇到了一个bug：【java.lang.NoClassDefFoundError: javax/xml/bind/DatatypeConverter】，降低jdk版本(11 -> 8)或者手动导入需要的依赖
