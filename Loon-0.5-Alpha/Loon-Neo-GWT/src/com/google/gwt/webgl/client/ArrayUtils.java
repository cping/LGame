/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.webgl.client;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.typedarrays.client.JsUtils;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.typedarrays.shared.Uint16Array;
import com.google.gwt.typedarrays.shared.Uint8Array;

public class ArrayUtils {

  // TODO(jgw): Get rid of these conversions in web mode.
  public static JsArrayInteger toJsArray(byte[] data) {
    JsArrayInteger jsan = (JsArrayInteger) JsArrayInteger.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  public static JsArrayNumber toJsArray(double[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  public static JsArrayNumber toJsArray(float[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  public static JsArrayInteger toJsArray(int[] data) {
    JsArrayInteger jsan = (JsArrayInteger) JsArrayInteger.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  public static JsArrayInteger toJsArray(short[] data) {
    JsArrayInteger jsan = (JsArrayInteger) JsArrayInteger.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  public static JsArrayInteger toJsArrayUnsigned(byte[] data) {
    JsArrayInteger jsan = (JsArrayInteger) JsArrayInteger.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i] & 255);
    }
    return jsan;
  }

  public static JsArrayInteger toJsArrayUnsigned(short[] data) {
    JsArrayInteger jsan = (JsArrayInteger) JsArrayInteger.createArray();
    int len = data.length;
    for (int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i] & 65535);
    }
    return jsan;
  }

  private ArrayUtils() {
  }

  public static Float32Array createFloat32Array(float[] array) {
    return JsUtils.createFloat32Array(toJsArray(array));
  }

  public static Int32Array createInt32Array(int[] array) {
    return JsUtils.createInt32Array(toJsArray(array));
  }

  public static Uint16Array createUint16Array(short[] array) {
    return JsUtils.createUint16Array(toJsArrayUnsigned(array));
  }

  public static Uint16Array createUint16Array(int[] array) {
    return JsUtils.createUint16Array(toJsArray(array));
  }

  public static Uint8Array createUint8Array(byte[] array) {
    return JsUtils.createUint8Array(toJsArrayUnsigned(array));
  }

  public static Uint8Array createUint8Array(int[] array) {
    return JsUtils.createUint8Array(toJsArray(array));
  }
}
