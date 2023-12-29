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

import com.gomyck.trans4j.filter.InnerConverterFilter;

/**
 * 转换处理抽象, 是具体负责实体转换的处理核心
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/12/12
 */
public interface ConverterHandler {

  /**
   * 是否支持当前实体
   *
   * @param obj 结果集
   * @return boolean
   */
  boolean support(Object obj);

  /**
   * 在处理之前, 不返回结果, 但可以对引用对象自身属性进行修改
   *
   * @param input 结果集
   * @return 如果返回 true 则继续, 返回 false 则进入下一个 handler 处理
   */
  default boolean beforeHandler(Object input) {
    return true;
  }

  void handle(Object obj);

  default boolean afterHandler(Object input) {
    return true;
  }

  default String getHandlerName() {
    return "CK_HANDLER_" + this.getClass()
      .getName();
  }

  default int getOrder() {
    return Integer.MAX_VALUE;
  }

  /**
   * 添加内部 filter, 个性化使用
   *
   * @param filter filter
   */
  default void addInnerFilter(InnerConverterFilter<?, ?> filter) {}

}
