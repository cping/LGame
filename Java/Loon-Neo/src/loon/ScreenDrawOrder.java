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

/**
 * 此类用于将Screen中的桌面组件、精灵组件、用户渲染画面按照指定顺序排序组合
 */
public enum ScreenDrawOrder {

	NONE,

	DEFAULT,

	ONLY_DESKTOP,

	ONLY_SPRITE,

	ONLY_USER,

	ONLY_DESKTOP_USER,

	ONLY_USER_DESKTOP,

	ONLY_SPRITE_USER,

	ONLY_USER_SPRITE,

	ONLY_SPRITE_DESKTOP,

	ONLY_DESKTOP_SPRITE,

	SPRITE_DESKTOP_USER,

	SPRITE_USER_DESKTOP,

	USER_DESKTOP_SPRITE,

	USER_SPRITE_DESKTOP,

	DESKTOP_SPRITE_USER,

	DESKTOP_USER_SPRITE
}
