/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.arrow.gandiva.evaluator;

import org.apache.arrow.vector.complex.ListVector;

/**
 * This class provides the functionality to expand output ListVectors using a callback mechanism from
 * gandiva.
 */
public class ListVectorExpander {
  private final ListVector[] bufferVectors;
  public static final int valueBufferIndex = 1;
  public static final int validityBufferIndex = 0;

  public ListVectorExpander(ListVector[] bufferVectors) {
    this.bufferVectors = bufferVectors;
  }

  /**
   * Result of ListVector expansion.
   */
  public static class ExpandResult {
    public long address;
    public long capacity;
    public long validityaddress;

    /**
     * Result of expanding the buffer.
     * @param address Data buffer address
     * @param capacity Capacity
     * @param validAdd Validity buffer address
     * 
     */
    public ExpandResult(long address, long capacity, long validAdd) {
      this.address = address;
      this.capacity = capacity;
      this.validityaddress = validAdd;
    }
  }

  /**
   * Expand vector at specified index. This is used as a back call from jni, and is only
   * relevant for ListVectors.
   *
   * @param index index of buffer in the list passed to jni.
   * @param toCapacity the size to which the buffer should be expanded to.
   *
   * @return address and size  of the buffer after expansion.
   */
  public ExpandResult expandOutputVectorAtIndex(int index, long toCapacity) {
    if (index >= bufferVectors.length || bufferVectors[index] == null) {
      throw new IllegalArgumentException("invalid index " + index);
    }

    ListVector vector = bufferVectors[index];
    while (vector.getDataVector().getFieldBuffers().get(ListVectorExpander.valueBufferIndex).capacity() < toCapacity) {
      //Just realloc the data vector.
      vector.getDataVector().reAlloc();
    }
    
    return new ExpandResult(
        vector.getDataVector().getFieldBuffers().get(ListVectorExpander.valueBufferIndex).memoryAddress(),
        vector.getDataVector().getFieldBuffers().get(ListVectorExpander.valueBufferIndex).capacity(),
        vector.getDataVector().getFieldBuffers().get(ListVectorExpander.validityBufferIndex).memoryAddress());
  }

}
