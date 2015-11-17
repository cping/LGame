package loon.action.map.tmx.renderers;

import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXTileLayer;
import loon.opengl.GLEx;

/**开发中，暂未找到六边形tmx地图文件……**/
public class TMXHexagonalMapRenderer extends TMXMapRenderer {

	public TMXHexagonalMapRenderer(TMXMap map) {
		super(map);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void renderTileLayer(GLEx gl, TMXTileLayer tileLayer) {
	
	}

	@Override
	protected void renderImageLayer(GLEx gl, TMXImageLayer imageLayer) {
	
	}

}
