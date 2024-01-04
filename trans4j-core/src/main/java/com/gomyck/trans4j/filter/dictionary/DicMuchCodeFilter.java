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
import java.util.List;

/**
 * 可同时对多个值进行翻译
 * <p>
 * [1,2,3,4]  ---  哈哈,呵呵,哦哦,嘿嘿
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/9/2
 */
public class DicMuchCodeFilter extends AbstractDicConverterFilter {

  private final List<String> includeColNames = new ArrayList<>();

  private String prefixStr = "";

  private String delimiter = ",";

  private String suffixStr = "";

  @Override
  public int getOrder() {
    return 1;
  }

  @Override
  public void clearProp() {
    includeColNames.clear();
    prefixStr = "";
    delimiter = ",";
    suffixStr = "";
  }

  @Override
  public void afterConvert(AfterDicHandleInfo convertInfo) {
    includeColNames.forEach(commonColName -> {
      if (ObjectJudge.isNull(commonColName)) return;
      if (commonColName.equals(convertInfo.getCommonColName())) {
        if (ObjectJudge.notNull(convertInfo.getOriginValue())) {
          String originValue = convertInfo.getOriginValue().toString();
          originValue = originValue.replaceFirst(prefixStr, "");
          originValue = DataFilter.replaceLast(originValue, suffixStr, "");
          String[] values = originValue.split(delimiter);
          StringBuffer targetValue = new StringBuffer();
          for (String s : values) {
            targetValue.append(convertInfo.getUsedDicInfo().get(s.trim())).append(delimiter);
          }
          if (targetValue.lastIndexOf(delimiter) == targetValue.length() - 1) {
            convertInfo.setTargetValue(targetValue.substring(0, targetValue.length() - 1));
          } else {
            convertInfo.setTargetValue(targetValue);
          }
        }
      }
    });
  }

  private DicMuchCodeFilter() {
  }

  public static DicMuchCodeFilter init() {
    return new DicMuchCodeFilter();
  }

  /**
   * 添加指定的字段名
   *
   * @param colName 多个字典值的字段名
   * @return 当前实体
   */
  public DicMuchCodeFilter addManyValueColName(String colName) {
    includeColNames.add(ConverterUtil.getCommonColName(colName));
    return this;
  }

  /**
   * 设置分隔符
   *
   * @param delimiter 分隔符
   * @return 当前实体
   */
  public DicMuchCodeFilter setDelimiter(String delimiter) {
    this.delimiter = delimiter;
    return this;
  }

  /**
   * 分隔符设置
   *
   * @param prefixStr prefixStr
   * @param suffixStr suffixStr
   * @return DicManyValueColFilter
   */
  public DicMuchCodeFilter setPrefixAndSuffixStr(String prefixStr, String suffixStr) {
    this.prefixStr = prefixStr == null ? "" : prefixStr;
    this.suffixStr = suffixStr == null ? "" : suffixStr;
    return this;
  }

}
