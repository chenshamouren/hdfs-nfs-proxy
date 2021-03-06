/**
 * Copyright 2012 Cloudera Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cloudera.hadoop.hdfs.nfs.nfs4.state;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FSDataInputStream;

import com.google.common.annotations.VisibleForTesting;

public class HDFSInputStream extends InputStream {
  private final FSDataInputStream in;
  public HDFSInputStream(FSDataInputStream in) {
    this.in = in;
  }
  @Override
  public void close() throws IOException {
    in.close();
  }
  @VisibleForTesting
  public FSDataInputStream getFSDataInputStream() {
    return in;
  }
  /**
   * Read upto the specified number of bytes, from a given
   * position within a file, and return the number of bytes read. This does not
   * change the current offset of a file, and is thread-safe.
   */
  public int read(long position, byte[] buffer, int offset, int length)
      throws IOException {
    return in.read(position, buffer, offset, length);
  }
  public long getPos() throws IOException {
    return in.getPos();
  }

  @Override
  public int read() throws IOException {
    return in.read();
  }
  @Override
  public int read(byte[] buffer) throws IOException {
    return in.read(buffer);
  }
  public void seek(long desired) throws IOException {
    in.seek(desired);
  }
}
