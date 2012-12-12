using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Input.Touch;
using Microsoft.Xna.Framework.Media;
using Loon;
using Loon.Core.Graphics;
using Loon.Utils.Debug;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Resource;
using Loon.Action.Sprite;
using Loon.Action.Map.Tmx;
using Loon.Core.Input;
using Loon.Core.Graphics.Component;
using Loon.Core;

namespace LGame_XNA
{
    public class HelloWorld : Screen
    {
        TMXTiledMap map;
  
        public override void OnLoad()
        {
            map = new TMXTiledMap("assets/testmap.tmx");
            LInfo info = new LInfo(300, 300);
            info.SetLocation(5, 0);
            info.PutMessage("传说中的");
            Add(info);
      
        }

        public override void Draw(Loon.Core.Graphics.Opengl.GLEx g)
        {
            if (IsOnLoadComplete())
            {
                map.Draw(g, 5, 5);
                g.DrawString("默认支持(DLL中自带)完整中文字库", 26, 66);
            }
        }

        public override void Alter(Loon.Core.Timer.LTimerContext timer)
        {
          
        }

        public override void TouchDown(Loon.Core.Input.LTouch e)
        {
          
        }

        public override void TouchUp(Loon.Core.Input.LTouch e)
        {
           
        }

        public override void TouchMove(Loon.Core.Input.LTouch e)
        {
            
        }

        public override void TouchDrag(Loon.Core.Input.LTouch e)
        {
           
        }

    }


    public class Game1 : LGame
    {

        public override void OnMain()
        {

            LSetting setting = new LSetting();
            setting.showLogo = false;
            setting.showFPS = true;
            setting.landscape = false;
            Register(setting, typeof(HelloWorld));
        }

        public override void OnGameResumed()
        {
            
        }

        public override void OnGamePaused()
        {
            
        }
    }
}
