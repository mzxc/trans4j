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

import com.gomyck.trans4j.handler.BeforeHandleInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 翻译前可获得信息
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/9/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BeforeDicHandleInfo extends BeforeHandleInfo {

  private final static ThreadLocal<BeforeDicHandleInfo> INNER_CACHE = new ThreadLocal<>();

  /**
   * 初始化实体, 因为单个线程翻译, 是串行的, 进需要一个实体即可, 删除无用的实体生成
   *
   * @return BeforeDicHandleInfo BeforeDicHandleInfo
   */
  public static BeforeDicHandleInfo initBeforeConvertInfo() {
    BeforeDicHandleInfo beforeDicHandleInfo;
    if ((beforeDicHandleInfo = INNER_CACHE.get()) != null) {
      clearProp(beforeDicHandleInfo);
    } else {
      beforeDicHandleInfo = new BeforeDicHandleInfo();
      INNER_CACHE.set(beforeDicHandleInfo);
    }
    return beforeDicHandleInfo;
  }

  /**
   * 属性还原
   *
   * @param beforeDicHandleInfo 转换信息
   */
  private static void clearProp(BeforeDicHandleInfo beforeDicHandleInfo) {
    beforeDicHandleInfo.commonColName = null;
    beforeDicHandleInfo.usedDicInfo = null;
    beforeDicHandleInfo.overallDicInfo = null;
  }

  private String commonColName; //通用的属性名--全大写
  private Object originValue;   //原值
  private Map<String, Object> usedDicInfo; //符合当前类型的字典信息
  private Map<String, Map<String, Object>> overallDicInfo; //总体字典信息

}
