/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package loon.jni;

import com.google.gwt.typedarrays.shared.ArrayBufferView;

/**
 * Allows us to get direct access to the typed array used by the nio buffer
 * emulation in GWT. All nio buffer types implement this interfaces in GWT mode.
 * This can be used only in HTML-platform specific code.
 */
public interface HasArrayBufferView {

	/* Returns the underlying typed array buffer view. */
	ArrayBufferView getTypedArray();

	/**
	 * Returns the element size in bytes (e.g. 4 for a FloatBuffer and 1 for a
	 * ByteBuffer).
	 */
	int getElementSize();

	/**
	 * Returns the open GL element type constant corresponding to the buffer
	 * contents.
	 */
	int getElementType();
}
