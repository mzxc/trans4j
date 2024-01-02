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

package com.gomyck.trans4j.handler.dictionary;

import com.gomyck.trans4j.profile.Trans4JProfiles;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DicConverterInitConditional implements Condition {

  public static final String DIC_CONVERTER_INIT_CONDITIONAL = Trans4JProfiles.CONVERTER_PREFIX + ".dic.enabled";

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String dicEnabled = context.getEnvironment().getProperty(DIC_CONVERTER_INIT_CONDITIONAL);
    return dicEnabled == null || "true".equalsIgnoreCase(dicEnabled);
  }
}
