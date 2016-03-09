/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory;

import java.util.Collection;

/**
 * 
 *<p>内存缓存功能接口</p><br/>
 *<p>拥有一个</p>
 * @since 1.0.0
 * @author bigpie
 * @param <K>
 * @param <V>
 */
public interface IMemoryCache<K, V> {
    
	boolean put(K key, V value);

	V get(K key);

	void remove(K key);

	Collection<K> keys();

	void clear();
}
