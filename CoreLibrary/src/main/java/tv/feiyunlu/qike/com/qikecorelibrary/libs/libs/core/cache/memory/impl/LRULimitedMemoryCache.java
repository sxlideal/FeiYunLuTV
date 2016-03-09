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
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.impl;


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.AbstractLimitedMemoryCache;


/**
 * Limited {@link CacheEntry CacheEntry} cache. Provides {@link CacheEntry CacheEntrys} storing. Size of all stored CacheEntrys will not to
 * exceed size limit. When cache reaches limit size then the least recently used CacheEntry is deleted from cache.<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored CacheEntrys. Strong references - for limited count of
 * CacheEntrys (depends on cache size), weak references - for all other cached CacheEntrys.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.3.0
 */
public class LRULimitedMemoryCache extends AbstractLimitedMemoryCache<String, CacheEntry> {

	private static final int INITIAL_CAPACITY = 10;
	private static final float LOAD_FACTOR = 1.1f;

	/** Cache providing Least-Recently-Used logic */
	private final Map<String, CacheEntry> lruCache = Collections.synchronizedMap(new LinkedHashMap<String, CacheEntry>(INITIAL_CAPACITY, LOAD_FACTOR, true));

	/** @param maxSize Maximum sum of the sizes of the CacheEntrys in this cache */
	public LRULimitedMemoryCache(int maxSize) {
		super(maxSize);
	}

	@Override
	public boolean put(String key, CacheEntry value) {
		if (super.put(key, value)) {
			lruCache.put(key, value);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public CacheEntry get(String key) {
		lruCache.get(key); // call "get" for LRU logic
		return super.get(key);
	}

	@Override
	public void remove(String key) {
		lruCache.remove(key);
		super.remove(key);
	}

	@Override
	public void clear() {
		lruCache.clear();
		super.clear();
	}

	@Override
	protected int getSize(CacheEntry entry) {
	    return entry.bitmap.getRowBytes() * entry.bitmap.getHeight();
	}

	@Override
	protected CacheEntry removeNext() {
		CacheEntry mostLongUsedValue = null;
		synchronized (lruCache) {
			Iterator<Entry<String, CacheEntry>> it = lruCache.entrySet().iterator();
			if (it.hasNext()) {
				Entry<String, CacheEntry> entry = it.next();
				mostLongUsedValue = entry.getValue();
				it.remove();
			}
		}
		return mostLongUsedValue;
	}

	@Override
	protected Reference<CacheEntry> createReference(CacheEntry value) {
		return new WeakReference<CacheEntry>(value);
	}
}
