/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils;

public class BinaryHeap {

	public interface ScoreFunction {

		float getScore(HeapNode node);

	}

	private final ScoreFunction scoreFunction;

	private final TArray<HeapNode> content;

	public BinaryHeap(ScoreFunction f) {
		this.scoreFunction = f;
		this.content = new TArray<HeapNode>();
	}

	public void push(HeapNode element) {
		this.content.add(element);
		this.sinkDown(this.content.size - 1);
	}

	public void sinkDown(int n) {
		HeapNode element = this.content.get(n);
		for (; n > 0;) {
			int parentN = ((n + 1) >> 1) - 1;
			HeapNode parent = this.content.get(parentN);
			if (scoreFunction.getScore(element) < scoreFunction.getScore(parent)) {
				this.content.set(parentN, element);
				this.content.set(n, parent);
				n = parentN;
			} else {
				break;
			}
		}
	}

	public void bubbleUp(int n) {

		int length = this.content.size;
		HeapNode element = this.content.get(n);
		float elemScore = this.scoreFunction.getScore(element);

		for (;;) {
			int child2N = (n + 1) << 1;
			int child1N = child2N - 1;

			int swap = -1;
			float child1Score = 0f;

			if (child1N < length) {

				HeapNode child1 = this.content.get(child1N);
				child1Score = this.scoreFunction.getScore(child1);

				if (child1Score < elemScore) {
					swap = child1N;
				}
			}

			if (child2N < length) {
				HeapNode child2 = this.content.get(child2N);
				float child2Score = this.scoreFunction.getScore(child2);
				if (child2Score < (swap == -1 ? elemScore : child1Score)) {
					swap = child2N;
				}
			}

			if (swap != -1) {
				this.content.set(n, this.content.get(swap));
				this.content.set(swap, element);
				n = swap;
			} else {
				break;
			}

		}
	}

	public void rescoreElement(HeapNode node) {
		this.sinkDown(this.content.indexOf(node));
	}

	public int size() {
		return this.content.size;
	}

	public boolean remove(HeapNode node) {
		int i = this.content.indexOf(node);
		HeapNode end = this.content.pop();
		if (i != this.content.size - 1) {
			this.content.set(i, end);
			if (this.scoreFunction.getScore(end) < this.scoreFunction.getScore(node)) {
				this.sinkDown(i);
			} else {
				this.bubbleUp(i);
			}
		}
		return i != -1;
	}

	public HeapNode pop() {
		HeapNode result = this.content.get(0);
		HeapNode end = this.content.pop();
		if (this.content.size > 0) {
			this.content.set(0, end);
			this.bubbleUp(0);
		}
		return result;
	}
}
