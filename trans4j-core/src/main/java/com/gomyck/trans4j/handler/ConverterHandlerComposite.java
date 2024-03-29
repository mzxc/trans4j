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

package com.gomyck.trans4j.handler;

import com.gomyck.trans4j.filter.FilterComposite;
import com.gomyck.trans4j.support.TransBus;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 转换器集合, 所有的 handler 以及 filter 逻辑在此计算
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/12/12
 */
@AllArgsConstructor
public class ConverterHandlerComposite implements ConverterHandler {

  protected FilterComposite<?, ?> filterComposite;

  private final List<ConverterHandler> converterHandlers = new ArrayList<>();

  public void addHandler(ConverterHandler converterHandler) {
    if (converterHandlers.contains(converterHandler)) return;
    converterHandlers.add(converterHandler);
    converterHandlers.sort(Comparator.comparingInt(ConverterHandler::getOrder));
  }

  @Override
  public boolean support(final Object obj) {
    return true;
  }

  @Override
  public Object handle(Object obj) {
    if (Objects.isNull(obj)) return obj;
    for (ConverterHandler handler : converterHandlers) {
      if (!handler.support(obj)) continue;
      TransBus.setCurrentHandler(handler);
      filterComposite.beforeConvert(obj);
      if ((obj = handler.beforeHandler(obj)) == null) continue;
      handler.handle(obj);
      if ((obj = handler.afterHandler(obj))  == null) continue;
      filterComposite.afterConvert(obj);
    }
    return obj;
  }

}
