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

namespace ACTSample
{
    public class Game1 : LGame
    {

        public override void OnMain()
        {
            LSetting setting = new LSetting();
            setting.showFPS = true;
            setting.landscape = true;
            Register(setting, typeof(GameMapTest));
        }

        public override void OnGameResumed()
        {
         
        }

        public override void OnGamePaused()
        {
            
        }
    }
}
