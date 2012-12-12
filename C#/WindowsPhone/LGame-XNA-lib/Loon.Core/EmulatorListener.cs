namespace Loon.Core {
	
	public interface EmulatorListener {
	
		void OnUpClick();
	
		void OnLeftClick();
	
		void OnRightClick();
	
		void OnDownClick();
	
		void OnTriangleClick();
	
		void OnSquareClick();
	
		void OnCircleClick();
	
		void OnCancelClick();
	
		void UnUpClick();
	
		void UnLeftClick();
	
		void UnRightClick();
	
		void UnDownClick();
	
		void UnTriangleClick();
	
		void UnSquareClick();
	
		void UnCircleClick();
	
		void UnCancelClick();
	
	}
}
