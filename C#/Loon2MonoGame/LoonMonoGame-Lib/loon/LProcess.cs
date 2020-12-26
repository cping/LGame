using loon.events;
using loon.geom;
using loon.utils;
using loon.utils.processes;
using loon.utils.reply;
using loon.utils.timer;

namespace loon
{
    public class LProcess
    {


        protected internal TArray<Updateable> resumes;

        protected internal TArray<Updateable> loads;

        protected internal TArray<Updateable> unloads;

        protected internal EmulatorListener emulatorListener;

        private EmulatorButtons emulatorButtons;

        private readonly ListMap<string, Screen> _screenMap;

        private readonly TArray<Screen> _screens;

        private bool isInstance;

        private int id;

        private bool _waitTransition;

        private bool _running;

        private Screen _currentScreen, _loadingScreen;

        private LTransition _transition;

        private LogDisplay _logDisplay;

        private readonly ObjectBundle _bundle;

        private readonly SysInputFactory _currentInput;

        private readonly LGame _game;
        public LProcess(LGame game) : base()
        {
            this._game = game;
            LSetting setting = _game.setting;
            setting.UpdateScale();
            LSystem.viewSize.SetSize(setting.width, setting.height);
            this._bundle = new ObjectBundle();
            this._currentInput = new SysInputFactory();
            this._screens = new TArray<Screen>();
            this._screenMap = new ListMap<string, Screen>();
            this.Clear();
            InputMake input = game.Input();
            if (input != null)
            {
                //这部分与Java版没必要1:1实现,因为XNA有TouchPanel.GetCapabilities().IsConnected方法判定是否支持触屏
                if (!game.setting.emulateTouch && !_game.Input().HasTouch())
                {
                    input.mouseEvents.Connect(new ButtonPort(this));
                }
                else
                {
                    input.touchEvents.Connect(new TouchPort(this));
                }
                input.keyboardEvents.Connect(new KeyPort(this));
            }
            game.status.Connect(new StatusPort(this));
        }

        private class ButtonPort : MouseMake.ButtonSlot
        {
            private readonly LProcess outer;

            public ButtonPort(LProcess outer)
            {
                this.outer = outer;
            }

            public override void OnEmit(MouseMake.ButtonEvent e)
            {
                outer._currentInput.CallMouse(e);
            }
        }


        private class TouchPort : Port<TouchMake.Event[]>
        {
            private readonly LProcess outer;

            public TouchPort(LProcess outer)
            {
                this.outer = outer;
            }

            public override void OnEmit(TouchMake.Event[] events)
            {
                outer._currentInput.CallTouch(events);
            }
        }

        private void SetScreen(Screen screen, bool put)
        {
            if (_loadingScreen != null && _loadingScreen.IsOnLoadComplete())
            {
                return;
            }
            try
            {
                lock (this)
                {
                    if (screen == null)
                    {
                        this.isInstance = false;
                        throw new LSysException("Cannot create a [Screen] instance !");
                    }

                    //screen.SetOnLoadState(false);
                    if (_currentScreen == null)
                    {
                        _currentScreen = screen;
                    }
                    else
                    {
                        KillScreen(screen);
                    }
                    this.isInstance = true;

                    screen.OnCreate(LSystem.viewSize.GetWidth(), LSystem.viewSize.GetHeight());

                    RealtimeProcess process = new RealtimeProcessImpl(this, screen);
                    process.SetProcessType(GameProcessType.Initialize);
                    process.SetDelay(0);

                    RealtimeProcessManager.Get().AddProcess(process);

                    if (put)
                    {
                        _screens.Add(screen);
                    }
                    _loadingScreen = null;
                }
            }
            catch (System.Exception cause)
            {
                LSystem.Error("Update Screen failed: " + screen, cause);
            }
        }

        private void KillScreen(Screen screen)
        {
            try
            {
                lock (_currentScreen)
                {
                    if (_currentScreen != null)
                    {
                        _currentScreen.Destroy();
                    }
                    if (screen == _currentScreen)
                    {
                        screen.Pause();
                    }
                    screen.Destroy();
                    _currentScreen = screen;
                }
            }
            catch (System.Exception cause)
            {
                LSystem.Error("Destroy screen failure", cause);
            }
        }

        private class RealtimeProcessImpl : RealtimeProcess
        {
            private readonly LProcess outerInstance;

            private loon.Screen screen;

            public RealtimeProcessImpl(LProcess outerInstance, loon.Screen screen)
            {
                this.outerInstance = outerInstance;
                this.screen = screen;
            }


            public override void Run(LTimerContext time)
            {
                if (outerInstance._game != null)
                {
                    try
                    {
                        //outerInstance.StartTransition();
                        //screen.SetClose(false);
                        //screen.ResetOrder();
                        //screen.ResetSize();
                        //screen.OnLoad();
                        //screen.OnLoaded();
                        //screen.SetOnLoadState(true);
                        screen.Resume();
                        //outerInstance.endTransition();
                    }
                    catch (System.Exception cause)
                    {
                        LSystem.Error("Screen onLoad dispatch failed: " + screen, cause);
                    }
                    finally
                    {
                        Kill();
                    }
                }
            }
        }

        private class KeyPort : KeyMake.KeyPort
        {
            private readonly LProcess outerInstance;

            public KeyPort(LProcess outerInstance)
            {
                this.outerInstance = outerInstance;
            }

            public override void OnEmit(KeyMake.KeyEvent e)
            {
                outerInstance._currentInput.CallKey(e);
            }
        }

        private class StatusPort : Port<LGame.Status>
        {
            private readonly LProcess outer;

            public StatusPort(LProcess outer)
            {
                this.outer = outer;
            }

            public override void OnEmit(LGame.Status e)
            {
                    switch (e)
                    {
                        case LGame.Status.EXIT:
                            outer.Stop();
                            break;
                        case LGame.Status.RESUME:
                            LSystem.PAUSED = false;
                            outer.Resume();
                            break;
                        case LGame.Status.PAUSE:
                            LSystem.PAUSED = true;
                            outer.Pause();
                            break;
                        default:
                            break;
                    
                }
            }
        }

        public virtual void Clear()
        {
            if (resumes == null)
            {
                resumes = new TArray<Updateable>(10);
            }
            else
            {
                resumes.Clear();
            }
            if (loads == null)
            {
                loads = new TArray<Updateable>(10);
            }
            else
            {
                loads.Clear();
            }
            if (unloads == null)
            {
                unloads = new TArray<Updateable>(10);
            }
            else
            {
                unloads.Clear();
            }
            ClearScreens();
        }

        public EmulatorButtons GetEmulatorButtons()
        {
            return emulatorButtons;
        }

        public virtual void Resize(int w, int h)
        {
            if (isInstance)
            {
                _currentInput.Reset();
                //_currentScreen.ResetSize(w, h);
            }
        }

        public virtual void Start()
        {

        }

        public virtual void Stop()
        {

        }

        public virtual Screen GetScreen()
        {
				return _currentScreen;
        }

        public virtual void SetScreen(Screen screen)
        {


            if (screen.handler == null)
            {
                //screen.ResetOrder();
                //screen.ResetSize();
            }
            /*if (_game.setting.isLogo && _game.display().showLogo)
            {
                _loadingScreen = value;
            }
            else
            {*/
            SetScreen(screen, true);
            //}

        }

        public virtual void ClearScreens()
        {
            _screenMap.Clear();
            foreach (Screen screen in _screens)
            {
                if (screen != null)
                {
                    screen.Destroy();
                }
            }
            _screens.Clear();
        }

        public virtual void ClearScreenMaps()
        {
            _screenMap.Clear();
        }

        public virtual void AddScreen(string name, Screen screen)
        {
            if (!_screenMap.ContainsKey(name))
            {
                _screenMap.Put(name, screen);
                AddScreen(screen);
            }
        }

        public virtual bool ContainsScreen(string name)
        {
            return _screenMap.ContainsKey(name);
        }

        public virtual Screen GetScreen(string name)
        {
            Screen screen = _screenMap.Get(name);
            if (screen != null)
            {
                return screen;
            }
            return null;
        }

        public virtual Screen RunScreenClassName(string name)
        {
            foreach (Screen screen in _screens)
            {
                if (screen != null)
                {
                    if (name.Equals(screen.GetName()))
                    {
                        SetScreen(screen);
                        return screen;
                    }
                }
            }
            return null;
        }

        public virtual Screen RunScreenName(string name)
        {
            foreach (Screen screen in _screens)
            {
                if (screen != null)
                {
                    if (name.Equals(screen.GetScreenName()))
                    {
                        SetScreen(screen);
                        return screen;
                    }
                }
            }
            return null;
        }

        public virtual Screen RunScreen(string name)
        {
            Screen screen = GetScreen(name);
            if (screen != null)
            {
                SetScreen(screen);
                return screen;
            }
            return null;
        }

        public virtual void RunPopScreen()
        {
            int size = _screens.size;
            if (size > 0)
            {
                Screen o = _screens.Pop();
                if (o != _currentScreen)
                {
                    SetScreen(o, false);
                }
            }
        }

        public virtual void RunPeekScreen()
        {
            RunLastScreen();
        }

        public virtual void RunFirstScreen()
        {
            int size = _screens.size;
            if (size > 0)
            {
                Screen o = _screens.First();
                if (o != _currentScreen)
                {
                    SetScreen(o, false);
                }
            }
        }

        public virtual void RunLastScreen()
        {
            int size = _screens.size;
            if (size > 0)
            {
                Screen o = _screens.Last();
                if (o != _currentScreen)
                {
                    SetScreen(o, false);
                }
            }
        }

        public virtual void RunPreviousScreen()
        {
            int size = _screens.size;
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    if (_currentScreen == _screens.Get(i))
                    {
                        if (i - 1 > -1)
                        {
                            SetScreen(_screens.Get(i - 1), false);
                            return;
                        }
                    }
                }
            }
        }

        public virtual void RunNextScreen()
        {
            int size = _screens.size;
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    if (_currentScreen == _screens.Get(i))
                    {
                        if (i + 1 < size)
                        {
                            SetScreen(_screens.Get(i + 1), false);
                            return;
                        }
                    }
                }
            }
        }

        public virtual void RunIndexScreen(int index)
        {
            int size = _screens.size;
            if (size > 0 && index > -1 && index < size)
            {
                object o = _screens.Get(index);
                if (_currentScreen != o)
                {
                    SetScreen(_screens.Get(index), false);
                }
            }
        }

        public virtual bool ContainsScreen(Screen screen)
        {
            if (screen == null)
            {
                throw new LSysException("Cannot create a [IScreen] instance !");
            }
            return _screens.Contains(screen);
        }

        public virtual void AddScreen(Screen screen)
        {
            if (screen == null)
            {
                throw new LSysException("Cannot create a [IScreen] instance !");
            }
            if (!_screens.Contains(screen))
            {
                _screens.Add(screen);
            }
        }

        public virtual TArray<Screen> GetScreens()
        {
                return _screens.Cpy();   
        }

        public virtual int GetScreenCount()
        {
                return _screens.size;
        }


        public virtual int GetHeight()
        {
            return Height;
        }

        public virtual int Height
        {
            get
            {
                if (isInstance)
                {
                    return _currentScreen.GetHeight();
                }
                return 0;
            }
        }

        public virtual int GetWidth()
        {
            return Width;
        }

        public virtual int Width
        {
            get
            {
                if (isInstance)
                {
                    return _currentScreen.GetWidth();
                }
                return 0;
            }
        }

        public virtual Screen GetCurrentScreen()
        {
                return _currentScreen;
        }

        public virtual void SetCurrentScreen(Screen screen, bool closed)
        {
            if (screen != null)
            {
                this.isInstance = false;
                if (closed && _currentScreen != null)
                {
                    _currentScreen.Destroy();
                }
                this._currentScreen = screen;
                /* _currentScreen.Lock = false;
                 _currentScreen.setLocation(0, 0);
                 _currentScreen.Close = false;
                 _currentScreen.OnLoadState = true;
                 if (screen.Background != null)
                 {
                     _currentScreen.RepaintMode = Screen.SCREEN_TEXTURE_REPAINT;
                 }
                 this.isInstance = true;
                 if (screen is EmulatorListener)
                 {
                     EmulatorListener = (EmulatorListener)screen;
                 }
                 else
                 {
                     EmulatorListener = null;
                 }*/
                this._screens.Add(screen);
            }
        }


        private static void CallUpdateable(TArray<Updateable> list)
        {
            lock (typeof(LProcess))
            {
                TArray<Updateable> loadCache;
                lock (list)
                {
                    loadCache = new TArray<Updateable>(list);
                    list.Clear();
                }
                for (int i = 0, size = loadCache.size; i < size; i++)
                {
                    Updateable r = loadCache.Get(i);
                    if (r == null)
                    {
                        continue;
                    }
                    lock (r)
                    {
                        try
                        {
                            r.Action(null);
                        }
                        catch (System.Exception cause)
                        {
                            LSystem.Error("Updateable dispatch failure", cause);
                        }
                    }
                }
                loadCache = null;
            }
        }


        public virtual void AddResume(Updateable u)
        {
            lock (resumes)
            {
                resumes.Add(u);
            }
        }

        public virtual void RemoveResume(Updateable u)
        {
            lock (resumes)
            {
                resumes.Remove(u);
            }
        }

        // --- Load start ---//

        public virtual void AddLoad(Updateable u)
        {
            lock (loads)
            {
                loads.Add(u);
            }
        }

        public virtual bool ContainsLoad(Updateable u)
        {
            lock (loads)
            {
                return loads.Contains(u);
            }
        }

        public virtual void RemoveLoad(Updateable u)
        {
            lock (loads)
            {
                loads.Remove(u);
            }
        }

        public virtual void RemoveAllLoad()
        {
            lock (loads)
            {
                loads.Clear();
            }
        }

        public virtual void Load()
        {
            if (isInstance)
            {
                int count = loads.size;
                if (count > 0)
                {
                    CallUpdateable(loads);
                }
            }
        }

        public virtual void AddUnLoad(Updateable u)
        {
            lock (unloads)
            {
                unloads.Add(u);
            }
        }

        public virtual bool ContainsUnLoad(Updateable u)
        {
            lock (unloads)
            {
                return unloads.Contains(u);
            }
        }

        public virtual void RemoveUnLoad(Updateable u)
        {
            lock (unloads)
            {
                unloads.Remove(u);
            }
        }

        public virtual void RemoveAllUnLoad()
        {
            lock (unloads)
            {
                unloads.Clear();
            }
        }

        public virtual void Unload()
        {
            if (isInstance)
            {
                int count = unloads.size;
                if (count > 0)
                {
                    CallUpdateable(unloads);
                }
            }
        }


        public virtual void Resume()
        {
            if (isInstance)
            {
                int count = resumes.size;
                if (count > 0)
                {
                    CallUpdateable(resumes);
                }
                _currentInput.Reset();
                _currentScreen.Resume();
            }
        }


        public virtual void Pause()
        {
            if (isInstance)
            {
                _currentInput.Reset();
                _currentScreen.Pause();
            }
        }
        public virtual void ResetTouch()
        {
            _currentInput.ResetSysTouch();
        }

        public float GetX()
        {
            return -1;
        }

        public float GetY()
        {
            return -1;
        }

        public virtual float GetScaleX()
        {
            if (isInstance)
            {
                return _currentScreen.GetScaleX();
            }
            return 1f;
        }

        public virtual float GetScaleY()
        {
            if (isInstance)
            {
                return _currentScreen.GetScaleY();
            }
            return 1f;
        }

        public virtual float GetRotation()
        {
            if (isInstance)
            {
                return _currentScreen.GetRotation();
            }
            return 0;
        }

        public bool IsFlipX()
        {
            if (isInstance)
            {
                return _currentScreen.IsFlipX();
            }
            return false;
        }

        public bool IsFlipY()
        {
            if (isInstance)
            {
                return _currentScreen.IsFlipY();
            }
            return false;
        }

        private static readonly Vector2f _tmpLocaltion = new Vector2f();

        public Vector2f ConvertXY(float x, float y)
        {
            float newX = ((x - GetX()) / (LSystem.GetScaleWidth()));
            float newY = ((y - GetY()) / (LSystem.GetScaleHeight()));
            if (isInstance && _currentScreen.IsTxUpdate())
            {
                float oldW = GetWidth();
                float oldH = GetHeight();
                float newW = GetWidth() * GetScaleX();
                float newH = GetHeight() * GetScaleY();
                float offX = oldW / 2f - newW / 2f;
                float offY = oldH / 2f - newH / 2f;
                float nx = (newX - offX);
                float ny = (newY - offY);
                int r = (int)GetRotation();
                switch (r)
                {
                    case -90:
                        offX = oldH / 2f - newW / 2f;
                        offY = oldW / 2f - newH / 2f;
                        nx = (newX - offY);
                        ny = (newY - offX);
                        _tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY()).RotateSelf(-90);
                        _tmpLocaltion.Set(-(_tmpLocaltion.x - GetWidth()), MathUtils.Abs(_tmpLocaltion.y));
                        break;
                    case 0:
                    case 360:
                        _tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY());
                        break;
                    case 90:
                        offX = oldH / 2f - newW / 2f;
                        offY = oldW / 2f - newH / 2f;
                        nx = (newX - offY);
                        ny = (newY - offX);
                        _tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY()).RotateSelf(90);
                        _tmpLocaltion.Set(-_tmpLocaltion.x, MathUtils.Abs(_tmpLocaltion.y - GetHeight()));
                        break;
                    case -180:
                    case 180:
                        _tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY()).RotateSelf(GetRotation()).AddSelf(GetWidth(),
                                GetHeight());
                        break;
                    default: // 原则上不处理非水平角度的触点
                        _tmpLocaltion.Set(newX, newY);
                        break;
                }
            }
            else
            {
                _tmpLocaltion.Set(newX, newY);
            }
            if (IsFlipX() || IsFlipY())
            {
                HelperUtils.Local2Global(IsFlipX(), IsFlipY(), GetWidth() / 2, GetHeight() / 2, _tmpLocaltion.x,
                        _tmpLocaltion.y, _tmpLocaltion);
                return _tmpLocaltion;
            }
            return _tmpLocaltion;
        }


        public virtual void KeyDown(GameKey e)
        {
            if (isInstance)
            {
                _currentScreen.KeyPressed(e);
            }
        }

        public virtual void KeyUp(GameKey e)
        {
            if (isInstance)
            {
                _currentScreen.KeyReleased(e);
            }
        }

        public virtual void KeyTyped(GameKey e)
        {
            if (isInstance)
            {
                _currentScreen.KeyTyped(e);
            }
        }

        public virtual void MousePressed(GameTouch e)
        {
            if (isInstance)
            {
                _currentScreen.MousePressed(e);
            }
        }

        public virtual void MouseReleased(GameTouch e)
        {
            if (isInstance)
            {
                _currentScreen.MouseReleased(e);
            }
        }

        public virtual void MouseMoved(GameTouch e)
        {
            if (isInstance)
            {
                _currentScreen.MouseMoved(e);
            }
        }

        public virtual void MouseDragged(GameTouch e)
        {
            if (isInstance)
            {
                _currentScreen.MouseDragged(e);
            }
        }

    }
}
