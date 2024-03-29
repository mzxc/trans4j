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

package com.gomyck.trans4j.converter.mvc;

import com.gomyck.trans4j.handler.dictionary.serialize.AutoEncoder;
import com.gomyck.trans4j.support.ConverterType;
import com.gomyck.trans4j.support.TransBus;
import com.gomyck.util.serialize.CKJSON;

public class DefaultMvcAdivceDicAutoEncoder implements AutoEncoder {

  @Override
  public Object encode(Object input) {
    if(!TransBus.getConvertType().contains(ConverterType.RESPONSE_BODY_ADVICE_CONVERTER)) {
      return input;
    }
    return CKJSON.getInstance().toJsonMap(input);
  }

}
