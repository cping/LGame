package loon.action.map.tmx;

import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.utils.xml.XMLElement;

public class TMXImage {
	public enum Format {
		PNG, GIF, JPG, BMP, OTHER
	}

	// 瓦片色彩格式
	private Format format;

	// 瓦片图像源
	private String source;

	// 过滤色
	private LColor trans;

	private int width;
	private int height;

	public void parse(XMLElement element, String tmxPath) {
		String sourcePath = element.getAttribute("source", "");
		source = sourcePath.trim();
		width = element.getIntAttribute("width", 0);
		height = element.getIntAttribute("height", 0);
		trans = new LColor(LColor.TRANSPARENT);
		if (element.hasAttribute("trans")) {
			String color = element.getAttribute("trans", "").trim();
			if (color.startsWith("#")) {
				color = color.substring(1);
			}
			trans = new LColor(Integer.parseInt(color, 16));
		}
		if (width == 0 || height == 0) {
			LTexture image = LTextures.loadTexture(source);
			if (width == 0) {
				width = image.getWidth();
			}
			if (height == 0) {
				height = image.getWidth();
			}
		}
	}

	public Format getFormat() {
		return format;
	}

	public String getSource() {
		return source;
	}

	public int getWidth() {
		return width;
	}

	public LColor getTrans() {
		return trans;
	}

	public int getHeight() {
		return height;
	}

}
