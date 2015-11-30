package loon.opengl.d3d;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.geom.BoundingBox;
import loon.geom.Matrix4;
import loon.opengl.GL20;
import loon.opengl.Mesh;
import loon.opengl.VertexAttributes;
import loon.opengl.d3d.materials.BlendingAttribute;
import loon.opengl.d3d.materials.ColorAttribute;
import loon.opengl.d3d.materials.FloatAttribute;
import loon.opengl.d3d.materials.Material;
import loon.opengl.d3d.materials.TextureAttribute;
import loon.opengl.d3d.materials.TextureDescriptor;
import loon.opengl.d3d.materials.TextureProvider;
import loon.opengl.d3d.models.Animation;
import loon.opengl.d3d.models.MeshPart;
import loon.opengl.d3d.models.ModelAnimation;
import loon.opengl.d3d.models.ModelData;
import loon.opengl.d3d.models.ModelMaterial;
import loon.opengl.d3d.models.ModelMesh;
import loon.opengl.d3d.models.ModelMeshPart;
import loon.opengl.d3d.models.ModelNode;
import loon.opengl.d3d.models.ModelNodeAnimation;
import loon.opengl.d3d.models.ModelNodeKeyframe;
import loon.opengl.d3d.models.ModelNodePart;
import loon.opengl.d3d.models.ModelTexture;
import loon.opengl.d3d.models.Node;
import loon.opengl.d3d.models.NodeAnimation;
import loon.opengl.d3d.models.NodeKeyframe;
import loon.opengl.d3d.models.NodePart;
import loon.utils.ListMap;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class Model implements LRelease {
	
	public final TArray<Material> materials = new TArray<Material>();

	public final TArray<Node> nodes = new TArray<Node>();

	public final TArray<Animation> animations = new TArray<Animation>();

	public final TArray<Mesh> meshes = new TArray<Mesh>();
	
	public final TArray<MeshPart> meshParts = new TArray<MeshPart>();

	protected final TArray<LRelease> disposables = new TArray<LRelease>();
	
	public Model() {}
	
	public Model(ModelData modelData) {
		this(modelData, new TextureProvider.FileTextureProvider());
	}
	
	public Model(ModelData modelData, TextureProvider textureProvider) {
		load(modelData, textureProvider);
	}
	
	private void load(ModelData modelData, TextureProvider textureProvider) {
		loadMeshes(modelData.meshes);
		loadMaterials(modelData.materials, textureProvider);
		loadNodes(modelData.nodes);
		loadAnimations(modelData.animations);
		calculateTransforms();
	}
	
	private void loadAnimations (Iterable<ModelAnimation> modelAnimations) {
		for (final ModelAnimation anim : modelAnimations) {
			Animation animation = new Animation();
			animation.id = anim.id;
			for (ModelNodeAnimation nanim : anim.nodeAnimations) {
				final Node node = getNode(nanim.nodeId);
				if (node == null){
					continue;
				}
				NodeAnimation nodeAnim = new NodeAnimation();
				nodeAnim.node = node;
				for (ModelNodeKeyframe kf : nanim.keyframes) {
					if (kf.keytime > animation.duration){
						animation.duration = kf.keytime;
					}
					NodeKeyframe keyframe = new NodeKeyframe();
					keyframe.keytime = kf.keytime;
					keyframe.rotation.set(kf.rotation == null ? node.rotation : kf.rotation);
					keyframe.scale.set(kf.scale == null ? node.scale : kf.scale);
					keyframe.translation.set(kf.translation == null ? node.translation : kf.translation);					
					nodeAnim.keyframes.add(keyframe);
				}
				if (nodeAnim.keyframes.size > 0)
					animation.nodeAnimations.add(nodeAnim);
			}
			if (animation.nodeAnimations.size > 0)
				animations.add(animation);
		}
	}

	private ObjectMap<NodePart, ListMap<String, Matrix4>> nodePartBones = new ObjectMap<NodePart, ListMap<String, Matrix4>>(); 
	private void loadNodes (Iterable<ModelNode> modelNodes) {
		nodePartBones.clear();
		for(ModelNode node: modelNodes) {
			nodes.add(loadNode(null, node));
		}
		for (ObjectMap.Entry<NodePart,ListMap<String, Matrix4>> e : nodePartBones.entries()) {
			if (e.key.invBoneBindTransforms == null)
				e.key.invBoneBindTransforms = new ListMap<Node, Matrix4>();
			e.key.invBoneBindTransforms.clear();
			for (int i=0;i<e.value.size;i++)
				e.key.invBoneBindTransforms.put(getNode(e.value.keys[i]), new Matrix4(e.value.values[i]).inv());
		}
	}

	private Node loadNode (Node parent, ModelNode modelNode) {
		Node node = new Node();
		node.id = modelNode.id;
		node.parent = parent;
		
		if (modelNode.translation != null)
			node.translation.set(modelNode.translation);
		if (modelNode.rotation != null)
			node.rotation.set(modelNode.rotation);
		if (modelNode.scale != null)
			node.scale.set(modelNode.scale);
	
		if (modelNode.parts != null) {
			for(ModelNodePart modelNodePart: modelNode.parts) {
				MeshPart meshPart = null;
				Material meshMaterial = null;
				
				if(modelNodePart.meshPartId != null) {
					for(MeshPart part: meshParts) {
						if(modelNodePart.meshPartId.equals(part.id)) {
							meshPart = part;
							break;
						}
					}
				}
				
				if(modelNodePart.materialId != null) {
					for(Material material: materials) {
						if(modelNodePart.materialId.equals(material.id)) {
							meshMaterial = material;
							break;
						}
					}
				}
				
				if (meshPart == null || meshMaterial == null)
					throw new RuntimeException("Invalid node: "+node.id);
				
				if(meshPart != null && meshMaterial != null) {
					NodePart nodePart = new NodePart();
					nodePart.meshPart = meshPart;
					nodePart.material = meshMaterial;
					node.parts.add(nodePart);
					if (modelNodePart.bones != null)
						nodePartBones.put(nodePart, modelNodePart.bones);
				}
			}
		}
		
		if(modelNode.children != null) {
			for(ModelNode child: modelNode.children) {
				node.children.add(loadNode(node, child));
			}
		}
		
		return node;
	}

	private void loadMeshes (Iterable<ModelMesh> meshes) {
		for(ModelMesh mesh: meshes) {
			convertMesh(mesh);
		}
	}

	private void convertMesh (ModelMesh modelMesh) {
		int numIndices = 0;
		for(ModelMeshPart part: modelMesh.parts) {
			numIndices += part.indices.length;
		}
		VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
		int numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);
		
		Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
		meshes.add(mesh);
		disposables.add(mesh);
		
		LSystem.base().support().copy(modelMesh.vertices, mesh.getVerticesBuffer(), 0,modelMesh.vertices.length);
		int offset = 0;
		mesh.getIndicesBuffer().clear();
		for(ModelMeshPart part: modelMesh.parts) {
			MeshPart meshPart = new MeshPart();
			meshPart.id = part.id;
			meshPart.primitiveType = part.primitiveType;
			meshPart.indexOffset = offset;
			meshPart.numVertices = part.indices.length;
			meshPart.mesh = mesh;
			mesh.getIndicesBuffer().put(part.indices);
			offset += meshPart.numVertices;
			meshParts.add(meshPart);
		}
		mesh.getIndicesBuffer().position(0);
	}

	private void loadMaterials (Iterable<ModelMaterial> modelMaterials, TextureProvider textureProvider) {
		for(ModelMaterial mtl: modelMaterials) {
			this.materials.add(convertMaterial(mtl, textureProvider));
		}
	}
	
	private Material convertMaterial(ModelMaterial mtl, TextureProvider textureProvider) {
		Material result = new Material();
		result.id = mtl.id;
		if (mtl.ambient != null)
			result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
		if (mtl.diffuse != null)
			result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
		if (mtl.specular != null)
			result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
		if (mtl.emissive != null)
			result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
		if (mtl.shininess > 0f)
			result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
		if (mtl.opacity != 1.f)
			result.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, mtl.opacity));
		
		ObjectMap<String, LTexture> textures = new ObjectMap<String, LTexture>();
		
		if(mtl.textures != null) {
			for(ModelTexture tex: mtl.textures) {
				LTexture texture;
				if(textures.containsKey(tex.fileName)) {
					texture = textures.get(tex.fileName);
				} else {
					texture = textureProvider.load(tex.fileName);
					textures.put(tex.fileName, texture);
					disposables.add(texture);
				}
				
				TextureDescriptor descriptor = new TextureDescriptor(texture);
				descriptor.minFilter = GL20.GL_LINEAR;
				descriptor.magFilter = GL20.GL_LINEAR;
				switch (tex.usage) {
				case ModelTexture.USAGE_DIFFUSE:
					result.set(new TextureAttribute(TextureAttribute.Diffuse, descriptor));
					break;
				case ModelTexture.USAGE_SPECULAR:
					result.set(new TextureAttribute(TextureAttribute.Specular, descriptor));
					break;
				case ModelTexture.USAGE_BUMP:
					result.set(new TextureAttribute(TextureAttribute.Bump, descriptor));
					break;
				case ModelTexture.USAGE_NORMAL:
					result.set(new TextureAttribute(TextureAttribute.Normal, descriptor));
					break;					
				}
			}
		}
		
		return result;
	}
	
	public void manageDisposable(LRelease disposable) {
		if (!disposables.contains(disposable, true))
			disposables.add(disposable);
	}
	
	public Iterable<LRelease> getManagedDisposables() {
		return disposables;
	}

	@Override
	public void close () {
		for(LRelease disposable: disposables) {
			disposable.close();
		}
	}
	
	public void calculateTransforms() {
		final int n = nodes.size;
		for(int i = 0; i < n; i++) {
			nodes.get(i).calculateTransforms(true);
		}
		for(int i = 0; i < n; i++) {
			nodes.get(i).calculateBoneTransforms(true);
		}
	}
	
	public BoundingBox calculateBoundingBox(final BoundingBox out) {
		out.inf();
		return extendBoundingBox(out);
	}
	
	public BoundingBox extendBoundingBox(final BoundingBox out) {
		final int n = nodes.size;
		for(int i = 0; i < n; i++)
			nodes.get(i).extendBoundingBox(out);
		return out;
	}

	public Animation getAnimation(final String id) {
		return getAnimation(id, true);
	}
	
	public Animation getAnimation(final String id, boolean ignoreCase) {
		final int n = animations.size;
		Animation animation;
		if (ignoreCase) {
			for (int i = 0; i < n; i++)
				if ((animation = animations.get(i)).id.equalsIgnoreCase(id))
					return animation;
		} else {
			for (int i = 0; i < n; i++)
				if ((animation = animations.get(i)).id.equals(id))
					return animation;
		}
		return null;
	}
	
	public Material getMaterial(final String id) {
		return getMaterial(id, true);
	}
	
	public Material getMaterial(final String id, boolean ignoreCase) {
		final int n = materials.size;
		Material material;
		if (ignoreCase) {
			for (int i = 0; i < n; i++)
				if ((material = materials.get(i)).id.equalsIgnoreCase(id))
					return material;
		} else {
			for (int i = 0; i < n; i++)
				if ((material = materials.get(i)).id.equals(id))
					return material;
		}
		return null;
	}
	
	public Node getNode(final String id) {
		return getNode(id, true);
	}
	
	public Node getNode(final String id, boolean recursive) {
		return getNode(id, recursive, false);
	}
	
	public Node getNode(final String id, boolean recursive, boolean ignoreCase) {
		return Node.getNode(nodes, id, recursive, ignoreCase);
	}
}
