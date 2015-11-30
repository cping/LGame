package loon.opengl.d3d.materials;

import loon.LTexture;

public interface TextureProvider {
	
	public LTexture load(String fileName);
	
	public static class FileTextureProvider implements TextureProvider {
		@Override
		public LTexture load (String fileName) {
			return LTexture.createTexture(fileName);
		}
	}
}
