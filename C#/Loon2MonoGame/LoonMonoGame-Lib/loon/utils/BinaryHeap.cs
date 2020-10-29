namespace loon.utils
{
    public class BinaryHeap
{
		public interface ScoreFunction
		{

			float GetScore(HeapNode node);

		}

		private readonly ScoreFunction scoreFunction;

	private readonly TArray<HeapNode> content;

		public BinaryHeap(ScoreFunction f)
		{
			this.scoreFunction = f;
			this.content = new TArray<HeapNode>();
		}

		public void Push(HeapNode element)
		{
			this.content.Add(element);
			this.SinkDown(this.content.size - 1);
		}

		public void SinkDown(int n)
		{
			HeapNode element = this.content.Get(n);
			for (; n > 0;)
			{
				int parentN = ((n + 1) >> 1) - 1;
				HeapNode parent = this.content.Get(parentN);
				if (scoreFunction.GetScore(element) < scoreFunction.GetScore(parent))
				{
					this.content.Set(parentN, element);
					this.content.Set(n, parent);
					n = parentN;
				}
				else
				{
					break;
				}
			}
		}

		public void BubbleUp(int n)
		{

			int length = this.content.size;
			HeapNode element = this.content.Get(n);
			float elemScore = this.scoreFunction.GetScore(element);

			for (; ; )
			{
				int child2N = (n + 1) << 1;
				int child1N = child2N - 1;

				int swap = -1;
				float child1Score = 0f;

				if (child1N < length)
				{

					HeapNode child1 = this.content.Get(child1N);
					child1Score = this.scoreFunction.GetScore(child1);

					if (child1Score < elemScore)
					{
						swap = child1N;
					}
				}

				if (child2N < length)
				{
					HeapNode child2 = this.content.Get(child2N);
					float child2Score = this.scoreFunction.GetScore(child2);
					if (child2Score < (swap == -1 ? elemScore : child1Score))
					{
						swap = child2N;
					}
				}

				if (swap != -1)
				{
					this.content.Set(n, this.content.Get(swap));
					this.content.Set(swap, element);
					n = swap;
				}
				else
				{
					break;
				}

			}
		}

		public void RescoreElement(HeapNode node)
		{
			this.SinkDown(this.content.IndexOf(node));
		}

		public int Size()
		{
			return this.content.size;
		}

		public bool Remove(HeapNode node)
		{
			int i = this.content.IndexOf(node);
			HeapNode end = this.content.Pop();
			if (i != this.content.size - 1)
			{
				this.content.Set(i, end);
				if (this.scoreFunction.GetScore(end) < this.scoreFunction.GetScore(node))
				{
					this.SinkDown(i);
				}
				else
				{
					this.BubbleUp(i);
				}
			}
			return i != -1;
		}

		public HeapNode Pop()
		{
			HeapNode result = this.content.Get(0);
			HeapNode end = this.content.Pop();
			if (this.content.size > 0)
			{
				this.content.Set(0, end);
				this.BubbleUp(0);
			}
			return result;
		}
	}
}
