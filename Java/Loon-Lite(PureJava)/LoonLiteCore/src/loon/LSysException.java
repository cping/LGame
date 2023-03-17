/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon;

public class LSysException extends RuntimeException {

    /**
	 *
	 */
	private static final long serialVersionUID = -3689299562639868277L;

	public LSysException() {
		this("Loon System Exception !");
	}

	public LSysException(String message) {
		super(message);
		LSystem.error(message);
	}

	public LSysException(String message, Throwable cause) {
		super(message, cause);
		LSystem.error(message, cause);
	}

	public LSysException(String message, int line) {
		super(message);
		LSystem.error("exception for line : " + line + "\n" + message);
	}

}
