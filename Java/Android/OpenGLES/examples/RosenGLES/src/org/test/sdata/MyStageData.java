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

public class MyStageData {
	 public final StageData[] sData = new StageData[3];
     private StageData stage1_1 = new StageData();
     private StageData stage1_2;
     private StageData stage1_3;

     public MyStageData()
     {
         this.stage1_1.player_vy = 0f;
         this.stage1_1.player_y = 0f;
         this.stage1_1.bbs = new StageData.bb[] { new StageData.bb(500f, 500f, -500f, 0f), new StageData.bb(500f, 500f, 0f, 350f), new StageData.bb(500f, 500f, 500f, 250f), new StageData.bb(500f, 500f, 1000f, 350f), new StageData.bb(200f, 50f, 1600f, 350f), new StageData.bb(200f, 50f, 1900f, 300f), new StageData.bb(200f, 50f, 2200f, 350f), new StageData.bb(200f, 50f, 2500f, 300f), new StageData.bb(500f, 500f, 2800f, 250f), new StageData.bb(500f, 500f, 3300f, 250f), new StageData.bb(500f, 500f, 3300f, -360f), new StageData.bb(500f, 500f, 3800f, -360f), new StageData.bb(500f, 500f, 4300f, -360f), new StageData.bb(500f, 500f, 3910f, 140f) };
         this.stage1_1.e1s = new StageData.enemy[] { new StageData.enemy(600f, 150f), new StageData.enemy(1800f, 200f), new StageData.enemy(2650f, 200f) };
         this.stage1_1.e2s = new StageData.enemy[] { new StageData.enemy(1400f, 350f), new StageData.enemy(2350f, 350f), new StageData.enemy(3100f, 250f), new StageData.enemy(3300f, 250f), new StageData.enemy(3400f, 250f), new StageData.enemy(3550f, 250f), new StageData.enemy(3600f, 250f) };
         this.stage1_1.mbgs = new StageData.mbg[] { new StageData.mbg(0.45f, -6f, 0) };
         this.stage1_1.bbType = 0;
         this.stage1_1.numMBG = 1;
         this.stage1_1.numMFG = 0;
         this.stage1_1.numMbb = 0;
         this.stage1_1.moveType = 0;
         this.stage1_1.G = 0.8f;
         this.stage1_1.dG = false;
         this.stage1_1.numbb = 13;
         this.stage1_2 = new StageData();
         this.stage1_2.player_vy = 0f;
         this.stage1_2.player_y = 0f;
         this.stage1_2.bbs = new StageData.bb[] { 
             new StageData.bb(500f, 500f, -250f, -150f), new StageData.bb(500f, 500f, -525f, 350f), new StageData.bb(500f, 500f, -25f, 350f), new StageData.bb(500f, 500f, 475f, 350f), new StageData.bb(500f, 500f, 975f, 250f), new StageData.bb(100f, 50f, 1600f, 350f), new StageData.bb(100f, 50f, 1800f, 300f), new StageData.bb(100f, 50f, 2000f, 250f), new StageData.bb(100f, 50f, 2200f, 200f), new StageData.bb(500f, 500f, 3100f, 200f), new StageData.bb(500f, 500f, 3600f, 400f), new StageData.bb(500f, 500f, 4100f, 400f), new StageData.bb(500f, 500f, 4600f, 0f), new StageData.bb(50f, 50f, 2400f, 200f, -0.04f, 200f), new StageData.bb(50f, 50f, 2600f, 200f, -0.04f, 200f), new StageData.bb(50f, 50f, 2800f, 200f, -0.04f, 200f), 
             new StageData.bb(50f, 50f, 3000f, 200f, -0.04f, 200f), new StageData.bb(150f, 50f, 3975f, 280f, -0.04f, 200f), new StageData.bb(150f, 50f, 4025f, 160f, 0.04f, 200f), new StageData.bb(150f, 50f, 4075f, 40f, -0.04f, 200f)
          };
         this.stage1_2.e1s = new StageData.enemy[] { new StageData.enemy(600f, 150f), new StageData.enemy(1200f, 200f), new StageData.enemy(2200f, 150f) };
         this.stage1_2.e2s = new StageData.enemy[] { new StageData.enemy(700f, 350f), new StageData.enemy(800f, 350f), new StageData.enemy(900f, 350f), new StageData.enemy(1080f, 250f), new StageData.enemy(1180f, 250f), new StageData.enemy(1230f, 250f) };
         this.stage1_2.mbgs = new StageData.mbg[] { new StageData.mbg(0.2f, 0f, 1), new StageData.mbg(0.6f, 0f, 2) };
         this.stage1_2.bbType = 0;
         this.stage1_2.numMBG = 2;
         this.stage1_2.numMFG = 0;
         this.stage1_2.numMbb = 11;
         this.stage1_2.moveType = 0;
         this.stage1_2.G = 0.8f;
         this.stage1_2.dG = false;
         this.stage1_2.numbb = 12;
         this.stage1_3 = new StageData();
         this.stage1_3.player_vy = -24f;
         this.stage1_3.player_y = 640f;
         this.stage1_3.bbs = new StageData.bb[] { new StageData.bb(500f, 500f, -470f, 0f), new StageData.bb(500f, 500f, 610f, 0f), new StageData.bb(580f, 50f, 30f, -20f), new StageData.bb(200f, 50f, 30f, 450f), new StageData.bb(200f, 50f, 410f, 450f) };
         this.stage1_3.e1s = new StageData.enemy[0];
         this.stage1_3.e2s = new StageData.enemy[0];
         this.stage1_3.mbgs = new StageData.mbg[0];
         this.stage1_3.bbType = 0;
         this.stage1_3.numMBG = 0;
         this.stage1_3.numMFG = 0;
         this.stage1_3.numMbb = 0;
         this.stage1_3.moveType = 1;
         this.stage1_3.G = 0.8f;
         this.stage1_3.dG = false;
         this.stage1_3.numbb = 5;
         this.sData[0] = this.stage1_1;
         this.sData[1] = this.stage1_2;
         this.sData[2] = this.stage1_3;
     }
}
