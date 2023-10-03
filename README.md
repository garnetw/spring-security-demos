# Spring Security 框架实战练习

### 01. 默认配置

Spring Security 框架有默认的登录页面，只需在项目中引入`spring-boot-starter-security` 包即可。  
登录用户名默认为 user， 密码会在项目启动时打印在控制台里。

### 02. 内存用户登录验证

通过`auth.inMemoryAuthentication()`或`InMemoryUserDetailsManager()` 创建基于内存的用户账号，这个账号将替代spring security
自带的账号密码，用于访问网络资源。  

进一步，我们可以通过`HttpSecurity`对象设置哪些 url 可以访问，哪些 url 必须登录后才可访问。

### 03. 内存用户角色访问控制
