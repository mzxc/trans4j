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

package com.gomyck.trans4j.context;

import com.gomyck.trans4j.exception.TransInfoNotFoundException;
import com.gomyck.trans4j.handler.dictionary.DicConverterHandler;
import com.gomyck.util.PropertyAppender;

import java.util.Objects;

public class DicTransInfo {

  private DicTransInfo(){}

  public static Object get$V(Object obj, String fieldName) {
    if(Objects.isNull(obj) || Objects.isNull(fieldName)) {
      return null;
    }
    try {
      return PropertyAppender.getValue(obj, fieldName.concat(DicConverterHandler.V));
    } catch (Exception e) {
      throw new TransInfoNotFoundException("Entity not found fieldName: " + fieldName + ", Please insure TransEnhance annotation ConverterType is [PERSISTENT_CONVERTER] OR Insure fieldName is dictionary");
    }

  }

}
