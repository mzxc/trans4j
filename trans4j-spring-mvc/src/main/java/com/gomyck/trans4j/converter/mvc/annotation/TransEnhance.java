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

package com.gomyck.trans4j.converter.mvc.annotation;

import java.lang.annotation.*;

/**
 * 消息增强
 *
 * @author 郝洋
 * @version [版本号, 2017年7月5日]
 * @since [产品/模块版本]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransEnhance {

  boolean overTurn() default false;

  /**
   * 国际化标识设置(方法体内设置优先级最高)
   *
   * @return 国际化标识
   */
  String i18nFlag() default "";

  /**
   * 把当前的 key 替换成翻译值, 用 key$K 来存储原值
   *
   * @return 是否使用原值替换
   */
  boolean originOverride() default false;

}
