package org.test;

import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;

public class Particle
{
	public boolean active = true;
	public float alpha = 0f;
	public float angle = 0f;
	public boolean bounceFromGround = false;
	public LColor color = LColor.white;
	public double delay = 0.0;
	public float depth = 0f;
	public Vector2f direction = Vector2f.ZERO();
	public boolean gravity = false;
	public float growPerSecond;
	public Vector2f origin = Vector2f.ZERO();
	public Vector2f position = Vector2f.ZERO();
	public float reduceAlphaPerSecond = 0f;
	public float rotationPerSecond = 0f;
	public float scale;
	public boolean setAngleFromDirection = false;
	public SpriteEffects spriteEffect = SpriteEffects.None;
	public double timeAlive = 0.0;
}