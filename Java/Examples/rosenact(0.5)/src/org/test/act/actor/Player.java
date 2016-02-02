/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except : compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to : writing, software
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

import loon.LSystem;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

import org.test.act.base.BaseSprite;
import org.test.act.item.Block;
import org.test.act.item.DeadEffect;
import org.test.act.item.SoundControl;
import org.test.act.stages.Control;

public class Player extends BaseSprite
{
    private Control _control;
    public final float actHeight = 58f;
    public final float actWidth = 42f;
    private int countAttack = 10;
    private int countGod;
    public DeadEffect de;
   
    public boolean god;
    public boolean haventPlayedDead = true;
    
    private boolean i_am_hardly_walking_to_left;
    private boolean i_am_hardly_walking_to_right;
    private boolean inAir = true;
    private boolean isfacingToLeft;
    private boolean isJumpingUp;
    private boolean isLanded;
    private final int[] JUMP_ATTACK_LOOP = new int[] { 10 };
    private final int[] JUMP_LOOP = new int[] { 9 };
    public boolean KA;
    public boolean KD;
    private boolean keyKisReleasedAfterAJump = true;
    public boolean KJ;
    public boolean KK;
    public int life = 10;
    private int MAX_ATTACK = 10;
    private int MAX_GOD = 100;
    public Shot[] shots = new Shot[3];
    private final int[] STAND_ATTACK_LOOP = new int[] { 0x10 };
    private final int[] STAND_LOOP = new int[1];
    public final float VX = 4f;
    public float vy;
    public final float VY = 15f;
    private final int[] WALK_ATTACK_LOOP = new int[] { 6, 7, 8, 7 };
    private final int[] WALK_LOOP = new int[] { 3, 4, 5, 4 };

    public Player(Control control)
    {
        this._control = control;
        super.Load( "assets/player", 0x15, 3, 9f, false);
        super.setAnimation(this.WALK_LOOP);
        this.Origin.x = super.getWidth() / 2f;
        this.Origin.y = super.getHeight();
        for (int i = 0; i < 3; i++)
        {
            this.shots[i] = new Shot();
        }
        super.effects = SpriteEffects.FlipHorizontally;
        this.bounds.x = (int) (-this.actWidth / 2f);
        this.bounds.y = -((int) this.actHeight);
        this.bounds.width = (int) this.actWidth;
        this.bounds.height = (int) this.actHeight;
      
        this.de = new DeadEffect();
        this.de.init("assets/playerDE", 0f);
    }

    private void applyGravity()
    {
        this.Pos.y += this.vy;
        this.inAir = true;
        this.vy += 0.8f;
        if (this.vy > this.VY)
        {
            this.vy = this.VY;
        }
    }

    private void attackFromKJ()
    {
        if (this.KJ)
        {
            this.countAttack++;
            if (this.countAttack > this.MAX_ATTACK)
            {
                this.countAttack = 0;
                for (Shot shot : this.shots)
                {
                    if (!shot.visible)
                    {
                        shot.shoot(this.Pos.x, this.Pos.y - 25f, this.isfacingToLeft);
                        return;
                    }
                }
            }
        }
        else
        {
            this.countAttack = this.MAX_ATTACK;
        }
    }

    private void checkKeyBoard()
    {
        if (!super.visible)
        {
            this.KA = false;
            this.KD = false;
            this.KK = false;
            this.KJ = false;
        }
        else
        {
            this.KA = this._control.KA;
            this.KD = this._control.KD;
            this.KJ = this._control.KJ;
            this.KK = this._control.KK;
            if (!this.KK)
            {
                this.keyKisReleasedAfterAJump = true;
            }
        }
    }

    private void cleanShots()
    {
        for (Shot shot : this.shots)
        {
            if (shot.visible)
            {
                if ((shot.Pos.x - (shot.actWidth / 2f)) > LSystem.viewSize.width)
                {
                    shot.visible = false;
                }
                else if ((shot.Pos.x + (shot.actWidth / 2f)) < 0f)
                {
                    shot.visible = false;
                }
                else if ((shot.Pos.y - (shot.actHeight / 2f)) > LSystem.viewSize.height)
                {
                    shot.visible = false;
                }
                else if ((shot.Pos.y - (shot.actHeight / 2f)) < 0f)
                {
                    shot.visible = false;
                }
            }
        }
    }

    public void die()
    {
        if (this.haventPlayedDead)
        {
            if (SoundControl.on)
            {
       
            }
            this.haventPlayedDead = false;
        }
        for (BaseSprite sprite : this.de.sps)
        {
            sprite.Pos = (super.Pos.sub(super.Origin)).add(new Vector2f(this.actWidth / 2f, this.actHeight / 2f));
        }
        this.life = 0;
        super.visible = false;
        this.de.shoot();
        this.god = false;
    }

    public void hitBlockBottom(Block block)
    {
        if (this.vy < 0f)
        {
            this.vy = -this.vy;
        }
    }

    public void hitBlockLeft(Block block)
    {
        if (this.i_am_hardly_walking_to_right)
        {
            this.Pos.x = block.Pos.x - (this.actWidth / 2f);
        }
    }

    public void hitBlockRight(Block block)
    {
        if (this.i_am_hardly_walking_to_left)
        {
            this.Pos.x = (block.Pos.x + block.getWidth()) + (this.actWidth / 2f);
        }
    }

    public void hitBlockTop(Block block)
    {
        if (!this.isJumpingUp)
        {
            this.vy = 0f;
            this.inAir = false;
            this.isLanded = true;
            this.Pos.y = block.Pos.y;
        }
    }

    public void hitted()
    {
        this.god = true;
        this.life--;
        if ((this.life <= 0) && super.visible)
        {
            this.die();
        }
        else if (SoundControl.on)
        {
      
        }
    }

    private void jumpByKK()
    {
        if ((this.isLanded && this.KK) && (!this.isJumpingUp && this.keyKisReleasedAfterAJump))
        {
            this.vy = -this.VY;
            this.isJumpingUp = true;
            this.inAir = true;
            this.isLanded = false;
            this.keyKisReleasedAfterAJump = false;
        }
        if (this.vy > 0f)
        {
            this.isJumpingUp = false;
        }
    }

    private void moveByKAKD()
    {
        if (this.KA && !this.KD)
        {
            this.i_am_hardly_walking_to_left = true;
            this.i_am_hardly_walking_to_right = false;
            this.isfacingToLeft = true;
            this.Pos.x -= this.VX;
        }
        else if (this.KD && !this.KA)
        {
            this.i_am_hardly_walking_to_left = false;
            this.i_am_hardly_walking_to_right = true;
            this.isfacingToLeft = false;
            this.Pos.x += this.VX;
        }
        else
        {
            this.i_am_hardly_walking_to_left = false;
            this.i_am_hardly_walking_to_right = false;
        }
    }

    protected void specificUpdate(GameTime gameTime)
    {
        this.de.update(gameTime);
        this.checkKeyBoard();
        this.applyGravity();
        this.moveByKAKD();
        this.jumpByKK();
        this.cleanShots();
        this.attackFromKJ();
        if (this.god)
        {
            this.countGod++;
            this.color.a = 0x80/255f;
            this.color.b = 0x80/255f;
            this.color.g = 0x80/255f;
            this.color.r = 0x80/255f;
            if (this.countGod > this.MAX_GOD)
            {
                this.god = false;
                this.countGod = 0;
                super.color = LColor.newWhite();
            }
        }
    }

    public void updateAnimation()
    {
        if (this.isfacingToLeft)
        {
            super.effects = SpriteEffects.None;
        }
        else
        {
            super.effects = SpriteEffects.FlipHorizontally;
        }
        if (!this.KJ)
        {
            if (this.inAir)
            {
                super.setAnimation(this.JUMP_LOOP);
            }
            else if (this.i_am_hardly_walking_to_left || this.i_am_hardly_walking_to_right)
            {
                super.setAnimation(this.WALK_LOOP);
            }
            else
            {
                super.setAnimation(this.STAND_LOOP);
            }
        }
        else if (this.inAir)
        {
            super.setAnimation(this.JUMP_ATTACK_LOOP);
        }
        else if (this.i_am_hardly_walking_to_left || this.i_am_hardly_walking_to_right)
        {
            super.setAnimation(this.WALK_ATTACK_LOOP);
        }
        else
        {
            super.setAnimation(this.STAND_ATTACK_LOOP);
        }
    }
}
