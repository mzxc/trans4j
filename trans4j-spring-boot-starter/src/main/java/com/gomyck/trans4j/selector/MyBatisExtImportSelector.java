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

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyBatisExtImportSelector implements ImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    try {
      Class.forName("org.apache.ibatis.executor.resultset.ResultSetHandler");
      Class.forName("com.gomyck.trans4j.converter.persistent.mybatis.MyBatisConverter");
      return new String[]{"com.gomyck.trans4j.converter.persistent.mybatis.MyBatisConverter"};
    } catch (ClassNotFoundException e) {
      return new String[]{};
    }
  }

}
