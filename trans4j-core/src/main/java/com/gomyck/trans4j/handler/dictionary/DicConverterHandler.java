/*
 * Copyright [2023] [trans4j@gomyck.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gomyck.trans4j.handler.dictionary;

import com.gomyck.trans4j.filter.ConverterFilter;
import com.gomyck.trans4j.filter.InnerConverterFilter;
import com.gomyck.trans4j.handler.AbstractConverterHandler;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.handler.dictionary.serialize.AutoEncoder;
import com.gomyck.trans4j.support.ConverterUtil;
import com.gomyck.trans4j.support.TransBus;
import com.gomyck.util.DataFilter;
import com.gomyck.util.FieldUtil;
import com.gomyck.util.ObjectJudge;
import com.gomyck.util.PropertyAppender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 字典转换处理器
 * 提供: 简单字典翻译
 * 正向 key - value 的翻译
 * 反向 value - key 的转换
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2019-07-17
 */
@Slf4j
public class DicConverterHandler extends AbstractConverterHandler {

  public static final String V = "$V";
  /**
   * 数据字典表结构适配器
   */
  private final DicDescribeAdaptor adaptor;

  /**
   * 默认 columnName 分隔符
   */
  private static final String SPLIT_CHARACTER = ",";

  /**
   * 字典表信息
   */
  private final Map<String, Map<String, Object>> DIC_INFO = new ConcurrentHashMap<>();

  /**
   * 字典表信息(反转)
   */
  private final Map<String, Map<String, Object>> DIC_INFO_OVERTURN = new ConcurrentHashMap<>();

  /**
   * 内部过滤器
   */
  private final List<ConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo>> filters = new ArrayList<>();

  private final AutoEncoder autoEncoder;

  /**
   * 初始化方法, reload 字典时使用
   */
  @Getter
  private final List<Function<Object, List<Map<String, Object>>>> initDicInfoFunc = new ArrayList<>();

  public DicConverterHandler(DicDescribeAdaptor adaptor, AutoEncoder autoEncoder, ConverterHandlerComposite converterHandlerComposite) {
    super(converterHandlerComposite);
    this.adaptor = adaptor;
    this.autoEncoder = autoEncoder;
  }

  /**
   * 简单的 key value 翻译
   *
   * @param ifOverturn  是否反转
   * @param typeCode    类型
   * @param originValue 原值
   * @return 翻译后的值
   */
  public String simpleConvert(boolean ifOverturn, String typeCode, String originValue) {
    final Map<String, Map<String, Object>> _finalDicInfo = !ifOverturn ? DIC_INFO : DIC_INFO_OVERTURN;
    String colName = ConverterUtil.getCommonColName(typeCode);
    Map<String, Object> usedDicInfo = _finalDicInfo.get(colName);
    return DataFilter.toString(usedDicInfo.get(originValue));
  }

  /**
   * 初始化字典表信息
   *
   * @param initDicInfoFunc 初始化方法
   */
  public void init(Function<Object, List<Map<String, Object>>> initDicInfoFunc) {
    log.info("Initializing gomyck result converter dic info ...");
    if (!this.initDicInfoFunc.contains(initDicInfoFunc)) this.initDicInfoFunc.add(initDicInfoFunc);
    if (initDicInfoFunc == null) return;
    List<Map<String, Object>> _dicInfo = initDicInfoFunc.apply(this);
    if (_dicInfo == null) return;
    initDicInfo(_dicInfo);
    initDicInfoOverturn(_dicInfo);
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }

  @Override
  public void addInnerFilter(InnerConverterFilter filter) {
    if (!filters.contains(filter)) filters.add(filter);
  }

  /**
   * 初始化方法
   *
   * @param _dicInfo 字典表信息
   */
  private void initDicInfo(List<Map<String, Object>> _dicInfo) {
    for (Map<String, Object> _dic : _dicInfo) {
      final String colNames = DataFilter.toString(_dic.get(adaptor.COLUMN_NAME)); //获取字段名> 转驼峰大写
      Arrays.stream(colNames.split(SPLIT_CHARACTER)).forEach(colName -> {
        // 下划线转驼峰并大写, 统一格式
        colName = ConverterUtil.getCommonColName(colName);
        String code = DataFilter.toString(_dic.get(adaptor.CODE));
        if (ObjectJudge.isNull(code)) return;
        // 加入国际化
        if (ObjectJudge.notNull(adaptor.I18N)) {
          code = code + DataFilter.toString(_dic.get(adaptor.I18N));
        }
        Map<String, Object> maps = DIC_INFO.get(colName); // 获取以当前分类为键的map
        if (maps != null) {
          maps.put(code, _dic.get(adaptor.VALUE)); // 存储code, value
        } else {
          maps = new ConcurrentHashMap<>();
          maps.put(code, _dic.get(adaptor.VALUE));
          DIC_INFO.put(colName, maps); // 把字典信息存到大类下
        }
      });
    }
    DicInfoHolder.DIC_INFO = DIC_INFO;
  }

  /**
   * 初始化方法(反转)
   *
   * @param _dicInfo 字典表信息
   */
  private void initDicInfoOverturn(List<Map<String, Object>> _dicInfo) {
    for (Map<String, Object> dic : _dicInfo) {
      final String colNames = DataFilter.toString(dic.get(adaptor.COLUMN_NAME));
      Arrays.stream(colNames.split(SPLIT_CHARACTER)).forEach(colName -> {
        // 下划线转驼峰并大写, 统一格式
        colName = ConverterUtil.getCommonColName(colName);
        final String value = DataFilter.toString(dic.get(adaptor.VALUE));
        Map<String, Object> maps = DIC_INFO_OVERTURN.get(colName);
        if (ObjectJudge.isNull(value)) return;
        if (maps != null) {
          maps.put(value, dic.get(adaptor.CODE));
        } else {
          maps = new ConcurrentHashMap<>();
          maps.put(value, dic.get(adaptor.CODE));
          DIC_INFO_OVERTURN.put(colName, maps);
        }
      });
    }
    DicInfoHolder.DIC_INFO_OVERTURN = DIC_INFO_OVERTURN;
  }

  @Override
  public boolean support(final Object obj) {
    if (obj == null) return false;
    if (obj instanceof Collection) return true;
    if (obj instanceof Map) return true;
    return !FieldUtil.isBaseType(obj.getClass().getTypeName());
  }

  @Override
  public Object handle(Object object) {
    if (Objects.isNull(object)) return null;
    Object result = super.handle(object);
    if(Objects.nonNull(result)) return result;
    if (object instanceof Map) {
      return this.convertDicColumnInfo4Map((Map<String, Object>) object);
    } else if (!FieldUtil.isJDKClass(object)) {
      return this.convertDicColumnInfo4Entity(object);
    }
    return null;
  }

  /**
   * 翻译map类数据
   *
   * @param resultSet4row 结果集(一行)  key是列名称, value是列值
   */
  private Object convertDicColumnInfo4Map(Map<String, Object> resultSet4row) {
    boolean ifOverturn = TransBus.isOverturn();
    final Map<String, Map<String, Object>> _finalDicInfo = !ifOverturn ? DIC_INFO : DIC_INFO_OVERTURN;
    Map<String, Object> addInfo = new HashMap<>();
    resultSet4row.keySet().forEach(resultColName -> {
      // 获取结果集信息  开始递归
      Object recursion = handle(resultSet4row.get(resultColName));
      if (Objects.nonNull(recursion)) {
        resultSet4row.put(resultColName, recursion);
        return;
      }
      // 字段名下划线转驼峰, 并大写, 与工具类统一格式
      String colName = ConverterUtil.getCommonColName(resultColName);
      Map<String, Object> usedDicInfo = _finalDicInfo.get(colName);

      // 获取原值
      String colValue = DataFilter.toString(resultSet4row.get(resultColName)); //当前字段的结果  exp: name(resultColName): 001(biz_value)

      // 翻译前置拦截器============在翻译之前允许替换字典
      final BeforeDicHandleInfo beforeDicHandleInfo = initBeforeConvertInfo(colName, colValue, usedDicInfo, _finalDicInfo);
      usedDicInfo = beforeDicHandleInfo.getUsedDicInfo();
      // 翻译前置拦截器============在翻译之前允许替换字典

      // 开始翻译
      Object convert_col_value = null;
      boolean ifMatchDic = usedDicInfo != null;
      if (ifMatchDic) {
        convert_col_value = usedDicInfo.get(colValue);
      } else {
        usedDicInfo = new HashMap<>();
      }

      // 翻译后置拦截器============在翻译之后允许替换翻译值
      final AfterDicHandleInfo afterDicHandleInfo = initAfterConvertInfo(colName, colValue, convert_col_value, usedDicInfo, _finalDicInfo);
      convert_col_value = afterDicHandleInfo.getTargetValue();
      // 翻译后置拦截器============在翻译之后允许替换翻译值

      if (convert_col_value != null || ifMatchDic) addInfo.put(resultColName.concat(V), convert_col_value); //匹配了字典也要有这个字段, 因为前端可能会用
    });
    resultSet4row.putAll(addInfo);
    return resultSet4row;
  }

  /**
   * 翻译实体类数据
   *
   * @param resultSet4Row 结果集(一行) field为列名, value为列值
   */
  private Object convertDicColumnInfo4Entity(Object resultSet4Row) {
    boolean ifOverturn = TransBus.isOverturn();
    final Map<String, Map<String, Object>> _finalDicInfo = !ifOverturn ? DIC_INFO : DIC_INFO_OVERTURN;
    List<Field> allFields = FieldUtil.getAllFields(resultSet4Row.getClass());
    Map<String, Object> kv = new HashMap<>();
    allFields.forEach(field -> {
      try {
        // 获取结果集信息  开始递归
        Method getMethod4FieldValue = FieldUtil.getMethod(resultSet4Row.getClass(), field.getName());
        Method setMethod4FieldValue = FieldUtil.setMethod(resultSet4Row.getClass(), field.getName());
        Object fieldValue = getMethod4FieldValue.invoke(resultSet4Row);
        Object recursion = handle(fieldValue);
        if (Objects.nonNull(recursion)) {
          setMethod4FieldValue.invoke(resultSet4Row, recursion);
          return;
        }
        // 下划线转驼峰并大写, 与工具类统一格式
        String colName = ConverterUtil.getCommonColName(field.getName());
        Map<String, Object> usedDicInfo = _finalDicInfo.get(colName);

        // 获取原值
        String colValue = DataFilter.toString(fieldValue);
        //Method setMethod = FieldUtil.setMethod(resultSet4Row.getClass(), field.getName(), fieldValue.getClass());

        // 翻译前置拦截器============在翻译之前允许替换字典
        final BeforeDicHandleInfo beforeDicHandleInfo = initBeforeConvertInfo(colName, colValue, usedDicInfo, _finalDicInfo);
        usedDicInfo = beforeDicHandleInfo.getUsedDicInfo();
        // 翻译前置拦截器============在翻译之前允许替换字典

        // 开始翻译
        Object convert_col_value = null;
        boolean ifMatchDic = usedDicInfo != null;
        if (usedDicInfo != null) {
          convert_col_value = usedDicInfo.get(colValue);
        } else {
          usedDicInfo = new HashMap<>();
        }

        // 翻译后置拦截器============在翻译之后允许替换翻译值
        final AfterDicHandleInfo afterDicHandleInfo = initAfterConvertInfo(colName, colValue, convert_col_value, usedDicInfo, _finalDicInfo);
        convert_col_value = afterDicHandleInfo.getTargetValue();
        // 翻译后置拦截器============在翻译之后允许替换翻译值

        if (convert_col_value != null || ifMatchDic) {
          kv.put(field.getName().concat(V), convert_col_value);
        }
        //setMethod.invoke(resultSet4Row, convert_col_value);
      } catch (Exception ignored) {
        ignored.printStackTrace();
      }
    });
    if(kv.isEmpty()) return resultSet4Row;
    Object generate;
    try {
      generate = PropertyAppender.generate(resultSet4Row, kv);
    } catch (Exception e) {
      e.printStackTrace();
      return resultSet4Row;
    }
    return generate;
  }

  private BeforeDicHandleInfo initBeforeConvertInfo(String commonName, Object originValue, Map<String, Object> usedDicInfo, Map<String, Map<String, Object>> overallDicInfo) {
    BeforeDicHandleInfo convertInfo = BeforeDicHandleInfo.initBeforeConvertInfo();
    convertInfo.setCommonColName(commonName);
    convertInfo.setOriginValue(originValue);
    convertInfo.setUsedDicInfo(usedDicInfo);
    convertInfo.setOverallDicInfo(overallDicInfo);
    for (ConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo> dicConvertFilter : filters) {
      dicConvertFilter.beforeConvert(convertInfo);
    }
    List<InnerConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo>> tempFilter = TransBus.getTempFilter();
    if (tempFilter != null) {
      for (ConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo> dicConvertFilter : tempFilter) {
        dicConvertFilter.beforeConvert(convertInfo);
      }
    }
    return convertInfo;
  }

  private AfterDicHandleInfo initAfterConvertInfo(String commonName, Object originValue, Object targetValue, Map<String, Object> usedDicInfo, Map<String, Map<String, Object>> overallDicInfo) {
    AfterDicHandleInfo convertInfo = AfterDicHandleInfo.initAfterConvertInfo();
    convertInfo.setCommonColName(commonName);
    convertInfo.setOriginValue(originValue);
    convertInfo.setTargetValue(targetValue);
    convertInfo.setUsedDicInfo(usedDicInfo);
    convertInfo.setAdaptor(this.adaptor);
    convertInfo.setOverallDicInfo(overallDicInfo);
    for (ConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo> dicConvertFilter : filters) {
      dicConvertFilter.afterConvert(convertInfo);
    }
    List<InnerConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo>> tempFilter = TransBus.getTempFilter();
    if (tempFilter != null) {
      for (ConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo> dicConvertFilter : tempFilter) {
        dicConvertFilter.afterConvert(convertInfo);
      }
    }
    return convertInfo;
  }

  @Override
  public Object beforeHandler(Object input) {
    return autoEncoder.encode(input);
  }

}
