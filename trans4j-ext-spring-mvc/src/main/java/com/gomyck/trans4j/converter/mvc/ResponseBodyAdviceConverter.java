
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

package com.gomyck.trans4j.converter.mvc;

import com.gomyck.trans4j.converter.Converter;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.support.ConverterType;
import com.gomyck.trans4j.support.TransBus;
import com.gomyck.util.log.logger.CkLogger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 消息增强
 * <p>
 * 该增强会导致消息转换器的一些特性失效: 比如 writeStringNullAsEmpty
 * <p>
 * 如果注入 massageConverter 按照 config 先转成 string 在转会 json, 也会有问题
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [gomyck-quickdev-1.0.0]
 * @since 2022/1/15 10:57
 */
@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class ResponseBodyAdviceConverter implements Converter, ResponseBodyAdvice<Object> {

  private ConverterHandlerComposite converterHandlerComposite;

  @Override
  public boolean supports(final MethodParameter returnType, final Class converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType, final Class selectedConverterType, final ServerHttpRequest request, final ServerHttpResponse response) {
    return doConvert(body);
  }

  @Override
  public Object doConvert(Object result) {
    try {
      if (TransBus.getConvertType().contains(ConverterType.RESPONSE_MESSAGE_ENHANCE_CONVERTER)) {
        result = converterHandlerComposite.handle(result);
      }
    } catch (Exception e) {
      log.debug("exception is {}", CkLogger.getTrace(e));
    }
    TransBus.clearCurrentBusInfo();
    return result;
  }
}
