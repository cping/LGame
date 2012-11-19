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
package org.test.sdata;

import loon.core.geom.Vector2f;

public class StageData {
    public bb[] bbs;
    public int bbType;
    public boolean dG;
    public enemy[] e1s;
    public enemy[] e2s;
    public float G;
    public mbg[] mbgs;
    public int moveType;
    public int numbb;
    public int numMbb;
    public int numMBG;
    public int numMFG;
    public float player_vy;
    public float player_y;

    public static class bb
    {
        public float _disTime;
        public float _h;
        public float _vx;
        public float _w;
        public Vector2f Pos;

        public bb(float w, float h, float x, float y)
        {
            this._w = w;
            this._h = h;
            this.Pos = new Vector2f(x, y);
            this._vx = 0f;
            this._disTime = 0f;
        }

        public bb(float w, float h, float x, float y, float vx, float disTime)
        {
            this._w = w;
            this._h = h;
            this.Pos = new Vector2f(x, y);
            this._vx = vx;
            this._disTime = disTime;
        }
    }

    public static class enemy
    {
        public float _x;
        public float _y;

        public enemy(float x, float y)
        {
            this._x = x;
            this._y = y;
        }
    }

    public static class mbg
    {
        public float _rate;
        public int _type;
        public float _y;

        public mbg(float rate, float y, int type)
        {
            this._rate = rate;
            this._y = y;
            this._type = type;
        }
    }
}
