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
package loon.utils;

import loon.LSystem;

/**
 * 文件地址辅助用类,用以格式化文件地址为需要格式
 */
public final class PathUtils {

	private PathUtils() {
	}

	/**
	 * 判定文件名是否为图像文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isImageFile(final String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}
		final String newName = fileName.toLowerCase().trim();
		return LSystem.isImage(PathUtils.getExtension(newName));
	}

	/**
	 * 格式化文件路径反斜杆为系统默认的反斜杠样式
	 *
	 * @param filename
	 * @return
	 */
	public static String normalize(String filename) {
		if (StringUtils.isEmpty(filename)) {
			return LSystem.EMPTY;
		}
		String result = StringUtils.filter(filename, new char[] { LSystem.BACKSLASH, LSystem.SLASH }, LSystem.FS);
		String doubleTag = LSystem.FS + LSystem.FS;
		if (result.indexOf(doubleTag) != -1) {
			result = StringUtils.replace(result, doubleTag, LSystem.FS);
		}
		return result;
	}

	public static String normalizeDir(String filename) {
		filename = PathUtils.normalize(PathUtils.getDirName(filename));
		if (filename.lastIndexOf(LSystem.BACKSLASH) != -1 || filename.lastIndexOf(LSystem.SLASH) != -1) {
			filename = filename.substring(0, filename.length() - 1);
		}
		return filename;
	}

	/**
	 * 格式化文件路径去除重复项后合并两个地址
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static String normalizeCombinePaths(String left, String right) {
		if (left == null && right == null) {
			return LSystem.EMPTY;
		}
		if (left == null && right != null) {
			return right;
		}
		if (left != null && right == null) {
			return left;
		}
		String path = null;
		if (right.indexOf(left) != -1) {
			path = right;
		} else {
			path = PathUtils.getCombinePaths(left, right);
		}
		return PathUtils.normalize(path);
	}

	/**
	 * 返回指定文件的基础文件名(不带.)
	 *
	 * @param filename
	 * @return
	 */
	public static String getBaseFileName(String filename) {
		if (StringUtils.isEmpty(filename)) {
			return LSystem.EMPTY;
		}
		String result = LSystem.EMPTY;
		if (filename.indexOf(LSystem.BACKSLASH) != -1) {
			result = filename.substring(filename.lastIndexOf(LSystem.BACKSLASH) + 1);
		} else if (filename.indexOf(LSystem.SLASH) != -1) {
			result = filename.substring(filename.lastIndexOf(LSystem.SLASH) + 1);
		} else {
			result = filename.substring(filename.lastIndexOf(LSystem.FS) + 1);
		}
		int idx = result.indexOf(LSystem.DOT);
		if (idx != -1) {
			result = result.substring(0, idx);
		}
		return result;
	}

	/**
	 * 返回完整文件名
	 *
	 * @param filename
	 * @return
	 */
	public static String getFullFileName(String filename) {
		if (StringUtils.isEmpty(filename)) {
			return LSystem.EMPTY;
		}
		int length = filename.length();
		if (filename.indexOf(LSystem.BACKSLASH) != -1) {
			int size = filename.lastIndexOf(LSystem.BACKSLASH) + 1;
			if (size < length) {
				return filename.substring(size, length);
			} else {
				return LSystem.EMPTY;
			}
		} else if (filename.indexOf(LSystem.SLASH) != -1) {
			int size = filename.lastIndexOf(LSystem.SLASH) + 1;
			if (size < length) {
				return filename.substring(size, length);
			} else {
				return LSystem.EMPTY;
			}
		} else {
			int size = filename.lastIndexOf(LSystem.FS) + 1;
			if (size < length) {
				return filename.substring(size, length);
			} else {
				return LSystem.EMPTY;
			}
		}
	}

	/**
	 * 返回最后一级文件夹的名称
	 *
	 * @param filename
	 * @return
	 */
	public static String getLastDirName(String dir) {
		if (StringUtils.isEmpty(dir)) {
			return LSystem.EMPTY;
		}
		if (dir.indexOf(LSystem.BACKSLASH) != -1) {
			String[] list = StringUtils.split(dir, LSystem.BACKSLASH);
			if (list.length > 1) {
				return list[list.length - 2];
			}
			return list[list.length - 1];
		} else if (dir.indexOf(LSystem.SLASH) != -1) {
			String[] list = StringUtils.split(dir, LSystem.SLASH);
			if (list.length > 1) {
				return list[list.length - 2];
			}
			return list[list.length - 1];

		} else if (dir.indexOf(LSystem.FS) != -1) {
			String[] list = StringUtils.split(dir, LSystem.FS);
			if (list.length > 1) {
				return list[list.length - 2];
			}
			return list[list.length - 1];

		}
		return dir;
	}

	/**
	 * 获得指定路径扩展名(文件后缀)
	 *
	 * @param filename
	 * @return
	 */
	public static String getExtension(String filename) {
		if (StringUtils.isEmpty(filename)) {
			return LSystem.EMPTY;
		}
		int index = filename.lastIndexOf(LSystem.DOT) + 1;
		if (index <= 0) {
			return LSystem.EMPTY;
		} else {
			return filename.substring(index, filename.length()).trim();
		}
	}

	/**
	 * 获得指定路径扩展名(指定标记后)
	 * 
	 * @param filename
	 * @param flag
	 * @return
	 */
	public static String getExtension(String filename, String flag) {
		if (StringUtils.isEmpty(filename)) {
			return LSystem.EMPTY;
		}
		int index = filename.lastIndexOf(flag) + 1;
		if (index <= 0) {
			return LSystem.EMPTY;
		} else {
			return filename.substring(index, filename.length()).trim();
		}
	}

	/**
	 * 返回文件夹所在的文件夹路径
	 *
	 * @param dir
	 * @return
	 */
	public static String getDirName(String dir) {
		if (StringUtils.isEmpty(dir)) {
			return LSystem.EMPTY;
		}
		int size = dir.length();
		if (dir.indexOf(LSystem.BACKSLASH) != -1) {
			size = dir.lastIndexOf(LSystem.BACKSLASH) + 1;
		} else if (dir.indexOf(LSystem.SLASH) != -1) {
			size = dir.lastIndexOf(LSystem.SLASH) + 1;
		} else {
			size = dir.lastIndexOf(LSystem.FS) + 1;
		}
		return dir.substring(0, size);
	}

	public static String getCombinePaths(String left, String right) {
		if (left == null || right == null) {
			return LSystem.EMPTY;
		}
		int cnt = left.length();
		if (cnt > 1 && left.charAt(cnt - 1) != LSystem.SLASH && left.charAt(cnt - 1) != LSystem.BACKSLASH
				&& (right.length() == 0
						|| (right.charAt(0) != LSystem.SLASH && right.charAt(0) != LSystem.BACKSLASH))) {
			left += LSystem.SLASH;
		}
		return left + right;
	}

	public static String getCombinePaths(String left, String middle, String right) {
		return getCombinePaths(getCombinePaths(left, middle), right);
	}

	/**
	 * 检查当前文件路径是否为相对路径
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isRelativePath(String filename) {
		return filename.charAt(0) != LSystem.SLASH && StringUtils.isMatch(filename, ":\\/\\/");
	}

	public static String convertRelativePathToAbsolute(String basePath, String path) {
		String result;
		if (isRelativePath(path)) {
			result = getCombinePaths(basePath, path);
		} else {
			result = path;
		}
		return normalize(result);
	}

	public static String extractPath(String filename) {
		return extractPath(filename, LSystem.SLASH);
	}

	/**
	 * 返回不带文件名的路徑，如果文件名是相对路径，以.开头
	 * 
	 * @param filename
	 * @param delimiter
	 * @return
	 */
	public static String extractPath(String filename, char delimiter) {
		if (StringUtils.isEmpty(filename)) {
			return LSystem.EMPTY;
		}
		String result = LSystem.EMPTY;
		String[] parts = StringUtils.split(filename, delimiter);
		int i = 0;
		if (parts.length > 1) {
			if (isRelativePath(filename)) {
				if (parts[0].equals(".")) {
					for (i = 0; i < parts.length - 1; ++i) {
						result += (i == 0) ? parts[i] : delimiter + parts[i];

					}
				} else if (parts[0].equals("..")) {
					for (i = 0; i < parts.length - 1; ++i) {
						result += (i == 0) ? parts[i] : delimiter + parts[i];
					}
				} else {
					result = ".";
					for (i = 0; i < parts.length - 1; ++i) {
						result += delimiter + parts[i];
					}
				}
			} else {
				for (i = 0; i < parts.length - 1; ++i) {
					result += (i == 0) ? parts[i] : delimiter + parts[i];
				}
			}
		}
		return result;
	}

	public static String removeNewLine(String s) {
		return StringUtils.replacesTrim(s, "\n", "\r");
	}

	public static String removeExtension(String fileName) {
		final int len = fileName.lastIndexOf(LSystem.DOT);
		if (len != -1) {
			return fileName.substring(0, len);
		}
		return fileName;
	}

	public static String fullRelativeName(String relativeName, String filename) {
		final String path = getDirName(relativeName);
		return getCombinePaths(path, filename);
	}
}
