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
using Loon.Core.Graphics.Opengl;

namespace LLKSample
{
    public class Main : LGame
    {
        public override void OnMain()
        {
            LTexture.ALL_LINEAR = true;
            LSetting setting = new LSetting();
            setting.showFPS = true;
            setting.landscape = true;
            Register(setting,typeof(LLKScreen));
        }

        public override void OnGameResumed()
        {

        }

        public override void OnGamePaused()
        {

        }
    }
}
