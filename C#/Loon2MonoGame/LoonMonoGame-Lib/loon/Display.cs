
using java.lang;
using loon.opengl;
using loon.utils;
using loon.utils.processes;
using loon.utils.reply;
using loon.utils.timer;
using System;

namespace loon
{
    public class Display
    {
        private sealed class PaintPort : Port<LTimerContext>
        {

            internal readonly Display _display;

            internal PaintPort(Display d)
            {
                this._display = d;
            }

            public override void OnEmit(LTimerContext clock)
            {
                    lock (clock)
                    {
                        if (!LSystem.PAUSED)
                        {
                            RealtimeProcessManager.Get().Tick(clock);
                            // _display.Draw(clock);
                        }
                    }
            }

        }

        private sealed class PaintAllPort : Port<LTimerContext>
        {

            internal readonly Display _display;

            internal PaintAllPort(Display d)
            {
                this._display = d;
            }

            public override void OnEmit(LTimerContext clock)
            {
                    lock (clock)
                    {
                        if (!LSystem.PAUSED)
                        {
                            RealtimeProcessManager.Get().Tick(clock);
                            //ActionControl.Get().Call(clock.timeSinceLastUpdate);
                            //_display.draw(clock);
                        }
                    }
            }

        }

        private sealed class UpdatePort : Port<LTimerContext>
        {

            internal UpdatePort()
            {
            }

            public override void OnEmit(LTimerContext clock)
            {
                lock (clock)
                {
                    if (!LSystem.PAUSED)
                    {
                        //ActionControl.get().call(clock.timeSinceLastUpdate);
                    }
                }
            }
        }
        private class PortImpl : Port<object>
        {
            private readonly Display _outer;

            public PortImpl(Display o)
            {
                this._outer = o;
            }

            public override void OnEmit(object game)
            {
                _outer.OnFrame();
            }
        }



        private const string FPS_STR = "FPS:";

        private const string MEMORY_STR = "MEMORY:";

        private const string SPRITE_STR = "SPRITE:";

        private const string DESKTOP_STR = "DESKTOP:";

        private string displayMemony = MEMORY_STR;

        private string displaySprites = SPRITE_STR;

        private StrBuilder displayMessage = new StrBuilder(32);

        public readonly Act<LTimerContext> update = Act<LTimerContext>.Create<LTimerContext>();

        public readonly Act<LTimerContext> paint = Act<LTimerContext>.Create<LTimerContext>();

        private readonly LTimerContext updateClock = new LTimerContext();

        private readonly LTimerContext paintClock = new LTimerContext();

        private readonly GLEx _glEx;

        private readonly LProcess _process;

        private LSetting _setting;

        private readonly LGame _game;

        private readonly long updateRate;

        private long nextUpdate;

        private readonly bool memorySelf;

        private PaintAllPort paintAllPort;

        private PaintPort paintPort;

        private UpdatePort updatePort;

        public Display(LGame g, long updateRate)
        {
            this.updateRate = updateRate;
            this._game = g;
            this._game.CheckBaseGame(g);
            this._setting = _game.setting;
            this._process = _game.Process();
            this.memorySelf = _game.IsHTML5();
            Graphics graphics = _game.Graphics();
            GL20 gl = graphics.gl;
            this._glEx = new GLEx(graphics, graphics.defaultRenderTarget, gl);
            this._glEx.Update();
            UpdateSyncTween(_setting.isSyncTween);
            this.displayMemony = MEMORY_STR + "0";
            this.displaySprites = SPRITE_STR + "0, " + DESKTOP_STR + "0";
            if (!_setting.isLogo)
            {
                _process.Start();
            }
            _game.frame.Connect(new PortImpl(this));
        }

        public virtual void UpdateSyncTween(bool sync)
        {
            if (paintAllPort != null)
            {
                paint.Disconnect(paintAllPort);
            }
            if (paintPort != null)
            {
                paint.Disconnect(paintPort);
            }
            if (update != null)
            {
                update.Disconnect(updatePort);
            }
            if (sync)
            {
                paint.Connect(paintAllPort = new PaintAllPort(this));
            }
            else
            {
                paint.Connect(paintPort = new PaintPort(this));
                update.Connect(updatePort = new UpdatePort());
            }
        }

        public void Update(LTimerContext clock)
        {
            update.Emit(clock);
        }

        public void Paint(LTimerContext clock)
        {
            paint.Emit(clock);
        }

        private void OnFrame()
        {
            if (!LSystem._auto_repaint)
            {
                return;
            }
            int updateTick = _game.Tick();
            LSetting setting = _game.setting;
            long paintLoop = setting.fixedPaintLoopTime;
            long updateLoop = setting.fixedUpdateLoopTime;

            long nextUpdate = this.nextUpdate;

            if (updateTick >= nextUpdate)
            {
                long updateRate = this.updateRate;
                long updates = 0;
                while (updateTick >= nextUpdate)
                {
                    nextUpdate += updateRate;
                    updates++;
                }
                this.nextUpdate = nextUpdate;
                long updateDt = updates * updateRate;
                updateClock.tick += updateDt;
                if (updateLoop == -1)
                {
                    updateClock.timeSinceLastUpdate = updateDt;
                }
                else
                {
                    updateClock.timeSinceLastUpdate = updateLoop;
                }
                Update(updateClock);
            }
            long paintTick = _game.Tick();
            if (paintLoop == -1)
            {
                paintClock.timeSinceLastUpdate = paintTick - paintClock.tick;
            }
            else
            {
                paintClock.timeSinceLastUpdate = paintLoop;
            }
            paintClock.tick = paintTick;
            paintClock.alpha = 1f - (nextUpdate - paintTick) / (float)updateRate;
            Paint(paintClock);
        }

        public LTimerContext GetUpdate()
        {
            return updateClock;
        }

        public virtual float Width()
        {
            return LSystem.viewSize.Width();
        }

        public virtual float Height()
        {
            return LSystem.viewSize.Height();
        }

        public LTimerContext GetPaint()
        {
            return paintClock;
        }

        public virtual LGame GetGame()
        {
            return Game;
        }

        public virtual LGame Game
        {
            get
            {
                return _game;
            }
        }


        public virtual Display Resize(int viewWidth, int viewHeight)
        {
            _process.Resize(viewWidth, viewHeight);
            return this;
        }

        public virtual void SetScreen(Screen v)
        {
            Screen = v;
        }

        public virtual Screen Screen
        {
            set
            {
                _process.SetScreen(value);
            }
        }

        public virtual LProcess GetProcess()
        {
            return this.Process;
        }

        public virtual LProcess Process
        {
            get
            {
                return _process;
            }
        }

        public virtual GLEx GetGL()
        {
            return GL;
        }

        public virtual GLEx GL
        {
            get
            {
                return _glEx;
            }
        }
    }
}
