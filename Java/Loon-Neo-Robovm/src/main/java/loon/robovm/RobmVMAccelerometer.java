package loon.robovm;

import loon.AccelerometerDefault;

public class RobmVMAccelerometer extends AccelerometerDefault {
	
	private RoboVMGame _game;
	
	public RobmVMAccelerometer(RoboVMGame game){
		super();
		this._game = game;
	}
	
	public RoboVMGame getGame(){
		return _game;
	}

}
