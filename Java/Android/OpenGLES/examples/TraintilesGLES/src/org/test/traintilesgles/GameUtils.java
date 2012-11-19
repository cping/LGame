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
package org.test.traintilesgles;

import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;

public class GameUtils {
	
     private static int gamerandom;
     private static int screenH;
     private static int screenW;
     private static int[] sinTable = new int[] { 
         0, 0x8e, 0x11d, 0x1ac, 0x23b, 0x2c9, 0x358, 0x3e6, 0x474, 0x501, 0x58e, 0x61b, 0x6a7, 0x732, 0x7bd, 0x848, 
         0x8d2, 0x95b, 0x9e3, 0xa6b, 0xaf1, 0xb77, 0xbfc, 0xc80, 0xd03, 0xd86, 0xe07, 0xe87, 0xf05, 0xf83, 0x1000, 0x107b, 
         0x10f5, 0x116d, 0x11e4, 0x125a, 0x12cf, 0x1342, 0x13b3, 0x1423, 0x1491, 0x14fe, 0x1569, 0x15d2, 0x163a, 0x16a0, 0x1704, 0x1767, 
         0x17c7, 0x1826, 0x1883, 0x18de, 0x1937, 0x198e, 0x19e3, 0x1a36, 0x1a87, 0x1ad6, 0x1b23, 0x1b6d, 0x1bb6, 0x1bfc, 0x1c41, 0x1c83, 
         0x1cc2, 0x1d00, 0x1d3b, 0x1d74, 0x1dab, 0x1ddf, 0x1e11, 0x1e41, 0x1e6f, 0x1e9a, 0x1ec2, 0x1ee8, 0x1f0c, 0x1f2e, 0x1f4c, 0x1f69, 
         0x1f83, 0x1f9b, 0x1fb0, 0x1fc2, 0x1fd3, 0x1fe0, 0x1fec, 0x1ff4, 0x1ffb, 0x1ffe, 0x2000
      };

     public static int cos(int angle)
     {
         return sin(90 - angle);
     }

     public static int getRandom()
     {
         gamerandom = (gamerandom + 0x275b23) ^ 0xe8ac269;
         return (((gamerandom & 0xffff) + (gamerandom >> 0x10)) & 0xfffffff);
     }

     public static int getRandomSeed()
     {
         return gamerandom;
     }

     public static int getScreenH()
     {
         return screenH;
     }

     public static int getScreenW()
     {
         return screenW;
     }

     public static void initRandom(int startvalue)
     {
         gamerandom = startvalue;
     }

     public static boolean isInside(Vector2f aPoint, RectBox aRect)
     {
         return ((((aPoint.x > aRect.x) && (aPoint.x < (aRect.x + aRect.width))) && (aPoint.y > aRect.y)) && (aPoint.y < (aRect.y + aRect.height)));
     }

     public static boolean isInside(int x1, int y1, int x2, int y2, int w, int h)
     {
         return ((((x1 > x2) && (x1 < (x2 + w))) && (y1 > y2)) && (y1 < (y2 + h)));
     }

     public static String levelPath(String file)
     {
         return file;
     }

     public static boolean paintButton(Painter painter, GameCore game, int x, int y, Sprite sprite, int frame, int selectedframe, int padding, boolean disable)
     {
         int num = sprite.getWidth();
         int num2 = sprite.getHeight();
         int num3 = game.getMouseX();
         int num4 = game.getMouseY();
         boolean flag = false;
         boolean flag2 = (((num3 > (x - padding)) && (num3 < ((x + num) + padding))) && (num4 > (y - padding))) && (num4 < ((y + num2) + padding));
         if ((!disable && (game.isMouseDown() || game.isMouseUp())) && flag2)
         {
             if (game.isMouseUp())
             {
                 flag = true;
             }
             if (frame == selectedframe)
             {
                 painter.setOpacity(0.7f);
             }
             else
             {
                 frame = selectedframe;
             }
         }
         sprite.Paint(painter, (float) x, (float) y, frame);
         painter.setOpacity(1.0f);
         return flag;
     }

     public static String resourcePath(String file)
     {
         return file;
     }

     public static void setScreenH(int value)
     {
         screenH = value;
     }

     public static void setScreenW(int value)
     {
         screenW = value;
     }

     public static int sin(int angle)
     {
         angle = angle % 360;
         if (angle < 0)
         {
             angle += 360;
         }
         if (angle <= 90)
         {
             return sinTable[angle];
         }
         if (angle <= 180)
         {
             return sinTable[180 - angle];
         }
         if (angle <= 270)
         {
             return -sinTable[angle - 180];
         }
         return -sinTable[360 - angle];
     }
}
