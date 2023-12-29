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
import com.gomyck.trans4j.support.TransBus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author gomyck
 */
@Getter
public class FilterComposite<B, A> implements OuterConverterFilter<B, A> {

  private final List<ConverterFilter<B, A>> converterFilters = new ArrayList<>();

  public void addFilter(ConverterFilter<B, A> converterFilter) {
    if (converterFilters.contains(converterFilter)) return;
    converterFilters.add(converterFilter);
    converterFilters.sort(Comparator.comparingInt(ConverterFilter::getOrder));
  }

  @Override
  public boolean support(final ConverterHandler converterHandler) {
    return true;
  }

  @Override
  public void beforeConvert(Object obj) {
    for (ConverterFilter converterFilter : converterFilters) {
      if (converterFilter.support(TransBus.getCurrentHandler())) {
        converterFilter.beforeConvert(obj);
      }
    }
  }

  @Override
  public void afterConvert(Object obj) {
    for (ConverterFilter converterFilter : converterFilters) {
      if (converterFilter.support(TransBus.getCurrentHandler())) {
        converterFilter.afterConvert(obj);
      }
    }
  }

  @Override
  public void afterPropertiesSet() {}

}
