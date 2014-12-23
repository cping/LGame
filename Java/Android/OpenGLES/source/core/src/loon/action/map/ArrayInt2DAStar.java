/**
 * Copyright 2014
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
 * @email javachenpeng@yahoo.com
 * @version 0.4.2
 */
package loon.action.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArrayInt2DAStar {
	
	public static interface TileFactory<T> {

		public T create(int m, int n);

	}

	public static interface Map {
		public int[][] adjacent(int[] position);

		public boolean reachable(int[] position);

		public int roughness(int[] position);

		public int baseRoughness();

		public int distance(int[] start, int[] end);
	}

	protected static class Node implements Comparable<Node> {
		int[] position;
		int f, g, h;
		Node parent;

		public Node(int[] position) {
			this.position = position.clone();
		}

		public boolean equals(int[] position) {
			return this.position[0] == position[0]
					&& this.position[1] == position[1];
		}

		@Override
		public int compareTo(Node another) {
			int result = this.f - another.f;
			if (result == 0) {
				result = this.h - another.h;
				;
			}
			return result;
		}
	}

	protected static class NodeList extends ArrayList<Node> {
		private static final long serialVersionUID = 1L;

		public Node find(int[] position) {
			for (int i = size() - 1; i >= 0; i--) {
				Node node = get(i);
				if (node.equals(position)) {
					return node;
				}
			}
			return null;
		}

		public void insert(Node node) {
			for (int i = size(); i > 0; i--) {
				Node n = get(i - 1);
				if (node.compareTo(n) >= 0) {
					add(i, node);
					return;
				}
			}
			add(0, node);
		}

		public void sort(Node node) {
			for (int i = size() - 1; i >= 0; i--) {
				Node n = get(i);
				if (n == node) {
					for (; i > 0; i--) {
						n = get(i - 1);
						if (node.compareTo(n) >= 0) {
							return;
						} else {
							set(i, n);
							set(i - 1, node);
						}
					}
				}
			}
		}
	}

	public static class Path {
		public List<int[]> positions;
		public int cost;
	}

	public static Path findPath(ArrayInt2DAStar.Map map, int[] start, int[] end) {
		return findPath(map, start, end, 0);
	}

	public static Path findPath(ArrayInt2DAStar.Map map, int[] start,
			int[] end, int endRadius) {
		NodeList openNodes = new NodeList();
		NodeList closedNodes = new NodeList();
		openNodes.add(new Node(start));
		Node found = null;
		for(;;) {
			if (openNodes.isEmpty()) {
				return null;
			}
			Node node = openNodes.remove(0);
			int distance = map.distance(node.position, end);
			if (distance <= endRadius) {
				found = node;
				break;
			}
			closedNodes.add(node);
			int[][] positions = map.adjacent(node.position);
			for (int[] position : positions) {
				if (!map.reachable(position)) {
					continue;
				}
				if (closedNodes.find(position) != null) {
					continue;
				}
				Node openNode = openNodes.find(position);
				if (openNode == null) {
					Node newNode = new Node(position);
					newNode.g = node.g + map.roughness(position);
					newNode.h = map.distance(position, end)
							* map.baseRoughness();
					newNode.f = newNode.g + newNode.h;
					newNode.parent = node;
					openNodes.insert(newNode);
				} else {
					int g = node.g + map.roughness(position);
					if (openNode.g > g) {
						openNode.g = g;
						openNode.f = openNode.g + openNode.h;
						openNode.parent = node;
						openNodes.sort(openNode);
					}
				}
			}
		} 
		if (found == null) {
			return null;
		}
		Path path = new Path();
		path.cost = found.g;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		while (found != null) {
			positions.addFirst(found.position.clone());
			found = found.parent;
		}
		path.positions = positions;
		return path;
	}
}
