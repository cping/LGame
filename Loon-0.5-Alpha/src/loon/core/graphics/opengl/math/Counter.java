package loon.core.graphics.opengl.math;

public class Counter {

	public int count;
	
	public float total;
	
	public float min;
	
	public float max;

	public float average;

	public float latest;

	public float value;

	public final Mean mean;

	public Counter(int windowSize) {
		mean = (windowSize > 1) ? new Mean(windowSize) : null;
		reset();
	}

	public void put(float value) {
		latest = value;
		total += value;
		count++;
		average = total / count;
		if (mean != null) {
			mean.addValue(value);
			this.value = mean.getMean();
		} else {
			this.value = latest;
		}
		if (mean == null || mean.hasEnoughData()) {
			if (this.value < min){
				min = this.value;
			}
			if (this.value > max){
				max = this.value;
			}
		}
	}

	public void reset() {
		count = 0;
		total = 0f;
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		average = 0f;
		latest = 0f;
		value = 0f;
		if (mean != null){
			mean.clear();
		}
	}
}
