
How to use? please copy android version code of here .

initialize code:

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.showFPS = true;
		GameScene.register(setting, yourscreen.class);
	}