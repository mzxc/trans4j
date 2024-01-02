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

package com.gomyck.trans4j.context;

import com.gomyck.trans4j.filter.FilterComposite;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.handler.ConverterHandlerFactory;
import com.gomyck.trans4j.handler.dictionary.DicConverterInitConditional;
import com.gomyck.trans4j.handler.dictionary.DicInfoConverterHandlerFactory;
import com.gomyck.trans4j.profile.Trans4JProfiles;
import com.gomyck.trans4j.schedule.DicConverterHandlerSchedule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class Trans4jContext {

  private Trans4JProfiles trans4jProfiles;

  @Nullable
  private DataSource dataSource;

  @Bean
  @Order(Integer.MIN_VALUE)
  public FilterComposite<?, ?> initFilterComposite() {
    return new FilterComposite<>();
  }

  @Bean
  @Order(Integer.MIN_VALUE)
  public ConverterHandlerComposite initHandlerComposite() {
    return new ConverterHandlerComposite();
  }

  @Bean
  @Conditional(DicConverterInitConditional.class)
  public ConverterHandlerFactory initDicConverterHandlerFactory() {
    DicInfoConverterHandlerFactory dicInfoConverterHandlerFactory = new DicInfoConverterHandlerFactory();
    dicInfoConverterHandlerFactory.setTrans4jProfiles(trans4jProfiles);
    dicInfoConverterHandlerFactory.setDataSource(dataSource);
    dicInfoConverterHandlerFactory.setConverterHandlerComposite(initHandlerComposite());
    return dicInfoConverterHandlerFactory;
  }

  @Bean
  @Conditional(DicConverterInitConditional.class)
  public DicConverterHandlerSchedule initDicConverterHandlerSchedule() {
    return new DicConverterHandlerSchedule();
  }

}
