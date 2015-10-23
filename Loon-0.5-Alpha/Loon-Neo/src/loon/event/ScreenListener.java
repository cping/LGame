/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.event;

import loon.opengl.GLEx;

public interface ScreenListener {
	
	public void draw(GLEx g);
	
	public void update(long elapsedTime);

	public void pressed(GameTouch e);

	public void released(GameTouch e);

	public void move(GameTouch e);

	public void drag(GameTouch e);
	
	public void pressed(GameKey e);

	public void released(GameKey e);
	
	public void dispose();
}
