package loon.opengl.d3d;

import loon.geom.BoundingBox;
import loon.geom.Matrix4;
import loon.geom.Vector3f;
import loon.opengl.d3d.materials.Material;
import loon.opengl.d3d.models.Animation;
import loon.opengl.d3d.models.MeshPart;
import loon.opengl.d3d.models.Node;
import loon.opengl.d3d.models.NodeAnimation;
import loon.opengl.d3d.models.NodeKeyframe;
import loon.opengl.d3d.models.NodePart;
import loon.utils.ListMap;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.cache.Pool;

public class ModelInstance implements RenderableProvider {

	public final TArray<Material> materials = new TArray<Material>();

	public final TArray<Node> nodes = new TArray<Node>();

	public final TArray<Animation> animations = new TArray<Animation>();

	public final Model model;

	public Matrix4 transform;

	public Object userData;
	
	public ModelInstance(Model model) {
		this(model, (String[])null);
	}
	
	public ModelInstance(Model model, final String nodeId, boolean mergeTransform) {
		this(model, null, nodeId, false, false, mergeTransform);
	}
	
	public ModelInstance(Model model, final Matrix4 transform, final String nodeId, boolean mergeTransform) {
		this(model, transform, nodeId, false, false, mergeTransform);
	}

	public ModelInstance(Model model, final String nodeId, boolean parentTransform, boolean mergeTransform) {
		this(model, null, nodeId, true, parentTransform, mergeTransform);
	}
	
	public ModelInstance(Model model, final Matrix4 transform, final String nodeId, boolean parentTransform, boolean mergeTransform) {
		this(model, transform, nodeId, true, parentTransform, mergeTransform);
	}
	
	public ModelInstance(Model model, final String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
		this(model, null, nodeId, recursive, parentTransform, mergeTransform);
	}
	
	public ModelInstance(Model model, final Matrix4 transform, final String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
		this.model = model;
		this.transform = transform == null ? new Matrix4() : transform; 
		nodePartBones.clear();
		Node copy, node = model.getNode(nodeId, recursive);
		this.nodes.add(copy = copyNode(null, node));
		if (mergeTransform) {
			this.transform.mul(parentTransform ? node.globalTransform : node.localTransform);
			copy.translation.set(0,0,0);
			copy.rotation.idt();
			copy.scale.set(1,1,1);
		} else if (parentTransform && copy.parent != null)
			this.transform.mul(node.parent.globalTransform);
		setBones();
		copyAnimations(model.animations);
		calculateTransforms();
	}
	
	public ModelInstance(Model model, final String... rootNodeIds) {
		this(model, null, rootNodeIds);
	}
	
	public ModelInstance(Model model, final Matrix4 transform, final String... rootNodeIds) {
		this.model = model;
		this.transform = transform == null ? new Matrix4() : transform;
		if (rootNodeIds == null)
			copyNodes(model.nodes);
		else
			copyNodes(model.nodes, rootNodeIds);
		copyAnimations(model.animations);
		calculateTransforms();
	}
	
	public ModelInstance(Model model, final TArray<String> rootNodeIds) {
		this(model, null, rootNodeIds);
	}
	
	public ModelInstance(Model model, final Matrix4 transform, final TArray<String> rootNodeIds) {
		this.model = model;
		this.transform = transform == null ? new Matrix4() : transform;
		copyNodes(model.nodes, rootNodeIds);
		copyAnimations(model.animations);
		calculateTransforms();
	}
	
	public ModelInstance(Model model, Vector3f position) {
		this(model);
		this.transform.setToTranslation(position);
	}
	
	public ModelInstance(Model model, float x, float y, float z) {
		this(model);
		this.transform.setToTranslation(x, y, z);
	}
	
	public ModelInstance(Model model, Matrix4 transform) {
		this(model, transform, (String[])null);
	}
	
	public ModelInstance(ModelInstance copyFrom) {
		this(copyFrom, copyFrom.transform.cpy());
	}
	
	public ModelInstance(ModelInstance copyFrom, final Matrix4 transform) {
		this.model = copyFrom.model;
		this.transform = transform == null ? new Matrix4() : transform;
		copyNodes(copyFrom.nodes);
		copyAnimations(copyFrom.animations);
		calculateTransforms();
	}
	
	public ModelInstance copy() {
		return new ModelInstance(this);
	}

	private ObjectMap<NodePart, ListMap<Node, Matrix4>> nodePartBones = new ObjectMap<NodePart, ListMap<Node, Matrix4>>();
	private void copyNodes (TArray<Node> nodes) {
		nodePartBones.clear();
		for(Node node: nodes) {
			this.nodes.add(copyNode(null, node));
		}
		setBones();
	}
	
	private void copyNodes (TArray<Node> nodes, final String... nodeIds) {
		nodePartBones.clear();
		for(Node node: nodes) {
			for (final String nodeId : nodeIds) {
				if (nodeId.equals(node.id)) {
					this.nodes.add(copyNode(null, node));
					break;
				}
			}
		}
		setBones();
	}
	
	private void copyNodes (TArray<Node> nodes, final TArray<String> nodeIds) {
		nodePartBones.clear();
		for(Node node: nodes) {
			for (final String nodeId : nodeIds) {
				if (nodeId.equals(node.id)) {
					this.nodes.add(copyNode(null, node));
					break;
				}
			}
		}
		setBones();
	}
	
	private void setBones() {
		for (ObjectMap.Entry<NodePart,ListMap<Node, Matrix4>> e : nodePartBones.entries()) {
			if (e.key.invBoneBindTransforms == null)
				e.key.invBoneBindTransforms = new ListMap<Node, Matrix4>(true, e.value.size);
			e.key.invBoneBindTransforms.clear();
			
			for (int i=0;i<e.value.size;i++)
			{
				e.key.invBoneBindTransforms.put(getNode(e.value.keys[i].id), e.value.values[i]);
			}

			e.key.bones = new Matrix4[e.value.size];
			for (int i = 0; i < e.key.bones.length; i++)
				e.key.bones[i] = new Matrix4();
		}
	}
	
	private Node copyNode(Node parent, Node node) {
		Node copy = new Node();
		copy.id = node.id;
		copy.parent = parent;
		copy.translation.set(node.translation);
		copy.rotation.set(node.rotation);
		copy.scale.set(node.scale);
		copy.localTransform.set(node.localTransform);
		copy.globalTransform.set(node.globalTransform);
		for(NodePart nodePart: node.parts) {
			copy.parts.add(copyNodePart(nodePart));
		}
		for(Node child: node.children) {
			copy.children.add(copyNode(copy, child));
		}
		return copy;
	}
	
	private NodePart copyNodePart (NodePart nodePart) {
		NodePart copy = new NodePart();
		copy.meshPart = new MeshPart();
		copy.meshPart.id = nodePart.meshPart.id;
		copy.meshPart.indexOffset = nodePart.meshPart.indexOffset;
		copy.meshPart.numVertices = nodePart.meshPart.numVertices;
		copy.meshPart.primitiveType = nodePart.meshPart.primitiveType;
		copy.meshPart.mesh = nodePart.meshPart.mesh;
		
		if (nodePart.invBoneBindTransforms != null)
			nodePartBones.put(copy, nodePart.invBoneBindTransforms);
		
		final int index = materials.indexOf(nodePart.material, false);
		if (index < 0)
			materials.add(copy.material = nodePart.material.cpy());
		else
			copy.material = materials.get(index);
		
		return copy;
	}
	
	private void copyAnimations (final Iterable<Animation> source) {
		for (final Animation anim : source) {
			Animation animation = new Animation();
			animation.id = anim.id;
			animation.duration = anim.duration;
			for (final NodeAnimation nanim : anim.nodeAnimations) {
				final Node node = getNode(nanim.node.id);
				if (node == null)
					continue;
				NodeAnimation nodeAnim = new NodeAnimation();
				nodeAnim.node = node;
				for (final NodeKeyframe kf : nanim.keyframes) {
					NodeKeyframe keyframe = new NodeKeyframe();
					keyframe.keytime = kf.keytime;
					keyframe.rotation.set(kf.rotation);
					keyframe.scale.set(kf.scale);
					keyframe.translation.set(kf.translation);
					nodeAnim.keyframes.add(keyframe);
				}
				if (nodeAnim.keyframes.size > 0)
					animation.nodeAnimations.add(nodeAnim);
			}
			if (animation.nodeAnimations.size > 0)
				animations.add(animation);
		}
	}
	
	public void getRenderables(TArray<Renderable> renderables, Pool<Renderable> pool) {
		for(Node node: nodes) {
			getRenderables(node, renderables, pool);
		}
	}

	public Renderable getRenderable(final Renderable out) {
		return getRenderable(out, nodes.get(0));
	}
	
	public Renderable getRenderable(final Renderable out, final Node node) {
		return getRenderable(out, node, node.parts.get(0));
	}
	
	public Renderable getRenderable(final Renderable out, final Node node, final NodePart nodePart) {
		nodePart.setRenderable(out);
		if (nodePart.bones == null && transform != null)
			out.worldTransform.set(transform).mul(node.globalTransform);
		else if (transform != null)
			out.worldTransform.set(transform);
		else
			out.worldTransform.idt();
		out.userData = userData;
		return out;
	}
	
	protected void getRenderables(Node node, TArray<Renderable> renderables, Pool<Renderable> pool) {
		if(node.parts.size > 0) {
			for(NodePart nodePart: node.parts) {
				renderables.add(getRenderable(pool.obtain(), node, nodePart));
			}
		}
		
		for(Node child: node.children) {
			getRenderables(child, renderables, pool);
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
