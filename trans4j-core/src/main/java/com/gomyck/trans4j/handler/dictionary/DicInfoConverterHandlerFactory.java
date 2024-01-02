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

package com.gomyck.trans4j.handler.dictionary;

import com.gomyck.trans4j.filter.dictionary.DicI18NFilter;
import com.gomyck.trans4j.handler.ConverterHandler;
import com.gomyck.trans4j.handler.ConverterHandlerComposite;
import com.gomyck.trans4j.handler.ConverterHandlerFactory;
import com.gomyck.trans4j.handler.dictionary.serialize.DefaultDicAutoEncoder;
import com.gomyck.trans4j.profile.DicConfig;
import com.gomyck.trans4j.profile.Trans4JProfiles;
import com.gomyck.util.CkFile;
import com.gomyck.util.CkNetWork;
import com.gomyck.util.ObjectJudge;
import com.gomyck.util.serialize.CKJSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.TrustAllStrategy;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DicInfoConverterHandlerFactory implements ConverterHandlerFactory {

  @Setter
  private Trans4JProfiles trans4jProfiles;
  @Setter
  private DataSource dataSource;
  @Setter
  private ConverterHandlerComposite converterHandlerComposite;

  private boolean fileExist = true;

  private ConverterHandler dicConverterHandler;

  @Override
  public ConverterHandler getObject() {
    return dicConverterHandler;
  }

  @Override
  public Class<?> getObjectType() {
    return DicConverterHandler.class;
  }

  @Override
  public void afterPropertiesSet() {
    DicConfig.CkDicAdaptorConfig ckDicAdaptorConfig = trans4jProfiles.getDic().getAdaptor();
    final DicConverterHandler dicConverterHandler = getDicConverterHandler(ckDicAdaptorConfig);
    initHandlerByDatabase(ckDicAdaptorConfig, dicConverterHandler);
    initHandlerByRestServer(ckDicAdaptorConfig, dicConverterHandler);
    initHandlerByFile(ckDicAdaptorConfig, dicConverterHandler);
    dicConverterHandler.refresh();
    this.dicConverterHandler = dicConverterHandler;
  }

  private void initHandlerByFile(DicConfig.CkDicAdaptorConfig ckDicAdaptorConfig, DicConverterHandler dicConverterHandler) {
    final String initDicFile = ckDicAdaptorConfig.getInitDicFile();
    dicConverterHandler.init(handler -> {
      if (!fileExist) return null;
      try {
        final byte[] file = CkFile.getFile(initDicFile);
        String dicInfo = new String(file);
        try {
          final List<Map<String, Object>> maps = CKJSON.getInstance().parseListMap(dicInfo);
          log.info(MessageFormat.format("init dic info with file: {0}, result size is: {1}", initDicFile, maps.size()));
          return maps;
        } catch (Exception e) {
          log.error("dicFile format error(must be json array), please check your file,  error is: ", e);
          return null;
        }
      } catch (Exception e) {
        fileExist = false;
        if (DicConfig.CkDicAdaptorConfig.DEFAULT_DIC_INFO_FILE_NAME.equals(initDicFile)) {
          log.warn("default dic file is not found.");
        } else {
          log.error("dicFile is not found, please check your file path.");
        }
      }
      return null;
    });
  }

  private static void initHandlerByRestServer(DicConfig.CkDicAdaptorConfig ckDicAdaptorConfig, DicConverterHandler dicConverterHandler) {
    final List<String> initDicUrl = ckDicAdaptorConfig.getInitDicUrl();
    if (ObjectJudge.isNull(initDicUrl)) return;
    dicConverterHandler.init(handler -> {
      CkNetWork ckNetWork = CkNetWork.init(TrustAllStrategy.INSTANCE);
      final List<Map<String, Object>> dicInfo = new ArrayList<>();
      initDicUrl.forEach(url -> {
        try {
          final String s = ckNetWork.doGet(url);
          List<Map<String, Object>> result = CKJSON.getInstance().parseListMap(s);
          log.info(MessageFormat.format("init dic info with url: {0}, result size is: {1}", url, result.size()));
          dicInfo.addAll(result);
        } catch (IOException e) {
          throw new RuntimeException(MessageFormat.format("init dic info error, url is: {0}, error is: {1}", url, e));
        }
      });
      return dicInfo;
    });
  }

  private void initHandlerByDatabase(DicConfig.CkDicAdaptorConfig ckDicAdaptorConfig, DicConverterHandler dicConverterHandler) {
    final List<String> initDicSql = ckDicAdaptorConfig.getInitDicSql();
    if (ObjectJudge.isNull(initDicSql)) return;
    if (dataSource == null) throw new RuntimeException("init dic info error, please declare a datasource on your server or delete init sql array in the yaml file");
    dicConverterHandler.init(handler -> {
      final List<Map<String, Object>> dicInfo = new ArrayList<>();
      initDicSql.forEach(sql -> {
        try (final PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql); final ResultSet resultSet = preparedStatement.executeQuery()) {
          final List<Map<String, Object>> result = convertDicInfo2ListMap(resultSet);
          log.info(MessageFormat.format("init dic info with sql: {0}, result size is: {1}", sql, result.size()));
          dicInfo.addAll(result);
        } catch (SQLException e) {
          throw new RuntimeException(MessageFormat.format("init dic info error, sql is: {0}, error is: {1}", sql, e));
        }
      });
      return dicInfo;
    });
  }

  private DicConverterHandler getDicConverterHandler(DicConfig.CkDicAdaptorConfig ckDicAdaptorConfig) {
    final boolean ifOpenI18N = ObjectJudge.notNull(ckDicAdaptorConfig.getI18n());
    DicDescribeAdaptor initDicAdaptor;
    if (ifOpenI18N) {
      initDicAdaptor = DicDescribeAdaptor.initAdaptor(ckDicAdaptorConfig.getCode(), ckDicAdaptorConfig.getValue(), ckDicAdaptorConfig.getColumnName(), ckDicAdaptorConfig.getI18n(), ckDicAdaptorConfig.getDefaultI18nFlag());
    } else {
      initDicAdaptor = DicDescribeAdaptor.initAdaptor(ckDicAdaptorConfig.getCode(), ckDicAdaptorConfig.getValue(), ckDicAdaptorConfig.getColumnName());
    }
    DicConverterHandler dicConverterHandler = new DicConverterHandler(initDicAdaptor, new DefaultDicAutoEncoder(), converterHandlerComposite);
    if (ifOpenI18N) {
      DicI18NFilter dicI18NFilter = new DicI18NFilter();
      dicConverterHandler.addInnerFilter(dicI18NFilter);
    }
    return dicConverterHandler;
  }

  private List<Map<String, Object>> convertDicInfo2ListMap(ResultSet rs) throws SQLException {
    List<Map<String, Object>> list = new ArrayList<>();
    ResultSetMetaData md = rs.getMetaData();
    int columnCount = md.getColumnCount();
    while (rs.next()) {
      Map<String, Object> rowData = new HashMap<>();
      for (int i = 1; i <= columnCount; i++) {
        final String columnLabel = md.getColumnLabel(i);
        if (ObjectJudge.notNull(columnLabel)) {
          rowData.put(columnLabel, rs.getObject(i));
        } else {
          rowData.put(md.getColumnName(i), rs.getObject(i));
        }
      }
      list.add(rowData);
    }
    return list;
  }

}
