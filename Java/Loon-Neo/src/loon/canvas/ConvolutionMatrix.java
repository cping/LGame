package loon.canvas;

public class ConvolutionMatrix {

	public static final int SIZE = 3;

	public float[][] matrix;
	public float factor = 1;
	public float offset = 1;

	public ConvolutionMatrix(int size) {
		matrix = new float[size][size];
	}

	public void setAll(float value) {
		for (int x = 0; x < SIZE; ++x) {
			for (int y = 0; y < SIZE; ++y) {
				matrix[x][y] = value;
			}
		}
	}

	public void applyConfig(float[][] config) {
		for (int x = 0; x < SIZE; ++x) {
			for (int y = 0; y < SIZE; ++y) {
				matrix[x][y] = config[x][y];
			}
		}
	}

	public static Pixmap computeConvolution3x3(Pixmap src, ConvolutionMatrix matrix) {
		int width = src.getWidth();
		int height = src.getHeight();
		Pixmap canvas = Pixmap.createImage(width, height);

		int A, R, G, B;
		int sumR, sumG, sumB;
		int[][] pixels = new int[SIZE][SIZE];

		for (int y = 0; y < height - 2; ++y) {
			for (int x = 0; x < width - 2; ++x) {

				for (int i = 0; i < SIZE; ++i) {
					for (int j = 0; j < SIZE; ++j) {
						pixels[i][j] = src.getPixel(x + i, y + j);
					}
				}

				A = LColor.alpha(pixels[1][1]);

				sumR = sumG = sumB = 0;

				for (int i = 0; i < SIZE; ++i) {
					for (int j = 0; j < SIZE; ++j) {
						sumR += (LColor.red(pixels[i][j]) * matrix.matrix[i][j]);
						sumG += (LColor.green(pixels[i][j]) * matrix.matrix[i][j]);
						sumB += (LColor.blue(pixels[i][j]) * matrix.matrix[i][j]);
					}
				}

				R = (int) (sumR / matrix.factor + matrix.offset);
				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}

				G = (int) (sumG / matrix.factor + matrix.offset);
				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}

				B = (int) (sumB / matrix.factor + matrix.offset);
				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}

				canvas.setRGB(LColor.argb(A, R, G, B), x + 1, y + 1);
			}
		}

		src.close();
		src = null;

		return canvas;
	}

	public static Image computeConvolution3x3(Image src, ConvolutionMatrix matrix) {
		int width = src.getWidth();
		int height = src.getHeight();
		Canvas canvas = Image.createCanvas(width, height);

		int A, R, G, B;
		int sumR, sumG, sumB;
		int[][] pixels = new int[SIZE][SIZE];

		for (int y = 0; y < height - 2; ++y) {
			for (int x = 0; x < width - 2; ++x) {

				for (int i = 0; i < SIZE; ++i) {
					for (int j = 0; j < SIZE; ++j) {
						pixels[i][j] = src.getPixel(x + i, y + j);
					}
				}

				A = LColor.alpha(pixels[1][1]);

				sumR = sumG = sumB = 0;

				for (int i = 0; i < SIZE; ++i) {
					for (int j = 0; j < SIZE; ++j) {
						sumR += (LColor.red(pixels[i][j]) * matrix.matrix[i][j]);
						sumG += (LColor.green(pixels[i][j]) * matrix.matrix[i][j]);
						sumB += (LColor.blue(pixels[i][j]) * matrix.matrix[i][j]);
					}
				}

				R = (int) (sumR / matrix.factor + matrix.offset);
				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}

				G = (int) (sumG / matrix.factor + matrix.offset);
				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}

				B = (int) (sumB / matrix.factor + matrix.offset);
				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}

				canvas.image.setPixel(LColor.argb(A, R, G, B), x + 1, y + 1);
			}
		}

		src.close();
		src = null;

		return canvas.image;
	}
}
