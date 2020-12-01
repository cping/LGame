namespace loon.events
{

	public interface LTouchArea
	{
		bool Contains(float x, float y);

		void OnAreaTouched(LTouchArea_Event e, float touchX, float touchY);
	}

	public enum LTouchArea_Event
	{
		DOWN,
		UP,
		MOVE,
		DRAG
	}

}
