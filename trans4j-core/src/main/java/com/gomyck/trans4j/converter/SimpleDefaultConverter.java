/*
 * Copyright [2024] [trans4j@gomyck.com]
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

package com.gomyck.trans4j.converter;

import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.support.ConverterType;
import com.gomyck.trans4j.support.TransBus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleDefaultConverter implements Converter {

  /**
   * 处理器集合
   */
  private ConverterHandlerComposite converterHandlerComposite;

  @Override
  public Object doConvert(Object object) {
    if(TransBus.getConvertType().contains(ConverterType.SIMPLE_DEFAULT_CONVERTER)) {
      return converterHandlerComposite.handle(object);
    }
    return object;
  }

}
