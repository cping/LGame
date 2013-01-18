namespace Loon.Core.Input
{
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Microsoft.Xna.Framework.Content;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework;
    using Loon.Java;
    using Loon.Core.Event;
    using Loon.Core.Graphics;
    using Loon.Core.Timer;
    using Loon.Core.Graphics.Opengl;
    using Loon.Core.Graphics.Device;
    using Loon.Utils;
    using Loon.Media;

    public class LProcess
    {

        private AssetsSoundManager asm;

        internal EmulatorListener emulatorListener;

        internal EmulatorButtons emulatorButtons;

        private LGameXNA2DActivity scene;

        private LinkedList<Screen> screens;

        private Screen currentControl, loading_Control;

        private bool running, waitTransition, loading_complete = false;

        private bool isInstance;

        private int id, width, height;

        private LInputFactory currentInput;

        private LTransition transition;

        internal GLEx gl;

        List<Updateable> loads;

        List<Updateable> unloads;

        List<Drawable> drawings;

        public LProcess(LGameXNA2DActivity p, int width, int height)
        {
            this.scene = p;
            this.currentInput = new LInputFactory(this);
            this.screens = new LinkedList<Screen>();
            this.width = width;
            this.height = height;
            this.Clear();
        }

        public void Clear()
        {
            if (loads == null)
            {
                loads = new List<Updateable>(10);
            }
            else
            {
                loads.Clear();
            }
            if (unloads == null)
            {
                unloads = new List<Updateable>(10);
            }
            else
            {
                unloads.Clear();
            }
            if (drawings == null)
            {
                drawings = new List<Drawable>(10);
            }
            else
            {
                drawings.Clear();
            }
        }

        public void Load(GL g)
        {
            if (gl == null)
            {
                this.gl = new GLEx(g);
            }
            else
            {
                gl.Load(g);
            }
            this.gl.Update();
        }

        public void Begin()
        {
            if (!running)
            {
                if (loading_Control != null)
                {
                    SetScreen(loading_Control);
                }
                running = true;
            }
        }

        public virtual void Resize(int w, int h)
        {
            if (isInstance)
            {
                currentControl.Resize(w, h);
            }
        }

        public LColor GetColor()
        {
            if (isInstance)
            {
                return currentControl.GetColor();
            }
            return null;
        }

        public void End()
        {
            if (running)
            {
                running = false;
            }
        }

        public void Calls()
        {
            if (isInstance)
            {
                currentControl.CallEvents(true);
            }
        }

        public void OnResume()
        {
            if (isInstance)
            {
                currentControl.OnResume();
            }
        }

        public void OnPause()
        {
            if (isInstance)
            {
                currentControl.OnPause();
            }
        }

        public bool Next()
        {
            if (isInstance)
            {

                if (currentControl.Next())
                {
                    return true;
                }
            }
            else
            {
                if (loading_complete && !LSystem.isLogo)
                {
                    if (loading_Control != null)
                    {
                        loading_complete = false;
                        SetScreen(loading_Control);
                    }
                }
                return false;
            }
            return false;
        }


        public void RunTimer(LTimerContext context)
        {
            if (isInstance)
            {
                if (waitTransition)
                {
                    if (transition != null)
                    {
                        switch (transition.code)
                        {
                            default:
                                if (!currentControl.IsOnLoadComplete())
                                {
                                    transition.Update(context.timeSinceLastUpdate);
                                }
                                break;
                            case 1:
                                if (!transition.Completed())
                                {
                                    transition.Update(context.timeSinceLastUpdate);
                                }
                                else
                                {
                                    EndTransition();
                                }
                                break;
                        }
                    }
                }
                else
                {
                    currentControl.RunTimer(context);
                    return;
               }
            }
        }

        private static void CallUpdateable(List<Updateable> list)
        {
            List<Updateable> loadCache;
            lock (list)
            {
                loadCache = new List<Updateable>(list);
                list.Clear();
            }
            for (int i = 0; i < loadCache.Count; i++)
            {
                Updateable running = loadCache[i];
                lock (running)
                {
                    running.Action();
                }
            }
            loadCache = null;
        }

        // --- Load start ---//

        public void AddLoad(Updateable u)
        {
            lock (loads)
            {
                loads.Add(u);
            }
        }

        public void RemoveLoad(Updateable u)
        {
            lock (loads)
            {
                loads.Remove(u);
            }
        }

        public void RemoveAllLoad()
        {
            lock (loads)
            {
                loads.Clear();
            }
        }

        public void Load()
        {
            if (isInstance)
            {
                int count = loads.Count;
                if (count > 0)
                {

                    CallUpdateable(loads);
                }
            }
        }

        // --- Load end ---//

        // --- UnLoad start ---//

        public void AddUnLoad(Updateable u)
        {
            lock (unloads)
            {
                unloads.Add(u);
            }
        }

        public void RemoveUnLoad(Updateable u)
        {
            lock (unloads)
            {
                unloads.Remove(u);
            }
        }

        public void RemoveAllUnLoad()
        {
            lock (unloads)
            {
                unloads.Clear();
            }
        }

        public void Unload()
        {
            if (isInstance)
            {
                int count = unloads.Count;
                if (count > 0)
                {
                    CallUpdateable(unloads);
                }
            }
        }

        // --- UnLoad end ---//

        // --- Drawable start ---//

        public void AddDrawing(Drawable d)
        {
            lock (drawings)
            {
                drawings.Add(d);
            }
        }

        public void RemoveDrawing(Drawable d)
        {
            lock (drawings)
            {
                drawings.Remove(d);
            }
        }

        public void RemoveAllDrawing()
        {
            lock (drawings)
            {
                drawings.Clear();
            }
        }

        public void Drawable(long elapsedTime)
        {
            if (isInstance)
            {
                int count = drawings.Count;
                if (count > 0)
                {
                    for (int i = 0; i < count; i++)
                    {
                        drawings[i].Action(elapsedTime);
                    }
                    // not delete
                    // drawings.clear();
                }
            }
        }

        // --- Drawable end ---//


        public void Draw()
        {
            if (isInstance)
            {
                if (waitTransition)
                {
                    if (transition != null)
                    {
                        if (transition.isDisplayGameUI)
                        {
                            currentControl.CreateUI(gl);
                        }
                        switch (transition.code)
                        {
                            default:
                                if (!currentControl.IsOnLoadComplete())
                                {
                                    transition.Draw(gl);
                                }
                                break;
                            case 1:
                                if (!transition.Completed())
                                {
                                    transition.Draw(gl);
                                }
                                break;
                        }
                    }
                }
                else
                {
                    currentControl.CreateUI(gl);
                    return;
                }
            }
        }

        public void DrawEmulator()
        {
            if (emulatorButtons != null)
            {
                emulatorButtons.Draw(gl);
            }
        }

        public float GetX()
        {
            if (isInstance)
            {
                return currentControl.GetX();
            }
            return 0;
        }

        public float GetY()
        {
            if (isInstance)
            {
                return currentControl.GetY();
            }
            return 0;
        }

        public LTexture GetBackground()
        {
            if (isInstance)
            {
                return currentControl.GetBackground();
            }
            return null;
        }

        public int GetRepaintMode()
        {
            if (isInstance)
            {
                return currentControl.GetRepaintMode();
            }
            return Screen.SCREEN_NOT_REPAINT;
        }

        public void SetEmulatorListener(EmulatorListener emulator)
        {
            this.emulatorListener = emulator;
            if (emulatorListener != null)
            {
                if (emulatorButtons == null)
                {
                    emulatorButtons = new EmulatorButtons(emulatorListener,
                            LSystem.screenRect.width, LSystem.screenRect.height);
                }
                else
                {
                    emulatorButtons.SetEmulatorListener(emulator);
                }
            }
            else
            {
                emulatorButtons = null;
            }
        }

        public EmulatorListener GetEmulatorListener()
        {
            return emulatorListener;
        }

        public EmulatorButtons GetEmulatorButtons()
        {
            return emulatorButtons;
        }

        public void SetID(int id)
        {
            this.id = id;
        }

        public int GetID()
        {
            return id;
        }

        public bool IsScale()
        {
            return LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1;
        }

        public void SetTransition(LTransition t)
        {
            this.transition = t;
        }

        public LTransition GetTransition()
        {
            return this.transition;
        }

        private void StartTransition()
        {
            if (transition != null)
            {
                waitTransition = true;
                if (currentControl != null)
                {
                    currentControl.SetLock(true);
                }
            }
        }

        private void EndTransition()
        {
            if (transition != null)
            {
               switch (transition.code)
                {
                    default:
                        waitTransition = false;
                        transition.Dispose();
                        break;
                    case 1:
                        if (transition.Completed())
                        {
                            waitTransition = false;
                            transition.Dispose();
                        }
                        break;
                }
                if (currentControl != null)
                {
                    currentControl.SetLock(false);
                }
            }
            else
            {
                waitTransition = false;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public Screen GetScreen()
        {
            return currentControl;
        }

        public void RunFirstScreen()
        {
            int size = screens.Count;
            if (size > 0)
            {
                Screen o = screens.First.Value;
                if (o != currentControl)
                {
                    SetScreen(o, false);
                }
            }
        }

        public void RunLastScreen()
        {
            int size = screens.Count;
            if (size > 0)
            {
                Screen o = screens.Last.Value;
                if (o != currentControl)
                {
                    SetScreen(o, false);
                }
            }
        }

        public void RunPreviousScreen()
        {
            int size = screens.Count;
            if (size > 0)
            {
                LinkedListNode<Screen> next = screens.Find(currentControl);
                if (next != null)
                {
                    SetScreen(next.Previous.Value, false);
                }
            }
        }

        public void RunNextScreen()
        {
            int size = screens.Count;
            if (size > 0)
            {
                LinkedListNode<Screen> next = screens.Find(currentControl);
                if (next != null)
                {
                    SetScreen(next.Next.Value, false);
                }
            }
        }

        public void RunIndexScreen(int index)
        {
            int size = screens.Count;
            if (size > 0 && index > -1 && index < size)
            {
                Screen o = null;
                LinkedListNode<Screen> next = screens.First;
                for (int i = 0; i < size; i++)
                {
                    if (i == index)
                    {
                        o = next.Value;
                        break;
                    }
                    next = next.Next;
                }
                if (o != null && currentControl != o)
                {
                    SetScreen(o, false);
                }
            }
        }

        public void AddScreen(Screen screen)
        {
            if (screen == null)
            {
                throw new RuntimeException("Cannot create a [IScreen] instance !");
            }
            screens.AddLast(screen);
        }

        public LinkedList<Screen> GetScreens()
        {
            return screens;
        }

        public int GetScreenCount()
        {
            return screens.Count;
        }

        public bool IsInstance
        {
            get
            {
                return isInstance;
            }
        }

        public GLEx GetGL
        {
            get
            {
                return gl;
            }
        }

        public void SetScreen(Screen screen)
        {
            if (GL.device == null)
            {
                loading_Control = screen;
                loading_complete = true;
            }
            else
            {
                SetScreen(screen, true);
            }
        }

        public void InputUpdate(long elapsedTime)
        {
            if (isInstance)
            {
                this.currentInput.Update(elapsedTime);
            }
        }

        internal class _LoadingScreen : Thread
        {

            private LProcess process;

            private Screen screen;

            public _LoadingScreen(LProcess p, Screen s)
                : base("ProcessThread")
            {
                this.process = p;
                this.screen = s;
            }

            public override void Run()
            {
                for (; LSystem.isLogo; )
                {
                    try
                    {
                        Thread.Sleep(60);
                    }
                    catch (System.Exception e)
                    {
                        Loon.Utils.Debug.Log.Exception(e);
                    }
                }
                screen.SetClose(false);
                screen.OnLoad();
                screen.SetOnLoadState(true);
                screen.OnLoaded();
                process.EndTransition();
            }

        }

        public void InitDestory()
        {

        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void SetScreen(Screen screen, bool put)
        {
            if (currentControl != null && currentControl.IsOnLoadComplete())
            {
                return;
            }
            lock (this)
            {
                if (screen == null)
                {
                    this.isInstance = false;
                    throw new RuntimeException(
                            "Cannot create a [Screen] instance !");
                }
                GLLoader.Destory();
                if (!LSystem.isLogo)
                {
                    if (currentControl != null)
                    {
                        SetTransition(screen.OnTransition());
                    }
                    else
                    {
                        LTransition transition = screen.OnTransition();
                        if (transition == null)
                        {
                            switch (MathUtils.Random(0, 3))
                            {
                                case 0:
                                    transition = LTransition.NewFadeIn();
                                    break;
                                case 1:
                                    transition = LTransition.NewArc();
                                    break;
                                case 2:
                                    transition = LTransition
                                            .NewSplitRandom(LColor.black);
                                    break;
                                case 3:
                                    transition = LTransition
                                            .NewCrossRandom(LColor.black);
                                    break;
                            }
                        }
                        SetTransition(transition);
                    }
                }
                screen.SetOnLoadState(false);
                if (currentControl == null)
                {
                    currentControl = screen;
                }
                else
                {
                    lock (currentControl)
                    {
                        currentControl.Destroy();
                        currentControl = screen;
                    }
                }
                this.isInstance = true;
                if (screen is EmulatorListener)
                {
                    SetEmulatorListener((EmulatorListener)screen);
                }
                else
                {
                    SetEmulatorListener(null);
                }

                screen.OnCreate(LSystem.screenRect.width, LSystem.screenRect.height);

                LSystem.CallScreenRunnable(new _LoadingScreen(this, screen));

                if (put)
                {
                    screens.AddLast(screen);
                }
                loading_Control = null;
            }
        }

        public AssetsSoundManager GetAssetsSound()
        {
            if (this.asm == null)
            {
                this.asm = AssetsSoundManager.GetInstance();
            }
            return asm;
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public LInputFactory GetInput()
        {
            return currentInput;
        }

        public void MousePressed(LTouch e)
        {

            if (isInstance)
            {

                currentControl.MousePressed(e);
            }
        }

        public void MouseReleased(LTouch e)
        {
            if (isInstance)
            {
                currentControl.MouseReleased(e);
            }
        }

        public void MouseMoved(LTouch e)
        {
            if (isInstance)
            {
                currentControl.MouseMoved(e);
            }
        }

        public void MouseDragged(LTouch e)
        {
            if (isInstance)
            {
                currentControl.MouseDragged(e);
            }
        }

        public void MouseClicked(LTouch e)
        {
            if (isInstance)
            {
                currentControl.MouseClicked(e);
            }
        }

        public void KeyDown(LKey e)
        {
            if (isInstance)
            {
                currentControl.OnKeyDown(e);
            }
        }

        public void KeyUp(LKey e)
        {
            if (isInstance)
            {
                currentControl.OnKeyUp(e);
            }
        }

        public void KeyTyped(LKey e)
        {
            if (isInstance)
            {
                currentControl.OnKeyTyped(e);
            }
        }

        public void OnDestroy()
        {
            EndTransition();
            if (GLEx.Self != null)
            {
                GLEx.Self.Dispose();
            }
            scene = null;
            screens.Clear();
            if (isInstance)
            {
                isInstance = false;
                loads.Clear();
                unloads.Clear();
                drawings.Clear();
                if (currentControl != null)
                {
                    currentControl.Destroy();
                    currentControl = null;
                }
                LSystem.isLogo = false;
                LSystem.isPaused = true;
                LSystem.AUTO_REPAINT = false;
                LImage.DisposeAll();
                LTextures.DisposeAll();
                XNACircle.Free();
                InitDestory();
            }
        }

        public void SetCurrentScreen(Screen screen)
        {
            if (screen != null)
            {
                this.isInstance = false;
                if (currentControl != null)
                {
                    currentControl.Destroy();
                }
                this.currentControl = screen;
                currentControl.SetLock(false);
                currentControl.SetLocation(0, 0);
                currentControl.SetClose(false);
                currentControl.SetOnLoadState(true);
                if (screen.GetBackground() != null)
                {
                    currentControl.SetRepaintMode(Screen.SCREEN_BITMAP_REPAINT);
                }
                this.isInstance = true;
                if (screen is EmulatorListener)
                {
                    SetEmulatorListener((EmulatorListener)screen);
                }
                else
                {
                    SetEmulatorListener(null);
                }
                screens.AddLast(screen);
            }
        }

    }
}
