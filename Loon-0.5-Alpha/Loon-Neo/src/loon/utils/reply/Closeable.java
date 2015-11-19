/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils.reply;

import loon.utils.ObjectSet;

//如果报错，请确认自己的jdk为1.7或以上，因为AutoCloseable这个资源标记是1.7才开始有的
public interface Closeable extends AutoCloseable {

    class Set implements Closeable {
    	
        protected ObjectSet<AutoCloseable> _set; 
    	
        @Override public void close () {
            if (_set != null) {
                ManyFailure error = null;
                for (AutoCloseable c : _set) try {
                    c.close();
                } catch (Exception e) {
                    if (error == null) {
                    	error = new ManyFailure();
                    }
                    error.addSuppressed(e);
                }
                _set.clear();
                if (error != null) {
                	throw error;
                }
            }
        }

        public <T extends AutoCloseable> T add (T c) {
            if (_set == null){
            	_set = new ObjectSet<>();
            }
            _set.add(c);
            return c;
        }

        public void remove (AutoCloseable c) {
            if (_set != null){
            	_set.remove(c);
            }
        }

    }

    class Shutdown {
        public static final Closeable DEF = new Closeable() {
            public void close () {} 
        };

        public static Closeable join (final Closeable... cons) {
            return new Closeable() {
                @Override public void close () {
                    for (int ii = 0; ii < cons.length; ii++) {
                        if (cons[ii] == null) {
                        	continue;
                        }
                        cons[ii].close();
                        cons[ii] = null;
                    }
                }
            };
        }

        public static Closeable close (Closeable con) {
            con.close();
            return DEF;
        }
    }

    void close ();
}
