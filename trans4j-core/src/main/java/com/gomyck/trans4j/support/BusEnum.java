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

package com.gomyck.trans4j.support;

/**
 * 总线消息枚举
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/12/12
 */
public enum BusEnum {
  // 国际化标识
  I18N_FLAG,
  // 转换过滤器
  CONVERT_FILTER,
  // 转换状态
  CONVERT_STATUS,
  // 翻转翻译
  CONVERT_OVERTURN_STATUS,
  // 是否携带分页
  CONVERT_WITH_PAGE_STATUS,
  // 携带的分页信息
  CONVERT_WITH_PAGE_INFO,
  // 当前的处理器
  CURRENT_CONVERTER_HANDLER,
  // 处理模式
  HANDLE_MODE,
  // 是否在原有属性上转换, false : value$V (仅在 responseAdvice 模式上实现了)
  ORIGIN_FLAG,
}
