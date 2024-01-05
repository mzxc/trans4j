/*
 * Copyright [2024] [trans4j@gomyck.com]
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

package com.gomyck.trans4j.profile;

import lombok.Data;

import java.util.List;

@Data
public class DicConfig {

  public static final String DIC_CONFIG_PREFIX = Trans4JProfiles.CONVERTER_PREFIX + ".dic";

  /**
   * 是否激活 dic 翻译
   */
  private boolean enabled = true;
  /**
   * dic 适配器
   */
  private CkDicAdaptorConfig adaptor = new CkDicAdaptorConfig();
  /**
   * dic job 配置
   */
  private CkDicJobConfig job = new CkDicJobConfig();

  @Data
  public static class CkDicAdaptorConfig {

    /**
     * 默认的字典文件名称
     */
    public static final String DEFAULT_DIC_INFO_FILE_NAME = "dic.json";
    /**
     * 字典表的code对应的字段名
     */
    private String code = "key";
    /**
     * 字典表的value对应的字段名
     */
    private String value = "value";
    /**
     * 对应业务表的字段名称 exp: key=1 value=中国 columnName=country 这个country就是业务表存储国家的字段
     */
    private String columnName = "columnName";
    /**
     * 国际化标识字段名称
     */
    private String i18nColumnName;
    /**
     * 默认的国际化标识 默认使用的标识代码: 比如 CN
     */
    private String defaultI18nFlag = "CN";
    /**
     * 初始化字典表信息sql
     */
    private List<String> initDicSql;
    /**
     * 初始化字典表信息 URI (get 请求)
     */
    private List<String> initDicUrl;
    /**
     * 初始化字典表信息文件, 在 classPath 下可以查询到
     */
    private String initDicFile = DEFAULT_DIC_INFO_FILE_NAME;
  }

  @Data
  public static class CkDicJobConfig {

    /**
     * 刷新字典表时间 默认 10 分钟
     */
    private Integer refreshInterval = 10;
  }

}

