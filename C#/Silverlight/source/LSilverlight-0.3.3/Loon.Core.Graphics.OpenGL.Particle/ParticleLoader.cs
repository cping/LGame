using Loon.Core.Resource;
using System.IO;
using Loon.Java;
using Loon.Utils.Xml;
using Loon.Utils.Debug;
using System.Collections.Generic;
using System;
using Loon.Core.Geom;
namespace Loon.Core.Graphics.OpenGL.Particle
{
    public class ParticleLoader
    {
        
	public static ParticleSystem LoadConfiguredSystem(string refs, LColor mask)
			{
		return LoadConfiguredSystem(Resources.OpenStream(refs), null,
				null, mask);
	}

	public static ParticleSystem LoadConfiguredSystem(string refs)
			 {
		return LoadConfiguredSystem(Resources.OpenStream(refs), null,
				null, null);
	}

	public static ParticleSystem LoadConfiguredSystem(InputStream refs,
			LColor mask)  {
		return LoadConfiguredSystem(refs, null, null, mask);
	}

	public static ParticleSystem LoadConfiguredSystem(InputStream refs)
			 {
		return LoadConfiguredSystem(refs, null, null, null);
	}

	public static ParticleSystem LoadConfiguredSystem(string refs,
			ConfigEmitterFactory factory)  {
		return LoadConfiguredSystem(Resources.OpenStream(refs),
				factory, null, null);
	}

	public static ParticleSystem LoadConfiguredSystem(InputStream refs,
			ConfigEmitterFactory factory)  {
		return LoadConfiguredSystem(refs, factory, null, null);
	}

        class NewConfigEmitterFactory : ConfigEmitterFactory{
        public ConfigEmitter CreateEmitter(string name) {
					return new ConfigEmitter(name);
				}
        }

	public static ParticleSystem LoadConfiguredSystem(InputStream refs,
			ConfigEmitterFactory factory, ParticleSystem system,
			LColor mask)  {
		if (factory == null) {
			factory = new NewConfigEmitterFactory();
		}
		try {
			XMLDocument document = XMLParser.Parse(refs);

			XMLElement element = document.GetRoot();
			if (!element.GetName().Equals("system",System.StringComparison.InvariantCultureIgnoreCase)) {
				Log.DebugWrite("Not a particle system file");
			}

			if (system == null) {
				system = new ParticleSystem("assets/particle.tga", 2000, mask);
			}
			bool additive = "true".Equals(element.GetAttribute("additive"));
			if (additive) {
				system.SetBlendingMode(ParticleSystem.BLEND_ADDITIVE);
			} else {
				system.SetBlendingMode(ParticleSystem.BLEND_COMBINE);
			}
			bool points = "true".Equals(element.GetAttribute("points"));
			system.SetUsePoints(points);

			List<XMLElement> List = element.List("emitter");
			for (int i = 0; i < List.Count; i++) {
				XMLElement em = (XMLElement) List[i];
				ConfigEmitter emitter = factory.CreateEmitter("new");
	
				ElementToEmitter(em, emitter);

				system.AddEmitter(emitter);
			}

			system.SetRemoveCompletedEmitters(false);
			return system;
		} catch (IOException e) {
			Log.Exception(e);
		} catch (Exception e) {
			Log.Exception(e);
			throw new IOException("Unable to load particle system config");
		}
		return system;
	}

	public static ConfigEmitter LoadEmitter(string refs)
			 {
		return LoadEmitter(Resources.OpenStream(refs), null);
	}

	public static ConfigEmitter LoadEmitter(InputStream refs)
			 {
		return LoadEmitter(refs, null);
	}

	public static ConfigEmitter LoadEmitter(string refs,
			ConfigEmitterFactory factory)  {
		return LoadEmitter(Resources.OpenStream(refs), factory);
	}

	

	public static ConfigEmitter LoadEmitter(InputStream refs,
			ConfigEmitterFactory factory) {
			if (factory == null) {
			factory = new NewConfigEmitterFactory();
		}
		try {

			XMLDocument document = XMLParser.Parse(refs);

			if (!document.GetRoot().GetName().Equals("emitter")) {
				throw new IOException("Not a particle emitter file");
			}

			ConfigEmitter emitter = factory.CreateEmitter("new");
			ElementToEmitter(document.GetRoot(), emitter);

			return emitter;
		} catch (IOException e) {
            Log.Exception(e);
			throw e;
		} catch (Exception e) {
            Log.Exception(e);
			throw new IOException("Unable to load emitter");
		}
	}

	private static XMLElement GetFirstNamedElement(XMLElement element,
			string name) {
		List<XMLElement> List = element.List(name);
		if (List.Count == 0) {
			return null;
		}

		return (XMLElement) List[0];
	}

	private static void ElementToEmitter(XMLElement element,
			ConfigEmitter emitter) {
	
		emitter.SetImageName(element.GetAttribute("img", ""));
		emitter.name = element.GetAttribute("name", "");
		string renderType = element.GetAttribute("renderType", "");
		emitter.usePoints = Particle.INHERIT_POINTS;
		if (renderType.Equals("quads")) {
			emitter.usePoints = Particle.USE_QUADS;
		}
		if (renderType.Equals("points")) {
			emitter.usePoints = Particle.USE_POINTS;
		}

		string useOriented = element.GetAttribute("useOriented", "");
		if (useOriented != null)
			emitter.useOriented = "true".Equals(useOriented);

		string useAdditive = element.GetAttribute("useAdditive", "");
		if (useAdditive != null)
			emitter.useAdditive = "true".Equals(useAdditive);

		ParseRangeElement(GetFirstNamedElement(element, "spawnInterval"),
				emitter.spawnInterval);
		ParseRangeElement(GetFirstNamedElement(element, "spawnCount"),
				emitter.spawnCount);
		ParseRangeElement(GetFirstNamedElement(element, "initialLife"),
				emitter.initialLife);
		ParseRangeElement(GetFirstNamedElement(element, "initialSize"),
				emitter.initialSize);
		ParseRangeElement(GetFirstNamedElement(element, "xOffset"),
				emitter.xOffset);
		ParseRangeElement(GetFirstNamedElement(element, "yOffset"),
				emitter.yOffset);
		ParseRangeElement(GetFirstNamedElement(element, "initialDistance"),
				emitter.initialDistance);
		ParseRangeElement(GetFirstNamedElement(element, "speed"), emitter.speed);
		ParseRangeElement(GetFirstNamedElement(element, "length"),
				emitter.length);
		ParseRangeElement(GetFirstNamedElement(element, "emitCount"),
				emitter.emitCount);

		parseValueElement(GetFirstNamedElement(element, "spread"),
				emitter.spread);
		parseValueElement(GetFirstNamedElement(element, "angularOffset"),
				emitter.angularOffset);
		parseValueElement(GetFirstNamedElement(element, "growthFactor"),
				emitter.growthFactor);
		parseValueElement(GetFirstNamedElement(element, "gravityFactor"),
				emitter.gravityFactor);
		parseValueElement(GetFirstNamedElement(element, "windFactor"),
				emitter.windFactor);
		parseValueElement(GetFirstNamedElement(element, "startAlpha"),
				emitter.startAlpha);
		parseValueElement(GetFirstNamedElement(element, "endAlpha"),
				emitter.endAlpha);
		parseValueElement(GetFirstNamedElement(element, "alpha"), emitter.alpha);
		parseValueElement(GetFirstNamedElement(element, "size"), emitter.size);
		parseValueElement(GetFirstNamedElement(element, "velocity"),
				emitter.velocity);
		parseValueElement(GetFirstNamedElement(element, "scaleY"),
				emitter.scaleY);

		XMLElement color = GetFirstNamedElement(element, "color");
		List<XMLElement> steps = color.List("step");
		emitter.colors.Clear();
		for (int i = 0; i < steps.Count; i++) {
			XMLElement step = (XMLElement) steps[i];
			float offset = step.GetFloatAttribute("offset", 0);
			float r = step.GetFloatAttribute("r", 0);
			float g = step.GetFloatAttribute("g", 0);
			float b = step.GetFloatAttribute("b", 0);

			emitter.AddColorPoint(offset, new LColor(r, g, b, 1));
		}

		emitter.Replay();
	}

	private static void ParseRangeElement(XMLElement element,
			Range range) {
		if (element == null) {
			return;
		}
		range.SetMin(element.GetFloatAttribute("min", 0));
		range.SetMax(element.GetFloatAttribute("max", 0));
		range.SetEnabled("true".Equals(element.GetAttribute("enabled", "")));
	}

	private static void parseValueElement(XMLElement element,
			Value value) {
		if (element == null) {
			return;
		}

		string type = element.GetAttribute("type", "");
		string v = element.GetAttribute("value", "");

		if (type == null || type.Length == 0) {
			if (value is SimpleValue) {
				((SimpleValue) value).SetValue(Convert.ToSingle(v));
			} else if (value is RandomValue) {
				((RandomValue) value).SetValue(Convert.ToSingle(v));
			} else {
				Log.DebugWrite("problems reading element, skipping: "
						+ element);
			}
		} else {
			if (type.Equals("simple")) {
				((SimpleValue) value).SetValue(Convert.ToSingle(v));
			} else if (type.Equals("random")) {
				((RandomValue) value).SetValue(Convert.ToSingle(v));
			} else if (type.Equals("linear")) {
				int min = element.GetIntAttribute("min", 0);
				int max = element.GetIntAttribute("max", 0);

				bool active = element.GetBoolAttribute("active", false);

				List<XMLElement> points = element.List("point");

				List<Vector2f> curve = new List<Vector2f>();
				for (int i = 0; i < points.Count; i++) {
					XMLElement point = (XMLElement) points[i];

					float x = point.GetFloatAttribute("x", 0);
					float y = point.GetFloatAttribute("y", 0);

					curve.Add(new Vector2f(x, y));
				}

				((LinearInterpolator) value).SetCurve(curve);
				((LinearInterpolator) value).SetMin(min);
				((LinearInterpolator) value).SetMax(max);
				((LinearInterpolator) value).SetActive(active);
			} else {
				Log.DebugWrite("unkown type detected: " + type);
			}
		}
	}
    }
}
