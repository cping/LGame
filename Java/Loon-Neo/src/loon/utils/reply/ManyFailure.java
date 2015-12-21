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

import java.io.PrintStream;
import java.io.PrintWriter;

import loon.utils.TArray;

public class ManyFailure extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1644703706897221876L;

    protected TArray<Throwable> _failures = new TArray<Throwable>();
    
	public Iterable<Throwable> failures () {
        return _failures;
    }

    public void addFailure (Throwable t) {
        _failures.add(t);
    }

    @Override
    public String getMessage () {
        StringBuilder buf = new StringBuilder();
        for (Throwable failure : _failures) {
            if (buf.length() > 0) buf.append(", ");
            buf.append(failure.getClass().getName()).append(": ").append(failure.getMessage());
        }
        return _failures.size + " failures: " + buf;
    }

    @Override
    public void printStackTrace (PrintStream s) {
        for (Throwable failure : _failures) {
            failure.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace (PrintWriter w) {
        for (Throwable failure : _failures) {
            failure.printStackTrace(w);
        }
    }

    @Override
    public Throwable fillInStackTrace () {
        return this;
    }

}
