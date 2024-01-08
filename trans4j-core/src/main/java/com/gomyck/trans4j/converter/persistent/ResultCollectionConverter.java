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

package com.gomyck.trans4j.converter.persistent;

import com.gomyck.trans4j.converter.Converter;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.profile.Trans4JProfiles;
import com.gomyck.trans4j.support.ConverterType;
import com.gomyck.trans4j.support.TransBus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 持久层-转换器核心
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/2/7
 */
@Slf4j
@AllArgsConstructor
public abstract class ResultCollectionConverter implements Converter {

  /**
   * 处理器集合
   */
  private ConverterHandlerComposite converterHandlerComposite;

  /**
   * 转换器配置类
   */
  private Trans4JProfiles trans4jProfiles;

  @Override
  public Object doConvert(Object result) {
    if (!TransBus.getConvertType().contains(ConverterType.JDBC_PERSISTENT_CONVERTER)) return result;
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement stackTraceElement : stackTrace) {
      if (stackTraceElement.getMethodName().toUpperCase().contains(trans4jProfiles.getIgnoreSuffix())) return result;
    }
    if (Objects.isNull(result)) return null;
    return converterHandlerComposite.handle(result);
  }

}
