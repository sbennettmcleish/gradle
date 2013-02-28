/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.changedetection;

import org.gradle.cache.PersistentIndexedCache;

import java.security.SecureRandom;

public class CacheBackedFileSnapshotRepository implements FileSnapshotRepository {
    private final PersistentIndexedCache<Object, Object> cache;
    private SecureRandom random;

    public CacheBackedFileSnapshotRepository(TaskArtifactStateCacheAccess cacheAccess) {
        cache = cacheAccess.createCache("fileSnapshots", Object.class, Object.class);
        random = new SecureRandom();
    }

    public Long add(FileCollectionSnapshot snapshot) {
        return random.nextLong();
    }

    public FileCollectionSnapshot get(Long id) {
        return (FileCollectionSnapshot) cache.get(id);
    }

    public void remove(Long id) {
        cache.remove(id);
    }
}
