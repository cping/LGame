namespace Loon{

	public class LSetting{

		public int width = LSystem.MAX_SCREEN_WIDTH;
		
		public int height = LSystem.MAX_SCREEN_HEIGHT;
		
		public int fps = LSystem.DEFAULT_MAX_FPS;
		
		public string title;
		
		public bool full = true;
		
		public bool showFPS;
		
		public bool showMemory;
		
		public bool showLogo;
		
		public bool landscape;
		
		public LMode mode = LMode.Fill;

	}

}