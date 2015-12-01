package loon.opengl.d3d.shaders;

import loon.action.camera.BaseCamera;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Vector3f;
import loon.opengl.GL20;
import loon.opengl.Mesh;
import loon.opengl.ShaderProgram;
import loon.opengl.VertexAttributes;
import loon.opengl.VertexAttributes.Usage;
import loon.opengl.d3d.RenderContext;
import loon.opengl.d3d.Renderable;
import loon.opengl.d3d.Shader;
import loon.opengl.d3d.materials.BlendingAttribute;
import loon.opengl.d3d.materials.ColorAttribute;
import loon.opengl.d3d.materials.FloatAttribute;
import loon.opengl.d3d.materials.IntAttribute;
import loon.opengl.d3d.materials.Material;
import loon.opengl.d3d.materials.TextureAttribute;
import loon.opengl.light.AmbientCubemap;
import loon.opengl.light.DirectionalLight;
import loon.opengl.light.Lights;
import loon.opengl.light.PointLight;
import loon.utils.TArray;

public class DefaultShader extends BaseShader {

	public final static String getDefaultVertexShader() {
		return null;
	}

	public final static String getDefaultFragmentShader() {
		return null;
	}

	protected static long implementedFlags = BlendingAttribute.Type
			| TextureAttribute.Diffuse | ColorAttribute.Diffuse
			| ColorAttribute.Specular | FloatAttribute.Shininess;

	public static boolean ignoreUnimplemented = true;

	public static int defaultCullFace = GL20.GL_BACK;

	public static int defaultDepthFunc = GL20.GL_LEQUAL;

	// Global uniforms
	protected final Input u_projTrans = register(new Input(GLOBAL_UNIFORM,
			"u_projTrans"));
	protected final Input u_cameraPosition = register(new Input(GLOBAL_UNIFORM,
			"u_cameraPosition"));
	protected final Input u_cameraDirection = register(new Input(
			GLOBAL_UNIFORM, "u_cameraDirection"));
	protected final Input u_cameraUp = register(new Input(GLOBAL_UNIFORM,
			"u_cameraUp"));

	protected final Input u_worldTrans = register(new Input(LOCAL_UNIFORM,
			"u_worldTrans"));
	protected final Input u_normalMatrix = register(new Input(LOCAL_UNIFORM,
			"u_normalMatrix", 0, Usage.Normal));
	protected final Input u_bones = register(new Input(LOCAL_UNIFORM, "u_bones"));
	
	protected final Input u_shininess = register(new Input(LOCAL_UNIFORM,
			"u_shininess", FloatAttribute.Shininess));
	protected final Input u_opacity = register(new Input(LOCAL_UNIFORM,
			"u_opacity", BlendingAttribute.Type));
	protected final Input u_diffuseColor = register(new Input(LOCAL_UNIFORM,
			"u_diffuseColor", ColorAttribute.Diffuse));
	protected final Input u_diffuseTexture = register(new Input(LOCAL_UNIFORM,
			"u_diffuseTexture", TextureAttribute.Diffuse));
	protected final Input u_specularColor = register(new Input(LOCAL_UNIFORM,
			"u_specularColor", ColorAttribute.Specular));
	protected final Input u_specularTexture = register(new Input(LOCAL_UNIFORM,
			"u_specularTexture", TextureAttribute.Specular));
	protected final Input u_normalTexture = register(new Input(LOCAL_UNIFORM,
			"u_normalTexture", TextureAttribute.Normal));
	protected final Input u_alphaTest = register(new Input(LOCAL_UNIFORM,
			"u_alphaTest", FloatAttribute.AlphaTest));

	protected final Input u_ambientLight = register(new Input(LOCAL_UNIFORM,
			"u_ambientLight"));
	protected final Input u_ambientCubemap = register(new Input(LOCAL_UNIFORM,
			"u_ambientCubemap"));
	protected final Input u_dirLights0color = register(new Input(LOCAL_UNIFORM,
			"u_dirLights[0].color"));
	protected final Input u_dirLights0direction = register(new Input(
			LOCAL_UNIFORM, "u_dirLights[0].direction"));
	protected final Input u_dirLights1color = register(new Input(LOCAL_UNIFORM,
			"u_dirLights[1].color"));
	protected final Input u_pointLights0color = register(new Input(
			LOCAL_UNIFORM, "u_pointLights[0].color"));
	protected final Input u_pointLights0position = register(new Input(
			LOCAL_UNIFORM, "u_pointLights[0].position"));
	protected final Input u_pointLights0intensity = register(new Input(
			LOCAL_UNIFORM, "u_pointLights[0].intensity"));
	protected final Input u_pointLights1color = register(new Input(
			LOCAL_UNIFORM, "u_pointLights[1].color"));
	protected final Input u_fogColor = register(new Input(LOCAL_UNIFORM,
			"u_fogColor"));

	protected int dirLightsLoc;
	protected int dirLightsColorOffset;
	protected int dirLightsDirectionOffset;
	protected int dirLightsSize;
	protected int pointLightsLoc;
	protected int pointLightsColorOffset;
	protected int pointLightsPositionOffset;
	protected int pointLightsIntensityOffset;
	protected int pointLightsSize;

	protected boolean lighting;
	protected boolean fog;
	protected final AmbientCubemap ambientCubemap = new AmbientCubemap();
	protected final DirectionalLight[] directionalLights;
	protected final PointLight[] pointLights;

	protected final float[] bones;

	protected long materialMask;
	protected long vertexMask;

	public DefaultShader(final Material material,
			final VertexAttributes attributes, boolean lighting, boolean fog,
			int numDirectional, int numPoint, int numSpot, int numBones) {
		this(getDefaultVertexShader(), getDefaultFragmentShader(), material,
				attributes, lighting, fog, numDirectional, numPoint, numSpot,
				numBones);
	}

	public DefaultShader(final long materialMask, final long vertexMask,
			boolean lighting, boolean fog, int numDirectional, int numPoint,
			int numSpot, int numBones) {
		this(getDefaultVertexShader(), getDefaultFragmentShader(),
				materialMask, vertexMask, lighting, fog, numDirectional,
				numPoint, numSpot, numBones);
	}

	public DefaultShader(final String vertexShader,
			final String fragmentShader, final Material material,
			final VertexAttributes attributes, boolean lighting, boolean fog,
			int numDirectional, int numPoint, int numSpot, int numBones) {
		this(vertexShader, fragmentShader, material.getMask(),
				getAttributesMask(attributes), lighting, fog, numDirectional,
				numPoint, numSpot, numBones);
	}

	public DefaultShader(final String vertexShader,
			final String fragmentShader, final long materialMask,
			final long vertexMask, boolean lighting, boolean fog,
			int numDirectional, int numPoint, int numSpot, int numBones) {
		this(createPrefix(materialMask, vertexMask, lighting, fog,
				numDirectional, numPoint, numSpot, numBones), vertexShader,
				fragmentShader, materialMask, vertexMask, lighting, fog,
				numDirectional, numPoint, numSpot, numBones);
	}

	public DefaultShader(final String prefix, final String vertexShader,
			final String fragmentShader, final long materialMask,
			final long vertexMask, boolean lighting, boolean fog,
			int numDirectional, int numPoint, int numSpot, int numBones) {
		this(new ShaderProgram(prefix + vertexShader, prefix + fragmentShader),
				materialMask, vertexMask, lighting, fog, numDirectional,
				numPoint, numSpot, numBones);
	}

	public DefaultShader(final ShaderProgram shaderProgram,
			final long materialMask, final long vertexMask, boolean lighting,
			boolean fog, int numDirectional, int numPoint, int numSpot,
			int numBones) {
		this.program = shaderProgram;
		this.lighting = lighting;
		this.fog = fog;
		this.materialMask = materialMask;
		this.vertexMask = vertexMask;

		this.directionalLights = new DirectionalLight[lighting
				&& numDirectional > 0 ? numDirectional : 0];
		for (int i = 0; i < directionalLights.length; i++)
			directionalLights[i] = new DirectionalLight();
		this.pointLights = new PointLight[lighting && numPoint > 0 ? numPoint
				: 0];
		for (int i = 0; i < pointLights.length; i++)
			pointLights[i] = new PointLight();
		bones = new float[numBones > 0 ? numBones * 16 : 0];

		if (!ignoreUnimplemented
				&& (implementedFlags & materialMask) != materialMask)
			throw new RuntimeException("Some attributes not implemented yet ("
					+ materialMask + ")");
	}

	@Override
	public void init() {
		final ShaderProgram program = this.program;
		this.program = null;
		init(program, materialMask, vertexMask, 0);

		dirLightsLoc = u_dirLights0color.location;
		dirLightsColorOffset = u_dirLights0color.location - dirLightsLoc;
		dirLightsDirectionOffset = u_dirLights0direction.location
				- dirLightsLoc;
		dirLightsSize = u_dirLights1color.location - dirLightsLoc;

		pointLightsLoc = u_pointLights0color.location;
		pointLightsColorOffset = u_pointLights0color.location - pointLightsLoc;
		pointLightsPositionOffset = u_pointLights0position.location
				- pointLightsLoc;
		pointLightsIntensityOffset = u_pointLights0intensity.location
				- pointLightsLoc;
		pointLightsSize = u_pointLights1color.location - pointLightsLoc;
	}

	protected final static long tangentAttribute = Usage.Generic << 1;
	protected final static long binormalAttribute = Usage.Generic << 2;
	protected final static long blendAttributes[] = { Usage.Generic << 3,
			Usage.Generic << 4, Usage.Generic << 5, Usage.Generic << 6,
			Usage.Generic << 7, Usage.Generic << 8, Usage.Generic << 9,
			Usage.Generic << 10 };

	protected static long getAttributesMask(final VertexAttributes attributes) {
		long result = 0;
		int currentBone = 0; 
		final int n = attributes.size();
		for (int i = 0; i < n; i++) {
			long a = (long) attributes.get(i).usage;
			if (a == Usage.BoneWeight)
				a = blendAttributes[attributes.get(i).unit];
			else if (a == Usage.Tangent)
				a = tangentAttribute;
			else if (a == Usage.BiNormal)
				a = binormalAttribute;
			result |= a;
		}
		return result;
	}

	private static String createPrefix(final long mask, final long attributes,
			boolean lighting, boolean fog, int numDirectional, int numPoint,
			int numSpot, int numBones) {
		String prefix = "";
		if (((attributes & Usage.Color) == Usage.Color)
				|| ((attributes & Usage.ColorPacked) == Usage.ColorPacked))
			prefix += "#define colorFlag\n";
		if ((attributes & Usage.Normal) == Usage.Normal) {
			prefix += "#define normalFlag\n";
			if (lighting) {
				prefix += "#define lightingFlag\n";
				prefix += "#define ambientCubemapFlag\n";
				prefix += "#define numDirectionalLights " + numDirectional
						+ "\n";
				prefix += "#define numPointLights " + numPoint + "\n";

				if (fog) {
					prefix += "#define fogFlag\n";
				}
			}
		}
		for (int i = 0; i < blendAttributes.length; i++) {
			if ((attributes & blendAttributes[i]) == blendAttributes[i])
				prefix += "#define boneWeight" + i + "Flag\n";
		}
		if ((attributes & tangentAttribute) == tangentAttribute)
			prefix += "#define tangentFlag\n";
		if ((attributes & binormalAttribute) == binormalAttribute)
			prefix += "#define binormalFlag\n";
		if ((mask & BlendingAttribute.Type) == BlendingAttribute.Type)
			prefix += "#define " + BlendingAttribute.Alias + "Flag\n";
		if ((mask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse)
			prefix += "#define " + TextureAttribute.DiffuseAlias + "Flag\n";
		if ((mask & TextureAttribute.Normal) == TextureAttribute.Normal)
			prefix += "#define " + TextureAttribute.NormalAlias + "Flag\n";
		if ((mask & ColorAttribute.Diffuse) == ColorAttribute.Diffuse)
			prefix += "#define " + ColorAttribute.DiffuseAlias + "Flag\n";
		if ((mask & ColorAttribute.Specular) == ColorAttribute.Specular)
			prefix += "#define " + ColorAttribute.SpecularAlias + "Flag\n";
		if ((mask & FloatAttribute.Shininess) == FloatAttribute.Shininess)
			prefix += "#define " + FloatAttribute.ShininessAlias + "Flag\n";
		if ((mask & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
			prefix += "#define " + FloatAttribute.AlphaTestAlias + "Flag\n";
		if (numBones > 0)
			prefix += "#define numBones " + numBones + "\n";
		return prefix;
	}

	@Override
	public boolean canRender(final Renderable renderable) {
		return materialMask == renderable.material.getMask()
				&& vertexMask == getAttributesMask(renderable.mesh
						.getVertexAttributes())
				&& (renderable.lights != null) == lighting
				&& ((renderable.lights != null && renderable.lights.fog != null) == fog);
	}

	private final boolean can(final long flag) {
		return (materialMask & flag) == flag;
	}

	@Override
	public int compareTo(Shader other) {
		if (other == null)
			return -1;
		if (other == this)
			return 0;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof DefaultShader) ? equals((DefaultShader) obj)
				: false;
	}

	public boolean equals(DefaultShader obj) {
		return (obj == this);
	}

	private Mesh currentMesh;
	private Matrix3 normalMatrix = new Matrix3();
	private BaseCamera camera;
	private final static Matrix4 idtMatrix = new Matrix4();

	@Override
	public void begin(final BaseCamera camera, final RenderContext context) {
		super.begin(camera, context);

		if (defaultDepthFunc == 0)
			context.setDepthTest(false, GL20.GL_LEQUAL);
		else
			context.setDepthTest(true, defaultDepthFunc);
/*
		float fogDist = 1.09f / camera.far;
		fogDist *= fogDist;

		set(u_projTrans, camera.combined);
		set(u_cameraPosition, camera.position.x, camera.position.y,
				camera.position.z, fogDist);
		set(u_cameraDirection, camera.direction);
		set(u_cameraUp, camera.up);*/

		for (final DirectionalLight dirLight : directionalLights)
			dirLight.set(0, 0, 0, 0, -1, 0);
		for (final PointLight pointLight : pointLights)
			pointLight.set(0, 0, 0, 0, 0, 0, 0);
		for (int i = 0; i < bones.length; i++)
			bones[i] = idtMatrix.val[i % 16];
	}

	private void setWorldTransform(final Matrix4 value) {
		set(u_worldTrans, value);
		set(u_normalMatrix, normalMatrix.set(value)); 
	}

	@Override
	public void render(final Renderable renderable) {
		if (!renderable.material.has(BlendingAttribute.Type))
			context.setBlending(false, GL20.GL_SRC_ALPHA,
					GL20.GL_ONE_MINUS_SRC_ALPHA);
		setWorldTransform(renderable.worldTransform);
		bindMaterial(renderable);
		if (lighting)
			bindLights(renderable);
		if (currentMesh != renderable.mesh) {
			if (currentMesh != null)
				currentMesh.unbind(program);
			renderable.mesh.setAutoBind(false);
			(currentMesh = renderable.mesh).bind(program);
		}
		if (has(u_bones)) {
			for (int i = 0; i < bones.length; i++) {
				final int idx = i / 16;
				bones[i] = (renderable.bones == null
						|| idx >= renderable.bones.length || renderable.bones[idx] == null) ? idtMatrix.val[i % 16]
						: renderable.bones[idx].val[i % 16];
			}
			program.setUniformMatrix4fv(u_bones.location, bones, 0,
					bones.length);
		}
		super.render(renderable);
	}

	@Override
	public void end() {
		if (currentMesh != null) {
			currentMesh.unbind(program);
			currentMesh = null;
		}
		currentTextureAttribute = null;
		currentMaterial = null;
		super.end();
	}

	Material currentMaterial;

	private final void bindMaterial(final Renderable renderable) {
		if (currentMaterial == renderable.material)
			return;
		int cullFace = defaultCullFace;
		currentMaterial = renderable.material;
		for (final Material.Attribute attr : currentMaterial) {
			final long t = attr.type;
			if (BlendingAttribute.is(t)) {
				context.setBlending(true,
						((BlendingAttribute) attr).sourceFunction,
						((BlendingAttribute) attr).destFunction);
				set(u_opacity, ((BlendingAttribute) attr).opacity);
			} else if (ColorAttribute.is(t)) {
				ColorAttribute col = (ColorAttribute) attr;
				if ((t & ColorAttribute.Diffuse) == ColorAttribute.Diffuse)
					set(u_diffuseColor, col.color);
				else if ((t & ColorAttribute.Specular) == ColorAttribute.Specular)
					set(u_specularColor, col.color);
			} else if (TextureAttribute.is(t)) {
				final TextureAttribute tex = (TextureAttribute) attr;
				if ((t & TextureAttribute.Diffuse) == TextureAttribute.Diffuse
						&& has(u_diffuseTexture))
					bindTextureAttribute(u_diffuseTexture.location, tex);
				if ((t & TextureAttribute.Normal) == TextureAttribute.Normal
						&& has(u_normalTexture))
					bindTextureAttribute(u_normalTexture.location, tex);
				// TODO else if (..)
			} else if ((t & FloatAttribute.Shininess) == FloatAttribute.Shininess)
				set(u_shininess, ((FloatAttribute) attr).value);
			else if ((t & IntAttribute.CullFace) == IntAttribute.CullFace)
				cullFace = ((IntAttribute) attr).value;
			else if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
				set(u_alphaTest, ((FloatAttribute) attr).value);
			else if (!ignoreUnimplemented)
				throw new RuntimeException("Unknown material attribute: "
						+ attr.toString());
		}
		context.setCullFace(cullFace);
	}

	TextureAttribute currentTextureAttribute;

	private final void bindTextureAttribute(final int uniform,
			final TextureAttribute attribute) {
		final int unit = context.textureBinder
				.bind(attribute.textureDescription);
		program.setUniformi(uniform, unit);
		currentTextureAttribute = attribute;
	}

	private final Vector3f tmpV1 = new Vector3f();

	private final void bindLights(final Renderable renderable) {
		final Lights lights = renderable.lights;
		final TArray<DirectionalLight> dirs = lights.directionalLights;
		final TArray<PointLight> points = lights.pointLights;

		if (has(u_ambientCubemap)) {
			renderable.worldTransform.getTranslation(tmpV1);
			ambientCubemap.set(lights.ambientLight);

			for (int i = directionalLights.length; i < dirs.size; i++)
				ambientCubemap.add(dirs.get(i).color, dirs.get(i).direction);

			for (int i = pointLights.length; i < points.size; i++)
				ambientCubemap.add(points.get(i).color, points.get(i).position,
						tmpV1, points.get(i).intensity);

			ambientCubemap.clamp();

			program.setUniform3fv(u_ambientCubemap.location,
					ambientCubemap.data, 0, ambientCubemap.data.length);
		}

		if (dirLightsLoc >= 0) {
			for (int i = 0; i < directionalLights.length; i++) {
				if (dirs == null || i >= dirs.size) {
					if (directionalLights[i].color.r == 0f
							&& directionalLights[i].color.g == 0f
							&& directionalLights[i].color.b == 0f)
						continue;
					directionalLights[i].color.setColor(0f, 0f, 0f, 1f);
				} else if (directionalLights[i].equals(dirs.get(i)))
					continue;
				else
					directionalLights[i].set(dirs.get(i));

				int idx = dirLightsLoc + i * dirLightsSize;
				program.setUniformf(idx + dirLightsColorOffset,
						directionalLights[i].color.r,
						directionalLights[i].color.g,
						directionalLights[i].color.b);
				program.setUniformf(idx + dirLightsDirectionOffset,
						directionalLights[i].direction);
			}
		}

		if (pointLightsLoc >= 0) {
			for (int i = 0; i < pointLights.length; i++) {
				if (points == null || i >= points.size) {
					if (pointLights[i].intensity == 0f)
						continue;
					pointLights[i].intensity = 0f;
				} else if (pointLights[i].equals(points.get(i)))
					continue;
				else
					pointLights[i].set(points.get(i));

				int idx = pointLightsLoc + i * pointLightsSize;
				program.setUniformf(idx + pointLightsColorOffset,
						pointLights[i].color.r, pointLights[i].color.g,
						pointLights[i].color.b);
				program.setUniformf(idx + pointLightsPositionOffset,
						pointLights[i].position);
				if (pointLightsIntensityOffset >= 0)
					program.setUniformf(idx + pointLightsIntensityOffset,
							pointLights[i].intensity);
			}
		}

		if (lights.fog != null) {
			program.setUniformf(u_fogColor.location, lights.fog);
		}
	}

	@Override
	public void close() {
		program.close();
	}
}
