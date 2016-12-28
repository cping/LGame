package loon.component.skin;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;

public class ControlSkin {

	private LTexture controlBaseTexture;

	private LTexture controlDotTexture;
	
	public static ControlSkin def(){
		return new ControlSkin();
	}

	public ControlSkin() {
		this(LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
				+ "control_base.png"), LTextures
				.loadTexture(LSystem.FRAMEWORK_IMG_NAME + "control_dot.png"));
	}

	public ControlSkin(LTexture basetex, LTexture dottex) {
		this.controlBaseTexture = basetex;
		this.controlDotTexture = dottex;
	}

	public LTexture getControlBaseTexture() {
		return controlBaseTexture;
	}

	public void setControlBaseTexture(LTexture controlBaseTexture) {
		this.controlBaseTexture = controlBaseTexture;
	}

	public LTexture getControlDotTexture() {
		return controlDotTexture;
	}

	public void setControlDotTexture(LTexture controlDotTexture) {
		this.controlDotTexture = controlDotTexture;
	}

}
