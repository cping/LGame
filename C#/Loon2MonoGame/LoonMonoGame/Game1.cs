using java.lang;
using loon;
using loon.monogame;
using loon.opengl;
using System;

namespace LoonMonoGame
{
    public class Game1 : Loon
    {
        /// <summary>
        /// Screen代码待移植中，暂时无功能，尽量做到1:1移植，所以进度比较慢(每天抽空写一点，计划是今年(2020)12月移植完C#版,明年1月左右移植完C++版,然后写Java版到C++与C#版自动转化代码)……
        /// </summary>
		public class MyScreen : Screen
        {
            public MyScreen()
            {
                LSystem.I("log format testing...");
                LSystem.I("newline");
                LSystem.I("newline");
                //get file
                JavaSystem.Out.Println(LSystem.Base.Assets().GetTextSync("test.txt"));
                JavaSystem.Out.Println(LSystem.Base.Assets().GetBytesSync("test.txt").Length == 14);
            }

            public override void Draw(GLEx g)
            {
                throw new NotImplementedException();
            }

            public override void Resize(int width, int height)
            {
                throw new NotImplementedException();
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
