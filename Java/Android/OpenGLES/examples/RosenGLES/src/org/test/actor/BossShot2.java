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
package org.test.actor;

import loon.core.geom.RectBox;
import loon.core.timer.GameTime;

import org.test.base.BaseSprite;
import org.test.item.SoundControl;

public class BossShot2 extends  BaseSprite
{
    private RectBox rect = new RectBox(110, 30, 580, 420);
    private boolean rollLeft;
   
    private boolean toLeft;
    private final float V = 20f;
    private final float VR = 0.5235988f;
    private float vx;
    private float vy;

    public BossShot2(boolean rollleft)
    {
        super.Load("assets/bossShot2", 1, 0f, true);
        this.Origin.x = super.getWidth() / 2f;
        this.Origin.y = super.getHeight() / 2f;
        super.visible = false;
        this.rollLeft = rollleft;
    }

    private void actingShoot()
    {
        if (this.toLeft)
        {
            if (this.rollLeft)
            {
                if (this.Pos.x > this.rect.Left())
                {
                    this.vy = 0f;
                    this.vx = -V;
                }
                else
                {
                    this.Pos.x= this.rect.Left();
                    this.vx = 0f;
                    this.vy = V;
                }
            }
            else if (this.Pos.y < this.rect.Bottom())
            {
                this.vx = 0f;
                this.vy = V;
            }
            else
            {
                this.Pos.y = this.rect.Bottom();
                this.vx = -V;
                this.vy = 0f;
            }
            if ((this.Pos.x < this.rect.Left()) || (this.Pos.y > this.rect.Bottom()))
            {
                super.visible = false;
            }
        }
        else
        {
            if (!this.rollLeft)
            {
                if (this.Pos.x < this.rect.Right())
                {
                    this.vy = 0f;
                    this.vx = V;
                }
                else
                {
                    this.Pos.x = this.rect.Right();
                    this.vx = 0f;
                    this.vy = V;
                }
            }
            else if (this.Pos.y < this.rect.Bottom())
            {
                this.vx = 0f;
                this.vy = V;
            }
            else
            {
                this.Pos.y = this.rect.Bottom();
                this.vx = V;
                this.vy = 0f;
            }
            if ((this.Pos.x > this.rect.Right()) || (this.Pos.y > this.rect.Bottom()))
            {
                super.visible = false;
            }
        }
    }

    public void beginShoot(boolean toL)
    {
        if (!super.visible)
        {
            if (SoundControl.on)
            {
            
            }
            this.toLeft = toL;
            if (this.toLeft)
            {
                this.Pos.x = this.rect.Right();
                this.Pos.y = this.rect.Top();
            }
            else
            {
                this.Pos.x = this.rect.Left();
                this.Pos.y = this.rect.Top();
            }
            super.visible = true;
        }
    }

    protected  void specificUpdate(GameTime gameTime)
    {
        if (this.rollLeft)
        {
            super.Rotation += VR;
        }
        else
        {
            super.Rotation -= VR;
        }
        if (super.visible)
        {
            this.actingShoot();
        }
        this.Pos.x += this.vx;
        this.Pos.y += this.vy;
    }
}
