
using loon.utils;
using loon.utils.reply;
using loon.utils.timer;
using System;

namespace loon
{
    public class Display
    {
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

        private readonly LGame _game;

        private readonly long updateRate;

        private long nextUpdate;

        public Display(LGame g, long updateRate)
        {
            this.updateRate = updateRate;
            this._game = g;
            _game.CheckBaseGame(g);
            _game.frame.Connect(new PortImpl(this));
        }

        private class PortImpl : Port<object>
        {
            private readonly Display outer;

            public PortImpl(Display outer)
            {
                this.outer = outer;
            }

            public override void OnEmit(object game)
            {
                outer.OnFrame();
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

        public LTimerContext GetPaint()
        {
            return paintClock;
        }

        public virtual LGame Game
        {
            get
            {
                return _game;
            }
        }

    }
}
