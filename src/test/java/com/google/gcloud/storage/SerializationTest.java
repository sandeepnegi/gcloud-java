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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import com.google.gcloud.AuthCredentials;
import com.google.gcloud.RetryParams;
import com.google.gcloud.storage.Acl.Project.ProjectRole;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;

public class SerializationTest {

  private static final Acl.Domain ACL_DOMAIN = new Acl.Domain("domain");
  private static final Acl.Group ACL_GROUP = new Acl.Group("group");
  private static final Acl.Project ACL_PROJECT_ = new Acl.Project(ProjectRole.VIEWERS, "pid");
  private static final Acl.User ACL_USER = new Acl.User("user");
  private static final Acl.RawEntity ACL_RAW = new Acl.RawEntity("raw");
  private static final Blob BLOB = Blob.of("b", "n");
  private static final Bucket BUCKET = Bucket.of("b");
  private static final Cors.Origin ORIGIN = Cors.Origin.any();
  private static final Cors CORS =
      Cors.builder().maxAgeSeconds(1).origins(Collections.singleton(ORIGIN)).build();
  private static final BatchRequest BATCH_REQUEST = BatchRequest.builder().delete("B", "N").build();
  private static final BatchResponse BATCH_RESPONSE = new BatchResponse(
      Collections.singletonList(new BatchResponse.Result<>(true)),
      Collections.<BatchResponse.Result<Blob>>emptyList(),
      Collections.<BatchResponse.Result<Blob>>emptyList());
  private static final ListResult<Blob> LIST_RESULT =
      new ListResult<>("c", Collections.singletonList(Blob.of("b", "n")));
  private static StorageService.BlobListOption BLOB_LIST_OPTIONS =
      StorageService.BlobListOption.maxResults(100);
  private static StorageService.BlobSourceOption BLOB_SOURCE_OPTIONS =
      StorageService.BlobSourceOption.generationMatch(1);
  private static StorageService.BlobTargetOption BLOB_TARGET_OPTIONS =
      StorageService.BlobTargetOption.generationMatch();
  private static StorageService.BucketListOption BUCKET_LIST_OPTIONS =
      StorageService.BucketListOption.prefix("bla");
  private static StorageService.BucketSourceOption BUCKET_SOURCE_OPTIONS =
      StorageService.BucketSourceOption.metagenerationMatch(1);
  private static StorageService.BucketTargetOption BUCKET_TARGET_OPTIONS =
      StorageService.BucketTargetOption.metagenerationNotMatch();

  @Test
  public void testServiceOptions() throws Exception {
    StorageServiceOptions options = StorageServiceOptions.builder()
        .projectId("p1")
        .authCredentials(AuthCredentials.createForAppEngine())
        .build();
    StorageServiceOptions serializedCopy = serializeAndDeserialize(options);
    assertEquals(options, serializedCopy);

    options = options.toBuilder()
        .projectId("p2")
        .retryParams(RetryParams.getDefaultInstance())
        .authCredentials(AuthCredentials.noCredentials())
        .pathDelimiter(":")
        .build();
    serializedCopy = serializeAndDeserialize(options);
    assertEquals(options, serializedCopy);
  }

  @Test
  public void testModelAndRequests() throws Exception {
    Serializable[] objects = {ACL_DOMAIN, ACL_GROUP, ACL_PROJECT_, ACL_USER, ACL_RAW, BLOB, BUCKET,
      ORIGIN, CORS, BATCH_REQUEST,BATCH_RESPONSE, LIST_RESULT, BLOB_LIST_OPTIONS,
        BLOB_SOURCE_OPTIONS, BLOB_TARGET_OPTIONS, BUCKET_LIST_OPTIONS, BUCKET_SOURCE_OPTIONS,
        BUCKET_TARGET_OPTIONS};
    for (Serializable obj : objects) {
      Object copy = serializeAndDeserialize(obj);
      assertEquals(obj, obj);
      assertEquals(obj, copy);
      assertNotSame(obj, copy);
      assertEquals(copy, copy);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends java.io.Serializable> T serializeAndDeserialize(T obj)
      throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
      output.writeObject(obj);
    }
    try (ObjectInputStream input =
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      return (T) input.readObject();
    }
  }
}
