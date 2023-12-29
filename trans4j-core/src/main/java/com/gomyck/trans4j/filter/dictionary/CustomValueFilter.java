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
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 自定义值翻译拦截器
 * <p>
 * 临时字典, 对指定的 key 做翻译
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2023/5/22
 */
public class CustomValueFilter extends AbsDicConverterFilter {

  private final List<CVInfo> cvInfos = new ArrayList<>();

  public static CustomValueFilter init() {
    return new CustomValueFilter();
  }

  @Override
  public void afterConvert(AfterDicHandleInfo convertInfo) {
    for (final CVInfo cvInfo : cvInfos) {
      if (ConverterUtil.getCommonColName(cvInfo.getColName()).equals(convertInfo.getCommonColName())) {
        if (cvInfo.getCvFunc() != null) {
          try {
            convertInfo.setTargetValue(cvInfo.getCvFunc().apply(convertInfo));
          } catch (Exception ignored) {
          }
        } else {
          convertInfo.setTargetValue(cvInfo.getValue());
        }
        break;
      } else {
        convertInfo.setTargetValue(convertInfo.getOriginValue());
      }
    }
  }

  public CustomValueFilter addCVInfo(CVInfo cvInfo) {
    cvInfos.add(cvInfo);
    return this;
  }

  @Override
  public void clearProp() {
    cvInfos.clear();
  }

  @Data
  @Builder
  public static class CVInfo {
    private String colName;
    private String value;
    private Function<AfterDicHandleInfo, Object> cvFunc; //优先走 func, 如果不存在的话, 返回 value
  }

}
