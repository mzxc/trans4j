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

package com.gomyck.trans4j.profile;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 数据转换服务配置
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2023/12/28
 */
@ConfigurationProperties(Trans4jProfiles.CONVERTER_PREFIX)
@Data
public class Trans4jProfiles {

  public static final String CONVERTER_PREFIX = "trans4j";

  /**
   * 忽略转换方法后缀(ConvertType 为 RESULT_SET 生效)
   */
  private String ignoreSuffix = "_DC";

  /**
   * 消息增强(默认为 true)
   */
  private boolean messageAdvice = true;

  /**
   * dic 转换器配置
   */
  private DicConfig dic = new DicConfig();

  private SecureConfig secure = new SecureConfig();

  @Data
  public static class SecureConfig {
    private String key;
  }

  @Data
  public static class DicConfig {
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
  }

  @ConfigurationProperties(Trans4jProfiles.CONVERTER_PREFIX + ".dic.adaptor")
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
    private String i18n;
    /**
     * 默认的国际化标识 默认使用的标识代码: 比如 CN
     */
    private String defaultI18nFlag;
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

  @ConfigurationProperties(Trans4jProfiles.CONVERTER_PREFIX + ".dic.job")
  @Data
  public static class CkDicJobConfig {
    /**
     * 刷新字典表时间 默认 10 分钟
     */
    private Integer refreshInterval = 10;
  }

}
