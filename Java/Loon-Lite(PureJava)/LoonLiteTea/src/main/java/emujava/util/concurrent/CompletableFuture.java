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
import emujava.util.concurrent.impl.DeferredExecutor;
import emujava.util.concurrent.impl.Impl;
import emujava.util.concurrent.impl.Promise;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Emulation of CompletableFuture.
 * See
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">
 * the official Java API doc</a> for details.
 */
public class CompletableFuture<T> implements Future<T>, emujava.util.concurrent.CompletionStage<T> {

    public interface AsynchronousCompletionTask {
    }

    public static CompletableFuture<Void> runAsync(Runnable action) {
        return runAsync(action, DEFAULT_EXECUTOR);
    }

    public static CompletableFuture<Void> runAsync(Runnable action, emujava.util.concurrent.Executor executor) {
        return supplyAsync(() -> {
            action.run();
            return null;
        }, executor);
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return supplyAsync(supplier, DEFAULT_EXECUTOR);
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier, emujava.util.concurrent.Executor executor) {
        checkNotNull(supplier);
        checkNotNull(executor);

        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                future.tryCompleteValue(supplier.get());
            }
            catch(Throwable t) {
                future.tryCompleteThrowable(t);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> completedFuture(T value) {
        return new CompletableFuture<>(value, null);
    }

    public static CompletableFuture<Void> allOf(CompletableFuture<?>... futures) {
        if(futures.length == 0) {
            return completedFuture(null);
        }

        CompletableFuture<?> completedFuture = null;
        for(CompletableFuture<?> future : futures) {
            if(!future.isDone()) {
                completedFuture = null;
                break;
            }
            if(completedFuture == null) {
                completedFuture = future;
            }
        }
        if(completedFuture != null) {
            return new CompletableFuture<>(null, completedFuture.reason);
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        and(futures).then((value, reason) -> {
            if(reason != null) {
                future.tryCompleteThrowable(reason);
            }
            else {
                future.tryCompleteValue(null);
            }
        });
        return future;
    }

    public static CompletableFuture<Object> anyOf(CompletableFuture<?>... futures) {
        if(futures.length == 0) {
            return new CompletableFuture<>();
        }

        for(CompletableFuture<?> future : futures) {
            if(future.isDone()) {
                return new CompletableFuture<>(future.value, future.reason);
            }
        }

        CompletableFuture<Object> future = new CompletableFuture<>();
        or(futures).then((value, reason) -> {
            if(reason != null) {
                future.tryCompleteThrowable(reason);
            }
            else {
                future.tryCompleteValue(value);
            }
        });
        return future;
    }

    private static Promise<Void> and(CompletableFuture<?>... futures) {
        return Impl.IMPL.allOf(toPromises(futures));
    }

    private static Promise<Object> or(CompletableFuture<?>... futures) {
        return Impl.IMPL.anyOf(toPromises(futures));
    }

    private static Promise[] toPromises(CompletableFuture<?>... futures) {
        int length = futures.length;
        Promise[] promises = new Promise[length];
        for(int i = 0; i < length; i++) {
            promises[i] = futures[i].promise;
        }
        return promises;
    }

    private static <T> BiConsumer<? super T, ? super Throwable> runAsync(
            emujava.util.concurrent.Executor executor, BiConsumer<? super T, ? super Throwable> action) {

        if(executor == null) {
            return action;
        }
        else {
            return (r, e) -> executor.execute(() -> action.accept(r, e));
        }
    }

    private static final emujava.util.concurrent.Executor DEFAULT_EXECUTOR = new DeferredExecutor();

    private final Promise<T> promise;
    private boolean done;
    private T value;
    private Throwable reason;

    public CompletableFuture() {
        promise = Impl.IMPL.incomplete();
    }

    private CompletableFuture(T value, Throwable reason) {
        this();
        completeStage(value, reason); // TODO
    }

    @Override
    public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) {
        return thenApplyAsync0(fn, null);
    }

    @Override
    public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return thenApplyAsync0(fn, DEFAULT_EXECUTOR);
    }

    @Override
    public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return thenApplyAsync0(fn, executor);
    }

    private <U> CompletableFuture<U> thenApplyAsync0(Function<? super T, ? extends U> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(fn);
        CompletableFuture<U> future = new CompletableFuture<>();
        onStageComplete((r, e) -> {
            if(e != null) {
                future.tryCompleteThrowable(e);
            }
            else {
                try {
                    future.tryCompleteValue(fn.apply(r));
                }
                catch(Throwable ex) {
                    future.tryCompleteThrowable(ex);
                }
            }
        }, executor);
        return future;
    }

    @Override
    public CompletableFuture<Void> thenAccept(Consumer<? super T> action) {
        return thenAcceptAsync0(action, null);
    }

    @Override
    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
        return thenAcceptAsync0(action, DEFAULT_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return thenAcceptAsync0(action, executor);
    }

    private CompletableFuture<Void> thenAcceptAsync0(Consumer<? super T> action, emujava.util.concurrent.Executor executor) {
        checkNotNull(action);
        return thenApplyAsync0((r) -> {
            action.accept(r);
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> thenRun(Runnable action) {
        return thenRunAsync0(action, null);
    }

    @Override
    public CompletableFuture<Void> thenRunAsync(Runnable action) {
        return thenRunAsync0(action, DEFAULT_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> thenRunAsync(Runnable action, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return thenRunAsync0(action, executor);
    }

    private CompletableFuture<Void> thenRunAsync0(Runnable action, emujava.util.concurrent.Executor executor) {
        checkNotNull(action);
        return thenApplyAsync0((r) -> {
            action.run();
            return null;
        }, executor);
    }

    @Override
    public <U, V> CompletableFuture<V> thenCombine(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                   BiFunction<? super T, ? super U, ? extends V> fn) {

        return thenCombineAsync0(other, fn, null);
    }

    @Override
    public <U, V> CompletableFuture<V> thenCombineAsync(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                        BiFunction<? super T, ? super U, ? extends V> fn) {

        return thenCombineAsync0(other, fn, DEFAULT_EXECUTOR);
    }

    @Override
    public <U, V> CompletableFuture<V> thenCombineAsync(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                        BiFunction<? super T, ? super U, ? extends V> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return thenCombineAsync0(other, fn, executor);
    }

    private <U, V> CompletableFuture<V> thenCombineAsync0(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                          BiFunction<? super T, ? super U, ? extends V> fn, emujava.util.concurrent.Executor executor) {

        checkNotNull(fn);
        CompletableFuture<V> future = new CompletableFuture<>();
        CompletableFuture<T> first = this;
        CompletableFuture<? extends U> second = other.toCompletableFuture();
        and(first, second).then(runAsync(executor, (ignored, e) -> {
            if(e != null) {
                future.tryCompleteThrowable(e);
            }
            else {
                try {
                    future.tryCompleteValue(fn.apply(first.get(), second.get()));
                }
                catch(Throwable ex) {
                    future.tryCompleteThrowable(ex);
                }
            }
        }));
        return future;
    }

    @Override
    public <U> CompletableFuture<Void> thenAcceptBoth(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                      BiConsumer<? super T, ? super U> action) {

        return thenAcceptBothAsync0(other, action, null);
    }

    @Override
    public <U> CompletableFuture<Void> thenAcceptBothAsync(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                           BiConsumer<? super T, ? super U> action) {

        return thenAcceptBothAsync0(other, action, DEFAULT_EXECUTOR);
    }

    @Override
    public <U> CompletableFuture<Void> thenAcceptBothAsync(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                           BiConsumer<? super T, ? super U> action, emujava.util.concurrent.Executor executor) {

        checkNotNull(executor);
        return thenAcceptBothAsync0(other, action, executor);
    }

    private <U> CompletableFuture<Void> thenAcceptBothAsync0(emujava.util.concurrent.CompletionStage<? extends U> other,
                                                             BiConsumer<? super T, ? super U> action, emujava.util.concurrent.Executor executor) {

        checkNotNull(action);
        return thenCombineAsync0(other, (a, b) -> {
            action.accept(a, b);
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> runAfterBoth(emujava.util.concurrent.CompletionStage<?> other, Runnable action) {
        return runAfterBothAsync0(other, action, null);
    }

    @Override
    public CompletableFuture<Void> runAfterBothAsync(emujava.util.concurrent.CompletionStage<?> other, Runnable action) {
        return runAfterBothAsync0(other, action, DEFAULT_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> runAfterBothAsync(emujava.util.concurrent.CompletionStage<?> other, Runnable action, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return runAfterBothAsync0(other, action, executor);
    }

    private CompletableFuture<Void> runAfterBothAsync0(emujava.util.concurrent.CompletionStage<?> other, Runnable action, emujava.util.concurrent.Executor executor) {
        checkNotNull(action);
        return thenCombineAsync0(other, (a, b) -> {
            action.run();
            return null;
        }, executor);
    }

    @Override
    public <U> CompletableFuture<U> applyToEither(emujava.util.concurrent.CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEitherAsync0(other, fn, null);
    }

    @Override
    public <U> CompletableFuture<U> applyToEitherAsync(emujava.util.concurrent.CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEitherAsync0(other, fn, DEFAULT_EXECUTOR);
    }

    @Override
    public <U> CompletableFuture<U> applyToEitherAsync(emujava.util.concurrent.CompletionStage<? extends T> other,
                                                       Function<? super T, U> fn, emujava.util.concurrent.Executor executor) {

        checkNotNull(executor);
        return applyToEitherAsync0(other, fn, executor);
    }

    @SuppressWarnings("unchecked")
    private <U> CompletableFuture<U> applyToEitherAsync0(emujava.util.concurrent.CompletionStage<? extends T> other,
                                                         Function<? super T, U> fn, emujava.util.concurrent.Executor executor) {

        checkNotNull(fn);
        CompletableFuture<U> future = new CompletableFuture<>();
        or(this, other.toCompletableFuture()).then(runAsync(executor, (r, e) -> {
            if(e != null) {
                future.tryCompleteThrowable(e);
            }
            else {
                try {
                    future.tryCompleteValue(fn.apply((T)r));
                }
                catch(Throwable ex) {
                    future.tryCompleteThrowable(ex);
                }
            }
        }));
        return future;
    }

    @Override
    public CompletableFuture<Void> acceptEither(emujava.util.concurrent.CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherAsync0(other, action, null);
    }

    @Override
    public CompletableFuture<Void> acceptEitherAsync(emujava.util.concurrent.CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherAsync0(other, action, DEFAULT_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> acceptEitherAsync(emujava.util.concurrent.CompletionStage<? extends T> other,
                                                     Consumer<? super T> action, emujava.util.concurrent.Executor executor) {

        checkNotNull(executor);
        return acceptEitherAsync0(other, action, executor);
    }

    private CompletableFuture<Void> acceptEitherAsync0(emujava.util.concurrent.CompletionStage<? extends T> other,
                                                       Consumer<? super T> action, emujava.util.concurrent.Executor executor) {
        checkNotNull(action);
        return applyToEitherAsync0(other, (r) -> {
            action.accept(r);
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> runAfterEither(emujava.util.concurrent.CompletionStage<?> other, Runnable action) {
        return runAfterEitherAsync0(other, action, null);
    }

    @Override
    public CompletableFuture<Void> runAfterEitherAsync(emujava.util.concurrent.CompletionStage<?> other, Runnable action) {
        return runAfterEitherAsync0(other, action, DEFAULT_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> runAfterEitherAsync(emujava.util.concurrent.CompletionStage<?> other, Runnable action, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return runAfterEitherAsync0(other, action, executor);
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<Void> runAfterEitherAsync0(emujava.util.concurrent.CompletionStage<?> other, Runnable action, emujava.util.concurrent.Executor executor) {
        checkNotNull(action);
        return ((CompletableFuture<Object>)this).applyToEitherAsync0(other, (r) -> {
            action.run();
            return null;
        }, executor);
    }

    @Override
    public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends emujava.util.concurrent.CompletionStage<U>> fn) {
        return thenComposeAsync0(fn, null);
    }

    @Override
    public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends emujava.util.concurrent.CompletionStage<U>> fn) {
        return thenComposeAsync0(fn, DEFAULT_EXECUTOR);
    }

    @Override
    public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends emujava.util.concurrent.CompletionStage<U>> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return thenComposeAsync0(fn, executor);
    }

    private <U> CompletableFuture<U> thenComposeAsync0(Function<? super T, ? extends CompletionStage<U>> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(fn);
        CompletableFuture<U> future = new CompletableFuture<>();
        onStageComplete((r, e) -> {
            if(e != null) {
                future.tryCompleteThrowable(e);
            }
            else {
                try {
                    CompletableFuture<U> newFuture = fn.apply(r).toCompletableFuture();
                    // TODO: async?
                    // TODO: whenCompleteAsync0?
                    newFuture.whenCompleteAsync0((r1, ex) -> {
                        if(ex != null) {
                            future.tryCompleteThrowable(ex);
                        }
                        else {
                            future.tryCompleteValue(r1);
                        }
                    }, executor);
                }
                catch(Throwable ex) {
                    future.tryCompleteThrowable(ex);
                }
            }
        }, null); // TODO: executor?
        return future;
    }

    @Override
    public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
        checkNotNull(fn);
        return handle((r, e) -> e != null ? fn.apply(e) : r);
    }

    @Override
    public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return whenCompleteAsync0(action, null);
    }

    @Override
    public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return whenCompleteAsync0(action, DEFAULT_EXECUTOR);
    }

    @Override
    public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return whenCompleteAsync0(action, executor);
    }

    private CompletableFuture<T> whenCompleteAsync0(BiConsumer<? super T, ? super Throwable> action, emujava.util.concurrent.Executor executor) {
        checkNotNull(action);
        return handleAsync0((r, e) -> {
            action.accept(r, e);
            return r;
        }, executor);
    }

    @Override
    public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return handleAsync0(fn, null);
    }

    @Override
    public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return handleAsync0(fn, DEFAULT_EXECUTOR);
    }

    @Override
    public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(executor);
        return handleAsync0(fn, executor);
    }

    private <U> CompletableFuture<U> handleAsync0(BiFunction<? super T, Throwable, ? extends U> fn, emujava.util.concurrent.Executor executor) {
        checkNotNull(fn);
        CompletableFuture<U> future = new CompletableFuture<>();
        onStageComplete((r, e) -> {
            try {
                future.tryCompleteValue(fn.apply(r, e));
            }
            catch(Throwable ex) {
                future.tryCompleteThrowable(ex);
            }
        }, executor);
        return future;
    }

    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return this;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return tryCompleteStage(null, new emujava.util.concurrent.CancellationException());
    }

    @Override
    public boolean isCancelled() {
        return reason instanceof emujava.util.concurrent.CancellationException;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public boolean isCompletedExceptionally() {
        return reason != null;
    }

    /**
     * This method does not implement blocking behaviour on CompletableFuture because
     * it's not possible to implement that in single thread browser environment.
     * Instead a method call, which would block in JVM, will act as if calling thread is interrupted
     * immediately and will throw InterruptedException.
     */
    @Override
    public T get() throws InterruptedException, emujava.util.concurrent.ExecutionException {
        if(!isDone()) {
            // TODO: according to GWT Future's javadoc
//      throw new IllegalStateException("blocking on CompletableFuture is not supported");
            throw new InterruptedException("blocking on CompletableFuture is not supported");
        }

        if(reason != null) {
            if(reason instanceof emujava.util.concurrent.CancellationException) {
                throw (emujava.util.concurrent.CancellationException)reason;
            }
            Throwable cause = null;
            if(reason instanceof emujava.util.concurrent.CompletionException) {
                cause = reason.getCause();
            }
            if(cause == null) {
                cause = reason;
            }
            throw new emujava.util.concurrent.ExecutionException(cause);
        }
        return value;
    }

    /**
     * This method does not implement blocking behaviour on CompletableFuture because
     * it's not possible to implement that in single thread browser environment.
     * Instead a method call, which would block in JVM, will act as if calling thread is interrupted
     * immediately and will throw InterruptedException.
     * Timeout parameters are ignored and considered indefinite.
     */
    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }

    /**
     * This method does not implement blocking behaviour on CompletableFuture because
     * it's not possible to implement that in single thread browser environment.
     * Instead a method call, which would block in JVM, will act as if calling thread is interrupted
     * immediately and will return null as a result.
     */
    public T join() {
//    if (!isDone()) {
// TODO: according to GWT Future's javadoc
//      throw new IllegalStateException("blocking on CompletableFuture is not supported");
//    }
        return getNow(null);
    }

    public T getNow(T valueIfAbsent) {
        return isDone() ? getJoinValue() : valueIfAbsent;
    }

    private T getJoinValue() {
        if(reason == null) {
            return value;
        }

        if(reason instanceof emujava.util.concurrent.CancellationException) {
            throw (CancellationException)reason;
        }
        if(reason instanceof emujava.util.concurrent.CompletionException) {
            throw (emujava.util.concurrent.CompletionException)reason;
        }
        throw new emujava.util.concurrent.CompletionException(reason);
    }

    /**
     * This method is a simple stub which returns 0.
     * It's done for simplicity because this method is designed to be used in monitoring systems
     * and has no use in GWT.
     */
    public int getNumberOfDependents() {
        return 0;
    }

    public boolean complete(T value) {
        return tryCompleteStage(value, null);
    }

    public boolean completeExceptionally(Throwable e) {
        checkNotNull(e);
        return tryCompleteStage(null, e);
    }

    public void obtrudeValue(T value) {
        completeStage(value, null);
    }

    public void obtrudeException(Throwable e) {
        checkNotNull(e);
        completeStage(null, e);
    }

    private void tryCompleteValue(T value) {
        tryCompleteStage(value, null);
    }

    private void tryCompleteThrowable(Throwable reason) {
        tryCompleteStage(null, wrap(reason));
    }

    private boolean tryCompleteStage(T value, Throwable reason) {
        if(done) {
            return false;
        }

        completeStage(value, reason);
        return true;
    }

    private void completeStage(T value, Throwable reason) {
        this.value = value;
        this.reason = reason;
        done = true;
        if(reason == null) {
            promise.resolve(value);
        }
        else {
            promise.reject(reason);
        }
    }

    private void onStageComplete(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        if(done) {
            runAsync(executor, action).accept(value, reason); // TODO
        }
        else {
            promise.then(runAsync(executor, action));
        }
    }

    private static RuntimeException wrap(Throwable t) {
        if(t instanceof emujava.util.concurrent.CompletionException) {
            return (emujava.util.concurrent.CompletionException)t;
        }
        return new CompletionException(t);
    }
}
