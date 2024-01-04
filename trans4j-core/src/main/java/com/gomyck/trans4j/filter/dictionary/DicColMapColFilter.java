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

package com.gomyck.trans4j.filter.dictionary;

import com.gomyck.trans4j.handler.dictionary.AfterDicHandleInfo;
import com.gomyck.trans4j.support.ConverterUtil;
import com.gomyck.util.DataFilter;
import com.gomyck.util.ObjectJudge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 映射翻译拦截器
 * <p>
 * 指定一个字段名翻译为另外一个指定的字段名
 * <p>
 * originColName  默认将不会再被翻译
 * targetColName  将会被翻译
 *
 * @author gomyck
 * @version 1.0.0
 * @since 2020-05-11
 */
public class DicColMapColFilter extends AbstractDicConverterFilter {

  /**
   * 包含的字段名称
   */
  private final List<Map<String, String>> includeColNames = new ArrayList<>();
  /**
   * 忽略的字段名称
   */
  private final List<String> ignoreColNames = new ArrayList<>();
  /**
   * 是否转换原属性
   */
  private boolean convertOriginCol = false;

  @Override
  public int getOrder() {
    return 10;
  }

  @Override
  public void clearProp() {
    includeColNames.clear();
    ignoreColNames.clear();
    convertOriginCol = false;
  }

  @Override
  public void afterConvert(AfterDicHandleInfo convertInfo) {
    //如果是原属性, 那么就不翻译了
    if (!convertOriginCol && ignoreColNames.contains(convertInfo.getCommonColName())) {
      convertInfo.setTargetValue(null); //convertInfo.getOriginValue()
    }
    includeColNames.forEach(e -> {
      String col = e.get(convertInfo.getCommonColName());
      if (ObjectJudge.isNull(col)) return;
      Map<String, Object> originDicInfo = convertInfo.getOverallDicInfo().get(col); //获取字典
      convertInfo.setTargetValue(originDicInfo.get(DataFilter.toString(convertInfo.getOriginValue())));
    });
  }

  private DicColMapColFilter() {
  }

  public static DicColMapColFilter init() {
    return new DicColMapColFilter();
  }

  /**
   * 添加指定的字段名 originColName 默认将不会再被翻译
   *
   * @param originColName 原字典字段名  exp: type  原本就会被翻译的字段名(字典表中dic_column_name字段值)
   * @param targetColName 被指定翻译的字段名  exp: typeName  结果集中这个字段将会被翻译(要翻译结果集哪个属性就写哪个属性)
   * @return 当前实体
   */
  public DicColMapColFilter addSpecialConvertColName(String originColName, String targetColName) {
    Map<String, String> specialCol = new HashMap<>();
    originColName = ConverterUtil.getCommonColName(originColName);
    specialCol.put(ConverterUtil.getCommonColName(targetColName), originColName);
    includeColNames.add(specialCol);
    ignoreColNames.add(originColName);
    return this;
  }

  /**
   * 翻译原本就应该翻译的字段
   *
   * @return 当前实体
   */
  public DicColMapColFilter convertOriginCol() {
    convertOriginCol = true;
    return this;
  }

}
