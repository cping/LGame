//
// React - a library for functional-reactive-like programming
// Copyright (c) 2013, Three Rings Design, Inc. - All rights reserved.
// http://github.com/threerings/react/blob/master/LICENSE

package java.lang.ref;

/**
 * An implementation of weak references in JavaScript that is not actually weak. This just serves
 * to keep GWT from choking when using react in a GWT app. Weak references are not supported in
 * GWT. Someday JavaScript may get WeakReferences, at which point we can use 'em.
 */
public class WeakReference<T>
{
    public WeakReference (T value) {
        _value = value;
    }

    public T get () {
        return _value;
    }

    protected final T _value;
}
