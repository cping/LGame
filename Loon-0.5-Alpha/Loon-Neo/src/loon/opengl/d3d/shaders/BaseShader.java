package loon.opengl.d3d.shaders;

import java.awt.image.renderable.RenderContext;

import loon.action.camera.BaseCamera;
import loon.canvas.LColor;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.opengl.ShaderProgram;
import loon.opengl.d3d.Renderable;
import loon.opengl.d3d.Shader;
import loon.utils.TArray;

public abstract class BaseShader implements Shader {

	public final static int VERTEX_ATTRIBUTE = 1;

	public final static int GLOBAL_UNIFORM = 2; 
	
	public final static int LOCAL_UNIFORM = 3;
	
	public static class Input {
		public interface Setter {
			void set(BaseShader shader, ShaderProgram program, Input input, BaseCamera camera, RenderContext context, Renderable renderable);
		}
		
		public final int scope;

		public final String name;
	
		public final long materialFlags;
	
		public final long vertexFlags;

		public final long userFlags;
	
		public final Setter setter;
	
		public int location = -1;
		
		public boolean compare(final long materialMask, final long vertexMask, final long userMask) {
			return (((materialMask & this.materialFlags) == this.materialFlags) && 
				((vertexMask & this.vertexFlags) == this.vertexFlags) && 
				((userMask & this.userFlags) == this.userFlags)); 
		}
		
		public Input(final int scope, final String name, final long materialFlags, final long vertexFlags, final long userFlags, final Setter setter) {
			this.scope = scope;
			this.name = name;
			this.materialFlags = materialFlags;
			this.vertexFlags = vertexFlags;
			this.userFlags = userFlags;
			this.setter = setter;
		}
		
		public Input(final int scope, final String name, final long materialFlags, final long vertexFlags, final long userFlags) {
			this(scope, name, materialFlags, vertexFlags, userFlags, null);
		}

		public Input(final int scope, final String name, final long materialFlags, final long vertexFlags, final Setter setter) {
			this(scope, name, materialFlags, vertexFlags, 0, setter);
		}
		
		public Input(final int scope, final String name, final long materialFlags, final long vertexFlags) {
			this(scope, name, materialFlags, vertexFlags, 0);
		}

		public Input(final int scope, final String name, final long materialFlags, final Setter setter) {
			this(scope, name, materialFlags, 0, 0, setter);
		}
		
		public Input(final int scope, final String name, final long materialFlags) {
			this(scope, name, materialFlags, 0, 0);
		}

		public Input(final int scope, final String name, final Setter setter) {
			this(scope, name, 0, 0, 0, setter);
		}
		
		public Input(final int scope, final String name) {
			this(scope, name, 0, 0, 0);
		}
	}
	
	private final TArray<Input> inputs = new TArray<Input>();
	public final TArray<Input> vertexAttributes = new TArray<Input>();
	public final TArray<Input> globalUniforms = new TArray<Input>();
	public final TArray<Input> localUniforms = new TArray<Input>();
	
	public ShaderProgram program;
	public RenderContext context;
	public BaseCamera camera;
	
	public Input register(final Input input) {
		if (program != null)
			throw new RuntimeException("Cannot register input after initialization");
		final Input existing = getInput(input.name);
		if (existing != null) {
			if (existing.scope != input.scope)
				throw new RuntimeException(input.name+": An input with the same name but different scope is already registered.");
			return existing;
		}
		inputs.add(input);
		return input;
	}
	
	public Iterable<Input> getInputs() {
		return inputs;
	}
	
	public Input getInput(final String alias) {
		for (final Input input : inputs)
			if (alias.equals(input.name))
				return input;
		return null;
	}

	public void init(final ShaderProgram program, final long materialMask, final long vertexMask, final long userMask) {
		if (this.program != null)
			throw new RuntimeException("Already initialized");
		if (!program.isCompiled())
			throw new RuntimeException(program.getLog());
		this.program = program;
		for (Input input : inputs) {
			if (input.compare(materialMask, vertexMask, userMask))  {
				if (input.scope == GLOBAL_UNIFORM) {
					input.location = program.fetchUniformLocation(input.name, false);
					if (input.location >= 0 && input.setter != null)
						globalUniforms.add(input);
				} else if (input.scope == LOCAL_UNIFORM) {
					input.location = program.fetchUniformLocation(input.name, false);
					if (input.location >= 0 && input.setter != null)
						localUniforms.add(input);
				} else if (input.scope == VERTEX_ATTRIBUTE) {
					input.location = program.getAttributeLocation(input.name);
					if (input.location >= 0)
						vertexAttributes.add(input);
				} else
					input.location = -1;
			} else
				input.location = -1;
		}
	}
	
	@Override
	public void begin (BaseCamera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		program.begin();
		for (final Input input : globalUniforms){
			input.setter.set(this, program, input, camera, context, null);
		}
	}

	@Override
	public void render (Renderable renderable) {
		for (final Input input : localUniforms){
			input.setter.set(this, program, input, camera, context, renderable);
		}
		renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
	}

	@Override
	public void end () {
		program.end();
	}
	
	@Override
	public void close () {
		program = null;
		inputs.clear();
		vertexAttributes.clear();
		localUniforms.clear();
		globalUniforms.clear();
	}
	
	public final boolean has(final Input input) {
		return input.location >= 0;
	}
	
	public final boolean set(final Input uniform, final Matrix4 value) {
		if (uniform.location < 0)
			return false;
		program.setUniformMatrix(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final Matrix3 value) {
		if (uniform.location < 0)
			return false;
		program.setUniformMatrix(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final Vector3f value) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final Vector2f value) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final LColor value) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final float value) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final float v1, final float v2) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, v1, v2);
		return true;
	}
	
	public final boolean set(final Input uniform, final float v1, final float v2, final float v3) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, v1, v2, v3);
		return true;
	}
	
	public final boolean set(final Input uniform, final float v1, final float v2, final float v3, final float v4) {
		if (uniform.location < 0)
			return false;
		program.setUniformf(uniform.location, v1, v2, v3, v4);
		return true;
	}
	
	public final boolean set(final Input uniform, final int value) {
		if (uniform.location < 0)
			return false;
		program.setUniformi(uniform.location, value);
		return true;
	}
	
	public final boolean set(final Input uniform, final int v1, final int v2) {
		if (uniform.location < 0)
			return false;
		program.setUniformi(uniform.location, v1, v2);
		return true;
	}
	
	public final boolean set(final Input uniform, final int v1, final int v2, final int v3) {
		if (uniform.location < 0)
			return false;
		program.setUniformi(uniform.location, v1, v2, v3);
		return true;
	}
	
	public final boolean set(final Input uniform, final int v1, final int v2, final int v3, final int v4) {
		if (uniform.location < 0)
			return false;
		program.setUniformi(uniform.location, v1, v2, v3, v4);
		return true;
	}
}
