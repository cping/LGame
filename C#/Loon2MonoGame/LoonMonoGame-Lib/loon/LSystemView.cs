
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

		private readonly long updateRate;
		private int nextUpdate;
		private LGame game;

		public LSystemView(LGame g, long updateRate)
		{
			this.updateRate = updateRate;
			this.game = g;
			//game.frame.connect(new PortAnonymousInnerClass(this));
		}

	}
}
