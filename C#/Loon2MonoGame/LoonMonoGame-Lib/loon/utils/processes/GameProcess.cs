using loon.utils.timer;

namespace loon.utils.processes
{

	public interface GameProcess
	{

		 GameProcessType GetProcessType();

		 void SetProcessType(GameProcessType processType);

		 void SetProcessHost(RealtimeProcessHost host);

		 void FireThisWhenFinished(GameProcess process);

		void Tick(LTimerContext time);

		void Kill();

		bool IsDead();

		string GetId();

		void Finish();
	}
}
