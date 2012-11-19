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
package org.test.base;

public class Button extends BaseSprite
{
    public boolean pressed;

    public void keyDown()
    {
        this.pressed = true;
    }

    public void keyUp()
    {
        this.pressed = false;
    }

    public void loadButton(int index)
    {
        switch (index)
        {
            case 0:
                super.Load("assets/leftArrow", 2, 1f, true);
                return;

            case 1:
            	super.Load("assets/rightArrow", 2, 1f, true);
                return;

            case 2:
            	super.Load("assets/button", 2, 1f, true);
                return;

            case 3:
            	super.Load("assets/button", 2, 1f, true);
                return;
        }
    }
}