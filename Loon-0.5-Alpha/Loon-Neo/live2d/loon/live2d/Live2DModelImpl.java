package loon.live2d;

import loon.LTexture;
import loon.live2d.graphics.*;
import loon.utils.ArrayByte;

public class Live2DModelImpl extends ALive2DModel
{
    DrawParamImpl g;
    
    Live2DModelImpl() {
        this.g = new DrawParamImpl();
    }
    
    @Override
    public void draw() {
        this.e.draw(this.g);
    }
    
    @Override
    public void deleteTextures() {

    }
    
    public void setTexture(final int textureNo, final LTexture tex) {
        this.g.loadTexture(textureNo, tex);
    }
    
    @Override
    public void releaseModelTextureNo(final int no) {
        this.g.releaseModelTextureNo(no);
    }
    
    public static Live2DModelImpl loadModel(final String filepath) {
        final Live2DModelImpl ret = new Live2DModelImpl();
        ALive2DModel.loadModel_exe(ret, filepath);
        return ret;
    }
    
    public static Live2DModelImpl loadModel(final ArrayByte bin) {
        final Live2DModelImpl ret = new Live2DModelImpl();
        ALive2DModel.loadModel_exe(ret, bin);
        return ret;
    }
    
    public static Live2DModelImpl loadModel(final byte[] data) {
        final ArrayByte bin = new ArrayByte(data);
        final Live2DModelImpl ret = new Live2DModelImpl();
        ALive2DModel.loadModel_exe(ret, bin);
        return ret;
    }
    
    @Override
    public DrawParam getDrawParam() {
        return this.g;
    }
    
    public void setTextureColor(final int textureNo, final float r, final float g, final float b) {
        this.getDrawParam().setTextureColor(textureNo, r, g, b, 1.0f);
    }
}
