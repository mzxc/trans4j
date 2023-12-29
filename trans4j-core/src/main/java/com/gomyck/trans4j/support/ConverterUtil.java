
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

import com.gomyck.util.CkParam;
import com.gomyck.util.ObjectJudge;

/**
 * 工具类
 *
 * @author gomyck
 */
public class ConverterUtil {

  /**
   * 获取通用的列名 KEY
   * <p>
   * 查询的结果可能是 map 也可能是 entity, 导致对应的列名称格式不统一
   * 使用该方法可以转换成统一个列名称格式, 统一处理
   *
   * @param name 列名称
   * @return 转换后的列名称
   */
  public static String getCommonColName(String name) {
    if (ObjectJudge.isNull(name)) return "null";
    return CkParam.underlineToHump(name.trim()).toUpperCase();
  }

}
