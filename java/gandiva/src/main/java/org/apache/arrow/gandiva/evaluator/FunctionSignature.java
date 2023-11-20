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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.List;
import org.apache.arrow.vector.types.pojo.ArrowType;

/** POJO to define a function signature. */
public class FunctionSignature {
  private final String name;
  private final ArrowType returnType;
  private final ArrowType returnListType;
  private final List<List<ArrowType>> paramTypes;

  public ArrowType getReturnType() {
    return returnType;
  }

  public ArrowType getReturnListType() {
    return returnListType;
  }

  public List<List<ArrowType>> getParamTypes() {
    return paramTypes;
  }

  public String getName() {
    return name;
  }

  /**
   * Ctor.
   *
   * @param name - name of the function.
   * @param returnType - data type of return
   * @param returnListType optional list type
   * @param paramTypes - data type of input args.
   */
  public FunctionSignature(String name, ArrowType returnType, ArrowType returnListType, 
      List<List<ArrowType>> paramTypes) {
    this.name = name;
    this.returnType = returnType;
    this.returnListType = returnListType;
    this.paramTypes = paramTypes;
  }

  /**
   * Ctor.
   * @param name - name of the function.
   * @param returnType - data type of return
   * @param paramTypes - data type of input args.
   */
  public FunctionSignature(String name, ArrowType returnType, List<ArrowType> paramTypes) {
    this.name = name;
    this.returnType = returnType;
    this.returnListType = ArrowType.Null.INSTANCE;
    this.paramTypes = new ArrayList<List<ArrowType>>();
    for (ArrowType paramType : paramTypes) {
      List<ArrowType> paramArrowList = new ArrayList<ArrowType>();
      paramArrowList.add(paramType);
      this.paramTypes.add(paramArrowList);
    }
    
  }

  /**
   * Override equals.
   *
   * @param signature - signature to compare
   * @return true if equal and false if not.
   */
  public boolean equals(Object signature) {
    if (signature == null) {
      return false;
    }
    if (getClass() != signature.getClass()) {
      return false;
    }
    final FunctionSignature other = (FunctionSignature) signature;
    return this.name.equalsIgnoreCase(other.name)
        && Objects.equal(this.returnType, other.returnType)
        && Objects.equal(this.paramTypes, other.paramTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name.toLowerCase(), this.returnType, this.returnListType, this.paramTypes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name ", name)
        .add("return type ", returnType)
        .add("param types ", paramTypes)
        .toString();
  }
}
