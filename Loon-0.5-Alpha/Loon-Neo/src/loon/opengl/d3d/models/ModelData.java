package loon.opengl.d3d.models;

import loon.utils.TArray;

public class ModelData {
	public String id;
	public final short version[] = new short[2];
	public final TArray<ModelMesh> meshes = new TArray<ModelMesh>();
	public final TArray<ModelMaterial> materials = new TArray<ModelMaterial>();
	public final TArray<ModelNode> nodes = new TArray<ModelNode>();
	public final TArray<ModelAnimation> animations = new TArray<ModelAnimation>();
	
	public void addMesh(ModelMesh mesh) {
		for(ModelMesh other: meshes) {
			if(other.id.equals(mesh.id)) {
				throw new RuntimeException("Mesh with id '" + other.id + "' already in model");
			}
		}
		meshes.add(mesh);
	}
}
