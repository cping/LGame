package loon.live2d.io;

import loon.LSystem;
import loon.live2d.base.*;
import loon.live2d.model.*;
import loon.live2d.param.*;

public class IOType
{
    public static final int a = 6;
    public static final int b = 7;
    public static final int c = 8;
    public static final int d = 9;
    public static final int e = 10;
    public static final int f = 11;
    public static final int g = 11;
    public static final int h = -2004318072;
    public static final int i = 0;
    public static final int j = 23;
    public static final int k = 33;
    
    private static void out(final int n) {
        LSystem.base().log().debug("FileFormat1 :: not implemented classNo : %d\n", n);
    }
    
    public static Object b(final int n) {
        if (n < 40) {
            out(n);
            return null;
        }
        if (n < 50) {
            out(n);
            return null;
        }
        if (n < 60) {
            out(n);
            return null;
        }
        if (n >= 100) {
            if (n < 150) {
                switch (n) {
                    case 131: {
                        return new ParamDefFloat();
                    }
                    case 133: {
                        return new PartsData();
                    }
                    case 136: {
                        return new ModelImpl();
                    }
                    case 137: {
                        return new ParamDefSet();
                    }
                    case 142: {
                        return new loon.live2d.Live2DObject();
                    }
                }
            }
            out(n);
            return null;
        }
        switch (n) {
            case 65: {
                return new BaseDataImpl();
            }
            case 66: {
                return new loon.live2d.param.ParamIOList();
            }
            case 67: {
                return new loon.live2d.param.ParamIO();
            }
            case 68: {
                return new loon.live2d.base.BaseDataListImpl();
            }
            case 69: {
                return new loon.live2d.base.Vertex();
            }
            case 70: {
                return new loon.live2d.draw.DrawDataImpl();
            }
            default: {
                out(n);
                return null;
            }
        }
    }
}
