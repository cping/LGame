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
package org.test;

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class Arrow {

    private final LColor BOU_COLOR = new LColor(185, 120, 0);
    private final LColor WING_COLOR;
    private final LColor HEAD_COLOR = new LColor(192, 192, 192);
    private float rad_;
    boolean isAvoidDamageCount_;
    float sizeW_,sizeH_;
    float maxW_,maxH_;
    float x_,y_;
    public Arrow(float x, float y, float d)
    {
     this.x_=x;
     this.y_=y;
        WING_COLOR = LColor.white;
        isAvoidDamageCount_ = true;
        sizeW_ = sizeH_ = 1000;
        maxW_ = maxH_ = 3000;
        rad_ = 360;
    }

    protected  void draw(GLEx g)
    {
        float d = MathUtils.sin(rad_);
        float d1 = MathUtils.cos(rad_);
        float ai[][] = {
            {
                -10, 14, 14, -10
            }, {
                -1, -1, 1, 1
            }
        };
        float ai1[][] = new float[2][4];
        for(int i = 0; i < 4; i++)
        {
            ai1[0][i] = x_ + (float)((float)ai[0][i] * d1 + (float)ai[1][i] * d);
            ai1[1][i] = y_ + (float)((float)ai[0][i] * d - (float)ai[1][i] * d1);
        }

        g.setColor(BOU_COLOR);
        g.fillPolygon(ai1[0], ai1[1], 4);
        float ai2[][] = {
            {
                -20, -20, -11, 0, 0, -8
            }, {
                -6, -5, -1, -1, -3, -6
            }
        };
        float ai3[][] = new float[2][6];
        float ai4[][] = new float[2][6];
        for(int j = 0; j < 6; j++)
        {
            ai3[0][j] = x_ + (float)((float)ai2[0][j] * d1 + (float)ai2[1][j] * d);
            ai3[1][j] = y_ + (float)((float)ai2[0][j] * d - (float)ai2[1][j] * d1);
            ai4[0][j] = x_ + (float)((float)ai2[0][j] * d1 - (float)ai2[1][j] * d);
            ai4[1][j] = y_ + (float)((float)ai2[0][j] * d + (float)ai2[1][j] * d1);
        }

        g.setColor(WING_COLOR);
        g.fillPolygon(ai3[0], ai3[1], 6);
        g.fillPolygon(ai4[0], ai4[1], 6);
        g.setColor(HEAD_COLOR);
        g.drawLine(ai3[0][0], ai3[1][0], ai3[0][5], ai3[1][5]);
        g.drawLine(ai3[0][4], ai3[1][4], ai3[0][5], ai3[1][5]);
        g.drawLine(ai4[0][0], ai4[1][0], ai4[0][5], ai4[1][5]);
        g.drawLine(ai4[0][4], ai4[1][4], ai4[0][5], ai4[1][5]);
        float ai5[][] = {
            {
                9, 14, 9, 20
            }, {
                -6, 0, 6, 0
            }
        };
        float ai6[][] = new float[2][4];
        for(int k = 0; k < 4; k++)
        {
            ai6[0][k] = x_ + (float)((float)ai5[0][k] * d1 + (float)ai5[1][k] * d);
            ai6[1][k] = y_ + (float)((float)ai5[0][k] * d - (float)ai5[1][k] * d1);
        }

        g.fillPolygon(ai6[0], ai6[1], 4);
    }

}
