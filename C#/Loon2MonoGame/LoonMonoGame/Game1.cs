using java.lang;
using loon;
using loon.events;
using loon.monogame;
using loon.opengl;
using loon.utils.timer;
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
                LSystem.I("\n{3}\n{1}\n{2}\n{0}", "只缘身在此山中", "远近高低各不同", "不识庐山真面目", "远看成岭侧成峰");
                //get file
                JavaSystem.Out.Println(LSystem.Base.Assets().GetTextSync("test.txt"));
                JavaSystem.Out.Println(LSystem.Base.Assets().GetBytesSync("test.txt").Length == 14);
            }

            public override void Alter(LTimerContext context)
            {
                throw new NotImplementedException();
            }

            public override void Draw(GLEx g)
            {
                throw new NotImplementedException();
            }

            public override void Resize(int width, int height)
            {
                throw new NotImplementedException();
            }

            public override void TouchDown(GameTouch e)
            {

                JavaSystem.Out.Println("down");
            }

            public override void TouchDrag(GameTouch e)
            {
                JavaSystem.Out.Println("drag");
            }

            public override void TouchMove(GameTouch e)
            {
                JavaSystem.Out.Println("move");
            }

            public override void TouchUp(GameTouch e)
            {
                JavaSystem.Out.Println("up");
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
        }
    }
}
