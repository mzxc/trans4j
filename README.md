## gomyck-result-converter-spring-boot-starter

gomyck-result-converter-spring-boot-starter 是一款开源的结果集转换插件, 功能主要是对结构化
数据进行自定义的转换, 现在主要的核心功能为字典表信息转换, 完美支持 mybatis, 使开发人员在对结果集进行查询时, 无需关注字典表

## 开始使用(字典翻译)

```xml
<dependency>
    <groupId>com.gomyck</groupId>
    <artifactId>gomyck-result-converter-spring-boot-starter</artifactId>
    <version>2.0.2-RELEASE</version>
</dependency>
```

使用该组件在进行 **单表查询或多表联查** 时, 如果结果集中存在字典数据列(exp: userId), 会自动翻译字典的 code 为 value (123
翻译为 张三), 而不需要联查字典表

声明一个类 implements InitializingBean, 并注册成 Bean

```java
@Component
public class CkResultConverterConfig implements InitializingBean {

    @Autowired
    DicColHandler dicColConvert;
    
    @Autowired
    DicService    service; //字典表的 service

    @Autowired
    OtherService  otherService; //其他表的 service


    @Override
    public void afterPropertiesSet() {
       dicColConvert.init(() -> {
           List<Map<String, Object>> li = service.selectMaps(new EntityWrapper<>());
           li.addAll(otherService.queryOtherList());
           return li;
       });
    }
}
```

```yaml

# 具体使用, 按照提示进行配置即可
gomyck:
  converter:
    default-skip-flag: true #是否默认跳过翻译, true 为不翻译, false 为翻译
    ignore-suffix: _DC #忽略翻译的方法名后缀
    dic:
      refresh-interval: 60 #字典表刷新时间
      adaptor:
        code: key #字典表 [键] 在数据库中的字段(查询字典表结果集中, 对应的键值)
        value: value #字典表 [值] 在数据库中的字段(查询字典表结果集中, 对应的键值)

```

### 需要看的类

ConverterBus, 这个类封装了常用的翻译方法