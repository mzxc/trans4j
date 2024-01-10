# 快速体验

Clone下面的仓库  本地运行即可快速体验 Trans4J:

https://gitee.com/mzxc_admin/trans4j-test

# Maven 依赖不能下载说明

Trans4J 2.0.3-RELEASE 版本于 2024/01/10 上传至 maven 公服, 小伙伴配置国内阿里云镜像加速的, 可能需要等 24 小时之后, 阿里云同步 index 即可下载

或配置镜像地址(setting.xml):

```xml
<mirror>
  <id>sonatype-Release</id>
  <url>https://oss.sonatype.org/content/repositories/releases/</url>
  <mirrorOf>central</mirrorOf>
</mirror>
```

# 文档地址

[https://trans4j.gomyck.com](https://trans4j.gomyck.com)

# 序言

**Trans4J 的`使命`: 减少无用 SQL 编写, 减少无用数据结构设计, 减少循环调用, 新手`10分钟`快速入门**

**Trans4J 的`设计理念`: 实用, 简单, 只为解决指定场景问题, 拒绝过度设计, 拒绝设计模式**

# 使用前:

```java
@GetMapping("/findOrderById")
public BizOrder findOrderById(String id) {
  BizOrder order = orderMapper.findOrderById(id);
  return order;
}
```
**返回报文:**
```json
{
  ...
  "payState": "1",
  "goodsType": "E00F2FCC80C2D5C66B5786AF94718420",
  "userId": "53378A85-0BF5-42F6-9A83-CFD91D614252",
  ...
}
```

# 使用后:

```java
@TransEnhance
@GetMapping("/findOrderById")
public BizOrder findOrderById(String id) {
  BizOrder order = orderMapper.findOrderById(id);
  return order;
}
```
**返回报文:**
```json
{
  ...
  "payState": "1",
  "payState$V": "待支付",
  "goodsType": "E00F2FCC80C2D5C66B5786AF94718420",
  "goodsType$V": "数码",
  "userId": "53378A85-0BF5-42F6-9A83-CFD91D614252",
  "userId$V": "张三",
  ...
}
```