/*
 *
 *  * Copyright (c) 2019 Gomyck
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.gomyck.trans4j.converter.annotation;

import com.gomyck.trans4j.cache.MemCache4ResultConvert;
import com.gomyck.trans4j.support.ConverterType;
import com.gomyck.trans4j.support.TransBus;
import com.gomyck.util.ObjectJudge;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * 消息增强 for springmvc
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2019/08/14
 */
@Aspect
@Primary
@AllArgsConstructor
@Component("MVCMessageAdvice-origin")
public class MVCMessageAdvice {

  MemCache4ResultConvert memCache4ResultConvert;

  @Pointcut("@annotation(com.gomyck.trans4j.converter.annotation.TransEnhance)")
  public void pointCut() {}

  @Around("pointCut()")
  public Object aroundIt(ProceedingJoinPoint point) throws Throwable {
    String className = point.getTarget().getClass().getName();
    MethodSignature signature = (MethodSignature) point.getSignature();
    String methodName = signature.getName();
    String fullMethodName = MessageFormat.format("{0}.{1}()", className, methodName);
    TransEnhance annotation = memCache4ResultConvert.getCache(fullMethodName);
    if (annotation == null) {
      Method method = signature.getMethod();
      annotation = method.getAnnotation(TransEnhance.class);
      memCache4ResultConvert.setCache(fullMethodName, annotation);
    }
    if (annotation.overTurn()) TransBus.overturn();
    if (ObjectJudge.notNull(annotation.i18nFlag())) TransBus.setI18nFlag(annotation.i18nFlag());
    TransBus.convert(annotation.converterType());
    Object proceed;
    try {
      proceed = point.proceed();
    } catch (Exception e) {
      TransBus.clearCurrentBusInfo();
      throw e;
    }finally {
      if(!TransBus.getConvertType().contains(ConverterType.RESPONSE_MESSAGE_ENHANCE_CONVERTER)) TransBus.clearCurrentBusInfo();
    }
    return proceed;
  }
}
