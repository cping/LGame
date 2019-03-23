package org.test;

import loon.Stage;
import loon.action.ActionScript;
import loon.action.sprite.Sprite;

public class ActionScriptTest extends Stage {


	@Override
	public void create() {

		Sprite sprite = new Sprite("assets/ball.png");
		sprite.setLocation(200,200);
		
		add(sprite);
		
		ActionScript script = act(sprite,
				"move(180,600,true)->rotate(360)->delay(2f)->fadein(60)->fadeout(90)");
		script.start();
	}

}
