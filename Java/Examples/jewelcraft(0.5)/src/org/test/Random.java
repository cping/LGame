package org.test;

import loon.utils.MathUtils;

public class Random {

	private int inext;
	private int inextp;
	private int[] SeedArray;

	public Random() {
		this((int) System.currentTimeMillis());

	}

	public Random(int Seed) {
		this.SeedArray = new int[0x38];
		int num4 = (Seed == -2147483648) ? 0x7fffffff : MathUtils.abs(Seed);
		int num2 = 0x9a4ec86 - num4;
		this.SeedArray[0x37] = num2;
		int num3 = 1;
		for (int i = 1; i < 0x37; i++) {
			int index = (0x15 * i) % 0x37;
			this.SeedArray[index] = num3;
			num3 = num2 - num3;
			if (num3 < 0) {
				num3 += 0x7fffffff;
			}
			num2 = this.SeedArray[index];
		}
		for (int j = 1; j < 5; j++) {
			for (int k = 1; k < 0x38; k++) {
				this.SeedArray[k] -= this.SeedArray[1 + ((k + 30) % 0x37)];
				if (this.SeedArray[k] < 0) {
					this.SeedArray[k] += 0x7fffffff;
				}
			}
		}
		this.inext = 0;
		this.inextp = 0x15;
		Seed = 1;
	}

	private double GetSampleForLargeRange() {
		int num = this.InternalSample();
		if ((this.InternalSample() % 2) == 0) {
			num = -num;
		}
		double num2 = num;
		num2 += 2147483646.0;
		return (num2 / 4294967293d);
	}

	private int InternalSample() {
		int inext = this.inext;
		int inextp = this.inextp;
		if (++inext >= 0x38) {
			inext = 1;
		}
		if (++inextp >= 0x38) {
			inextp = 1;
		}
		int num = this.SeedArray[inext] - this.SeedArray[inextp];
		if (num == 0x7fffffff) {
			num--;
		}
		if (num < 0) {
			num += 0x7fffffff;
		}
		this.SeedArray[inext] = num;
		this.inext = inext;
		this.inextp = inextp;
		return num;
	}

	public int Next() {
		return this.InternalSample();
	}

	public int Next(int maxValue) {
		if (maxValue < 0) {
			throw new RuntimeException();
		}
		return (int) (this.Sample() * maxValue);
	}

	public int Next(int minValue, int maxValue) {
		if (minValue > maxValue) {
			throw new RuntimeException();
		}
		long num = maxValue - minValue;
		if (num <= 0x7fffffffL) {
			return (((int) (this.Sample() * num)) + minValue);
		}
		return (((int) ((long) (this.GetSampleForLargeRange() * num))) + minValue);
	}

	public void NextBytes(byte[] buffer) {
		if (buffer == null) {
			throw new RuntimeException();
		}
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) (this.InternalSample() % 0x100);
		}
	}

	public double NextDouble() {
		return this.Sample();
	}

	protected double Sample() {
		return (this.InternalSample() * 4.6566128752457969E-10);
	}

}
