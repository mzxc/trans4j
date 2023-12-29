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

package com.gomyck.trans4j.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

  private static final Map<Object, Object> MEM_CACHE = new ConcurrentHashMap<>();

  @Override
  public void setCache(K key, V param) {
    MEM_CACHE.put(key, param);
  }

  @Override
  public V getCache(String key) {
    return (V) MEM_CACHE.get(key);
  }

  @Override
  public boolean delCache(String key) {
    return MEM_CACHE.remove(key) != null;
  }

}
