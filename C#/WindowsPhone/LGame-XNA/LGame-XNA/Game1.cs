using System;
using System.Collections.Generic;
using System.Linq;
using Loon;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Resource;
using Loon.Action.Sprite;
using Loon.Action.Map.Tmx;
using Loon.Core.Input;
using Loon.Core.Graphics.Component;
using Loon.Core;
using Loon.Media;
using Loon.Core.Event;
using Loon.Utils.Debugging;
using Loon.Core.Geom;

namespace LGame_XNA
{

    public class HelloWorld2 : SpriteBatchScreen
    {

        SpriteFont font;

        public override void Create()
        {
            font = SpriteFont.Read("assets/ScoreFont.pak");
        }

        public override void After(Loon.Action.Sprite.SpriteBatch batch)
        {
           // batch.Draw(font,"ABCD\nEF", 66, 66,LColor.red);
           batch.Draw(font, "Test", new Vector2f(150, 150), LColor.red, 0,
                     Vector2f.Zero, new Vector2f(1f, 1f), SpriteEffects.None);
        }

        public override void Before(Loon.Action.Sprite.SpriteBatch batch)
        {
           
        }

        public override void Press(LKey e)
        {
          
        }

        public override void Release(LKey e)
        {
     
        }

        public override void Update(long elapsedTime)
        {
           
        }

        public override void Close()
        {
           
        }

        public override void TouchDown(LTouch e)
        {
         
        }

        public override void TouchUp(LTouch e)
        {
         
        }

        public override void TouchMove(LTouch e)
        {
           
        }

        public override void TouchDrag(LTouch e)
        {
         
        }
    }

    public class HelloWorld : Screen,EmulatorListener
    {
        TMXTiledMap map;

        //GLRenderer renderer;
  
        public override void OnLoad()
        {
            //renderer = new GLRenderer();
            map = new TMXTiledMap("assets/testmap.tmx");
            LInfo info = new LInfo(300, 300);
            info.SetLocked(true);
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
                /*this.renderer.Begin(GLType.Filled);
                this.renderer.SetColor(LColor.red);
                this.renderer.Oval(166, 166, 20);
                this.renderer.End();*/
            }
        }

        public override void Alter(Loon.Core.Timer.LTimerContext timer)
        {
          
        }

        /*public class play : Updateable
        {
            public void Action()
            {
                SoundEffect explosion = LSystem.screenActivity.GameRes.Load<SoundEffect>("Content/decide1");
                explosion.Play();
            }
        }*/

        public override void TouchDown(Loon.Core.Input.LTouch e)
        {
         //   PlaySound("decide1.wav");
            //LSystem.Unload(new play());
          ///  SoundPlayer audio = new SoundPlayer("decide1");
           // audio.Start();
         
           // PlaySound("000");

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


        public void OnUpClick()
        {
            Log.DebugWrite("Up");
        }

        public void OnLeftClick()
        {
            Log.DebugWrite("Left");
        }

        public void OnRightClick()
        {
            Log.DebugWrite("Right");
        }

        public void OnDownClick()
        {
            Log.DebugWrite("Down");
        }

        public void OnTriangleClick()
        {
            Log.DebugWrite("Triangle");
        }

        public void OnSquareClick()
        {
            Log.DebugWrite("Square");
        }

        public void OnCircleClick()
        {
            Log.DebugWrite("Circle");
        }

        public void OnCancelClick()
        {
            Log.DebugWrite("Cancel");
        }

        public void UnUpClick()
        {
            Log.DebugWrite("UnUp");
        }

        public void UnLeftClick()
        {
            Log.DebugWrite("UnLeft");
        }

        public void UnRightClick()
        {
            Log.DebugWrite("UnRight");
        }

        public void UnDownClick()
        {
            Log.DebugWrite("UnDown");
        }

        public void UnTriangleClick()
        {
            Log.DebugWrite("UnTriangle");
        }

        public void UnSquareClick()
        {
            Log.DebugWrite("UnSquare");
        }

        public void UnCircleClick()
        {
            Log.DebugWrite("UnCircle");
        }

        public void UnCancelClick()
        {
            Log.DebugWrite("UnCancel");
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
