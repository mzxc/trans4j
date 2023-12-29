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

import com.gomyck.trans4j.handler.AfterHandleInfo;
import lombok.Data;

import java.util.Map;
import java.util.function.Function;

/**
 * 翻译后信息
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/9/2
 */
@Data
public class AfterDicHandleInfo extends AfterHandleInfo {

  private final static ThreadLocal<AfterDicHandleInfo> INNER_CACHE = new ThreadLocal<>();

  /**
   * 初始化实体, 因为单个线程翻译, 是串行的, 只需要一个实体即可, 删除无用的实体生成
   *
   * @return AfterDicHandleInfo AfterDicHandleInfo
   */
  public static AfterDicHandleInfo initAfterConvertInfo() {
    AfterDicHandleInfo afterDicHandleInfo;
    if ((afterDicHandleInfo = INNER_CACHE.get()) != null) {
      clearProp(afterDicHandleInfo);
    } else {
      afterDicHandleInfo = new AfterDicHandleInfo();
      INNER_CACHE.set(afterDicHandleInfo);
    }
    return afterDicHandleInfo;
  }

  /**
   * 属性还原
   *
   * @param afterDicHandleInfo 转换信息
   */
  private static void clearProp(AfterDicHandleInfo afterDicHandleInfo) {
    afterDicHandleInfo.commonColName = null;
    afterDicHandleInfo.originValue = null;
    afterDicHandleInfo.targetValue = null;
    afterDicHandleInfo.adaptor = null;
    afterDicHandleInfo.usedDicInfo = null;
    afterDicHandleInfo.overallDicInfo = null;
    afterDicHandleInfo.targetWrapper = null;
  }

  private String commonColName; //通用的属性名--全大写
  private Object originValue; //原值
  private Object targetValue; //转换值
  private DicDescribeAdaptor adaptor; //翻译适配器
  private Map<String, Object> usedDicInfo; //符合当前类型的字典信息
  private Map<String, Map<String, Object>> overallDicInfo; //总体字典信息
  //用的时候初始化, 不用不要初始化
  //该包装器可以保证多个 filter 对当前实体做链式增强(避免对 targetValue 反复的覆盖导致值丢失)
  //比如翻译了 A-150401-00001 --- A-赤峰-00001  如果不用 wrapper, 那么其他的 filter 会覆盖的 set 回 A-150401-00001 (原值)
  //targetWrapper存储了优先级最低的值包装器, 会在最终使用值的时候, 调用该包装器的 func 对 key 做翻译
  private Map<String, Function<Object, Object>> targetWrapper; //返回值包装器, 可以对翻译后的返回值做增强

  public Object getTargetValue() {
    if (targetWrapper == null || targetWrapper.get(commonColName) == null) return targetValue;
    return targetWrapper.get(commonColName).apply(targetValue);
  }

}
