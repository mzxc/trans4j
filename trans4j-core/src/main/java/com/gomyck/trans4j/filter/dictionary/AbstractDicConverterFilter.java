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

import com.gomyck.trans4j.filter.InnerConverterFilter;
import com.gomyck.trans4j.handler.ConverterHandler;
import com.gomyck.trans4j.handler.dictionary.AfterDicHandleInfo;
import com.gomyck.trans4j.handler.dictionary.BeforeDicHandleInfo;
import com.gomyck.trans4j.handler.dictionary.DicConverterHandler;

import java.util.Objects;

/**
 * 字典翻译的抽象类
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/9/1
 */
public abstract class AbstractDicConverterFilter implements InnerConverterFilter<BeforeDicHandleInfo, AfterDicHandleInfo> {

  private final String filterName = "CK_INNER_DIC_FILTER_" + this.getClass().getName();

  @Override
  public String getFilterName() {
    return filterName;
  }

  @Override
  public boolean support(final ConverterHandler converterHandler) {
    return converterHandler instanceof DicConverterHandler;
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj) || !(obj instanceof AbstractDicConverterFilter)) return false;
    return this.getFilterName().equals(((AbstractDicConverterFilter) obj).getFilterName());
  }

  /**
   * 转换之前
   *
   * @param beforeDicHandleInfo 转换前入参
   */
  @Override
  public void beforeConvert(BeforeDicHandleInfo beforeDicHandleInfo) {
    // do nothing
  }

  /**
   * 转换
   *
   * @param convertInfo 转换信息
   */
  @Override
  public void afterConvert(AfterDicHandleInfo convertInfo) {
    // do nothing
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }

  public abstract void clearProp();

  @Override
  public void afterPropertiesSet() {

  }
}
