package loon.opengl.light;

import loon.canvas.LColor;
import loon.geom.Vector3f;


public class DirectionalLight extends BaseLight {
	public final Vector3f direction = new Vector3f();
	
	public DirectionalLight set(final DirectionalLight copyFrom) {
		return set(copyFrom.color, copyFrom.direction);
	}
	
	public DirectionalLight set(final LColor color, final Vector3f direction) {
		if (color != null){
			this.color.setColor(color);
		}
		if (direction != null){
			this.direction.set(direction).norSelf();
		}
		return this;
	}
	
	public DirectionalLight set(final float r, final float g, final float b, final Vector3f direction) {
		this.color.setColor(r,g,b,1f);
		if (direction != null){
			this.direction.set(direction).norSelf();
		}
		return this;
	}
	
	public DirectionalLight set(final LColor color, final float dirX, final float dirY, final float dirZ) {
		if (color != null){
			this.color.setColor(color);
		}
		this.direction.set(dirX, dirY, dirZ).norSelf();
		return this;
	}
	
	public DirectionalLight set(final float r, final float g, final float b, final float dirX, final float dirY, final float dirZ) {
		this.color.setColor(r,g,b,1f);
		this.direction.set(dirX, dirY, dirZ).norSelf();
		return this;
	}
	
	@Override
	public boolean equals (Object o) {
		return (o instanceof DirectionalLight) ? equals((DirectionalLight)o) : false;
	}
	
	public boolean equals (final DirectionalLight other) {
		return (other != null) && ((other == this) || ((color.equals(other.color) && direction.equals(other.direction))));
	}
}
