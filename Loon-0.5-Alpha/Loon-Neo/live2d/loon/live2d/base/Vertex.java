package loon.live2d.base;

import loon.live2d.io.*;

public class Vertex implements loon.live2d.io.IOBase
{
    float a;
    float b;
    float c;
    float d;
    float e;
    boolean f;
    boolean g;
    
    public Vertex() {
        this.a = 0.0f;
        this.b = 0.0f;
        this.c = 1.0f;
        this.d = 1.0f;
        this.e = 0.0f;
        this.f = false;
        this.g = false;
    }
    
    void a(final Vertex a) {
        this.a = a.a;
        this.b = a.b;
        this.c = a.c;
        this.d = a.d;
        this.e = a.e;
        this.f = a.f;
        this.g = a.g;
    }
    
    @Override
    public void readV2(final BReader br) {
        this.a = br.readerFloat();
        this.b = br.readerFloat();
        this.c = br.readerFloat();
        this.d = br.readerFloat();
        this.e = br.readerFloat();
        if (br.getVersion() >= 10) {
            this.f = br.readExist();
            this.g = br.readExist();
        }
    }
    
}
