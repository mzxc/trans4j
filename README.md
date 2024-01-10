# 文档地址

[https://trans4j.gomyck.com](https://trans4j.gomyck.com)

# 序言

**Trans4J 的`使命`: 减少无用 SQL 编写, 减少无用数据结构设计, 减少循环调用, 新手`10分钟`快速入门**

**Trans4J 的`设计理念`: 实用, 简单, 只为解决指定场景问题, 拒绝过度设计, 拒绝设计模式**


# 项目介绍

## 1. Why Trans4J?

在业务系统开发中, `系统字典` 与 `业务表` 的设计关系通常会使用下面的方式:

> 1. 业务系统的字典信息, 通常会存储在`一个`或`多个`字典表中
> 2. `业务表`为了减少`维护成本`, 通常只存储相关`字典表`的码值(code), 而不会存储字典值(value)
> 3. 字典表除了码值(code), 通常是非稳态的`(用户需求变更)`, 在`生产环境`中, 存在`新增修改删除`字典项的业务需求
> 4. `业务表`(如机构信息, 用户信息), 其实也是`非稳态`的字典表, 比如在查询订单时, 需要关联查询对应的用户名或机构名
> 5. 删除的字典项, 要在已经落地的业务数据中, 继续得到翻译展示, 仅对于后续的新增表示删除

如上所述, 业务表与字典表的关系导致: 在进行`列表页`或者`详情页`查询时, 会面临字典表`码值`的翻译问题`(前端需要展示人类可读的字典值, 而非码值)`

*EXP*: **biz_order 表中的下述字段, 在实际使用时, 需要翻译以下字段的 code 值:**

> pay_state = 1 : 待支付
>
> pay_state = 2 : 支付中
>
> pay_state = 3 : 已支付
>
> goods_type   =  E00F2FCC80C2D5C66B5786AF94718420 : 数码
>
> goods_type   =  E2BE242218769288229D3D554CD18D0E : 家纺
>
> user_id  =  53378A85-0BF5-42F6-9A83-CFD91D614252 : 张三
>
> user_id  =  BF9F3D35-53F2-44C9-B67D-9E50E0F1F05D : 李四


**大多数项目会使用以下的处理方式:**

- 1. 业务表中增加冗余字段, 在新增时`插入`, 在字典有变动时, `修改`

```sql
ALTER TABLE biz_order ADD COLUMN user_name VARCHAR(50);
ALTER TABLE biz_order ADD COLUMN pay_state_value VARCHAR(50);
ALTER TABLE biz_order ADD COLUMN goods_type_value VARCHAR(50);
```

- 2. 使用 sql 关联查询

```sql
SELECT
    bOrder.*,
    dic1.dic_value AS payStateValue,
    dic2.user_name AS userName,
    dic3.dic_value AS goodsTypeValue 
FROM
    biz_order bOrder
    LEFT JOIN sys_dic dic1 ON bOrder.pay_state = dic1.dic_code 
                           AND dic1.dic_type = 'payState'
    LEFT JOIN biz_user_info dic2 ON bOrder.buyer = dic2.user_id
    LEFT JOIN sys_dic dic3 ON bOrder.goods_type = dic3.dic_code 
                           AND dic3.dic_type = 'goodsType' 
WHERE
    1 = 1
```

- 3. 使用 java 代码, 在循环中单独处理

```java
public List<BizOrder> findOrderList(BizOrder order) {
  List<BizOrder> list = orderMapper.findOrderList(order);
  for (BizOrder bizOrder : list) {

    // 获取字典值可能是基于枚举, 也可能是基于远程调用字典服务

    bizOrder.setUserName(dicService.getDicValue("userName", bizOrder.getBuyer()));
    bizOrder.setPayStateValue(dicService.getDicValue("payState", bizOrder.getPayState()));
    bizOrder.setGoodsTypeValue(dicService.getDicValue("goodsType", bizOrder.getGoodsType()));
  }
  return list;
}
```

- 4. 前端自行处理 (自行脑补, 不敢看, 不敢写)


**上述三种方式, 会导致以下问题:**

- 1. 实体类会被迫改变, 或新增子类(`额外的属性`) 来承载字典值

```java
public class BizOrder {
  // 额外新增的属性, 破坏了实体结构
  private String payStateValue;   
  private String userName;
  private String goodsTypeValue;
}
```

- 2. 额外的数据库性能开销或系统调用开销
- 3. 在字典信息被逻辑删除后, 原有的业务数据查询时, 可能导致一些副作用
- 4. 工程内`可能`要维护与字典表实时一致的状态枚举
- 5. 日以继赴的重复性翻译工作, 会给 coder 带来额外的精神与心智负担, 导致脱发

**如果你正面临上述问题, 那么, 请尽快`食用` Trans4J, 消除无效的代码, 反人类的数据库设计, 拯救你乌黑亮丽的秀发!**

## 2. Trans4J 是什么?

> Trans4J 是一款开源的, 用于翻译 `任意JAVA对象` 字典表码值的工具`(地中海拯救者)`, 以插件形式进行扩展, 最小化依赖为: spring-framework, 仅使用一行代码即可完成翻译工作, 对项目无任何额外的代码入侵, 无任何额外的副作用, 请放心 `食用`

## 3. 谁应该使用 Trans4J?

如果你符合下述条件, 那么请尽快添加 Trans4J 到你的工程中

- 1. JAVA 服务端开发人员
- 2. 查询业务数据时, 为了把 `码值` 翻译成 `业务值` 而绞尽脑汁
- 3. 做数据导入时, 为了把 `业务值` 转换成 `码值` 而费尽心思
- 4. 由于上述原因而严重脱发, 并且拖慢开发进度, 经常性 996

## 4. 何时使用 Trans4J?

> 无论你的项目开发进度如何, 现在开始使用 Trans4J 都不晚, 它不会对你现有的项目造成任何副作用

## 5. 何处使用 Trans4J?

> 对于服务端接口, 在响应客户端的查询请求时, 如果返回的报文中, 存在字典表码值, 那么就需要使用 Trans4J, 仅需要一行注解即可

```java
@TransEnhance
public BizOrder findOrderById(String id) {
  BizOrder order = orderMapper.findOrderById(id);
  return order;
}
```

使用之前的数据报文:

```json
{
  ...
  "payState": "1",
  "goodsType": "E00F2FCC80C2D5C66B5786AF94718420",
  "userId": "53378A85-0BF5-42F6-9A83-CFD91D614252"
  ...
}
```

使用 Trans4J 后的数据报文:
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

# 快速开始

## 1. 添加依赖

如果你正在使用 spring boot 技术栈, 那么添加下面的依赖到你的工程中即可

```xml
<dependency>
    <groupId>com.gomyck</groupId>
    <artifactId>trans4j-spring-boot-starter</artifactId>
    <version>2.0.3-RELEASE</version>
</dependency>
```

## 2. 字典结构

**无论数据库里的字典表结构如何, 请保证投喂给 Trans4J 的字典信息是下面的格式**

Trans4J 默认接受的字典信息, 格式如下:

```text
[{
    "key": "100",
    "value": "教练车牌",
    "columnName": "plateType"
}, {
    "key": "101",
    "value": "临时牌照",
    "columnName": "plateType"
}, {
    "key": "2",
    "value": "货车",
    "columnName": "carType"
}, {
    "key": "3",
    "value": "轿车",
    "columnName": "carType"
}]

```

> 说明: key 是字典的码值, value 是字典值, columnName 对应的是 JAVA Entity 属性名称, `同一类字典`可以对应多个属性名称, 只需要用逗号隔开即可

*EXP: 如果你的实体类, 由于各种原因, 导致字典的属性名不一致, 那么请使用下面的方式初始化字典信息*

```json
[{
    "key": "2",
    "value": "货车",
    "columnName": "carType,biz_car_type,cType"
}, {
    "key": "3",
    "value": "轿车",
    "columnName": "carType,biz_car_type,cType"
}, {
    "key": "4",
    "value": "面包车",
    "columnName": "carType,biz_car_type,cType"
}]
```  

> columnName 的值支持驼峰, 也支持下划线, `大小写不敏感`, 所以无需担心 JAVA 实体类里的属性命名问题, 只要按照`命名风格`互转后一致即可, 比如 JAVA 属性名是 carType, 那么 columnName 可以为: `car_type` `CAR_TYPE` `carType`, 其余名称为`非法命名`


<div style="color: yellow;">columnName 实际是 [同一类字典数据] 的类型名称, 比如 性别字典, 国籍字典等等, 所以不同字典类型的 columnName 必须不一致, 这是规范问题, 请遵守规范</div>

```java
public class Xxxx {
  private Integer sex;    //性别字典  columnName 为 sex
  private String country; //国家字典  columnName 为 country
  private String carType; //车辆类型  columnName 为 carType
}

```

## 3. 配置文件

spring-boot 的配置文件如下:

```yaml
trans4j:
  dic:
    adaptor:
      # 初始化字典信息 URI (可选)
      init-dic-url:
        - http://oss.gomyck.com/trans4j/dic.json
      # 初始化字典信息 SQL (可选)
      init-dic-sql:
        - "select  paramValue as `key`, `name` as value, 'tempType' as columnName  from sys_setting_config where type = 5"
        - "select  paramValue as `key`, `name` as value, 'category' as columnName  from sys_setting_config where type = 6"
        - "select orgId as `key`, `name` as value, 'serviceOrgId' as columnName from biz_org_info
           union all
           select id as `key`, orgName as `value`, 'serviceOrgId' as columnName from service_org"
      # 初始化字典信息文件, 相对路径则从当前 classpath 查找, 外部文件请使用绝对路径 (可选)
      init-dic-file: dic.json
    enabled: true # 是否启用 trans4j
    job:
      refresh-interval: 1 # 字典信息刷新间隔 (单位: 分)
```

## 4. 实际使用

上述步骤配置完毕后, 即可在项目中使用 Trans4J, 在需要翻译字典值的接口方法上添加 `@TransEnhance` 注解即可

<h2>返回值类型可以是任意类型, 并且支持嵌套</h2>

```java
@TransEnhance
public BizOrder func1(String id) {
  BizOrder order = orderMapper.findOrderById(id);
  return order;
}

@TransEnhance
public List<BizOrder> func2() {
  List<BizOrder> orderList = orderMapper.queryForList();
  return orderList;
}

@TransEnhance
public List<Map<Object, Object> func3() {
  List<Map<Object, Object> orderListMap = orderMapper.queryForListMap();
  return orderListMap;
}

@TransEnhance
public R func4() {
  List<BizOrder> orderList = orderMapper.queryForList();
  return R.ok(orderList);
}
```

# 加入讨论

## 讨论QQ群

59116211 `加群请注明来意`

# 更多用法

此部分内容为付费内容, 请访问以下链接购买查看:

<a href="https://mbd.pub/o/bread/ZZmYl5Zw" id="targetLink">https://mbd.pub/o/bread/ZZmYl5Zw</a>

**购买后您将获得以下内容:**

1. 使用 JAVA 代码初始化 Trans4J
2. 完美适配 Trans4J 的字典表数据结构SQL
3. Trans4J 插件说明以及使用方式
4. trans4j-ext-spring-mvc 的额外特性
5. 运行时动态获取翻译后的字典值
6. 文件导入时, 把业务值翻译成码值
7. 拦截器的使用
8. 自己定义拦截器
9. 使用配置适配器
10. 国际化







