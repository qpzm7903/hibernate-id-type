# 用户故事：Hibernate乐观锁版本跟踪

## 背景
作为一个系统维护者，我需要跟踪所有实体的版本变化，以便于排查并发问题和审计数据变更。

## 描述
系统需要记录每个实体的版本变化，包括：
- 哪个实体发生了变化
- 版本号从多少变成多少
- 触发变更的业务堆栈信息（过滤掉框架内部调用）

## 验收标准

### 功能性需求
1. 实现一个版本跟踪切面，拦截所有EntityManager的操作
2. 记录实体变更前后的版本号
3. 记录触发变更的业务方法调用堆栈
4. 过滤掉非业务相关的堆栈信息
5. 支持配置要跟踪的包名前缀

### 技术要求
1. 使用Spring AOP实现拦截
2. 使用SLF4J进行日志记录
3. 支持通过配置文件开启/关闭跟踪
4. 支持配置堆栈过滤规则

### 日志格式
```
Entity: {entityName} 
ID: {entityId}
Version changed: {oldVersion} -> {newVersion}
Business stack:
  at com.example.service.UserService.updateUser(UserService.java:25)
  at com.example.controller.UserController.update(UserController.java:31)
```

## 实现建议
1. 创建`@VersionTracking`注解标记需要跟踪的实体
2. 实现`VersionTrackingAspect`处理版本跟踪逻辑
3. 提供`VersionTrackingProperties`配置类
4. 添加示例和测试用例

## 优先级
P1 - 高优先级，影响数据一致性和问题排查

## 工作量估算
- 后端开发: 2人天
- 测试: 1人天
- 文档: 0.5人天

## 相关文档
- [Hibernate乐观锁文档](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#locking)
- [Spring AOP文档](https://docs.spring.io/spring-framework/reference/core/aop.html) 