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

import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * 转换抽象, 一些公共的方法
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/12/8
 */
@AllArgsConstructor
public abstract class AbstractConverterHandler implements ConverterHandler {

  ConverterHandlerComposite converterHandlerComposite;

  public void refresh() {
    converterHandlerComposite.addHandler(this);
  }

  @Override
  public boolean support(Object obj) {
    return true;
  }

  @Override
  public final boolean equals(Object obj) {
    if (Objects.isNull(obj) || !(obj instanceof AbstractConverterHandler)) return false;
    return this.getHandlerName().equals(((AbstractConverterHandler) obj).getHandlerName());
  }

}
