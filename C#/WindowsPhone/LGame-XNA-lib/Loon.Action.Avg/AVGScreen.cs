#region LGame License
/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email£ºjavachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
namespace Loon.Action.Avg
{
    using System;
    using System.IO;
    using System.Runtime.CompilerServices;
    using System.Collections;
    using Microsoft.Xna.Framework;
    using Loon.Core.Graphics;
    using Loon.Java;
    using Loon.Core.Graphics.Component;
    using Loon.Action.Sprite;
    using Loon.Core.Graphics.Opengl;
    using Loon.Utils;
    using Loon.Action.Sprite.Effect;
    using Loon.Media;
    using Loon.Core;
    using Loon.Core.Timer;
    using Loon.Core.Input;
    using Loon.Action.Avg.Drama;
    using Loon.Core.Graphics.Device;

    public abstract class AVGScreen : Screen, Runnable
    {

        private object synch = new object();

        private bool isSelectMessage, scrFlag, isRunning, running;

        private int delay;

        private string scriptName;

        private string selectMessage;

        private string dialogFileName;

        private bool locked;

        private LColor color;

        protected internal Command command;

        protected internal LTexture dialog;

        protected internal AVGCG scrCG;

        protected internal LSelect select;

        protected internal LMessage message;

        protected internal Desktop desktop;

        protected internal Sprites sprites;

        private Thread avgThread;

        private bool autoPlay;

        public AVGScreen(string initscript, string initdialog):this(initscript, new LTexture(initdialog))
        {
            
        }

        public AVGScreen(string initscript, LTexture img)
        {
            if (initscript == null)
            {
                return;
            }
            this.scriptName = initscript;
            if (img != null)
            {
                this.dialogFileName = img.GetFileName();
                this.dialog = img;
            }
        }

        public AVGScreen(string initscript)
        {
            if (initscript == null)
            {
                return;
            }
            this.scriptName = initscript;
        }

        public override void OnCreate(int width, int height)
        {
            base.OnCreate(width, height);
            this.SetRepaintMode(Screen.SCREEN_NOT_REPAINT);
            this.delay = 30;
            if (dialog == null && dialogFileName != null)
            {
                this.dialog = new LTexture(dialogFileName);
            }
            this.running = true;
        }

        public override void OnLoad()
        {

        }

        public override void OnLoaded()
        {
            this.avgThread = new Thread(this,"AVGThread");
            this.avgThread.Start();
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void InitDesktop()
        {
            if (desktop != null && sprites != null)
            {
                return;
            }
            this.desktop = new Desktop(this, GetWidth(), GetHeight());
            this.sprites = new Sprites(GetWidth(), GetHeight());
            if (dialog == null)
            {
                LImage tmp = LImage.CreateImage(GetWidth() - 20,
            GetHeight() / 2 - 20, true);
                LGraphics g = tmp.GetLGraphics();
                g.SetColor(0, 0, 0, 125);
                g.FillRect(0, 0, tmp.GetWidth(), tmp.GetHeight());
                g.Dispose();
                g = null;
                dialog = new LTexture(GLLoader.GetTextureData(tmp));
                if (tmp != null)
                {
                    tmp.Dispose();
                    tmp = null;
                }
            }
            this.message = new LMessage(dialog, 0, 0);
            this.message.SetFontColor(LColor.white);
            int size = message.GetWidth() / (message.GetMessageFont().GetSize());
            if (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1)
            {
                if (size % 2 != 0)
                {
                    size = size + 2;
                }
                else
                {
                    size = size + 3;
                }
            }
            else
            {
                if (size % 2 != 0)
                {
                    size = size - 3;
                }
                else
                {
                    size = size - 4;
                }
            }
            this.message.SetMessageLength(size);
            this.message.SetLocation((GetWidth() - message.GetWidth()) / 2,
                    GetHeight() - message.GetHeight() - 10);
            this.message.SetVisible(false);
            this.select = new LSelect(dialog, 0, 0);
            this.select.SetLocation(message.X(), message.Y());
            this.scrCG = new AVGCG();
            this.desktop.Add(message);
            this.desktop.Add(select);
            this.select.SetVisible(false);
        }

        public abstract bool NextScript(string message);

        public abstract void OnSelect(string message, int type);

        public abstract void InitMessageConfig(LMessage message);

        public abstract void InitSelectConfig(LSelect select);

        public abstract void InitCommandConfig(Command command);

        public void Select(int type)
        {
            if (command != null)
            {
                command.Select(type);
                isSelectMessage = false;
            }
        }

        public string GetSelect()
        {
            if (command != null)
            {
                return command.GetSelect();
            }
            return null;
        }

        public override void Add(LComponent c)
        {
            if (desktop == null)
            {
                InitDesktop();
            }
            desktop.Add(c);

        }

        public override void Add(ISprite s)
        {
            if (sprites == null)
            {
                InitDesktop();
            }
            sprites.Add(s);
        }

        public override void Remove(ISprite sprite)
        {
            sprites.Remove(sprite);

        }

        public override void Remove(LComponent comp)
        {
            desktop.Remove(comp);
        }

        public override void RemoveAll()
        {
            sprites.RemoveAll();
            desktop.GetContentPane().Clear();
        }

        public override void Draw(GLEx g)
        {
            if (!running || !IsOnLoadComplete() || IsClose())
            {
                return;
            }
            if (scrCG == null)
            {
                return;
            }
            if (scrCG.sleep == 0)
            {
                scrCG.Paint(g);
                DrawScreen(g);
                if (desktop != null)
                {
                    desktop.CreateUI(g);
                }
                if (sprites != null)
                {
                    sprites.CreateUI(g);
                }
            }
            else
            {
                scrCG.sleep--;
                if (color != null)
                {
                    float alpha = (float)(scrCG.sleepMax - scrCG.sleep)
                            / scrCG.sleepMax;
                    if (alpha > 0 && alpha < 1.0)
                    {
                        if (scrCG.getBackgroundCG() != null)
                        {
                            g.DrawTexture(scrCG.getBackgroundCG(), 0, 0);
                        }
                        LColor c = g.GetColor();
                        g.SetColor(color.R, color.G, color.B, (byte)(alpha * 255));
                        g.FillRect(0, 0, GetWidth(), GetHeight());
                        g.SetColor(c);
                    }
                    else
                    {
                        LColor c = g.GetColor();
                        g.SetColor(color);
                        g.FillRect(0, 0, GetWidth(), GetHeight());
                        g.SetColor(c);
                    }
                }
                if (scrCG.sleep <= 0)
                {
                    scrCG.sleep = 0;
                    color = null;
                }
                g.SetAlpha(1.0f);
            }
        }

        public abstract void DrawScreen(GLEx g);

        public override void Alter(LTimerContext context)
        {
        }

        public void NextScript()
        {
            lock (synch)
            {
                if (command != null && !IsClose() && running)
                {
                    for (; isRunning = command.Next(); )
                    {
                        string result = command.DoExecute();
                        if (result == null)
                        {
                            continue;
                        }
                        if (!NextScript(result))
                        {
                            break;
                        }
                        IList commands = Command.SplitToList(result, " ");
                        int size = commands.Count;
                        string cmdFlag = (string)commands[0];

                        string mesFlag = null, orderFlag = null, lastFlag = null;
                        if (size == 2)
                        {
                            mesFlag = (string)commands[1];
                        }
                        else if (size == 3)
                        {
                            mesFlag = (string)commands[1];
                            orderFlag = (string)commands[2];
                        }
                        else if (size == 4)
                        {
                            mesFlag = (string)commands[1];
                            orderFlag = (string)commands[2];
                            lastFlag = (string)commands[3];
                        }
                        if (cmdFlag.Equals(CommandType.L_APLAY,StringComparison.InvariantCultureIgnoreCase))
                        {
                            autoPlay = true;
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_ASTOP,StringComparison.InvariantCultureIgnoreCase))
                        {
                            autoPlay = false;
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_ADELAY,StringComparison.InvariantCultureIgnoreCase))
                        {
                            if (mesFlag != null)
                            {
                                if (MathUtils.IsNan(mesFlag))
                                {
                                    autoTimer.SetDelay(int.Parse(mesFlag));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_WAIT,StringComparison.InvariantCultureIgnoreCase))
                        {
                            scrFlag = true;
                            break;
                        }
                        if (cmdFlag.Equals(CommandType.L_SNOW,StringComparison.InvariantCultureIgnoreCase)
                                || cmdFlag.Equals(CommandType.L_RAIN,StringComparison.InvariantCultureIgnoreCase)
                                || cmdFlag.Equals(CommandType.L_PETAL,StringComparison.InvariantCultureIgnoreCase))
                        {
                            if (sprites != null)
                            {
                                bool flag = false;
                                ISprite[] ss = sprites.GetSprites();

                                for (int i = 0; i < ss.Length; i++)
                                {
                                    ISprite s = ss[i];
                                    if (s is FreedomEffect)
                                    {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag)
                                {
                                    if (cmdFlag
                                            .Equals(CommandType.L_SNOW))
                                    {
                                        sprites.Add(FreedomEffect.GetSnowEffect());
                                    }
                                    else if (cmdFlag
                                          .Equals(CommandType.L_RAIN))
                                    {
                                        sprites.Add(FreedomEffect.GetRainEffect());
                                    }
                                    else if (cmdFlag
                                          .Equals(CommandType.L_PETAL))
                                    {
                                        sprites.Add(FreedomEffect.GetPetalEffect());
                                    }
                                }

                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_SNOWSTOP)
                                || cmdFlag.Equals(CommandType.L_RAINSTOP)
                                || cmdFlag
                                        .Equals(CommandType.L_PETALSTOP))
                        {
                            if (sprites != null)
                            {
                                ISprite[] ss = sprites.GetSprites();

                                for (int i = 0; i < ss.Length; i++)
                                {
                                    ISprite s = ss[i];
                                    if (s is FreedomEffect)
                                    {
                                        if (cmdFlag
                                                .Equals(CommandType.L_SNOWSTOP))
                                        {
                                            if (((FreedomEffect)s).GetKernels()[0] is SnowKernel)
                                            {
                                                sprites.Remove(s);
                                            }
                                        }
                                        else if (cmdFlag
                                              .Equals(CommandType.L_RAINSTOP))
                                        {
                                            if (((FreedomEffect)s).GetKernels()[0] is RainKernel)
                                            {
                                                sprites.Remove(s);
                                            }
                                        }
                                        else if (cmdFlag
                                              .Equals(CommandType.L_PETALSTOP))
                                        {
                                            if (((FreedomEffect)s).GetKernels()[0] is PetalKernel)
                                            {
                                                sprites.Remove(s);
                                            }
                                        }
                                    }
                                }

                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_PLAY))
                        {
                            AssetsSoundManager.GetInstance().PlaySound(mesFlag,false);
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_PLAYLOOP))
                        {
                            AssetsSoundManager.GetInstance().PlaySound(mesFlag, true);
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_PLAYSTOP))
                        {
                            if (MathUtils.IsNan(mesFlag))
                            {
                                AssetsSoundManager.GetInstance().StopSound(int.Parse(mesFlag));
                            }
                            else
                            {
                                AssetsSoundManager.GetInstance().StopSoundAll();
                            }
                            continue;
                        }

                        if (cmdFlag.Equals(CommandType.L_FADEOUT)
                                || cmdFlag.Equals(CommandType.L_FADEIN))
                        {
                            scrFlag = true;
                            LColor color = LColor.black;
                            if (mesFlag.Equals("red"))
                            {
                                color = LColor.red;
                            }
                            else if (mesFlag.Equals("yellow"))
                            {
                                color = LColor.yellow;
                            }
                            else if (mesFlag.Equals("white"))
                            {
                                color = LColor.white;
                            }
                            else if (mesFlag.Equals("black"))
                            {
                                color = LColor.black;
                            }
                            else if (mesFlag.Equals("cyan"))
                            {
                                color = LColor.cyan;
                            }
                            else if (mesFlag.Equals("green"))
                            {
                                color = LColor.green;
                            }
                            else if (mesFlag.Equals("orange"))
                            {
                                color = LColor.orange;
                            }
                            else if (mesFlag.Equals("pink"))
                            {
                                color = LColor.pink;
                            }
                            if (sprites != null)
                            {
                                sprites.RemoveAll();
                                if (cmdFlag.Equals(CommandType.L_FADEIN))
                                {
                                    sprites.Add(FadeEffect.GetInstance(
                                            ISprite_Constants.TYPE_FADE_IN, color));
                                }
                                else
                                {
                                    sprites.Add(FadeEffect.GetInstance(
                                            ISprite_Constants.TYPE_FADE_OUT, color));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_SELLEN))
                        {
                            if (mesFlag != null)
                            {
                                if (MathUtils.IsNan(mesFlag))
                                {
                                    select.SetLeftOffset(int.Parse(mesFlag));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_SELTOP))
                        {
                            if (mesFlag != null)
                            {
                                if (MathUtils.IsNan(mesFlag))
                                {
                                    select.SetTopOffset(int.Parse(mesFlag));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_MESLEN))
                        {
                            if (mesFlag != null)
                            {
                                if (MathUtils.IsNan(mesFlag))
                                {
                                    message.SetMessageLength(int
                                            .Parse(mesFlag));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_MESTOP))
                        {
                            if (mesFlag != null)
                            {
                                if (MathUtils.IsNan(mesFlag))
                                {
                                    message.SetTopOffset(int.Parse(mesFlag));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_MESLEFT))
                        {
                            if (mesFlag != null)
                            {
                                if (MathUtils.IsNan(mesFlag))
                                {
                                    message.SetLeftOffset(int.Parse(mesFlag));
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_MESCOLOR))
                        {
                            if (mesFlag != null)
                            {
                                if (mesFlag.Equals("red"))
                                {
                                    message.SetFontColor(LColor.red);
                                }
                                else if (mesFlag.Equals("yellow"))
                                {
                                    message.SetFontColor(LColor.yellow);
                                }
                                else if (mesFlag.Equals("white"))
                                {
                                    message.SetFontColor(LColor.white);
                                }
                                else if (mesFlag.Equals("black"))
                                {
                                    message.SetFontColor(LColor.black);
                                }
                                else if (mesFlag.Equals("cyan"))
                                {
                                    message.SetFontColor(LColor.cyan);
                                }
                                else if (mesFlag.Equals("green"))
                                {
                                    message.SetFontColor(LColor.green);
                                }
                                else if (mesFlag.Equals("orange"))
                                {
                                    message.SetFontColor(LColor.orange);
                                }
                                else if (mesFlag.Equals("pink"))
                                {
                                    message.SetFontColor(LColor.pink);
                                }
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_MES))
                        {
                            if (select.IsVisible())
                            {
                                select.SetVisible(false);
                            }
                            scrFlag = true;
                            string nMessage = mesFlag;
                            message.SetMessage(StringUtils.Replace(nMessage, "&",
                                    " "));
                            message.SetVisible(true);
                            break;
                        }
                        if (cmdFlag.Equals(CommandType.L_MESSTOP))
                        {
                            scrFlag = true;
                            message.SetVisible(false);
                            select.SetVisible(false);
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_SELECT))
                        {
                            selectMessage = mesFlag;
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_SELECTS))
                        {
                            if (message.IsVisible())
                            {
                                message.SetVisible(false);
                            }
                            select.SetVisible(true);
                            scrFlag = true;
                            isSelectMessage = true;
                            string[] selects = command.GetReads();
                            select.SetMessage(selectMessage, selects);
                            break;
                        }
                        if (cmdFlag.Equals(CommandType.L_SHAKE))
                        {
                            scrCG.shakeNumber = int.Parse(mesFlag);
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_CGWAIT))
                        {
                            scrFlag = false;
                            break;
                        }
                        if (cmdFlag.Equals(CommandType.L_SLEEP))
                        {
                            scrCG.sleep = int.Parse(mesFlag);
                            scrCG.sleepMax = int.Parse(mesFlag);
                            scrFlag = false;
                            break;
                        }
                        if (cmdFlag.Equals(CommandType.L_FLASH))
                        {
                            scrFlag = true;
                            string[] colors = null;
                            if (mesFlag != null)
                            {
                                colors = StringUtils.Split(mesFlag,",");
                            }
                            else
                            {
                                colors = new string[] { "0", "0", "0" };
                            }
                            if (color == null && colors != null
                                    && colors.Length == 3)
                            {
                                color = new LColor(int.Parse(colors[0])
                                        , int.Parse(colors[1])
                                        , int.Parse(colors[2])
                                        );
                                scrCG.sleep = 20;
                                scrCG.sleepMax = scrCG.sleep;
                                scrFlag = false;
                            }
                            else
                            {
                                color = null;
                            }

                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_GB))
                        {
                            if (mesFlag == null)
                            {
                                return;
                            }
                            if (mesFlag.Equals("none"))
                            {
                                scrCG.NoneBackgroundCG();
                            }
                            else
                            {
                                scrCG.SetBackgroundCG(mesFlag);
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_CG))
                        {

                            if (mesFlag == null)
                            {
                                return;
                            }
                            if (scrCG != null
                                    && scrCG.Count() > LSystem.DEFAULT_MAX_CACHE_SIZE)
                            {
                                scrCG.Dispose();
                            }
                            if (mesFlag.Equals(CommandType.L_DEL))
                            {
                                if (orderFlag != null)
                                {
                                    scrCG.Remove(orderFlag);
                                }
                                else
                                {
                                    scrCG.Dispose();
                                }
                            }
                            else if (lastFlag != null
                                  && CommandType.L_TO.Equals(orderFlag))
                            {
                                scrCG.Replace(mesFlag, lastFlag);
                            }
                            else
                            {
                                int x = 0, y = 0;
                                if (orderFlag != null)
                                {
                                    x = int.Parse(orderFlag);
                                }
                                if (size >= 4)
                                {
                                    y = int.Parse((string)commands[3]);
                                }
                                int tx = x;
                                int ty = y;
                                string name = mesFlag;
                                scrCG.Add(name, tx, ty, GetWidth(), GetHeight());
                            }
                            continue;
                        }
                        if (cmdFlag.Equals(CommandType.L_EXIT))
                        {
                            scrFlag = true;
                            running = false;
                            OnExit();
                            break;
                        }
                    }
                }
            }
        }

        public abstract void OnExit();

        private int count = 0;

        private LTimer autoTimer = new LTimer(LSystem.SECOND);

        private void PlayAutoNext()
        {
            if (!autoTimer.Action(elapsedTime))
            {
                return;
            }
            if (scrCG.sleep != 0)
            {
                return;
            }
            if (isSelectMessage)
            {
                return;
            }
            if (message.IsVisible() && !message.IsComplete())
            {
                return;
            }
            NextScript();
        }

        public virtual void Click()
        {
            if (!running)
            {
                return;
            }
            if (locked)
            {
                return;
            }
            if (message.IsVisible() && !message.IsComplete())
            {
                return;
            }
            bool isNext = false;
            if (!isSelectMessage && scrCG.sleep <= 0)
            {
                if (!scrFlag)
                {
                    scrFlag = true;
                }
                if (message.IsVisible())
                {
                    isNext = message.Intersects(GetTouchX(), GetTouchY());
                }
                else
                {
                    isNext = true;
                }
            }
            else if (scrFlag && select.GetResultIndex() != -1)
            {
                OnSelect(selectMessage, select.GetResultIndex());
                isNext = select.Intersects(GetTouchX(), GetTouchY());
                if (isNext)
                {
                    if (count++ >= 1)
                    {
                        message.SetVisible(false);
                        select.SetVisible(false);
                        isSelectMessage = false;
                        selectMessage = null;
                        count = 0;
                        return;
                    }
                }
            }
            if (isNext && !isSelectMessage)
            {
                NextScript();
            }
        }

        protected internal bool initNextScript = true;

        public virtual void InitCommandConfig(string fileName)
        {
            if (fileName == null)
            {
                return;
            }
            Command.ResetCache();
            if (command == null)
            {
                command = new Command(fileName);
            }
            else
            {
                command.FormatCommand(fileName);
            }
            InitCommandConfig(command);
            if (initNextScript)
            {
                NextScript();
            }
        }

        public virtual void InitCommandConfig(Stream ins)
        {
            if (ins == null)
            {
                return;
            }
            Command.ResetCache();
            if (command == null)
            {
                command = new Command(ins);
            }
            else
            {
                command.FormatCommand(ins);
            }
            InitCommandConfig(command);
            if (initNextScript)
            {
                NextScript();
            }
        }

        public virtual bool IsScrFlag()
        {
            return scrFlag;
        }

        public virtual string GetSelectMessage()
        {
            return selectMessage;
        }

        private void InitAVG()
        {
            this.InitDesktop();
            this.InitMessageConfig(message);
            this.InitSelectConfig(select);
            this.InitCommandConfig(scriptName);
        }

        public abstract void OnLoading();

        public virtual void Run()
        {
            InitAVG();
            OnLoading();
            for (; running; )
            {
                if (desktop != null)
                {
                    desktop.Update(delay);
                }
                if (sprites != null)
                {
                    sprites.Update(delay);
                }
                Sleep(delay);
                if (autoPlay)
                {
                    PlayAutoNext();
                }
            }

        }

        public virtual bool IsCommandGo()
        {
            return isRunning;
        }

        public virtual LMessage MessageConfig()
        {
            return message;
        }

        public virtual void SetDialogImage(LTexture dialog)
        {
            this.dialog = dialog;
        }

        public virtual LTexture GetDialogImage()
        {
            return dialog;
        }

        public int GetPause()
        {
            return delay;
        }

        public void SetPause(int pause)
        {
            this.delay = pause;
        }

        public int GetDelay()
        {
            return delay;
        }

        public void SetDelay(int delay)
        {
            this.delay = delay;
        }

        public override Desktop GetDesktop()
        {
            return desktop;
        }

        public LTexture GetDialog()
        {
            return dialog;
        }

        public void SetDialog(LTexture dialog)
        {
            this.dialog = dialog;
        }

        public LMessage GetMessage()
        {
            return message;
        }

        public void SetMessage(LMessage message)
        {
            this.message = message;
        }

        public bool IsRunning()
        {
            return running;
        }

        public void SetRunning(bool running)
        {
            this.running = running;
        }

        public AVGCG GetScrCG()
        {
            return scrCG;
        }

        public void SetScrCG(AVGCG scrCG)
        {
            this.scrCG = scrCG;
        }

        public string GetScriptName()
        {
            return scriptName;
        }

        public void SetScriptName(string scriptName)
        {
            this.scriptName = scriptName;
        }

        public Command GetCommand()
        {
            return command;
        }

        public void SetCommand(Command command)
        {
            this.command = command;
        }

        public bool IsSelectMessage()
        {
            return isSelectMessage;
        }

        public LSelect GetLSelect()
        {
            return select;
        }

        public int GetSleep()
        {
            return scrCG.sleep;
        }

        public void SetSleep(int sleep)
        {
            scrCG.sleep = sleep;
        }

        public int GetSleepMax()
        {
            return scrCG.sleepMax;
        }

        public void SetSleepMax(int sleepMax)
        {
            scrCG.sleepMax = sleepMax;
        }

        public override Sprites GetSprites()
        {
            return sprites;
        }

        public void SetCommandGo(bool isRunning)
        {
            this.isRunning = isRunning;
        }

        public void SetScrFlag(bool scrFlag)
        {
            this.scrFlag = scrFlag;
        }

        public bool IsLocked()
        {
            return locked;
        }

        public void SetLocked(bool locked)
        {
            this.locked = locked;
        }

        public override void TouchDown(LTouch touch)
        {
            if (desktop != null)
            {
                LComponent[] cs = desktop.GetContentPane().GetComponents();
                for (int i = 0; i < cs.Length; i++)
                {
                    if (cs[i] is LButton)
                    {
                        LButton btn = ((LButton)cs[i]);
                        if (btn != null && btn.IsVisible())
                        {
                            if (btn.Intersects(touch.X(), touch.Y()))
                            {
                                btn.DoClick();
                            }
                        }
                    }
                    else if (cs[i] is LPaper)
                    {
                        LPaper paper = ((LPaper)cs[i]);
                        if (paper != null && paper.IsVisible())
                        {
                            if (paper.Intersects(touch.X(), touch.Y()))
                            {
                                paper.DoClick();
                            }
                        }
                    }
                }
            }
            Click();
        }

        public override void TouchMove(LTouch e)
        {

        }

        public override void TouchUp(LTouch touch)
        {

        }

        public override void TouchDrag(LTouch touch)
        {

        }

        public bool IsAutoPlay()
        {
            return autoPlay;
        }

        public void SetAutoPlay(bool autoPlay)
        {
            this.autoPlay = autoPlay;
        }

        public void SetAutoDelay(long d)
        {
            autoTimer.SetDelay(d);
        }

        public long GetAutoDelay()
        {
            return autoTimer.GetDelay();
        }

        public override void Dispose()
        {
            running = false;
            try
            {
                if (avgThread != null)
                {
                    avgThread.Interrupt();
                    avgThread = null;
                }
            }
            catch (Exception)
            {
            }
            if (desktop != null)
            {
                desktop.Dispose();
                desktop = null;
            }
            if (sprites != null)
            {
                sprites.Dispose();
                sprites = null;
            }
            if (command != null)
            {
                command = null;
            }
            if (scrCG != null)
            {
                scrCG.Dispose();
                scrCG = null;
            }
            if (dialog != null)
            {
                if (dialog.GetFileName() != null)
                {
                    dialog.Destroy();
                    dialog = null;
                }
            }
            base.Dispose();
        }

    }
}
