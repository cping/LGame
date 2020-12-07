package loon.robovm;

import loon.AccelerometerDefault;

public class RoboVMAccelerometer extends AccelerometerDefault {
	
	private RoboVMGame _game;
	
	public RoboVMAccelerometer(RoboVMGame game){
		super();
		this._game = game;
	}
	
	public RoboVMGame getGame(){
		return _game;
	}

}
