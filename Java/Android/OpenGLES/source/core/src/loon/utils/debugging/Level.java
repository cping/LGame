package loon.utils.debugging;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Level {

	public static final Level DEBUG = new Level("Debug", 1);

	public static final Level INFO = new Level("Info", 2);

	public static final Level WARN = new Level("Warn", 3);

	public static final Level ERROR = new Level("Error", 4);

	public static final Level IGNORE = new Level("Ignore", 5);

    public static final Level ALL = new Level("Ignore", 0);
    
	String levelString;

	final int level;

	private Level(String levelString, int levelInt) {
		this.levelString = levelString;
		this.level = levelInt;
	}

	@Override
	public String toString() {
		return levelString;
	}

	public int toType() {
		return level;
	}
}
