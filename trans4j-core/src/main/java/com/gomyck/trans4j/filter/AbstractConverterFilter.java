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

import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * @author gomyck
 */
@AllArgsConstructor
public abstract class AbstractConverterFilter<B, A> implements OuterConverterFilter<B, A> {

  FilterComposite<B, A> filterComposite;

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj) || !(obj instanceof AbstractConverterFilter)) return false;
    return this.getFilterName().equals(((AbstractConverterFilter<B, A>) obj).getFilterName());
  }

  @Override
  public void afterPropertiesSet() {
    if (!this.getFilterName().equals(filterComposite.getFilterName())) {
      filterComposite.addFilter(this);
    }
  }
}
