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
package java.nio;

import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Float64Array;
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;

class Numbers {
        static Int8Array wba = TypedArrays.createInt8Array(8);
        static Int32Array wia = TypedArrays.createInt32Array(wba.buffer(), 0, 2);
        static Float32Array wfa = TypedArrays.createFloat32Array(wba.buffer(), 0, 2);
        static Float64Array wda = TypedArrays.createFloat64Array(wba.buffer(), 0, 1);

        public static final int floatToIntBits(float f) {
                wfa.set(0, f);
                return wia.get(0);
        }

        public static final float intBitsToFloat(int i) {
                wia.set(0, i);
                return wfa.get(0);
        }

        public static final double longBitsToDouble(long i) {
          throw new RuntimeException("longBitsToDouble NYI");
        }

        public static final long doubleToRawLongBits(double i) {
          throw new RuntimeException("doubleToRawLongBits NYI");
        }

        // TODO(jgw): Ugly hack to avoid longs.
        public static final void setDouble(double d) {
          wda.set(0, d);
        }

        public static final double getDouble() {
          return wda.get(0);
        }

        public static final int getLoInt() {
          return wia.get(0);
        }

        public static final int getHiInt() {
          return wia.get(1);
        }

        public static final void setLoInt(int i) {
          wia.set(0, i);
        }

        public static final void setHiInt(int i) {
          wia.set(1, i);
        }
}
