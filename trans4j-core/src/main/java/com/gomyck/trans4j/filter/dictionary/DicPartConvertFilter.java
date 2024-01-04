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
import com.gomyck.util.ObjectJudge;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 部分翻译拦截器
 * <p>
 * A-150401-00001 --- A-赤峰-00001
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/9/2
 */
public class DicPartConvertFilter extends AbstractDicConverterFilter {

  private final List<String> includeColNames = new ArrayList<>();

  private Integer from = 0;

  private String endFlag = "-";

  @Override
  public int getOrder() {
    return 1;
  }

  @Override
  public void clearProp() {
    includeColNames.clear();
    from = 0;
    endFlag = "-";
  }

  @Override
  public void afterConvert(AfterDicHandleInfo convertInfo) {
    includeColNames.forEach(commonColName -> {
      if (ObjectJudge.isNull(commonColName)) return;
      if (commonColName.equals(convertInfo.getCommonColName())) {
        String preStr = "";
        String sufStr = "";
        String originValue = convertInfo.getOriginValue().toString();
        try {
          //下面的逻辑顺序不要变, 否则字符串截取会出现错乱, 有问题
          preStr = originValue.substring(0, this.from);
          originValue = originValue.substring(this.from);
          sufStr = originValue.substring(originValue.indexOf(endFlag));
          originValue = originValue.substring(0, originValue.indexOf(endFlag));

        } catch (Exception ignore) {
        }
        final String finalPreStr = preStr;
        final String finalSufStr = sufStr;
        convertInfo.setTargetWrapper(new HashMap<>()); //用的时候初始化, 节省空间
        convertInfo.getTargetWrapper().put(commonColName, e -> MessageFormat.format("{0}{1}{2}", finalPreStr, e.toString(), finalSufStr));
        convertInfo.setOriginValue(originValue);
        convertInfo.setTargetValue(convertInfo.getUsedDicInfo().get(originValue.trim()));
      }
    });
  }

  private DicPartConvertFilter() {
  }

  public static DicPartConvertFilter init() {
    return new DicPartConvertFilter();
  }

  /**
   * 添加指定的字段名
   *
   * @param colName 需要翻译的字段名
   * @return 当前实体
   */
  public DicPartConvertFilter addPartColName(String colName) {
    includeColNames.add(ConverterUtil.getCommonColName(colName));
    return this;
  }

  /**
   * 设置开始位置
   *
   * @param from 开始位置
   * @return 当前实体
   */
  public DicPartConvertFilter setPartFrom(Integer from) {
    this.from = from;
    return this;
  }

  /**
   * 结束符
   *
   * @param endFlag endFlag
   * @return DicManyValueColFilter
   */
  public DicPartConvertFilter setEndFlag(String endFlag) {
    this.endFlag = endFlag;
    return this;
  }

}
