using Loon.Core.Graphics.Opengl;
using Loon.Core.Input;
namespace Loon.Core.Event {
	
	public interface ScreenListener {
	
		void Fraw(GLEx g);
	
		void Update(long elapsedTime);
	
		void Pressed(LTouch e);
	
		void Released(LTouch e);
	
		void Move(LTouch e);
	
		void Drag(LTouch e);
	
		void Pressed(LKey e);
	
		void Released(LKey e);
	
		void Dispose();
	}
}
