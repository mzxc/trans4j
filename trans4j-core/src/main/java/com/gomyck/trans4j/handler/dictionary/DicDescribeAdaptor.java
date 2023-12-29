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

package com.gomyck.trans4j.handler.dictionary;

import lombok.Data;

@Data
public class DicDescribeAdaptor {

  /**
   * 初始化适配器
   *
   * @return Adaptor 适配器
   */
  public static DicDescribeAdaptor initAdaptor() {
    return new DicDescribeAdaptor("key", "value", "columnName");
  }

  /**
   * 初始化适配器
   *
   * @param keyName    字典表的  [键]  字段名   在集合里的 key 名
   * @param valueName  字典表的   [值]   字段名  在集合里的 key 名
   * @param columnName 字典表的  [翻译属性]  字段名  在集合里的 key 名
   * @return Adaptor 适配器
   */
  public static DicDescribeAdaptor initAdaptor(String keyName, String valueName, String columnName) {
    return new DicDescribeAdaptor(keyName, valueName, columnName);
  }

  /**
   * 初始化适配器
   *
   * @param keyName         字典表的  [键]  字段名   在集合里的 key 名
   * @param valueName       字典表的   [值]   字段名  在集合里的 key 名
   * @param columnName      字典表的  [翻译属性]  字段名  在集合里的 key 名
   * @param i18n            字典表的 [国际化] 字段名  在集合里的 key 名
   * @param defaultI18nFlag 默认的国际化标识 比如: CN
   * @return Adaptor 适配器
   */
  public static DicDescribeAdaptor initAdaptor(String keyName, String valueName, String columnName, String i18n, String defaultI18nFlag) {
    return new DicDescribeAdaptor(keyName, valueName, columnName, i18n, defaultI18nFlag);
  }

  /**
   * 字典表的code对应的字段名
   */
  String CODE;
  /**
   * 字典表的value对应的字段名
   */
  String VALUE;
  /**
   * 对应业务表的字段名称 exp: key=1 value=中国 columnName=country 这个country就是业务表存储国家的字段
   */
  String COLUMN_NAME;
  /**
   * 国际化标识字段名称
   */
  String I18N;
  /**
   * 默认的国际化标识 默认使用的标识代码: 比如 CN
   */
  String DEFAULT_I18N_FLAG;

  public DicDescribeAdaptor(String code, String value, String columnName) {
    CODE = code;
    VALUE = value;
    COLUMN_NAME = columnName;
  }

  public DicDescribeAdaptor(String code, String value, String columnName, String i18n, String defaultI18nFlag) {
    CODE = code;
    VALUE = value;
    COLUMN_NAME = columnName;
    I18N = i18n;
    DEFAULT_I18N_FLAG = defaultI18nFlag;
  }

}
