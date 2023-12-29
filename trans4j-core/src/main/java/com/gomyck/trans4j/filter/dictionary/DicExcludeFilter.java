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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 翻译排除, 排除指定 col 名称的翻译
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/9/1
 */
public class DicExcludeFilter extends AbsDicConverterFilter {

  private final Set<String> excludeColNames = new HashSet<>();

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void clearProp() {
    excludeColNames.clear();
  }

  @Override
  public void afterConvert(AfterDicHandleInfo convertInfo) {
    final Optional<String> any = excludeColNames.stream().filter(e -> e.equals(convertInfo.getCommonColName())).findAny();
    if (any.isPresent()) {
      convertInfo.setTargetValue(null); //convertInfo.getOriginValue()
    }
  }

  private DicExcludeFilter() {
  }

  public static DicExcludeFilter init() {
    return new DicExcludeFilter();
  }

  public DicExcludeFilter addExcludeColNames(String... colNames) {
    for (final String colName : colNames) {
      excludeColNames.add(ConverterUtil.getCommonColName(colName));
    }
    return this;
  }
}
