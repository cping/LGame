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

import java.util.function.BiConsumer;
import org.teavm.jso.JSObject;

/**
 *
 */
final class NativePromiseImpl<V> implements Promise<V> {

    final JsPromise jsPromise;
    private JsPromise.Resolver resolver;
    private JsPromise.Rejector rejector;

    NativePromiseImpl() {
        jsPromise = JsPromise.create((resolve, reject) -> {
            resolver = resolve;
            rejector = reject;
        });
    }

    NativePromiseImpl(JsPromise promise) {
        assert promise != null;
        this.jsPromise = promise;
    }

    @Override
    public void resolve(V value) {
        JSObject jsObject = JsPromise.asJsObject(value);
        resolver.resolve(jsObject);
    }

    @Override
    public void reject(Throwable reason) {
        assert reason != null;
        JSObject jsObject = JsPromise.asJsObject(reason);
        rejector.reject(jsObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void then(BiConsumer<? super V, ? super Throwable> callback) {
        assert callback != null;

        JsPromise.OnSettledCallback fullFilled = new JsPromise.OnSettledCallback() {
            @Override
            public void onSettled(JSObject reason) {
                Object obj = JsPromise.asJavaObject(reason);
                callback.accept((V)obj, null);
            }
        };

        JsPromise.OnSettledCallback onRejected = new JsPromise.OnSettledCallback() {
            @Override
            public void onSettled(JSObject value) {
                Object obj = JsPromise.asJavaObject(value);
                callback.accept(null, (Throwable)obj);
            }
        };

        jsPromise.then(fullFilled, onRejected);
    }

    @Override
    public void then(Runnable callback) {
        assert callback != null;
        JsPromise.OnSettledCallback func = value -> callback.run();
        jsPromise.then(func, func);
    }
}