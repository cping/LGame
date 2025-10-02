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
package loon;

public final class LTextures {

	public static boolean contains(int id) {
		return LSystem.containsTexture(id);
	}

	public static int getMemSize() {
		return LSystem.getTextureMemSize();
	}

	public static LTexture createTexture(int width, int height) {
		return LSystem.createTexture(width, height);
	}

	public static LTexture newTexture(String path) {
		return LSystem.newTexture(path);
	}

	public static int count() {
		return LSystem.countTexture();
	}

	public static boolean containsValue(LTexture texture) {
		return LSystem.containsTextureValue(texture);
	}

	public static int getRefCount(String fileName) {
		return LSystem.getRefTextureCount(fileName);
	}

	public static LTexture loadTexture(String fileName) {
		return LSystem.loadTexture(fileName);
	}

	public static void destroySourceAllCache() {
		LSystem.destroySourceAllCache();
	}

	public static void destroyAllCache() {
		LSystem.destroyAllCache();
	}

	public static void dispose() {
		LSystem.disposeTextureAll();
	}

	public static void reload() {
		LSystem.reloadTexture();
	}

	public static void close() {
		LSystem.closeAllTexture();
	}

}
