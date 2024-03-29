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

package com.gomyck.trans4j.schedule;

import com.gomyck.trans4j.handler.dictionary.DicConverterHandler;
import com.gomyck.trans4j.handler.dictionary.DicConverterInitConditional;
import com.gomyck.trans4j.profile.Trans4JProfiles;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * 字典翻译定时任务
 *
 * @author gomyck
 * --------------------------------
 * | qq: 474798383                 |
 * | email: hao474798383@163.com   |
 * --------------------------------
 * @version [1.0.0]
 * @since 2022/12/12
 */
@Slf4j
@Component
@EnableScheduling
@AllArgsConstructor
@Conditional(DicConverterInitConditional.class)
public class DicConverterHandlerSchedule implements SchedulingConfigurer {

  private Trans4JProfiles trans4jProfiles;

  private DicConverterHandler dicConverterHandler;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    String cron = "0 */" + trans4jProfiles.getDic().getJob().getRefreshInterval() + " * * * *";
    log.info("registry refresh dic table info task on cron: {}...", cron);
    taskRegistrar.addCronTask(() -> {
      log.debug("refresh dic table info start...");
      dicConverterHandler.getInitDicInfoFunc().forEach(func -> {
        try {
          dicConverterHandler.init(func);
        } catch (Exception e) {
          log.error("dic schedule in error: {}", e.toString());
        }
      });
      log.debug("refresh dic table info end...");
    }, cron);
  }
}

