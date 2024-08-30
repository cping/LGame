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

/**
 *
 */
final class PromisesImpl implements Promises {
    @Override
    public Promise<Void> allOf(Promise[] promises) {
        assert promises.length > 0;

        Promise<Void> andPromise = new PromiseImpl<>();
        BiConsumer<Object, Throwable> callback = new BiConsumer<Object, Throwable>() {
            int counter = promises.length;

            @Override
            public void accept(Object value, Throwable e) {
                if(e != null) {
                    andPromise.reject(e);
                }
                else if(--counter == 0) {
                    andPromise.resolve(null);
                }
            }
        };

        for(Promise<?> promise : promises) {
            promise.then(callback);
        }
        return andPromise;
    }

    @Override
    public Promise<Object> anyOf(Promise[] promises) {
        assert promises.length > 0;

        Promise<Object> orPromise = new PromiseImpl<>();
        BiConsumer<Object, Throwable> callback = (value, e) -> {
            if(e != null) {
                orPromise.reject(e);
            }
            else {
                orPromise.resolve(value);
            }
        };

        for(Promise<?> promise : promises) {
            promise.then(callback);
        }
        return orPromise;
    }

    @Override
    public <V> Promise<V> completed(V value) {
        PromiseImpl<V> promise = new PromiseImpl<>();
        promise.resolve(value);
        return promise;
    }

    @Override
    public <V> Promise<V> incomplete() {
        return new PromiseImpl<>();
    }
}