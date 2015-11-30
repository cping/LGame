package loon.opengl.d3d.shaders;

import loon.utils.TArray;

public class UniformNode extends ShaderNode {
	
	public UniformNode(String name, String type) {
		this.name = name;
		this.defines = new TArray<ShaderDefine>();
		this.requires = new TArray<String>();
		this.inputs = new TArray<ShaderInput>();
		this.outputs = new TArray<ShaderOutput>();
	}
	
}
