# 文档地址

[https://trans4j.gomyck.com](https://trans4j.gomyck.com)

# 序言

**Trans4J 的`使命`: 减少无用 SQL 编写, 减少无用数据结构设计, 减少循环调用, 新手`10分钟`快速入门**

**Trans4J 的`设计理念`: 实用, 简单, 只为解决指定场景问题, 拒绝过度设计, 拒绝设计模式**

# 解决的问题

返回给前端的`任意`码值(用户ID, 商品ID, 性别, 支付状态...), 自动翻译成中文。无需额外联查字典表, 或单独写翻译逻辑

```text
0  ->  男    
1  ->  女  

[0,1] -> 男,女
```

excel 导入, 把单元格的中文转成码值

```text
男  -> 0

女  -> 1
```

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

# 快速体验

Clone下面的仓库  本地运行即可快速体验 Trans4J:

https://gitee.com/mzxc_admin/trans4j-test
