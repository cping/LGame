package org.test;

import loon.Stage;
import loon.component.LClickButton;
public class SoundTest extends Stage{

	@Override
	public void create() {
		
		LClickButton click1 = new LClickButton("Sound Ogg", 150, 100, 100, 25){
			
			public void downClick(){
				playSound("assets/shotgun.ogg");
			}
		};
		add(click1);
		

		LClickButton click2 = new LClickButton("Sound Wav", 150, 150, 100, 25){
			
			public void downClick(){
				playSound("assets/shotgun.wav");
			}
		};
		add(click2);
		
		add(MultiScreenTest.getBackButton(this,0));

		
	}
	
}
