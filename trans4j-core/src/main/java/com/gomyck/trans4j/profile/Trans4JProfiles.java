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
@Data
public class Trans4JProfiles {

  public static final String CONVERTER_PREFIX = "trans4j";

  /**
   * 忽略转换方法后缀(ConvertType 为 RESULT_SET 生效)
   */
  private String ignoreSuffix = "_DC";

  /**
   * dic 转换器配置
   */
  private DicConfig dic = new DicConfig();

  /**
   * 加密机配置
   */
  private SecureConfig secure = new SecureConfig();

}
