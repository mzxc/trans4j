
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

package com.gomyck.trans4j.converter.rest;

import com.gomyck.trans4j.converter.annotation.TransEnhance;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.handler.dictionary.DicInfoHolder;
import com.gomyck.util.ObjectJudge;
import com.gomyck.util.servlet.R;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 结果转换 rest 服务, 有局限性: 字典不能用 filter
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2023/11/3
 */
@RestController
@AllArgsConstructor
@RequestMapping("ckConverter")
public class RestConverter {

  private ConverterHandlerComposite converterHandlerComposite;

  @PostMapping(value = "doConverter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @TransEnhance
  public R converter(@RequestBody Map<String, Object> jsonData) {
    return R.ok(jsonData);
  }

  @GetMapping("getDicInfo")
  public R getDicInfo(String typeCode) {
    if (ObjectJudge.notNull(DicInfoHolder.DIC_INFO, typeCode)) return R.ok(DicInfoHolder.DIC_INFO.get(typeCode.toUpperCase()));
    return R.ok(DicInfoHolder.DIC_INFO);
  }

  @GetMapping("getDicInfoOverturn")
  public R getDicInfoOverturn(String typeCode) {
    if (ObjectJudge.notNull(DicInfoHolder.DIC_INFO_OVERTURN, typeCode)) return R.ok(DicInfoHolder.DIC_INFO_OVERTURN.get(typeCode.toUpperCase()));
    return R.ok(DicInfoHolder.DIC_INFO_OVERTURN);
  }

}
