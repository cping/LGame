package loon.core.graphics.component;

import loon.core.LSystem;
import loon.core.graphics.opengl.LSubTexture;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.utils.collection.Array;

public class DefUI {

	private static Array<LTexture> defaultTextures;

	public static LTexture getDefaultTextures(int index) {
		if (defaultTextures == null) {
			defaultTextures = new Array<LTexture>();
			LTexture spritesheet = LTextures
					.loadTexture(LSystem.FRAMEWORK_IMG_NAME + "ui.png");
			LSubTexture windowbar = new LSubTexture(spritesheet, 0, 0, 512, 32); 
			LSubTexture panelbody = new LSubTexture(spritesheet, 1, 41 - 8, 17,
					57 - 8); 
			LSubTexture panelborder = new LSubTexture(spritesheet, 0, 41 - 8,
					1, 512 - 8); 
			LSubTexture buttonleft = new LSubTexture(spritesheet, 17, 41 - 8,
					33, 72 - 8); 
			LSubTexture buttonbody = new LSubTexture(spritesheet, 34, 41 - 8,
					48, 72 - 8);
			LSubTexture checkboxunchecked = new LSubTexture(spritesheet, 49,
					41 - 8, 72, 63 - 8); 
			LSubTexture checkboxchecked = new LSubTexture(spritesheet, 73,
					41 - 8, 96, 63 - 8); 
			LSubTexture imagebuttonidle = new LSubTexture(spritesheet, 145,
					41 - 8, 176, 72 - 8);
			LSubTexture imagebuttonhover = new LSubTexture(spritesheet, 177,
					41 - 8, 208, 72 - 8); 
			LSubTexture imagebuttonactive = new LSubTexture(spritesheet, 209,
					41 - 8, 240, 72 - 8); 
			LSubTexture textfieldleft = new LSubTexture(spritesheet, 218,
					40 - 8, 233, 72 - 8); 
			LSubTexture textfieldbody = new LSubTexture(spritesheet, 234,
					40 - 8, 250, 72 - 8); 
			defaultTextures.add(windowbar.get());
			defaultTextures.add(panelbody.get());
			defaultTextures.add(panelborder.get());
			defaultTextures.add(buttonleft.get());
			defaultTextures.add(buttonbody.get());
			defaultTextures.add(checkboxunchecked.get());
			defaultTextures.add(checkboxchecked.get());
			defaultTextures.add(imagebuttonidle.get());
			defaultTextures.add(imagebuttonhover.get());
			defaultTextures.add(imagebuttonactive.get());
			defaultTextures.add(textfieldleft.get());
			defaultTextures.add(textfieldbody.get());
		}

		return defaultTextures.get(index);
	}
}
