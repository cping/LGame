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
package org.test.act.actor;

import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.utils.timer.GameTime;

public class Enemy1 extends Enemy
{
    private final int FLY_MAX = 100;
    private int flyCount = 100;
    protected float vx = 3f;
    protected final float VX = 3f;

    public Enemy1()
    {
        super.loadSe( "assets/enemyDE", 0.5f);
        super.Load( "assets/e1", 3, 2f, false);
        super.setAnimation(new int[] { 0, 0, 1, 2, 2, 1 });
        this.Scale.x = 0.8f;
        this.Scale.y = 0.8f;
        super.actWidth = 207.2f;
        super.actHeight = 193.6f;
        this.bounds.width = (int) super.getWidth();
        this.bounds.height = (int) super.getHeight();
    }

    protected void specificUpdate(GameTime gameTime)
    {
        this.flyCount++;
        super.de.update(gameTime);
        if (super.effects == SpriteEffects.None)
        {
            this.vx = -this.VX;
            if (this.flyCount > this.FLY_MAX)
            {
                this.flyCount = 0;
                super.effects = SpriteEffects.FlipHorizontally;
            }
        }
        else
        {
            this.vx = this.VX;
            if (this.flyCount > this.FLY_MAX)
            {
                this.flyCount = 0;
                super.effects = SpriteEffects.None;
            }
        }
        this.Pos.x += this.vx;
        super.color = LColor.newWhite();
    }
}
