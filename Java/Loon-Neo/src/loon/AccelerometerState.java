package loon;

import loon.geom.Vector3f;

public class AccelerometerState {

	protected boolean _isConnected;

	final Vector3f _acceleration = new Vector3f();

	public Vector3f getAcceleration() {
		return _acceleration;
	}

	public boolean isConnected() {
		return _isConnected;
	}
	
	public void setConnected(boolean connected){
		this._isConnected = connected;
	}

}
