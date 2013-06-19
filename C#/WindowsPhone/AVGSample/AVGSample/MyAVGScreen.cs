using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.Component;
using Loon.Action.Avg;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Graphics;
using Loon.Action.Avg.Drama;

namespace AVGTest
{
    public class MyAVGScreen : AVGScreen
    {

        LPaper roleName;

        String flag = "自定义命令.";

        String[] selects = { "鹏凌三千帅不帅？" };

        int type;

        public MyAVGScreen()
            : base("assets/script/s1.txt", AVGDialog.GetRMXPDialog("assets/w6.png",
                460, 150))
        {

        }

        public override void OnLoading()
        {
            roleName = new LPaper("assets/name0.png", 25, 25);
            LeftOn(roleName);
            roleName.SetLocation(5, 15);
            Add(roleName);
        }

        public override void DrawScreen(GLEx g)
        {
            switch (type)
            {
                case 1:
                    g.SetAntiAlias(true);
                    g.DrawSixStart(LColor.yellow, 130, 100, 100);
                    g.SetAntiAlias(false);
                    break;
            }
            g.ResetColor();
        }

        LButton yes;

        public override void InitCommandConfig(Command command)
        {
            // 初始化时预设变量
            command.SetVariable("p", "assets/p.png");
            command.SetVariable("sel0", selects[0]);
        }

        public override void InitMessageConfig(LMessage message)
        {

        }

        public override void InitSelectConfig(LSelect select)
        {
        }

        class YesClick : ClickListener
        {
            public void DoClick(LComponent comp)
            {
             
            }

            public void DownClick(LComponent comp, float x, float y)
            {

                if (comp.Tag is AVGScreen)
                {
                    AVGScreen screen = (AVGScreen)comp.Tag;
                    // 解除锁定
                    screen.SetLocked(false);
                    // 触发事件
                    // click();
                    // 删除当前按钮
                    screen.Remove(comp);
                }
            }

            public void UpClick(LComponent comp, float x, float y)
            {

            }

            public void DragClick(LComponent comp, float x, float y)
            {

            }
        }

        public override bool NextScript(String mes)
        {

            // 自定义命令（有些自定义命令为了突出写成了中文，实际不推荐）
            if (roleName != null)
            {
                if ("noname".Equals(mes, StringComparison.InvariantCultureIgnoreCase))
                {
                    roleName.SetVisible(false);
                }
                else if ("name0".Equals(mes, StringComparison.InvariantCultureIgnoreCase))
                {
                    roleName.SetVisible(true);
                    roleName.SetBackground("assets/name0.png");
                    roleName.SetLocation(5, 15);
                }
                else if ("name1".Equals(mes, StringComparison.InvariantCultureIgnoreCase))
                {
                    roleName.SetVisible(true);
                    roleName.SetBackground("assets/name1.png");
                    roleName.SetLocation(GetWidth() - roleName.GetWidth() - 5, 15);
                }
            }
            if ((flag + "星星").Equals(mes, StringComparison.InvariantCultureIgnoreCase))
            {
                // 添加脚本事件标记（需要点击后执行）
                SetScrFlag(true);
                type = 1;
                return false;
            }
            else if ((flag + "去死吧，星星").Equals(mes, StringComparison.InvariantCultureIgnoreCase))
            {
                type = 0;
            }
            else if ((flag + "关于天才").Equals(mes, StringComparison.InvariantCultureIgnoreCase))
            {
                message.SetVisible(false);
                SetScrFlag(true);
                // 强行锁定脚本
                SetLocked(true);
                yes = new LButton("assets/dialog_yes.png", 112, 33);
                yes.Tag = this;
                yes.Click = new YesClick();
                CenterOn(yes);
                Add(yes);
                return false;
            }
            return true;
        }

        public override void OnExit()
        {
            // 重新返回标题画面
            SetScreen(new AVGTitle());
        }

        public override void OnSelect(String message, int type)
        {
            if (selects[0].Equals(message, StringComparison.InvariantCultureIgnoreCase))
            {
                command.SetVariable("sel0", Convert.ToString(type));
            }
        }

    }
}
