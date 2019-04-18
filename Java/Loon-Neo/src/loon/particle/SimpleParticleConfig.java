/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.particle;

import loon.LSysException;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.particle.SimpleConfigurableEmitter.LinearInterpolator;
import loon.particle.SimpleConfigurableEmitter.RandomValue;
import loon.particle.SimpleConfigurableEmitter.SimpleValue;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class SimpleParticleConfig {

	public interface ConfigurableEmitterFactory {

		public SimpleConfigurableEmitter createEmitter(String name);
	}

	public static SimpleParticleSystem loadConfiguredSystem(String path, LColor mask) {
		return loadConfiguredSystem(path, null, null, mask);
	}

	public static SimpleParticleSystem loadConfiguredSystem(String path) {
		return loadConfiguredSystem(path, null, null, null);
	}

	public static SimpleParticleSystem loadConfiguredSystem(String path, ConfigurableEmitterFactory factory) {
		return loadConfiguredSystem(path, factory, null, null);
	}

	public static SimpleParticleSystem loadConfiguredSystem(String path, ConfigurableEmitterFactory factory,
			SimpleParticleSystem system, LColor mask) {
		if (factory == null) {
			factory = new ConfigurableEmitterFactory() {
				public SimpleConfigurableEmitter createEmitter(String name) {
					return new SimpleConfigurableEmitter(name);
				}
			};
		}
		try {

			XMLDocument doc = XMLParser.parse(path);
			XMLElement docElement = doc.getRoot();

			if (!docElement.getName().equals("system")) {
				throw new LSysException("Not a particle system file");
			}

			if (system == null) {
				system = new SimpleParticleSystem(LSystem.getSystemImagePath() + "par.png", 2000, mask);
			}
			boolean additive = docElement.getBoolAttribute("additive", false);
			if (additive) {
				system.setBlendingState(LSystem.MODE_ADD);
			} else {
				system.setBlendingState(LSystem.MODE_ALPHA_ONE);
			}
			boolean points = docElement.getBoolAttribute("points", false);
			system.setUsePoints(points);

			TArray<XMLElement> list = docElement.list();
			for (int i = 0; i < list.size; i++) {
				XMLElement em = list.get(i);

				SimpleConfigurableEmitter emitter = factory.createEmitter("new");
				elementToEmitter(em, emitter);

				system.addEmitter(emitter);
			}

			system.setRemoveCompletedEmitters(false);
			return system;
		} catch (Throwable e) {
			throw new LSysException(e.getMessage(), e);
		}
	}

	public static SimpleConfigurableEmitter loadEmitter(String path) {
		return loadEmitter(path, null);
	}

	public static SimpleConfigurableEmitter loadEmitter(String path, ConfigurableEmitterFactory factory) {
		if (factory == null) {
			factory = new ConfigurableEmitterFactory() {
				public SimpleConfigurableEmitter createEmitter(String name) {
					return new SimpleConfigurableEmitter(name);
				}
			};
		}
		try {

			XMLDocument doc = XMLParser.parse(path);
			XMLElement docElement = doc.getRoot();

			if (!docElement.getName().equals("emitter")) {
				throw new LSysException("Not a particle emitter file");
			}

			SimpleConfigurableEmitter emitter = factory.createEmitter("new");
			elementToEmitter(docElement, emitter);

			return emitter;
		} catch (Throwable e) {
			throw new LSysException("Unable to load emitter");
		}
	}

	private static XMLElement getFirstNamedElement(XMLElement element, String name) {
		TArray<XMLElement> list = element.list(name);
		if (list.size == 0) {
			return null;
		}
		return list.get(0);
	}

	private static void elementToEmitter(XMLElement element, SimpleConfigurableEmitter emitter) {

		emitter.name = element.getAttribute("name", "");
		emitter.setImageName(element.getAttribute("imageName", ""));

		String renderType = element.getAttribute("renderType", "");
		emitter.usePoints = SimpleParticle.INHERIT_POINTS;
		if (renderType.equals("quads")) {
			emitter.usePoints = SimpleParticle.USE_QUADS;
		}
		if (renderType.equals("points")) {
			emitter.usePoints = SimpleParticle.USE_POINTS;
		}

		emitter.useOriented = element.getBoolAttribute("useOriented", false);

		emitter.useAdditive = element.getBoolAttribute("useAdditive", false);

		parseRangeElement(getFirstNamedElement(element, "spawnInterval"), emitter.spawnInterval);
		parseRangeElement(getFirstNamedElement(element, "spawnCount"), emitter.spawnCount);
		parseRangeElement(getFirstNamedElement(element, "initialLife"), emitter.initialLife);
		parseRangeElement(getFirstNamedElement(element, "initialSize"), emitter.initialSize);
		parseRangeElement(getFirstNamedElement(element, "xOffset"), emitter.xOffset);
		parseRangeElement(getFirstNamedElement(element, "yOffset"), emitter.yOffset);
		parseRangeElement(getFirstNamedElement(element, "initialDistance"), emitter.initialDistance);
		parseRangeElement(getFirstNamedElement(element, "speed"), emitter.speed);
		parseRangeElement(getFirstNamedElement(element, "length"), emitter.length);
		parseRangeElement(getFirstNamedElement(element, "emitCount"), emitter.emitCount);

		parseValueElement(getFirstNamedElement(element, "spread"), emitter.spread);
		parseValueElement(getFirstNamedElement(element, "angularOffset"), emitter.angularOffset);
		parseValueElement(getFirstNamedElement(element, "growthFactor"), emitter.growthFactor);
		parseValueElement(getFirstNamedElement(element, "gravityFactor"), emitter.gravityFactor);
		parseValueElement(getFirstNamedElement(element, "windFactor"), emitter.windFactor);
		parseValueElement(getFirstNamedElement(element, "startAlpha"), emitter.startAlpha);
		parseValueElement(getFirstNamedElement(element, "endAlpha"), emitter.endAlpha);
		parseValueElement(getFirstNamedElement(element, "alpha"), emitter.alpha);
		parseValueElement(getFirstNamedElement(element, "size"), emitter.size);
		parseValueElement(getFirstNamedElement(element, "velocity"), emitter.velocity);
		parseValueElement(getFirstNamedElement(element, "scaleY"), emitter.scaleY);

		XMLElement color = getFirstNamedElement(element, "color");
		emitter.colors.clear();

		if (color != null) {
			TArray<XMLElement> steps = color.list();
			for (int i = 0; i < steps.size; i++) {
				XMLElement step = steps.get(i);
				float offset = step.getFloatAttribute("offset", 0);
				float r = step.getFloatAttribute("r", 0);
				float g = step.getFloatAttribute("g", 0);
				float b = step.getFloatAttribute("b", 0);
				emitter.addColorPoint(offset, new LColor(r, g, b, 1));
			}
		}
		emitter.replay();
	}

	private static void parseRangeElement(XMLElement element, SimpleConfigurableEmitter.Range range) {
		if (element == null) {
			return;
		}
		range.setMin(element.getFloatAttribute("min", 0));
		range.setMax(element.getFloatAttribute("max", 0));
		range.setEnabled(element.getBoolAttribute("enabled", false));
	}

	private static void parseValueElement(XMLElement element, SimpleConfigurableEmitter.Value value) {
		if (element == null) {
			return;
		}

		String type = element.getAttribute("type", null);
		String v = element.getAttribute("value", null);

		if (type == null || type.length() == 0) {
			if (value instanceof SimpleValue) {
				((SimpleValue) value).setValue(Float.parseFloat(v));
			} else if (value instanceof RandomValue) {
				((RandomValue) value).setValue(Float.parseFloat(v));
			}
		} else {
			if (type.equals("simple")) {
				((SimpleValue) value).setValue(Float.parseFloat(v));
			} else if (type.equals("random")) {
				((RandomValue) value).setValue(Float.parseFloat(v));
			} else if (type.equals("linear")) {
				String min = element.getAttribute("min", null);
				String max = element.getAttribute("max", null);
				String active = element.getAttribute("active", null);

				TArray<XMLElement> points = element.list("point");

				TArray<Vector2f> curve = new TArray<Vector2f>(points.size);
				for (int i = 0; i < points.size; i++) {
					XMLElement point = (XMLElement) points.get(i);
					float x = point.getFloatAttribute("x", 0);
					float y = point.getFloatAttribute("y", 0);
					curve.add(new Vector2f(x, y));
				}

				((LinearInterpolator) value).setCurve(curve);
				((LinearInterpolator) value).setMin(Integer.parseInt(min));
				((LinearInterpolator) value).setMax(Integer.parseInt(max));
				((LinearInterpolator) value).setActive(StringUtils.toBoolean(active));
			}
		}
	}
}
