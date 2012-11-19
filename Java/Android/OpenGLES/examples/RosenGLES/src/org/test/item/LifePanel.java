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
package org.test.item;

import loon.core.geom.Vector2f;

import org.test.base.BaseSprite;

public class LifePanel {
	 private int _n;
     public BaseSprite[] sps;

     public LifePanel(int n, String asset, Vector2f pos)
     {
         this._n = n;
         this.sps = new BaseSprite[n];
         for (int i = 0; i < n; i++)
         {
             this.sps[i] = new BaseSprite();
             this.sps[i].Load(asset, 1, 1f, true);
             this.sps[i].Pos = pos;
             this.sps[i].Pos.y += i * this.sps[i].getHeight();
             this.sps[i].visible = false;
         }
     }

     public void minusLife(int i)
     {
         if (i < this.sps.length)
         {
             if (i < 0)
             {
                 i = 0;
             }
             this.sps[i].visible = false;
             if (i == 0)
             {
                 for (int j = 0; j < this._n; j++)
                 {
                     this.sps[j].visible = false;
                 }
             }
         }
     }

     public void reset(int n)
     {
         for (int i = 0; i < n; i++)
         {
             this.sps[i].visible = true;
         }
     }
}
