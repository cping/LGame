using loon.utils.timer;

namespace loon.utils.processes
{
	public interface RealtimeProcessEvent
	{
		void AddProcess(GameProcess process);

		bool ContainsProcess(GameProcess process);

		void Tick(LTimerContext clock);
	}
}
