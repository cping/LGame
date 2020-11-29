
using loon.utils.reply;
using loon.utils.timer;

namespace loon
{
    public abstract class LSystemView
    {

        public readonly Act<LTimerContext> update = Act<LTimerContext>.Create<LTimerContext>();

        public readonly Act<LTimerContext> paint = Act<LTimerContext>.Create<LTimerContext>();

        private readonly LTimerContext updateClock = new LTimerContext();
        private readonly LTimerContext paintClock = new LTimerContext();

        private readonly LGame _game;

        private readonly long updateRate;

        private long nextUpdate;

        public LSystemView(LGame g, long updateRate)
        {
            this.updateRate = updateRate;
            this._game = g;
            _game.CheckBaseGame(g);
            _game.frame.Connect(new PortImpl(this));
        }

        private class PortImpl : Port<LGame>
        {
            private readonly LSystemView outer;

            public PortImpl(LSystemView outer)
            {
                this.outer = outer;
            }

            public override void OnEmit(LGame game)
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
