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

/**
 * Emulation of Future. Since GWT environment is single threaded, attempting to block on the future
 * by calling {@link #get()} or {@link #get(long, emujava.util.concurrent.TimeUnit)} when it is not yet done is
 * considered illegal because it would lead to a deadlock. Future implementations must throw
 * {@link IllegalStateException} to avoid a deadlock.
 * See
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html">
 * the official Java API doc</a> for details.
 */
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();

    boolean isDone();

    V get() throws InterruptedException, emujava.util.concurrent.ExecutionException;

    V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
}