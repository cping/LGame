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
package emujava.util.concurrent;

import static emujava.util.concurrent.CompletableFutureUtils.checkNotNull;

/**
 * Emulation of executors.
 */
public class Executors {

    public static <T> emujava.util.concurrent.Callable<T> callable(Runnable task, T result) {
        return new RunnableAdapter<>(task, result);
    }

    public static emujava.util.concurrent.Callable<Object> callable(Runnable task) {
        return callable(task, null);
    }

    private static class RunnableAdapter<T> implements Callable<T> {

        private final Runnable task;
        private final T result;

        private RunnableAdapter(Runnable task, T result) {
            this.task = checkNotNull(task);
            this.result = result;
        }

        @Override
        public T call() {
            task.run();
            return result;
        }
    }

    private Executors() {
    }
}