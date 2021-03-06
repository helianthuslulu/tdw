/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.serde2.typeinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.io.IntWritable;

public class TypeInfoFactory {

  static ConcurrentHashMap<String, TypeInfo> cachedPrimitiveTypeInfo = new ConcurrentHashMap<String, TypeInfo>();

  public static TypeInfo getPrimitiveTypeInfo(String typeName) {
    if (null == PrimitiveObjectInspectorUtils
        .getTypeEntryFromTypeName(typeName)) {
      throw new RuntimeException("Cannot getPrimitiveTypeInfo for " + typeName);
    }
    TypeInfo result = cachedPrimitiveTypeInfo.get(typeName);
    if (result == null) {
      result = new PrimitiveTypeInfo(typeName);
      cachedPrimitiveTypeInfo.put(typeName, result);
    }
    return result;
  }

  public static final TypeInfo voidTypeInfo = getPrimitiveTypeInfo(Constants.VOID_TYPE_NAME);
  public static final TypeInfo booleanTypeInfo = getPrimitiveTypeInfo(Constants.BOOLEAN_TYPE_NAME);
  public static final TypeInfo intTypeInfo = getPrimitiveTypeInfo(Constants.INT_TYPE_NAME);
  public static final TypeInfo longTypeInfo = getPrimitiveTypeInfo(Constants.BIGINT_TYPE_NAME);
  public static final TypeInfo stringTypeInfo = getPrimitiveTypeInfo(Constants.STRING_TYPE_NAME);
  public static final TypeInfo floatTypeInfo = getPrimitiveTypeInfo(Constants.FLOAT_TYPE_NAME);
  public static final TypeInfo doubleTypeInfo = getPrimitiveTypeInfo(Constants.DOUBLE_TYPE_NAME);
  public static final TypeInfo byteTypeInfo = getPrimitiveTypeInfo(Constants.TINYINT_TYPE_NAME);
  public static final TypeInfo shortTypeInfo = getPrimitiveTypeInfo(Constants.SMALLINT_TYPE_NAME);
  public static final TypeInfo timestampTypeInfo = getPrimitiveTypeInfo(Constants.TIMESTAMP_TYPE_NAME);

  public static final TypeInfo unknownTypeInfo = getPrimitiveTypeInfo("unknown");

  public static TypeInfo getPrimitiveTypeInfoFromPrimitiveWritable(
      Class<?> clazz) {
    String typeName = PrimitiveObjectInspectorUtils
        .getTypeNameFromPrimitiveWritable(clazz);
    if (typeName == null) {
      throw new RuntimeException("Internal error: Cannot get typeName for "
          + clazz);
    }
    return getPrimitiveTypeInfo(typeName);
  }

  public static TypeInfo getPrimitiveTypeInfoFromJavaPrimitive(Class<?> clazz) {
    return getPrimitiveTypeInfo(PrimitiveObjectInspectorUtils
        .getTypeNameFromPrimitiveJava(clazz));
  }

  static ConcurrentHashMap<ArrayList<List<?>>, TypeInfo> cachedStructTypeInfo = new ConcurrentHashMap<ArrayList<List<?>>, TypeInfo>();

  public static TypeInfo getStructTypeInfo(List<String> names,
      List<TypeInfo> typeInfos) {
    ArrayList<List<?>> signature = new ArrayList<List<?>>(2);
    signature.add(names);
    signature.add(typeInfos);
    TypeInfo result = cachedStructTypeInfo.get(signature);
    if (result == null) {
      result = new StructTypeInfo(names, typeInfos);
      cachedStructTypeInfo.put(signature, result);
    }
    return result;
  }

  static ConcurrentHashMap<TypeInfo, TypeInfo> cachedListTypeInfo = new ConcurrentHashMap<TypeInfo, TypeInfo>();

  public static TypeInfo getListTypeInfo(TypeInfo elementTypeInfo) {
    TypeInfo result = cachedListTypeInfo.get(elementTypeInfo);
    if (result == null) {
      result = new ListTypeInfo(elementTypeInfo);
      cachedListTypeInfo.put(elementTypeInfo, result);
    }
    return result;
  }
  

  static TypeInfo cachedByteArrayTypeInfo = new ByteArrayTypeInfo();
  public static TypeInfo getByteArrayTypeInfo() {
    return cachedByteArrayTypeInfo;
  }

  static ConcurrentHashMap<ArrayList<TypeInfo>, TypeInfo> cachedMapTypeInfo = new ConcurrentHashMap<ArrayList<TypeInfo>, TypeInfo>();

  public static TypeInfo getMapTypeInfo(TypeInfo keyTypeInfo,
      TypeInfo valueTypeInfo) {
    ArrayList<TypeInfo> signature = new ArrayList<TypeInfo>(2);
    signature.add(keyTypeInfo);
    signature.add(valueTypeInfo);
    TypeInfo result = cachedMapTypeInfo.get(signature);
    if (result == null) {
      result = new MapTypeInfo(keyTypeInfo, valueTypeInfo);
      cachedMapTypeInfo.put(signature, result);
    }
    return result;
  };

  static HashMap<List<?>, TypeInfo> cachedUnionTypeInfo = new HashMap<List<?>, TypeInfo>();

  public static TypeInfo getUnionTypeInfo(List<TypeInfo> typeInfos) {
    TypeInfo result = cachedUnionTypeInfo.get(typeInfos);
    if (result == null) {
      result = new UnionTypeInfo(typeInfos);
      cachedUnionTypeInfo.put(typeInfos, result);
    }
    return result;
  }

}
