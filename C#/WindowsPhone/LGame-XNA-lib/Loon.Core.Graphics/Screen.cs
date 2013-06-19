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
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Microsoft.Xna.Framework;
using Loon.Core.Timer;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Input;
using Loon.Core.Event;
using Loon.Java;
using Loon.Action.Sprite;
using Loon.Core.Geom;
using Loon.Action.Collision;
using Loon.Core.Graphics.Component;
using Loon.Utils.Debugging;
using Loon.Action;
using Loon.Utils;
using Loon.Media;

namespace Loon.Core.Graphics
{
    // Screen切换方式
    public enum MoveMethod
    {
        FROM_LEFT, FROM_UP, FROM_DOWN, FROM_RIGHT, FROM_UPPER_LEFT, FROM_UPPER_RIGHT, FROM_LOWER_LEFT, FROM_LOWER_RIGHT, OUT_LEFT, OUT_UP, OUT_DOWN, OUT_RIGHT, OUT_UPPER_LEFT, OUT_UPPER_RIGHT, OUT_LOWER_LEFT, OUT_LOWER_RIGHT
    }

    public enum SensorDirection
    {
        NONE, LEFT, RIGHT, UP, DOWN
    }

    public interface LEvent
    {
        void Call();
    }

    public abstract class Screen :_DoubleAbstract, LInput
    {

        private List<ScreenListener> screens;

        private bool useScreenListener;

        public virtual void AddScreenListener(ScreenListener l)
        {
            if (l != null)
            {
                if (screens == null)
                {
                    screens = new List<ScreenListener>(10);
                }
                CollectionUtils.Add(screens, l);
            }
            useScreenListener = (screens != null && screens.Count > 0);
        }

        public virtual void RemoveScreenListener(ScreenListener l)
        {
            if (screens == null)
            {
                return;
            }
            if (l != null)
            {
                CollectionUtils.Remove(screens, l);
            }
            useScreenListener = (screens != null && screens.Count > 0);
        }

        private List<LRelease> releases;

        public virtual void PutRelease(LRelease r)
        {
            if (releases == null)
            {
                releases = new List<LRelease>(10);
            }
            CollectionUtils.Remove(releases, r);
        }

        public virtual void PutReleases(params LRelease[] rs)
        {
            if (releases == null)
            {
                releases = new List<LRelease>(10);
            }
            int size = rs.Length;
            for (int i = 0; i < size; i++)
            {
                releases.Add(rs[i]);
            }
        }

        public virtual void AddAction(ActionEvent e, ActionBind act)
        {
            ActionControl.GetInstance().AddAction(e, act);
        }

        public virtual void RemoveAction(ActionEvent e)
        {
            ActionControl.GetInstance().RemoveAction(e);
        }

        public virtual void RemoveAction(object tag, ActionBind act)
        {
            ActionControl.GetInstance().RemoveAction(tag, act);
        }

        public virtual void RemoveAllActions(ActionBind act)
        {
            ActionControl.GetInstance().RemoveAllActions(act);
        }

        public virtual void Sleep(long time)
        {
            try
            {
                Thread.Sleep(time);
            }
            catch (Exception)
            {
            }
        }

        public float GetDeltaTime()
        {
            return elapsedTime / 1000f;
        }

        protected static Screen StaticCurrentSceen;

        public const byte DRAW_USER = 0;

        public const byte DRAW_SPRITE = 1;

        public const byte DRAW_DESKTOP = 2;

        public sealed class PaintOrder
        {

            private byte type;

            private Screen screen;

            public PaintOrder(byte t, Screen s)
            {
                this.type = t;
                this.screen = s;
            }

            internal void Paint(GLEx g)
            {
                switch (type)
                {
                    case DRAW_USER:
                        lock (this)
                        {
                            screen.Draw(g);
                        }
                        break;
                    case DRAW_SPRITE:
                        lock (this)
                        {
                            if (screen.spriteRun)
                            {
                                screen.sprites.CreateUI(g);
                            }
                            else if (screen.spriteRun = (screen.sprites != null && screen.sprites.Size() > 0))
                            {
                                screen.sprites.CreateUI(g);
                            }
                        }
                        break;
                    case DRAW_DESKTOP:
                        lock (this)
                        {
                            if (screen.desktopRun)
                            {
                                screen.desktop.CreateUI(g);
                            }
                            else if (screen.desktopRun = (screen.desktop != null && screen.desktop.Size() > 0))
                            {
                                screen.desktop.CreateUI(g);
                            }
                        }
                        break;
                }
            }

            internal void Update(LTimerContext c)
            {
                switch (type)
                {
                    case DRAW_USER:
                        lock (this)
                        {
                            screen.Alter(c);
                        }
                        break;
                    case DRAW_SPRITE:
                        lock (this)
                        {
                            screen.spriteRun = (screen.sprites != null && screen.sprites.Size() > 0);
                            if (screen.spriteRun)
                            {
                                screen.sprites.Update(c.timeSinceLastUpdate);
                            }
                        }
                        break;
                    case DRAW_DESKTOP:
                        lock (this)
                        {
                            screen.desktopRun = (screen.desktop != null && screen.desktop.Size() > 0);
                            if (screen.desktopRun)
                            {
                                screen.desktop.Update(c.timeSinceLastUpdate);
                            }
                        }
                        break;
                }
            }

        }

        private bool isDrawing;

        public virtual void YieldDraw()
        {
            NotifyDraw();
            WaitUpdate();
        }

        public virtual void YieldUpdate()
        {
            NotifyUpdate();
            WaitDraw();
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void NotifyDraw()
        {
            this.isDrawing = true;
            try
            {
                LSystem.screenActivity.NotifyAll();
            }
            catch (Exception)
            {
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void NotifyUpdate()
        {
            this.isDrawing = false;
            try
            {
                LSystem.screenActivity.NotifyAll();
            }
            catch (Exception)
            {
            }

        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void WaitDraw()
        {
            for (; !isDrawing; )
            {
                try
                {
                    LSystem.screenActivity.Wait();
                }
                catch (Exception)
                {
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void WaitUpdate()
        {
            for (; isDrawing; )
            {
                try
                {
                    LSystem.screenActivity.Wait();
                }
                catch (Exception)
                {
                }
            }
        }

        private bool spriteRun, desktopRun;

        private bool fristPaintFlag;

        private bool secondPaintFlag;

        private bool lastPaintFlag;

        private bool isMultitouch = false;

        public abstract void Draw(GLEx g);

        public const int SCREEN_NOT_REPAINT = 0;

        public const int SCREEN_BITMAP_REPAINT = -1;

        public const int SCREEN_CANVAS_REPAINT = -2;

        public const int SCREEN_COLOR_REPAINT = -3;

        // 线程事件集合
        private readonly List<Runnable> runnables;

        // 0.3.2版新增的简易重力控制接口
        private GravityHandler gravityHandler;

        private LColor color;

        private int touchX, touchY, lastTouchX, lastTouchY, touchDX, touchDY;

        public long elapsedTime;

        private readonly static bool[] touchType, keyType;

        public const int NO_BUTTON = -1;

        public const int NO_KEY = -1;

        public const int UPPER_LEFT = 0;

        public const int UPPER_RIGHT = 1;

        public const int LOWER_LEFT = 2;

        public const int LOWER_RIGHT = 3;

        private int touchButtonPressed = NO_BUTTON,
                touchButtonReleased = NO_BUTTON;

        private int keyButtonPressed = NO_KEY,
                keyButtonReleased = NO_KEY;

        bool isNext;

        private int mode, frame;

        private LTexture currentScreen;

        protected LProcess handler;

        private int width, height, halfWidth, halfHeight;

        private SensorDirection direction = SensorDirection.NONE;

        private LInput baseInput;

        // 精灵集合
        private Sprites sprites;

        // 桌面集合
        private Desktop desktop;

        private Loon.Core.Geom.Point.Point2i touch = new Loon.Core.Geom.Point.Point2i(0, 0);

        private bool isLoad, isLock, isClose, isTranslate, isGravity;

        private float tx, ty;

        // 首先绘制的对象
        private PaintOrder fristOrder;

        // 其次绘制的对象
        private PaintOrder secondOrder;

        // 最后绘制的对象
        private PaintOrder lastOrder;

        private PaintOrder userOrder, spriteOrder, desktopOrder;

        private List<RectBox> limits = new List<RectBox>(10);

        private bool replaceLoading;

        private int replaceScreenSpeed = 8;

        private LTimer replaceDelay = new LTimer(0);

        private Screen replaceDstScreen;

        private EmptyObject dstPos = new EmptyObject();

        private MoveMethod replaceMethod = MoveMethod.FROM_LEFT;

        private bool isScreenFrom = false;

        private class Replace_Thread : Thread
        {
            private Screen screen;

            public Replace_Thread(Screen s)
            {
                this.screen = s;
            }

            public override void Run()
            {
                screen.OnCreate(LSystem.screenRect.width,
                        LSystem.screenRect.height);
                screen.SetClose(false);
                screen.OnLoad();
                screen.SetRepaintMode(SCREEN_CANVAS_REPAINT);
                screen.OnLoaded();
                screen.SetOnLoadState(true);

            }
        }

        public virtual void ReplaceScreen(Screen screen, MoveMethod m)
        {
            if (screen != null && screen != this)
            {
                screen.SetOnLoadState(false);
                SetLock(true);
                screen.SetLock(true);
                this.replaceMethod = m;
                this.replaceDstScreen = screen;

                screen.SetRepaintMode(SCREEN_CANVAS_REPAINT);
                switch (m)
                {
                    case MoveMethod.FROM_LEFT:
                        dstPos.SetLocation(-GetWidth(), 0);
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_RIGHT:
                        dstPos.SetLocation(GetWidth(), 0);
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_UP:
                        dstPos.SetLocation(0, -GetHeight());
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_DOWN:
                        dstPos.SetLocation(0, GetHeight());
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_UPPER_LEFT:
                        dstPos.SetLocation(-GetWidth(), -GetHeight());
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_UPPER_RIGHT:
                        dstPos.SetLocation(GetWidth(), -GetHeight());
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_LOWER_LEFT:
                        dstPos.SetLocation(-GetWidth(), GetHeight());
                        isScreenFrom = true;
                        break;
                    case MoveMethod.FROM_LOWER_RIGHT:
                        dstPos.SetLocation(GetWidth(), GetHeight());
                        isScreenFrom = true;
                        break;
                    default:
                        dstPos.SetLocation(0, 0);
                        isScreenFrom = false;
                        break;
                }

                Replace_Thread loading = new Replace_Thread(screen);
                CallEvent(loading);
                replaceLoading = true;
            }
        }



        public virtual int GetReplaceScreenSpeed()
        {
            return replaceScreenSpeed;
        }

        public virtual void SetReplaceScreenSpeed(int s)
        {
            this.replaceScreenSpeed = s;
        }

        public virtual void SetReplaceScreenDelay(long d)
        {
            replaceDelay.SetDelay(d);
        }

        public virtual long GetReplaceScreenDelay()
        {
            return replaceDelay.GetDelay();
        }

        private void SubmitReplaceScreen()
        {
            if (handler != null)
            {
                handler.SetCurrentScreen(replaceDstScreen);
            }
            replaceLoading = false;
        }

        public virtual void AddTouchLimit(LObject c)
        {
            if (c != null)
            {
                limits.Add(c.GetCollisionArea());
            }
        }

        public virtual void AddTouchLimit(RectBox r)
        {
            if (r != null)
            {
                limits.Add(r);
            }
        }

        public virtual bool IsClickLimit(LTouch e)
        {
            return IsClickLimit(e.X(), e.Y());
        }

        public virtual bool IsClickLimit(int x, int y)
        {
            if (limits.Count == 0)
            {
                return false;
            }
            foreach (RectBox rect in limits)
            {
                if (rect.Contains(x, y))
                {
                    return true;
                }
            }
            return false;
        }

        protected virtual PaintOrder DRAW_USER_PAINT()
        {
            if (userOrder == null)
            {
                userOrder = new PaintOrder(DRAW_USER, this);
            }
            return userOrder;
        }

        protected virtual PaintOrder DRAW_SPRITE_PAINT()
        {
            if (spriteOrder == null)
            {
                spriteOrder = new PaintOrder(DRAW_SPRITE, this);
            }
            return spriteOrder;
        }

        protected virtual PaintOrder DRAW_DESKTOP_PAINT()
        {
            if (desktopOrder == null)
            {
                desktopOrder = new PaintOrder(DRAW_DESKTOP, this);
            }
            return desktopOrder;
        }

        static Screen()
        {
            keyType = new bool[15];
            touchType = new bool[15];
        }

        public Screen()
        {
            LSystem.AUTO_REPAINT = true;
            Screen.StaticCurrentSceen = this;
            this.runnables = new List<Runnable>(1);
            this.handler = LSystem.screenProcess;
            this.width = LSystem.screenRect.width;
            this.height = LSystem.screenRect.height;
            this.halfWidth = width / 2;
            this.halfHeight = height / 2;
            this.fristOrder = DRAW_USER_PAINT();
            this.secondOrder = DRAW_SPRITE_PAINT();
            this.lastOrder = DRAW_DESKTOP_PAINT();
            this.fristPaintFlag = true;
            this.secondPaintFlag = true;
            this.lastPaintFlag = true;
        }

        public virtual bool Contains(float x, float y)
        {
            return LSystem.screenRect.Contains(x, y);
        }

        public virtual bool Contains(float x, float y, float w, float h)
        {
            return LSystem.screenRect.Contains(x, y, w, h);
        }

        public virtual void OnCreate(int width, int height)
        {
            this.mode = SCREEN_CANVAS_REPAINT;
            this.baseInput = this;
            this.width = width;
            this.height = height;
            this.halfWidth = width / 2;
            this.halfHeight = height / 2;
            this.touchX = touchY = lastTouchX = lastTouchY = touchDX = touchDY = 0;
            this.isLoad = isLock = isClose = isTranslate = isGravity = false;
            if (sprites != null)
            {
                sprites.Dispose();
                sprites = null;
            }
            this.sprites = new Sprites(width, height);
            if (desktop != null)
            {
                desktop.Dispose();
                desktop = null;
            }
            this.desktop = new Desktop(baseInput, width, height);
            this.isNext = true;
        }

        public virtual void Resize(int width, int height)
        {
            this.touchX = touchY = lastTouchX = lastTouchY = touchDX = touchDY = 0;
        }

        public virtual void AddLoad(Updateable u)
        {
            if (handler != null)
            {
                handler.AddLoad(u);
            }
        }

        public virtual void RemoveLoad(Updateable u)
        {
            if (handler != null)
            {
                handler.RemoveLoad(u);
            }
        }

        public virtual void RemoveAllLoad()
        {
            if (handler != null)
            {
                handler.RemoveAllLoad();
            }
        }

        public virtual void AddUnLoad(Updateable u)
        {
            if (handler != null)
            {
                handler.AddUnLoad(u);
            }
        }

        public virtual void RemoveUnLoad(Updateable u)
        {
            if (handler != null)
            {
                handler.RemoveUnLoad(u);
            }
        }

        public virtual void RemoveAllUnLoad()
        {
            if (handler != null)
            {
                handler.RemoveAllUnLoad();
            }
        }

        public virtual void AddDrawing(Drawable d)
        {
            if (handler != null)
            {
                handler.AddDrawing(d);
            }
        }

        public virtual void RemoveDrawing(Drawable d)
        {
            if (handler != null)
            {
                handler.RemoveDrawing(d);
            }
        }

        public virtual void RemoveAllDrawing()
        {
            if (handler != null)
            {
                handler.RemoveAllDrawing();
            }
        }

        public virtual LTransition OnTransition()
        {
            return null;
        }

        public virtual GravityHandler SetGravity(bool g)
        {
            if (g && gravityHandler == null)
            {
                gravityHandler = new GravityHandler();
            }
            this.isGravity = g;
            return gravityHandler;
        }

        public virtual bool IsGravity()
        {
            return this.isGravity;
        }

        public virtual GravityHandler GetGravityHandler()
        {
            return SetGravity(true);
        }

        public virtual bool IsLock()
        {
            return isLock;
        }

        public virtual void SetLock(bool l)
        {
            this.isLock = l;
        }

        public virtual void SetClose(bool close)
        {
            this.isClose = close;
        }

        public virtual bool IsClose()
        {
            return isClose;
        }

        public virtual void SetFrame(int frame)
        {
            this.frame = frame;
        }

        public virtual int GetFrame()
        {
            return frame;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual bool Next()
        {
            this.frame++;
            return isNext;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void WaitFrame(int i)
        {
            for (int wait = frame + i; frame < wait; )
            {
                try
                {
                    LSystem.screenActivity.Wait(i);
                }
                catch (Exception)
                {
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void WaitTime(long i)
        {
            for (long time = JavaRuntime.CurrentTimeMillis() + i; JavaRuntime
                    .CurrentTimeMillis() < time; )
                try
                {
                    LSystem.screenActivity.Wait(time - JavaRuntime.CurrentTimeMillis());
                }
                catch (Exception)
                {
                }
        }

        public virtual void OnLoad()
        {

        }

        public virtual void OnLoaded()
        {

        }

        public virtual void SetOnLoadState(bool flag)
        {
            this.isLoad = flag;
        }

        public virtual bool IsOnLoadComplete()
        {
            return isLoad;
        }

        public virtual void RunFirstScreen()
        {
            if (handler != null)
            {
                handler.RunFirstScreen();
            }
        }

        public virtual void RunLastScreen()
        {
            if (handler != null)
            {
                handler.RunLastScreen();
            }
        }

        public virtual void RunIndexScreen(int index)
        {
            if (handler != null)
            {
                handler.RunIndexScreen(index);
            }
        }

        public virtual void RunPreviousScreen()
        {
            if (handler != null)
            {
                handler.RunPreviousScreen();
            }
        }

        public virtual void RunNextScreen()
        {
            if (handler != null)
            {
                handler.RunNextScreen();
            }
        }

        public virtual void AddScreen(Screen screen)
        {
            if (handler != null)
            {
                handler.AddScreen(screen);
            }
        }

        public virtual LinkedList<Screen> getScreens()
        {
            if (handler != null)
            {
                return handler.GetScreens();
            }
            return null;
        }

        public virtual int GetScreenCount()
        {
            if (handler != null)
            {
                return handler.GetScreenCount();
            }
            return 0;
        }

        public virtual Loon.Action.Sprite.Sprites.SpriteListener GetSprListerner()
        {
            if (sprites == null)
            {
                return null;
            }
            return sprites.GetSprListerner();
        }

        public virtual void SetSprListerner(Loon.Action.Sprite.Sprites.SpriteListener sprListerner)
        {
            if (sprites == null)
            {
                return;
            }
            sprites.SetSprListerner(sprListerner);
        }

        public virtual string GetName()
        {
            return GetType().Name;
        }

        public virtual void SetEmulatorListener(EmulatorListener emulator)
        {
            if (LSystem.screenProcess != null)
            {
                LSystem.screenProcess.SetEmulatorListener(emulator);
            }
        }

        public virtual EmulatorButtons GetEmulatorButtons()
        {
            if (LSystem.screenProcess != null)
            {
                return LSystem.screenProcess.GetEmulatorButtons();
            }
            return null;
        }

        public virtual void EmulatorButtonsVisible(bool visible)
        {
            if (LSystem.screenProcess != null)
            {
                try
                {
                    EmulatorButtons es = LSystem.screenProcess.GetEmulatorButtons();
                    es.SetVisible(visible);
                }
                catch (Exception)
                {
                }
            }
        }

        public virtual void SetBackground(LTexture background)
        {
            if (background != null)
            {
                SetRepaintMode(SCREEN_BITMAP_REPAINT);
                LTexture screen = null;
                if (background.GetWidth() != GetWidth()
                        || background.GetHeight() != GetHeight())
                {
                    screen = background.Scale(GetWidth(), GetHeight());
                }
                else
                {
                    screen = background;
                }
                LTexture tmp = currentScreen;
                currentScreen = screen;
                if (tmp != null)
                {
                    tmp.Destroy();
                    tmp = null;
                }
            }
            else
            {
                SetRepaintMode(SCREEN_CANVAS_REPAINT);
            }
        }

        public virtual void SetBackground(string fileName)
        {
            this.SetBackground(new LTexture(fileName, Loon.Core.Graphics.Opengl.LTexture.Format.STATIC));
        }

        public virtual void SetBackground(LColor c)
        {
            SetRepaintMode(SCREEN_COLOR_REPAINT);
            if (color == null)
            {
                color = new LColor(c);
            }
            else
            {
                color.SetColor(c.r, c.g, c.b, c.a);
            }
        }

        public virtual LColor GetColor()
        {
            return color;
        }

        public virtual LTexture GetBackground()
        {
            return currentScreen;
        }

        public virtual Desktop GetDesktop()
        {
            return desktop;
        }

        public virtual Sprites GetSprites()
        {
            return sprites;
        }

        public virtual List<LComponent> GetComponents(Type clazz)
        {
            if (desktop != null)
            {
                return desktop.GetComponents(clazz);
            }
            return null;
        }

        public virtual LComponent GetTopComponent()
        {
            if (desktop != null)
            {
                return desktop.GetTopComponent();
            }
            return null;
        }

        public virtual LComponent GetBottomComponent()
        {
            if (desktop != null)
            {
                return desktop.GetBottomComponent();
            }
            return null;
        }


        public virtual LLayer GetTopLayer()
        {
            if (desktop != null)
            {
                return desktop.GetTopLayer();
            }
            return null;
        }

        public virtual LLayer GetBottomLayer()
        {
            if (desktop != null)
            {
                return desktop.GetBottomLayer();
            }
            return null;
        }


        public virtual List<ISprite> GetSprites(Type clazz)
        {
            if (sprites != null)
            {
                return sprites.GetSprites(clazz);
            }
            return null;
        }

        public virtual ISprite GetTopSprite()
        {
            if (sprites != null)
            {
                return sprites.GetTopSprite();
            }
            return null;
        }

        public virtual ISprite GetBottomSprite()
        {
            if (sprites != null)
            {
                return sprites.GetBottomSprite();
            }
            return null;
        }

        public virtual void Add(ISprite sprite)
        {
            if (sprites != null)
            {
                sprites.Add(sprite);
            }
        }

        public virtual void Add(LComponent comp)
        {
            if (desktop != null)
            {
                desktop.Add(comp);
            }
        }

        public virtual void Remove(ISprite sprite)
        {
            if (sprites != null)
            {
                sprites.Remove(sprite);
            }
        }

        public virtual void RemoveSprite(Type clazz)
        {
            if (sprites != null)
            {
                sprites.Remove(clazz);
            }
        }

        public virtual void Remove(LComponent comp)
        {
            if (desktop != null)
            {
                desktop.Remove(comp);
            }
        }

        public virtual void RemoveComponent(Type clazz)
        {
            if (desktop != null)
            {
                desktop.Remove(clazz);
            }
        }

        public virtual void RemoveAll()
        {
            if (sprites != null)
            {
                sprites.RemoveAll();
            }
            if (desktop != null)
            {
                desktop.GetContentPane().Clear();
            }
        }

        public virtual bool OnClick(ISprite sprite)
        {
            if (sprite == null)
            {
                return false;
            }
            if (sprite.IsVisible())
            {
                RectBox rect = sprite.GetCollisionBox();
                if (rect.Contains(touchX, touchY) || rect.Intersects(touchX, touchY))
                {
                    return true;
                }
            }
            return false;
        }

        public virtual bool OnClick(LComponent component)
        {
            if (component == null)
            {
                return false;
            }
            if (component.IsVisible())
            {
                RectBox rect = component.GetCollisionBox();
                if (rect.Contains(touchX, touchY) || rect.Intersects(touchX, touchY))
                {
                    return true;
                }
            }
            return false;
        }

        public virtual void CenterOn(LObject o)
        {
            LObject.CenterOn(o, GetWidth(), GetHeight());
        }

        public virtual void TopOn(LObject o)
        {
            LObject.TopOn(o, GetWidth(), GetHeight());
        }

        public virtual void LeftOn(LObject o)
        {
            LObject.LeftOn(o, GetWidth(), GetHeight());
        }

        public virtual void RightOn(LObject o)
        {
            LObject.RightOn(o, GetWidth(), GetHeight());
        }

        public virtual void BottomOn(LObject o)
        {
            LObject.BottomOn(o, GetWidth(), GetHeight());
        }

        public virtual int GetRepaintMode()
        {
            return mode;
        }

        public virtual void SetRepaintMode(int mode)
        {
            this.mode = mode;
        }

        private class _Call_Event : Thread
        {

            LEvent e;

            public _Call_Event(LEvent eve)
            {
                this.e = eve;
            }

            public override void Run()
            {
                e.Call();
            }
        }

        public virtual void CallEvent(LEvent e)
        {
            if (e == null)
            {
                return;
            }
            CallEvent(new _Call_Event(e));
        }

        public virtual void CallEvent(Runnable runnable)
        {
            lock (runnables)
            {
                runnables.Add(runnable);
            }
        }

        public virtual void CallEventWait(Runnable runnable)
        {
            lock (runnable)
            {
                lock (runnables)
                {
                    runnables.Add(runnable);
                }
                try
                {
                    JavaRuntime.Wait(runnable);
                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                }
            }
        }

        public virtual void CallEventInterrupt()
        {
            lock (runnables)
            {
                for (IEnumerator<Runnable> it = runnables.GetEnumerator(); it.MoveNext(); )
                {
                    object running = it.Current;
                    lock (running)
                    {
                        if (running is Thread)
                        {
                            ((Thread)running).Interrupt();
                        }
                    }
                }
            }
        }

        public virtual void CallEvents()
        {
            CallEvents(true);
        }

        public virtual void CallEvents(bool execute)
        {
            if (!execute)
            {
                lock (runnables)
                {
                    runnables.Clear();
                }
                return;
            }
            if (runnables.Count == 0)
            {
                return;
            }
            List<Runnable> runnableList;
            lock (runnables)
            {
                runnableList = new List<Runnable>(runnables);
                runnables.Clear();
            }
            for (IEnumerator<Runnable> it = runnableList.GetEnumerator(); it.MoveNext(); )
            {
                object running = it.Current;
                lock (running)
                {
                    try
                    {
                        if (running is Thread)
                        {
                            Thread thread = (Thread)running;
                            if (!thread.IsAlive())
                            {
                                thread.Start();
                            }

                        }
                        else
                        {
                            ((Runnable)running).Run();
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.Exception(ex);
                    }
                    JavaRuntime.NotifyAll(running);
                }
            }
            runnableList = null;
        }

        public virtual void SetLocation(float x, float y)
        {
            this.tx = x;
            this.ty = y;
            this.isTranslate = (tx != 0 || ty != 0);
        }

        public virtual void SetX(float x)
        {
            SetLocation(x, ty);
        }

        public virtual void SetY(float y)
        {
            SetLocation(tx, y);
        }

        public virtual float GetX()
        {
            return this.tx;
        }

        public virtual float GetY()
        {
            return this.ty;
        }

        protected internal virtual void AfterUI(GLEx g)
        {

        }

        protected internal virtual void BeforeUI(GLEx g)
        {

        }

        private void Repaint(GLEx g)
        {
            if (!isClose)
            {
                if (isTranslate)
                {
                    g.Translate(tx, ty);
                }
                AfterUI(g);
                if (fristPaintFlag)
                {
                    fristOrder.Paint(g);
                }
                if (secondPaintFlag)
                {
                    secondOrder.Paint(g);
                }
                if (lastPaintFlag)
                {
                    lastOrder.Paint(g);
                }
                BeforeUI(g);
                if (isTranslate)
                {
                    g.Translate(-tx, -ty);
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void CreateUI(GLEx g)
        {
            if (isClose)
            {
                return;
            }
            if (replaceLoading)
            {
                if (replaceDstScreen == null
                        || !replaceDstScreen.IsOnLoadComplete())
                {
                    Repaint(g);
                }
                else if (replaceDstScreen.IsOnLoadComplete())
                {
                    if (isScreenFrom)
                    {
                        Repaint(g);
                        if (replaceDstScreen.color != null)
                        {
                            g.SetColor(replaceDstScreen.color);
                            g.FillRect(dstPos.X(), dstPos.Y(), GetWidth(),
                                    GetHeight());
                            g.ResetColor();
                        }
                        if (replaceDstScreen.currentScreen != null)
                        {
                            g.DrawTexture(replaceDstScreen.currentScreen,
                                    dstPos.X(), dstPos.Y(), GetWidth(), GetHeight());
                        }
                        if (dstPos.X() != 0 || dstPos.Y() != 0)
                        {
                            g.SetClip(dstPos.X(), dstPos.Y(), GetWidth(),
                                    GetHeight());
                            g.Translate(dstPos.X(), dstPos.Y());
                        }
                        replaceDstScreen.CreateUI(g);
                        if (dstPos.X() != 0 || dstPos.Y() != 0)
                        {
                            g.Translate(-dstPos.X(), -dstPos.Y());
                            g.ClearClip();
                        }
                    }
                    else
                    {
                        if (replaceDstScreen.color != null)
                        {
                            g.SetColor(replaceDstScreen.color);
                            g.FillRect(0, 0, GetWidth(), GetHeight());
                            g.ResetColor();
                        }
                        if (replaceDstScreen.currentScreen != null)
                        {
                            g.DrawTexture(replaceDstScreen.currentScreen, 0, 0,
                                    GetWidth(), GetHeight());
                        }
                        replaceDstScreen.CreateUI(g);
                        if (color != null)
                        {
                            g.SetColor(color);
                            g.FillRect(dstPos.X(), dstPos.Y(), GetWidth(),
                                    GetHeight());
                            g.ResetColor();
                        }
                        if (GetBackground() != null)
                        {
                            g.DrawTexture(currentScreen, dstPos.X(), dstPos.Y(),
                                    GetWidth(), GetHeight());
                        }
                        if (dstPos.X() != 0 || dstPos.Y() != 0)
                        {
                            g.SetClip(dstPos.X(), dstPos.Y(), GetWidth(),
                                    GetHeight());
                            g.Translate(dstPos.X(), dstPos.Y());
                        }
                        Repaint(g);
                        if (dstPos.X() != 0 || dstPos.Y() != 0)
                        {
                            g.Translate(-dstPos.X(), -dstPos.Y());
                            g.ClearClip();
                        }
                    }
                }
            }
            else
            {
                Repaint(g);
            }
        }


        private void Process(LTimerContext timer)
        {
            this.elapsedTime = timer.GetTimeSinceLastUpdate();
            if (!isClose)
            {
                if (isGravity)
                {
                    gravityHandler.Update(elapsedTime);
                }
                if (fristPaintFlag)
                {
                    fristOrder.Update(timer);
                }
                if (secondPaintFlag)
                {
                    secondOrder.Update(timer);
                }
                if (lastPaintFlag)
                {
                    lastOrder.Update(timer);
                }
            }
            this.touchDX = touchX - lastTouchX;
            this.touchDY = touchY - lastTouchY;
            this.lastTouchX = touchX;
            this.lastTouchY = touchY;
            this.touchButtonReleased = NO_BUTTON;
        }


        public virtual void RunTimer(LTimerContext timer)
        {
            if (isClose)
            {
                return;
            }
            if (replaceLoading)
            {
                if (replaceDstScreen == null
                        || !replaceDstScreen.IsOnLoadComplete())
                {
                    Process(timer);
                }
                else if (replaceDstScreen.IsOnLoadComplete())
                {
                    Process(timer);
                    if (replaceDelay.Action(timer))
                    {
                        switch (replaceMethod)
                        {
                            case MoveMethod.FROM_LEFT:
                                dstPos.Move_right(replaceScreenSpeed);
                                if (dstPos.X() >= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_RIGHT:
                                dstPos.Move_left(replaceScreenSpeed);
                                if (dstPos.X() <= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_UP:
                                dstPos.Move_down(replaceScreenSpeed);
                                if (dstPos.Y() >= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_DOWN:
                                dstPos.Move_up(replaceScreenSpeed);
                                if (dstPos.Y() <= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_LEFT:
                                dstPos.Move_left(replaceScreenSpeed);
                                if (dstPos.X() < -GetWidth())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_RIGHT:
                                dstPos.Move_right(replaceScreenSpeed);
                                if (dstPos.X() > GetWidth())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_UP:
                                dstPos.Move_up(replaceScreenSpeed);
                                if (dstPos.Y() < -GetHeight())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_DOWN:
                                dstPos.Move_down(replaceScreenSpeed);
                                if (dstPos.Y() > GetHeight())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_UPPER_LEFT:
                                if (dstPos.Y() < 0)
                                {
                                    dstPos.Move_45D_right(replaceScreenSpeed);
                                }
                                else
                                {
                                    dstPos.Move_right(replaceScreenSpeed);
                                }
                                if (dstPos.Y() >= 0 && dstPos.X() >= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_UPPER_RIGHT:
                                if (dstPos.Y() < 0)
                                {
                                    dstPos.Move_45D_down(replaceScreenSpeed);
                                }
                                else
                                {
                                    dstPos.Move_left(replaceScreenSpeed);
                                }
                                if (dstPos.Y() >= 0 && dstPos.X() <= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_LOWER_LEFT:
                                if (dstPos.Y() > 0)
                                {
                                    dstPos.Move_45D_up(replaceScreenSpeed);
                                }
                                else
                                {
                                    dstPos.Move_right(replaceScreenSpeed);
                                }
                                if (dstPos.Y() <= 0 && dstPos.X() >= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.FROM_LOWER_RIGHT:
                                if (dstPos.Y() > 0)
                                {
                                    dstPos.Move_45D_left(replaceScreenSpeed);
                                }
                                else
                                {
                                    dstPos.Move_left(replaceScreenSpeed);
                                }
                                if (dstPos.Y() <= 0 && dstPos.X() <= 0)
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_UPPER_LEFT:
                                dstPos.Move_45D_left(replaceScreenSpeed);
                                if (dstPos.X() < -GetWidth()
                                        || dstPos.Y() <= -GetHeight())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_UPPER_RIGHT:
                                dstPos.Move_45D_up(replaceScreenSpeed);
                                if (dstPos.X() > GetWidth()
                                        || dstPos.Y() < -GetHeight())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_LOWER_LEFT:
                                dstPos.Move_45D_down(replaceScreenSpeed);
                                if (dstPos.X() < -GetWidth()
                                        || dstPos.Y() > GetHeight())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            case MoveMethod.OUT_LOWER_RIGHT:
                                dstPos.Move_45D_right(replaceScreenSpeed);
                                if (dstPos.X() > GetWidth() || dstPos.Y() > GetHeight())
                                {
                                    SubmitReplaceScreen();
                                    return;
                                }
                                break;
                            default:
                                break;
                        }
                        replaceDstScreen.RunTimer(timer);
                    }
                }
            }
            else
            {
                Process(timer);
            }
        }


        public LInput GetInput()
        {
            return baseInput;
        }

        public virtual void SetNext(bool next)
        {
            this.isNext = next;
        }

        public abstract void Alter(LTimerContext timer);

        public virtual void SetScreen(Screen screen)
        {
            if (handler != null)
            {
                this.handler.SetScreen(screen);
            }
        }

        public virtual int GetWidth()
        {
            return width;
        }

        public virtual int GetHeight()
        {
            return height;
        }

        public virtual void Refresh()
        {
            for (int i = 0; i < touchType.Length; i++)
            {
                touchType[i] = false;
            }
            touchDX = touchDY = 0;
            for (int i = 0; i < keyType.Length; i++)
            {
                keyType[i] = false;
            }
        }

        public virtual Loon.Core.Geom.Point.Point2i GetTouch()
        {
            touch.Set(touchX, touchY);
            return touch;
        }

        public virtual bool IsPaused()
        {
            return LSystem.isPaused;
        }

        public virtual int GetTouchPressed()
        {
            return touchButtonPressed > NO_BUTTON ? touchButtonPressed
                    : NO_BUTTON;
        }

        public virtual int GetTouchReleased()
        {
            return touchButtonReleased > NO_BUTTON ? touchButtonReleased
                    : NO_BUTTON;
        }

        public virtual bool IsTouchPressed(int button)
        {
            return touchButtonPressed == button;
        }

        public virtual bool IsTouchReleased(int button)
        {
            return touchButtonReleased == button;
        }

        public virtual int GetTouchX()
        {
            return touchX;
        }

        public virtual int GetTouchY()
        {
            return touchY;
        }

        public virtual int GetTouchDX()
        {
            return touchDX;
        }

        public virtual int GetTouchDY()
        {
            return touchDY;
        }

        public virtual bool IsTouchType(int type)
        {
            return touchType[type];
        }

        public virtual int GetKeyPressed()
        {
            return keyButtonPressed > NO_KEY ? keyButtonPressed
                    : NO_KEY;
        }

        public virtual bool IsKeyPressed(int keyCode)
        {
            return keyButtonPressed == keyCode;
        }

        public virtual int GetKeyReleased()
        {
            return keyButtonReleased > NO_KEY ? keyButtonReleased
                    : NO_KEY;
        }

        public virtual bool IsKeyReleased(int keyCode)
        {
            return keyButtonReleased == keyCode;
        }

        public virtual bool IsKeyType(int keyCode)
        {
            return keyType[keyCode];
        }

        public virtual void KeyPressed(LKey e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            int type = e.GetCode();
            int code = e.GetKeyCode();
            try
            {
                if (useScreenListener)
                {
                    foreach (ScreenListener t in screens)
                    {
                        t.Pressed(e);
                    }
                }
                this.OnKeyDown(e);
                keyType[type] = true;
                keyButtonPressed = code;
                keyButtonReleased = NO_KEY;
            }
            catch (Exception ex)
            {
                keyButtonPressed = NO_KEY;
                keyButtonReleased = NO_KEY;
                Log.Exception(ex);
            }
        }

        public virtual void SetKeyDown(int button)
        {
            try
            {
                keyButtonPressed = button;
                keyButtonReleased = NO_KEY;
            }
            catch (Exception)
            {
            }
        }

        public virtual void KeyReleased(LKey e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            int type = e.GetCode();
            int code = e.GetKeyCode();
            try
            {
                if (useScreenListener)
                {
                    foreach (ScreenListener t in screens)
                    {
                        t.Released(e);
                    }
                }
                this.OnKeyUp(e);
                keyType[type] = false;
                keyButtonReleased = code;
                keyButtonPressed = NO_KEY;
            }
            catch (Exception ex)
            {
                keyButtonPressed = NO_KEY;
                keyButtonReleased = NO_KEY;
                Log.Exception(ex);
            }
        }

        public virtual void SetKeyUp(int button)
        {
            try
            {
                keyButtonReleased = button;
                keyButtonPressed = NO_KEY;
            }
            catch (Exception)
            {
            }
        }

        public virtual void OnKeyDown(LKey e)
        {

        }

        public virtual void OnKeyUp(LKey e)
        {

        }

        public virtual void KeyTyped(LKey e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            OnKeyTyped(e);
        }

        public virtual void OnKeyTyped(LKey e)
        {

        }

        public virtual void MousePressed(LTouch e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            int type = e.GetCode();
            int button = e.GetButton();
            try
            {
                if (useScreenListener)
                {
                    foreach (ScreenListener t in screens)
                    {
                        t.Pressed(e);
                    }
                }
                this.touchX = e.X();
                this.touchY = e.Y();
                if (!IsClickLimit(e))
                {
                    TouchDown(e);
                }
                touchType[type] = true;
                touchButtonPressed = button;
                touchButtonReleased = NO_BUTTON;
                if (isMultitouch && desktop != null)
                {
                    desktop.DoClick(touchX, touchY);
                }
            }
            catch (Exception ex)
            {
                touchButtonPressed = NO_BUTTON;
                touchButtonReleased = NO_BUTTON;
                Log.Exception(ex);
            }
        }

        public abstract void TouchDown(LTouch e);

        public virtual void MouseReleased(LTouch e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            int type = e.GetCode();
            int button = e.GetButton();
            try
            {
                if (useScreenListener)
                {
                    foreach (ScreenListener t in screens)
                    {
                        t.Released(e);
                    }
                }
                this.touchX = e.X();
                this.touchY = e.Y();
                if (!IsClickLimit(e))
                {
                    TouchUp(e);
                }
                touchType[type] = false;
                touchButtonReleased = button;
                touchButtonPressed = NO_BUTTON;
                if (isMultitouch && desktop != null)
                {
                    desktop.DoClicked(touchX, touchY);
                }
            }
            catch (Exception ex)
            {
                touchButtonPressed = NO_BUTTON;
                touchButtonReleased = NO_BUTTON;
                Log.Exception(ex);
            }
        }

        public abstract void TouchUp(LTouch e);

        public virtual void MouseMoved(LTouch e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            if (useScreenListener)
            {
                foreach (ScreenListener t in screens)
                {
                    t.Move(e);
                }
            }
            this.touchX = e.X();
            this.touchY = e.Y();
            if (!IsClickLimit(e))
            {
                TouchMove(e);
            }
        }

        public abstract void TouchMove(LTouch e);


        public virtual void MouseDragged(LTouch e)
        {
            if (isLock || isClose || !isLoad)
            {
                return;
            }
            if (isTranslate)
            {
                e.Offset(tx, ty);
            }
            if (useScreenListener)
            {
                foreach (ScreenListener t in screens)
                {
                    t.Drag(e);
                }
            }
            this.touchX = e.X();
            this.touchY = e.Y();
            if (!IsClickLimit(e))
            {
                TouchDrag(e);
            }
        }

        public abstract void TouchDrag(LTouch e);

        public virtual void MouseClicked(LTouch e)
        {

        }

        public virtual void OnResume()
        {

        }

        public virtual void OnPause()
        {

        }

        public virtual void Move(double x, double y)
        {
            this.touchX = (int)x;
            this.touchY = (int)y;
        }

        public virtual bool IsMoving()
        {
            return Touch.IsDrag();
        }

        public virtual int GetHalfWidth()
        {
            return halfWidth;
        }

        public virtual int GetHalfHeight()
        {
            return halfHeight;
        }

        public virtual SensorDirection GetSensorDirection()
        {
            return direction;
        }

        public virtual bool IsMultitouch()
        {
            return isMultitouch;
        }

        public virtual void SetMultitouch(bool isMultitouch)
        {
            this.isMultitouch = isMultitouch;
        }

        public virtual PaintOrder GetFristOrder()
        {
            return fristOrder;
        }

        public virtual void SetFristOrder(PaintOrder fristOrder)
        {
            if (fristOrder == null)
            {
                this.fristPaintFlag = false;
            }
            else
            {
                this.fristPaintFlag = true;
                this.fristOrder = fristOrder;
            }
        }

        public virtual PaintOrder GetSecondOrder()
        {
            return secondOrder;
        }

        public virtual void SetSecondOrder(PaintOrder secondOrder)
        {
            if (secondOrder == null)
            {
                this.secondPaintFlag = false;
            }
            else
            {
                this.secondPaintFlag = true;
                this.secondOrder = secondOrder;
            }
        }

        public virtual PaintOrder GetLastOrder()
        {
            return lastOrder;
        }

        public virtual void SetLastOrder(PaintOrder lastOrder)
        {
            if (lastOrder == null)
            {
                this.lastPaintFlag = false;
            }
            else
            {
                this.lastPaintFlag = true;
                this.lastOrder = lastOrder;
            }
        }

        public virtual void Destroy()
        {
            lock (this)
            {
                if (useScreenListener)
                {
                    foreach (ScreenListener t in screens)
                    {
                        t.Dispose();
                    }
                }
                useScreenListener = false;
                replaceLoading = false;
                replaceDelay.SetDelay(10);
                tx = ty = 0;
                isClose = true;
                CallEvents(false);
                isTranslate = false;
                isNext = false;
                isGravity = false;
                isNext = false;
                isLock = true;
                if (sprites != null)
                {
                    sprites.Dispose();
                    sprites = null;
                }
                if (desktop != null)
                {
                    desktop.Dispose();
                    desktop = null;
                }
                if (currentScreen != null)
                {
                    LTexture parent = currentScreen.GetParent();
                    if (parent != null)
                    {
                        parent.CloseChildAll();
                        parent.Destroy();
                    }
                    else
                    {
                        currentScreen.Destroy();
                    }
                    currentScreen = null;
                }
                if (gravityHandler != null)
                {
                    gravityHandler.Dispose();
                    gravityHandler = null;
                }
                if (releases != null)
                {
                    foreach (LRelease r in releases)
                    {
                        if (r != null)
                        {
                            r.Dispose();
                        }
                    }
                    CollectionUtils.Clear(releases);
                }
                Dispose();
            }
        }

        public virtual void SetAutoDestory(bool a)
        {
            if (desktop != null)
            {
                desktop.SetAutoDestory(a);
            }
        }

        public virtual bool IsAutoDestory()
        {
            if (desktop != null)
            {
                return desktop.IsAutoDestory();
            }
            return false;
        }

        public virtual void Dispose()
        {

        }

    }
}
