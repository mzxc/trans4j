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
import com.gomyck.trans4j.support.ConverterUtil;
import com.gomyck.trans4j.support.TransBus;
import com.gomyck.util.DataFilter;
import com.gomyck.util.FieldUtil;
import com.gomyck.util.ObjectJudge;
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

  /**
   * 初始化方法, reload 字典时使用
   */
  @Getter
  private final List<Function<Object, List<Map<String, Object>>>> initDicInfoFunc = new ArrayList<>();

  public DicConverterHandler(DicDescribeAdaptor adaptor, ConverterHandlerComposite converterHandlerComposite) {
    super(converterHandlerComposite);
    this.adaptor = adaptor;
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
    if (obj instanceof Iterable) return true;
    if (obj instanceof Map) return true;
    return !FieldUtil.isBaseType(obj.getClass().getTypeName());
  }

  @Override
  public void handle(Object resultSet) {
    if (resultSet instanceof Map) {
      this.convertDicColumnInfo4Map((Map) resultSet);
    } else if (!FieldUtil.isBaseType(resultSet.getClass().getTypeName())) {
      this.convertDicColumnInfo(resultSet);
    }
  }

  /**
   * 翻译map类数据
   * 如果是使用 mvc 增强方式, 则永远会用此函数做翻译操作, 在前一步会把实体转换成 map
   *
   * @param resultSet 结果集
   */
  private void convertDicColumnInfo4Map(Map<String, Object> resultSet) {
    boolean ifOverturn = TransBus.isOverturn();
    boolean originFlag = TransBus.getOriginFlag();
    final Map<String, Map<String, Object>> _finalDicInfo = !ifOverturn ? DIC_INFO : DIC_INFO_OVERTURN;
    Map<String, Object> addInfo = new HashMap<>();
    resultSet.keySet().forEach(resultColName -> {
      // 获取结果集信息  开始递归
      if (recursion(resultSet.get(resultColName), ifOverturn)) return;

      // 字段名下划线转驼峰, 并大写, 与工具类统一格式
      String colName = ConverterUtil.getCommonColName(resultColName);
      Map<String, Object> usedDicInfo = _finalDicInfo.get(colName);

      // 获取原值
      String biz_code = DataFilter.toString(resultSet.get(resultColName)); //当前字段的结果  exp: name(resultColName): 001(biz_value)

      // 翻译前置拦截器============在翻译之前允许替换字典
      final BeforeDicHandleInfo beforeDicHandleInfo = initBeforeConvertInfo(colName, biz_code, usedDicInfo, _finalDicInfo);
      usedDicInfo = beforeDicHandleInfo.getUsedDicInfo();
      // 翻译前置拦截器============在翻译之前允许替换字典

      // 开始翻译
      Object convert_biz_value = null;
      boolean ifMatchDic = usedDicInfo != null;
      if (ifMatchDic) {
        convert_biz_value = usedDicInfo.get(biz_code);
      } else {
        usedDicInfo = new HashMap<>();
      }

      // 翻译后置拦截器============在翻译之后允许替换翻译值
      final AfterDicHandleInfo afterDicHandleInfo = initAfterConvertInfo(colName, biz_code, convert_biz_value, usedDicInfo, _finalDicInfo);
      convert_biz_value = afterDicHandleInfo.getTargetValue();
      // 翻译后置拦截器============在翻译之后允许替换翻译值

      if (originFlag) {
        if (convert_biz_value != null) {
          resultSet.put(resultColName, convert_biz_value); //不为空才做翻译处理, 否则返回原值
          addInfo.put(resultColName.concat("$K"), biz_code); //保留原值到新的字段
        }
      } else {
        if (convert_biz_value != null || ifMatchDic) addInfo.put(resultColName.concat("$V"), convert_biz_value); //匹配了字典也要有这个字段, 因为前端可能会用
      }
    });
    resultSet.putAll(addInfo);
  }

  /**
   * 翻译实体类数据
   *
   * @param resultSet 结果集
   */
  private void convertDicColumnInfo(Object resultSet) {
    boolean ifOverturn = TransBus.isOverturn();
    final Map<String, Map<String, Object>> _finalDicInfo = !ifOverturn ? DIC_INFO : DIC_INFO_OVERTURN;
    List<Field> allFields = FieldUtil.getAllFields(resultSet.getClass());
    allFields.forEach(field -> {
      try {
        // 获取结果集信息  开始递归
        Method getMethod = FieldUtil.getMethod(resultSet.getClass(), field.getName());
        Object invoke = getMethod.invoke(resultSet);
        if (recursion(invoke, ifOverturn)) return;

        // 下划线转驼峰并大写, 与工具类统一格式
        String colName = ConverterUtil.getCommonColName(field.getName());
        Map<String, Object> usedDicInfo = _finalDicInfo.get(colName);

        // 获取原值
        String biz_code = DataFilter.toString(invoke);
        Method setMethod = FieldUtil.setMethod(resultSet.getClass(), field.getName(), invoke.getClass());

        // 翻译前置拦截器============在翻译之前允许替换字典
        final BeforeDicHandleInfo beforeDicHandleInfo = initBeforeConvertInfo(colName, biz_code, usedDicInfo, _finalDicInfo);
        usedDicInfo = beforeDicHandleInfo.getUsedDicInfo();
        // 翻译前置拦截器============在翻译之前允许替换字典

        // 开始翻译
        Object convert_biz_value = null;
        if (usedDicInfo != null) {
          convert_biz_value = usedDicInfo.get(biz_code);
        } else {
          usedDicInfo = new HashMap<>();
        }

        // 翻译后置拦截器============在翻译之后允许替换翻译值
        final AfterDicHandleInfo afterDicHandleInfo = initAfterConvertInfo(colName, biz_code, convert_biz_value, usedDicInfo, _finalDicInfo);
        convert_biz_value = afterDicHandleInfo.getTargetValue();
        // 翻译后置拦截器============在翻译之后允许替换翻译值

        if (convert_biz_value != null) setMethod.invoke(resultSet, convert_biz_value);
      } catch (Exception ignored) {
      }
    });
  }

  /**
   * 递归翻译
   *
   * @param _value     当前数据中的子集
   * @param ifOverturn 是否翻转翻译
   * @return boolean 是否发生递归动作
   */
  private boolean recursion(Object _value, boolean ifOverturn) {
    if (_value == null) return true;
    if (_value instanceof Map) {
      this.convertDicColumnInfo4Map((Map) _value);
      return true;
    } else if (_value instanceof Iterable) {
      ((Iterable) _value).forEach(e -> {
        if (Objects.isNull(e)) return;
        this.handle(e);
      });
      return true;
    }
    //都不满足的话, 说明当前属性不可继续下钻, 返回false, 释放foreach的循环继续向下
    //反之返回true, 转换方法里return相当于普通for的continue
    return false;
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

}
