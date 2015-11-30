package loon.opengl.d3d.shaders;

import loon.opengl.d3d.shaders.ShaderNode.ShaderNodeType;
import loon.utils.ObjectMap;
import loon.utils.ObjectSet;
import loon.utils.TArray;

public class ShaderGraph {
	
	private final ShaderNodeType type;
	private final ObjectMap<String, ShaderNode> nodeTypes = new ObjectMap<String, ShaderNode>();
	private final ObjectSet<ShaderNode> nodeSet = new ObjectSet<ShaderNode>();
	private final TArray<ShaderNode> nodes = new TArray<ShaderNode>();
	private final TArray<ShaderConnection> connections = new TArray<ShaderConnection>();
	private final ObjectMap<ShaderNode, TArray<ShaderConnection>> connectionMap = new ObjectMap<ShaderNode, TArray<ShaderConnection>>();
	
	public ShaderGraph(ShaderNodeType type) {
		this.type = type;
	}
	
	public ShaderNodeType getType () {
		return type;
	}

	public void addNodeType(ShaderNode node) {
		if(node.getType() != type) throw new RuntimeException("graph type " + type + " != node type " + node.getType());
		if(nodeTypes.get(node.getName()) != null) throw new RuntimeException("Node type with name '" + node.getName() + "' already in graph");
		nodeTypes.put(node.getName(), node);
	}
	
	public ShaderNode newNode(String nodeType) {
		ShaderNode node = nodeTypes.get(nodeType);
		if(node == null) throw new RuntimeException("Node type '" + nodeType + "' not in graph, add it with addNodeType() first!");
		node = node.copy();
		nodes.add(node);
		nodeSet.add(node);
		return node;
	}
	
	public void connect(ShaderNode outputNode, String outputName, ShaderNode inputNode, String inputName) {
		if(!nodeSet.contains(outputNode)) throw new RuntimeException("output node not in graph");
		if(!nodeSet.contains(inputNode)) throw new RuntimeException("input node not in graph");
		ShaderOutput output = outputNode.getOutput(outputName);
		if(output == null) throw new RuntimeException("shader output '" + outputName + "' not in node '" + outputNode.getName() + "'");
		ShaderInput input = inputNode.getInput(inputName);
		if(input == null) throw new RuntimeException("shader input '" + inputName + "' not in node '" + inputNode.getName() + "'");
		if(!output.getType().equals(input.getType())) throw new RuntimeException("shader output '" + output.getName() + "' has type '" + output.getType() + "'"
																										 + ", does not match shader input '" + input.getName() + "' type '" + input.getType() + "'");
		ShaderConnection connection = new ShaderConnection(outputNode, output, inputNode, input);
		connections.add(connection);
		addConnection(outputNode, connection);
		addConnection(inputNode, connection);
	}
	
	private void addConnection(ShaderNode node, ShaderConnection connection) {
		TArray<ShaderConnection> connections = connectionMap.get(node);
		if(connections == null) {
			connections = new TArray<ShaderConnection>();
			connectionMap.put(node, connections);
		}
		connections.add(connection);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("types {\n");
		for(String type: nodeTypes.keys()) {
			builder.append("   {\n");
			String[] lines = nodeTypes.get(type).toString().split("\n");
			for(String line: lines) {
				builder.append("      " + line + "\n");
			}
			builder.append("   }\n");
		}
		builder.append("\n}\n");
		
		builder.append("nodes {\n");
		int i = 0;
		for(ShaderNode node: nodes) {
			builder.append("   node " + node.getName() + "_" + i + ";\n");
			++i;
		}
		builder.append("\n}\n");
		
		builder.append("connections {\n");
		for(ShaderConnection con: connections) {
			int outputIdx = nodes.indexOf(con.getOutputNode(), true);
			int inputIdx = nodes.indexOf(con.getInputNode(), true);
			builder.append("   " + con.getOutputNode().getName() + "_" + outputIdx + con.getOutput().getName() + " -> ");
			builder.append(con.getInputNode().getName() + "_" + inputIdx + con.getInput().getName() + ";\n");
			++i;
		}
		builder.append("\n}\n");
		
		return builder.toString();
	}
}
