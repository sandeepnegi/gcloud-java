/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gcloud.storage;


/**
 * A base class for StorageService factories.
 */
public abstract class StorageServiceFactory {

  private static final StorageServiceFactory INSTANCE = new StorageServiceFactory() {
    @Override
    public StorageService get(StorageServiceOptions options) {
      return new StorageServiceImpl(options);
    }
  };

  /**
   * Returns the default factory instance.
   */
  public static StorageServiceFactory instance() {
    return INSTANCE;
  }

  /**
   * Returns a {@code StorageService} for the given options.
   */
  public abstract StorageService get(StorageServiceOptions options);
}
