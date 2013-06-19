using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Component;
using Loon.Core.Input;
using Loon.Core.Timer;
using Loon.Core.Graphics.Opengl;
using Loon.Action.Sprite.Effect;
using Loon.Core.Event;
using Loon.Core;

namespace AVGTest
{

    public class AVGTitle : Screen
    {

        LButton start, end;

        LPaper title;

        class StartClick : ClickListener
        {

            ActionKey action = new ActionKey(
                    ActionKey.DETECT_INITIAL_PRESS_ONLY);

            public void DoClick(LComponent comp)
            {
                if (!action.IsPressed())
                {
                    action.Press();
                    if (comp.Tag is Screen)
                    {
                        Screen screen = (Screen)comp.Tag;
                        screen.ReplaceScreen(new MyAVGScreen(), MoveMethod.FROM_LEFT);
                    }
                }
            }

            public void DownClick(LComponent comp, float x, float y)
            {
             
            }

            public void UpClick(LComponent comp, float x, float y)
            {

            }

            public void DragClick(LComponent comp, float x, float y)
            {

            }
        }

        public AVGTitle()
        {

        }

        public override LTransition OnTransition()
        {
            return LTransition.NewArc();
        }

        public override void OnLoad()
        {
            // 设置指定背景图
           // SetBackground("assets/back2.png");

            // 变更背景
            SetBackground("assets/back1.png");

            // 创建一个开始按钮，按照宽191，高57分解按钮图，并设定其Click事件
            start = new LButton("assets/title_start.png", 191, 57);
            // 设定按钮位置为x=2,y=5
            start.SetLocation(2, 5);
            // 设定此按钮不可用
            start.SetEnabled(false);
            start.Tag = this;
            start.Click = new StartClick();
            // 添加按钮
            Add(start);

            // 创建一个记录读取按钮，按照宽160，高56分解按钮图
            LButton btn2 = new LButton("assets/title_load.png", 160, 56);
            // 设定按钮位置为x=2,y=start位置类推
            btn2.SetLocation(2, start.GetY() + start.GetHeight() + 20);
            // 设定此按钮不可用
            btn2.SetEnabled(false);
            // 添加按钮
            Add(btn2);

            // 创建一个环境设置按钮，按照宽215，高57分解按钮图
            LButton btn3 = new LButton("assets/title_option.png", 215, 57);
            // 设定按钮位置为x=2,y=btn2位置类推
            btn3.SetLocation(2, btn2.GetY() + btn2.GetHeight() + 20);
            // 设定此按钮不可用
            btn3.SetEnabled(false);
            // 添加按钮
            Add(btn3);

            // 创建一个退出按钮，按照宽142，高57分解按钮图，并设定其Click事件
            end = new LButton("assets/title_end.png", 142, 57);
            // 设定按钮位置为x=2,y=btn3位置类推
            end.SetLocation(2, btn3.GetY() + btn3.GetHeight() + 20);
            // 设定此按钮不可用
            end.SetEnabled(false);
            // 添加按钮
            Add(end);
            // 增加一个标题
            title = new LPaper("assets/title.png", -200, 0);
            // 添加标题
            Add(title);
        }

        public override void Alter(LTimerContext c)
        {
            // 初始化完毕
            if (IsOnLoadComplete())
            {
                // 标题未达到窗体边缘
                if (title.GetX() + title.GetWidth() + 25 <= GetWidth())
                {
                    // 以三倍速移动（红色无角……）
                    title.Move_right(3);
                }
                else
                {
                    // 设定开始按钮可用
                    start.SetEnabled(true);
                    // 设定结束按钮可用
                    end.SetEnabled(true);
                }
            }
        }

        public override void Draw(GLEx g)
        {
        }

        public override void TouchDown(LTouch touch)
        {

        }

        public override void TouchDrag(LTouch e)
        {

        }

        public override void TouchMove(LTouch e)
        {

        }

        public override void TouchUp(LTouch touch)
        {

        }


    }
}
