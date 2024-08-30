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

/**
 *
 */
public class Impl {

    public static final Promises IMPL = isSupported() ? new NativePromisesImpl() :
            new PromisesImpl();

    @JSBody(script = "" +
            "return typeof Promise === \"function\"\n" +
            "        // Some of these methods are missing from\n" +
            "        // Firefox/Chrome experimental implementations\n" +
            "        && \"resolve\" in Promise\n" +
            "        && \"reject\" in Promise\n" +
            "        && \"all\" in Promise\n" +
            "        && \"race\" in Promise\n" +
            "        // Older version of the spec had a resolver object\n" +
            "        // as the arg rather than a function\n" +
            "        && (function() {\n" +
            "          var resolve;\n" +
            "          new Promise(function(r) { resolve = r; });\n" +
            "          return typeof resolve === \"function\";\n" +
            "        }());")
    private static native boolean isSupported();

    private Impl() {
    }
}