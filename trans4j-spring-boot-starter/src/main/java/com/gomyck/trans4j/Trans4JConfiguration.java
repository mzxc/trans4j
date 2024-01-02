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

package com.gomyck.trans4j;

import com.gomyck.trans4j.converter.mvc.ResponseBodyAdviceConverter;
import com.gomyck.trans4j.selector.MyBatisExtImportSelector;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
@AutoConfigureAfter(DataSource.class)
@ComponentScan(basePackages = {"com.gomyck.trans4j"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ResponseBodyAdviceConverter.class)})
@Import({MyBatisExtImportSelector.class})
public class Trans4JConfiguration {

  @Bean
  @ConditionalOnProperty(value = ResponseBodyAdviceConverter.RESPONSE_ADVICE_CONFIG_VALUE, havingValue = "true", matchIfMissing = true)
  public ResponseBodyAdviceConverter initResponseBodyAdviceConverter() {
    return new ResponseBodyAdviceConverter();
  }

}
