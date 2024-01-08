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

package com.gomyck.trans4j.selector;

import com.gomyck.trans4j.converter.SimpleDefaultConverter;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.profile.DicConfig;
import com.gomyck.trans4j.profile.SecureConfig;
import com.gomyck.trans4j.profile.Trans4JProfiles;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class Trans4JCoreImportSelector implements ImportSelector {

  @Bean
  @ConfigurationProperties(prefix = Trans4JProfiles.CONVERTER_PREFIX, ignoreInvalidFields = true)
  public Trans4JProfiles initTrans4JProfiles() {
    return new Trans4JProfiles();
  }

  @Bean
  @ConfigurationProperties(prefix = DicConfig.DIC_CONFIG_PREFIX, ignoreInvalidFields = true)
  public DicConfig initDicConfig() {
    return new DicConfig();
  }

  @Bean
  @ConfigurationProperties(prefix = SecureConfig.SECURE_CONFIG_PREFIX, ignoreInvalidFields = true)
  public SecureConfig initSecureConfig() {
    return new SecureConfig();
  }

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return new String[0];
  }

  @Bean
  public SimpleDefaultConverter initSimpleDefaultConverter(ConverterHandlerComposite converterHandlerComposite) {
    return new SimpleDefaultConverter(converterHandlerComposite);
  }

}
