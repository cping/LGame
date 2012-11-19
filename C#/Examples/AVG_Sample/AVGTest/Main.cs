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

namespace AVGTest
{

    public class Main : LFXPlus
    {
        public override void OnMain()
        {
            //加载LGame默认资源(不进行此操作，LGame内置的模拟按钮之类功能无法使用)
          
            XNAFont = new LFont("Content/Fonts", "black", 0, 20);
            LSetting setting = new LSetting();
            setting.fps = 60;
            setting.width = 480;
            setting.height = 320;
            setting.showFPS = true;
            setting.landscape = true;

            Register(setting, typeof(AVGTitle));

            /*

            //加载LGame使用的默认字体(不进行此操作，LGame默认的DrawString之类函数无法使用(PS:XNAConfig载入
            //默认包时，可获得部分英文字体支持))
            XNAFont = new LFont("Content/Fonts", "black", 0, 20);
            MaxScreen(480, 320);
            //设定初始化屏幕为竖屏，自动最大化游戏画面为屏幕大小
            Initialization(true, LMode.Fill);
            //帧数60
            SetFPS(60);
            //显示帧数
            SetShowFPS(true);
            //加载Screen
            SetScreen(new AVGTitle());
            //显示Screen
            ShowScreen();*/
        }

        public override void OnGameResumed()
        {

        }

        public override void OnGamePaused()
        {

        }
    }
}
