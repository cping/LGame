package loon.core.graphics.opengl.math;

public final class Mean {
	float values[];
	int added_values = 0;
	int last_value;
	float mean = 0;
	boolean dirty = true;

	public Mean(int window_size) {
		values = new float[window_size];
	}

	public boolean hasEnoughData() {
		return added_values >= values.length;
	}

	public void clear() {
		added_values = 0;
		last_value = 0;
		for (int i = 0; i < values.length; i++){
			values[i] = 0;
		}
		dirty = true;
	}

	public void addValue(float value) {
		if (added_values < values.length){
			added_values++;
		}
		values[last_value++] = value;
		if (last_value > values.length - 1){
			last_value = 0;
		}
		dirty = true;
	}

	public float getMean() {
		if (hasEnoughData()) {
			if (dirty) {
				float mean = 0;
				for (int i = 0; i < values.length; i++){
					mean += values[i];
				}
				this.mean = mean / values.length;
				dirty = false;
			}
			return this.mean;
		} else
			return 0;
	}

	public float getOldest() {
		return last_value == values.length - 1 ? values[0]
				: values[last_value + 1];
	}

	public float getLatest() {
		return values[last_value - 1 == -1 ? values.length - 1 : last_value - 1];
	}

	public float standardDeviation() {
		if (!hasEnoughData()){
			return 0;
		}
		float mean = getMean();
		float sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += (values[i] - mean) * (values[i] - mean);
		}
		return (float) Math.sqrt(sum / values.length);
	}

	public int getWindowSize() {
		return values.length;
	}
}
