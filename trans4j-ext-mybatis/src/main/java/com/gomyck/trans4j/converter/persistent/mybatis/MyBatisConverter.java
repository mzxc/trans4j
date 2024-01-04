
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

package com.gomyck.trans4j.converter.persistent.mybatis;

import com.gomyck.trans4j.converter.Converter;
import com.gomyck.trans4j.converter.persistent.ResultCollectionConverter;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.profile.Trans4JProfiles;
import com.gomyck.trans4j.support.TransBus;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * 结果集转换
 * mybatis 插件, 如果当前工程持久层框架为 mybatis 会自动注册该类为 bean
 *
 * @author 郝洋 QQ:474798383
 * @version [版本号/1.0]
 * @since [2019-07-17]
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class MyBatisConverter extends ResultCollectionConverter implements Interceptor, Converter {

  public MyBatisConverter(ConverterHandlerComposite converterHandlerComposite, Trans4JProfiles trans4jProfiles) {
    super(converterHandlerComposite, trans4jProfiles);
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    try {
      List<Object> result = (List<Object>) invocation.proceed();
      TransBus.setOriginFlag(true);
      return doConvert(result);
    } catch (Exception e) {
      TransBus.clearCurrentBusInfo();
      throw e;
    }
  }

  @Override
  public Object plugin(Object o) {
    return Plugin.wrap(o, this);
  }

  @Override
  public void setProperties(Properties properties) {
  }

}
