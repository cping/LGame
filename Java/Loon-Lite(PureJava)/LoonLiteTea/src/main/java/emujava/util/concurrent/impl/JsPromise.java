/*
 * Copyright 2016 Google Inc.
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
package emujava.util.concurrent.impl;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.platform.Platform;

/**
 * ECMA 6 Promise API.
 * See
 * <a href="https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/Promise">
 * MDN Promise documentation</a>.
 */
//@JsType(isNative = true, name = "Promise", namespace = JsPackage.GLOBAL)
public abstract class JsPromise implements JSObject {

    @JSBody(params = {"promises"}, script = "return Promise.all(promises);")
    public static native JsPromise all(JsPromise[] promises);

    @JSBody(params = {"promises"}, script = "return Promise.race(promises);")
    public static native JsPromise race(JsPromise[] promises);

    @JSBody(params = {"reason"}, script = "return Promise.reject(reason);")
    public static native JsPromise reject(JSObject reason);

    @JSBody(params = {"value"}, script = "return Promise.all(value);")
    public static native JsPromise resolve(JSObject value);

    @JSBody(params = {"executor"}, script = "return new Promise(executor);")
    public static native JsPromise create(Executor executor);

    public static JSObject asJsObject(Object javaObject) {
        return Platform.getPlatformObject(javaObject).cast();
    }

    public static <T> T asJavaObject(JSObject that) {
        return (T)that;
    }

    /*
     * Method has no return value for simplicity because the return value of the method,
     * onFulfilled and onRejected (OnSettledCallback) are not used.
     */
    // TODO: $entry ?
    @JSBody(params = {"onFulfilled", "onRejected"}, script = "then(onFulfilled, onRejected);")
    public native void then(OnSettledCallback onFulfilled,
                            OnSettledCallback onRejected);

    @JSFunctor
    interface Executor extends JSObject {
        void executor(Resolver resolve, Rejector reject);
    }

    @JSFunctor
    interface Resolver extends JSObject {
        void resolve(JSObject value);
    }

    @JSFunctor
    interface Rejector extends JSObject {
        void reject(JSObject reason);
    }

    /*
     * Single interface for onFulfilled and onRejected callbacks are used for simplicity.
     */
    @JSFunctor
    interface OnSettledCallback extends JSObject {
        void onSettled(JSObject value);
    }
}