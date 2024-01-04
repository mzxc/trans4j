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
import com.gomyck.util.CkPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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
    if (!TransBus.getConvertType().contains(ConverterType.PERSISTENT_CONVERTER)) return result;
    try {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      for (StackTraceElement stackTraceElement : stackTrace) {
        if (stackTraceElement.getMethodName().toUpperCase().contains(trans4jProfiles.getIgnoreSuffix())) return result;
      }
      if (Objects.isNull(result)) return null;
      result = converterHandlerComposite.handle(result);
    } catch (Exception e) {
      TransBus.clearCurrentBusInfo();
    } finally {
      if (TransBus.isWithPage()) {
        // 清除分页信息, 以便于第二次查询列表时可以正常进行翻译
        TransBus.cleanPageInfo();
        // 查询的记录数 如果查询总数为 0 或者 记录数少于查询数, 清空总线
        if (result instanceof List) {
          List<Long> pageInfo = (List) result;
          try {
            Long record = pageInfo.get(0);
            CkPage.PageInfo withPageInfo = TransBus.getPageInfo();
            if (withPageInfo == null) throw new RuntimeException("not found pageInfo, please use TransBus.setPageInfo() declare pageInfo");
            if (record < ((withPageInfo.getPage() - 1L) * withPageInfo.getLimit() + 1)) throw new RuntimeException("record num not enough");
          } catch (Exception e) {
            TransBus.clearCurrentBusInfo();
          }
        }
      } else {
        TransBus.clearCurrentBusInfo();
      }
    }
    return result;
  }

}
