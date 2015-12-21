package loon.live2d.framework;

public class Live2DFramework {
	
	private static IPlatformManager platformManager;

	public Live2DFramework()
	{
		
	}
	
	public static IPlatformManager getPlatformManager() {
		return platformManager;
	}

	public static void setPlatformManager(IPlatformManager platformManager) {
		Live2DFramework.platformManager = platformManager;
	}


}
