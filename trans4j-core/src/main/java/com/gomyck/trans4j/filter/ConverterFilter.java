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

package com.gomyck.trans4j.filter;

import com.gomyck.trans4j.handler.ConverterHandler;
import org.springframework.beans.factory.InitializingBean;

/**
 * 转换拦截器
 * <p>
 * 对于 converter 的转换过程进行拦截, 并试图干预转换结果
 *
 * @author gomyck
 */
public interface ConverterFilter<B, A> extends InitializingBean {

  boolean support(ConverterHandler converterHandler);

  void beforeConvert(final B obj);

  void afterConvert(final A obj);

  default String getFilterName() {
    return "CK_OUTER_FILTER_" + this.getClass().getName();
  }

  default int getOrder() {
    return Integer.MAX_VALUE;
  }

}
