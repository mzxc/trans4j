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

import com.gomyck.trans4j.filter.ConverterFilter;
import com.gomyck.trans4j.filter.InnerConverterFilter;
import com.gomyck.trans4j.handler.ConverterHandler;
import com.gomyck.util.CkPage;
import com.gomyck.util.ObjectJudge;

import java.util.*;

/**
 * 线程总线
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/12/12
 */
public class TransBus {
  /**
   * 转换器线程共享
   */
  private static final ThreadLocal<Map<BusEnum, Object>> CONVERTER_SHARE_HOLDER = new ThreadLocal<>();

  /**
   * 转换
   */
  public static void convert() {
    convert(ConverterType.PERSISTENT_CONVERTER);
  }

  /**
   * 转换
   *
   * @param converterType 转换类型
   */
  public static void convert(ConverterType... converterType) {
    if (converterType == null || converterType.length == 0) return;
    List<ConverterType> types = new ArrayList<>(Arrays.asList(converterType));
    threadLocalSet(BusEnum.CONVERT_STATUS, types);
  }

  /**
   * 转换类型
   *
   * @return 转换开关标识
   */
  public static List<ConverterType> getConvertType() {
    List<ConverterType> converterTypes = (List<ConverterType>) threadLocalGet(BusEnum.CONVERT_STATUS);
    return converterTypes == null ? Collections.EMPTY_LIST : converterTypes;
  }

  /**
   * 设置翻转状态
   */
  public static void overturn() {
    threadLocalSet(BusEnum.CONVERT_OVERTURN_STATUS, true);
  }

  /**
   * 是否翻转
   *
   * @return boolean
   */
  public static boolean isOverturn() {
    return threadLocalGet(BusEnum.CONVERT_OVERTURN_STATUS) != null;
  }

  /**
   * 分页转换(有时存在查询异常, 导致紧挨着的查询被翻译)
   *
   * @param page  page
   * @param limit limit
   */
  public static void convertWithPage(Long page, Long limit) {
    CkPage.PageInfo pageInfo = new CkPage.PageInfo(page, limit);
    convertWithPage(pageInfo);
  }

  /**
   * 分页转换
   *
   * @param pageInfo pageInfo
   */
  public static void convertWithPage(CkPage.PageInfo pageInfo) {
    convert();
    threadLocalSet(BusEnum.CONVERT_WITH_PAGE_STATUS, true);
    threadLocalSet(BusEnum.CONVERT_WITH_PAGE_INFO, pageInfo);
  }

  /**
   * 是否携带分页
   *
   * @return 转换开关标识
   */
  public static boolean isWithPage() {
    return threadLocalGet(BusEnum.CONVERT_WITH_PAGE_STATUS) != null;
  }

  /**
   * 分页信息
   *
   * @return 分页信息
   */
  public static CkPage.PageInfo getPageInfo() {
    return (CkPage.PageInfo) threadLocalGet(BusEnum.CONVERT_WITH_PAGE_INFO);
  }

  /**
   * 清除分页翻译标识
   */
  public static void cleanPageInfo() {
    if (isWithPage()) threadLocalSet(BusEnum.CONVERT_WITH_PAGE_STATUS, null);
  }

  /**
   * 设置国际化
   *
   * @param flag 国际化标识
   */
  public static void setI18nFlag(String flag) {
    threadLocalSet(BusEnum.I18N_FLAG, ObjectJudge.isNull(flag) ? "" : flag);
  }

  /**
   * 获取国际化标识
   *
   * @return 标识
   */
  public static String getI18nFlag() {
    return threadLocalGet(BusEnum.I18N_FLAG) == null ? null : Objects.requireNonNull(threadLocalGet(BusEnum.I18N_FLAG)).toString();
  }

  /**
   * 为本次转换添加过滤器
   *
   * @param convertFilter 过滤器
   */
  public static void addTempFilter(InnerConverterFilter<?, ?> convertFilter) {
    List<ConverterFilter> filters = (List<ConverterFilter>) threadLocalGet(BusEnum.CONVERT_FILTER);
    if (filters == null) {
      filters = new ArrayList<>();
      threadLocalSet(BusEnum.CONVERT_FILTER, filters);
    }
    filters.add(convertFilter);
    filters.sort(Comparator.comparingInt(ConverterFilter::getOrder));
  }

  /**
   * 获取过滤器
   *
   * @return List 过滤器
   */
  public static <T extends InnerConverterFilter<?, ?>> List<T> getTempFilter() {
    Object result = threadLocalGet(BusEnum.CONVERT_FILTER);
    if (result == null) return null;
    return (List<T>) result;
  }

  /**
   * 设置当前 handler
   *
   * @param converterHandler converterHandler
   */
  public static void setCurrentHandler(ConverterHandler converterHandler) {
    threadLocalSet(BusEnum.CURRENT_CONVERTER_HANDLER, converterHandler);
  }

  /**
   * 获取当前 handler
   *
   * @return handler
   */
  public static ConverterHandler getCurrentHandler() {
    return (ConverterHandler) threadLocalGet(BusEnum.CURRENT_CONVERTER_HANDLER);
  }

  /**
   * 清空
   */
  public static void clearCurrentBusInfo() {
    CONVERTER_SHARE_HOLDER.remove();
  }

  /**
   * set
   *
   * @param key   key
   * @param value value
   */
  private static void threadLocalSet(BusEnum key, Object value) {
    if (CONVERTER_SHARE_HOLDER.get() == null) {
      CONVERTER_SHARE_HOLDER.set(new HashMap<>());
    }
    CONVERTER_SHARE_HOLDER.get().put(key, value);
  }

  /**
   * get
   *
   * @param key key
   * @return value
   */
  private static Object threadLocalGet(BusEnum key) {
    if (CONVERTER_SHARE_HOLDER.get() == null) return null;
    return CONVERTER_SHARE_HOLDER.get().get(key);
  }

}
