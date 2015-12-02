package loon.opengl.d3d.shaders;

import loon.opengl.d3d.shaders.ShaderInput.ShaderInputQualifier;
import loon.opengl.d3d.shaders.ShaderNode.ShaderNodeType;
import loon.utils.TArray;

public class ShaderNodeBuilder {
	private String name;
	private ShaderNodeType type;
	private TArray<ShaderInput> inputs = new TArray<ShaderInput>();
	private TArray<ShaderOutput> outputs = new TArray<ShaderOutput>();
	private TArray<String> requires = new TArray<String>();
	private TArray<ShaderDefine> defines = new TArray<ShaderDefine>();
	private String code;

	public ShaderNodeBuilder name(String name) {
		if (name == null)
			throw new RuntimeException("name must not be null");
		if (name.length() == 0)
			throw new RuntimeException("name must not be empty string");
		this.name = name;
		return this;
	}

	public ShaderNodeBuilder type(ShaderNodeType type) {
		if (type == null)
			throw new RuntimeException("type must not be null");
		this.type = type;
		return this;
	}

	public ShaderNodeBuilder define(String define, int value) {
		if (define == null)
			throw new RuntimeException("define must not be null");
		if (define.length() == 0)
			throw new RuntimeException("define must not be zero");
		defines.add(new ShaderDefine(define, value));
		return this;
	}

	public ShaderNodeBuilder require(String require) {
		if (require == null)
			throw new RuntimeException("requires must not be null");
		if (require.length() == 0)
			throw new RuntimeException("requires must not be zero");
		requires.add(require);
		return this;
	}

	public ShaderNodeBuilder input(String name, String type) {
		return input(name, type, ShaderInputQualifier.Local);
	}

	public ShaderNodeBuilder input(String name, String type,
			ShaderInputQualifier qualifier) {
		if (name == null)
			throw new RuntimeException("name must not be null");
		if (name.length() == 0)
			throw new RuntimeException("name must not be empty string");
		if (type == null)
			throw new RuntimeException("type must not be null");
		if (type.length() == 0)
			throw new RuntimeException("type must not be empty string");
		if (qualifier == null)
			throw new RuntimeException("qualifier must not be null");

		for (ShaderInput i : inputs) {
			if (i.getName().equals(name)) {
				throw new RuntimeException("input with name '" + i.getName()
						+ "' already in shader node");
			}
		}
		inputs.add(new ShaderInput(name, type, qualifier));
		return this;
	}

	public ShaderNodeBuilder output(String name, String type) {
		return output(name, type, false);
	}

	public ShaderNodeBuilder output(String name, String type, boolean isVarying) {
		if (name == null)
			throw new RuntimeException("name must not be null");
		if (name.length() == 0)
			throw new RuntimeException("name must not be empty string");
		if (type == null)
			throw new RuntimeException("type must not be null");
		if (type.length() == 0)
			throw new RuntimeException("type must not be empty string");
		for (ShaderOutput o : outputs) {
			if (o.getName().equals(name)) {
				throw new RuntimeException("output with name '" + o.getName()
						+ "' already in shader node");
			}
		}
		outputs.add(new ShaderOutput(name, type, isVarying));
		return this;
	}

	public ShaderNodeBuilder code(String code) {
		if (code == null)
			throw new RuntimeException("code must not be null");
		if (code.length() == 0)
			throw new RuntimeException("code must not be empty string");
		this.code = code;
		return this;
	}

	public ShaderNode build() {
		if (name == null)
			throw new RuntimeException("name is not set");
		if (type == null)
			throw new RuntimeException("type is not set");
		if (code == null)
			throw new RuntimeException("code is not set");
		if (inputs.size == 0 && outputs.size == 0)
			throw new RuntimeException("node has neither inputs nor outputs");
		return new ShaderNode(name, type, new TArray<String>(requires),
				new TArray<ShaderDefine>(defines), new TArray<ShaderInput>(
						inputs), new TArray<ShaderOutput>(outputs), code);
	}

	public void clear() {
		this.name = null;
		this.type = null;
		this.code = null;
		this.inputs.clear();
		this.outputs.clear();
		this.requires.clear();
		this.defines.clear();
	}
}
