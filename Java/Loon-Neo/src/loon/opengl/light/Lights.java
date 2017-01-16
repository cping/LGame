package loon.opengl.light;

import loon.LSystem;
import loon.canvas.LColor;
import loon.utils.TArray;

public class Lights {
	
	public final LColor ambientLight = new LColor(0f,0f,0f,1f);
    public LColor fog;
	public final TArray<DirectionalLight> directionalLights = new TArray<DirectionalLight>();
	public final TArray<PointLight> pointLights = new TArray<PointLight>();
	
	public Lights() {}
	
	public Lights(final LColor ambient) {
		ambientLight.setColor(ambient);
	}
	
	public Lights(final float ambientRed, final float ambientGreen, final float ambientBlue) {
		ambientLight.setColor(ambientRed, ambientGreen, ambientBlue, 1f);
	}
	
	public Lights(final LColor ambient, final BaseLight... lights) {
		this(ambient);
		add(lights);
	}
	
	public Lights clear() {
		ambientLight.setColor(0f,0f,0f,1f);
		directionalLights.clear();
		pointLights.clear();
		return this;
	}
	
	public Lights add(final BaseLight... lights) {
		for (final BaseLight light : lights){
			add(light);
		}
		return this;
	}
	
	public Lights add(final TArray<BaseLight> lights) {
		for (final BaseLight light : lights){
			add(light);
		}
		return this;
	}

	public Lights add(BaseLight light) {
		if (light instanceof DirectionalLight){
			directionalLights.add((DirectionalLight)light);
		}
		else if (light instanceof PointLight){
			pointLights.add((PointLight)light);
		}
		else{
			throw LSystem.runThrow("Unknown light type");
		}
		return this;
	}
}
