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
package org.test.stages;

import loon.LSystem;
import loon.core.timer.GameTime;

import org.test.base.BaseSprite;

public class Gameover extends BaseSprite
{
    public boolean playedOver;

    public Gameover()
    {
        super.Load( "assets/gameover", 10, 15f, false);
        super.setAnimation(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        this.Origin.x = super.getWidth() / 2f;
        this.Origin.y = super.getHeight() / 2f;
        this.Pos.x = LSystem.screenRect.width/2;
        this.Pos.y = LSystem.screenRect.height/2;
        super.visible = false;
    }

    protected void reset()
    {
        super.setAnimation(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        super._Frame = 0;
        this.playedOver = false;
        super.visible = false;
    }

    protected void specificUpdate(GameTime gameTime)
    {
        if (super.visible && (super._Frame == 9))
        {
            super.setAnimation(new int[] { 9 });
            this.playedOver = true;
        }
    }
}
