using java.lang;
using loon;
using loon.canvas;
using loon.monogame;
using loon.utils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;

namespace LoonMonoGame
{
    public class Game1 : Loon
    {
        /// <summary>
        /// Screen代码待移植中，暂时无功能，尽量做到1:1移植，所以进度比较慢……
        /// </summary>
		public class MyScreen : Screen
        {
            public MyScreen()
            {

                LSystem.I("log format testing...");
                LSystem.I("newline");
                LSystem.I("newline");
            }

        }

        public override void OnMain()
        {
            MonoGameSetting setting = new MonoGameSetting
            {
                isFPS = true,
                isDisplayLog = true,
                isDebug = true,
                isMemory = false,
                isLogo = false,
                fullscreen = false,
                width = 480,
                height = 320,
                width_zoom = 640,
                height_zoom = 480,
                logoPath = "loon_logo.png",
                fps = 60,
                fontName = "Dialog",
                appName = "test",
                emulateTouch = false
            };

            //Lambda方式注入运行参数与显示用Screen
            Register(setting, () => { return new MyScreen(); });

			/**
			 * //1123418112

			JavaSystem.Out.Println(NumberUtils.FloatToIntBits(123f));


            //2079648233
            JavaSystem.Out.Println(LColor.GetARGB(244, 233, 233, 123));
            //ff000000
            JavaSystem.Out.Println(new LColor(LColor.TRANSPARENT));
            //7bf4e9e9
            JavaSystem.Out.Println(new LColor(244, 233, 233, 123));
            //1,2,3,4
            JavaSystem.Out.Println(new LColor(1, 2, 3, 4).GetXNAColor());*/
        }
    }
}
