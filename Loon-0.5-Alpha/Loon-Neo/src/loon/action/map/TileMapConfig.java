package loon.action.map;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import loon.BaseIO;
import loon.LSystem;
import loon.utils.CollectionUtils;

public class TileMapConfig {
	
	private int[][] backMap;

	public int[][] getBackMap() {
		return backMap;
	}

	public void setBackMap(int[][] backMap) {
		this.backMap = backMap;
	}

	public static Field2D loadCharsField(String resName, int tileWidth,
			int tileHeight) {
		Field2D field = new Field2D(loadCharsMap(resName), tileWidth,
				tileHeight);
		return field;
	}

	public static int[][] loadCharsMap(String resName) {
		int[][] map = null;
		String result = BaseIO.loadText(resName);
		if (result == null) {
			return map;
		}
		StringTokenizer br = new StringTokenizer(result, LSystem.LS);
		String line = br.nextToken();
		int width = Integer.parseInt(line);
		line = br.nextToken();
		int height = Integer.parseInt(line);
		map = new int[width][height];
		for (int i = 0; i < width; i++) {
			line = br.nextToken();
			for (int j = 0; j < height; j++) {
				map[i][j] = line.charAt(j);
			}
		}
		return map;
	}

	public static List<int[]> loadList(final String fileName) {
		String result = BaseIO.loadText(fileName);
		if (result == null) {
			return null;
		}
		StringTokenizer br = new StringTokenizer(result, "\n");
		List<int[]> records = new ArrayList<int[]>(
				CollectionUtils.INITIAL_CAPACITY);

		for (; br.hasMoreTokens();) {
			result = br.nextToken();
			if (!"".equals(result)) {
				String[] stringArray = result.split(",");
				int size = stringArray.length;
				int[] intArray = new int[size];
				for (int i = 0; i < size; i++) {
					intArray[i] = Integer.parseInt(stringArray[i]);
				}
				records.add(intArray);
			}
		}
		return records;
	}

	public static int[][] reversalXandY(final int[][] array) {
		int col = array[0].length;
		int row = array.length;
		int[][] result = new int[col][row];
		for (int y = 0; y < col; y++) {
			for (int x = 0; x < row; x++) {
				result[y][x] = array[x][y];
			}
		}
		return result;
	}

	public static int[][] loadAthwartArray(final String fileName) {
		List<?> list = loadList(fileName);
		int col = list.size();
		int[][] result = new int[col][];
		for (int i = 0; i < col; i++) {
			result[i] = (int[]) list.get(i);
		}
		return result;
	}

	public static int[][] loadJustArray(final String fileName) {
		List<?> list = loadList(fileName);
		int col = list.size();
		int[][] mapArray = new int[col][];
		for (int i = 0; i < col; i++) {
			mapArray[i] = (int[]) list.get(i);
		}
		int row = ((mapArray[col > 0 ? col - 1 : 0]).length);
		int[][] result = new int[row][col];
		for (int y = 0; y < col; y++) {
			for (int x = 0; x < row; x++) {
				result[x][y] = mapArray[y][x];
			}
		}
		return result;
	}
}
