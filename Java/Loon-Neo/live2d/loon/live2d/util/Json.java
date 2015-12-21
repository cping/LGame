package loon.live2d.util;


import java.util.Iterator;

import loon.utils.ListMap;
import loon.utils.TArray;

public class Json
{
    byte[] a;
    int b;
    int c;
    Value d;
    
    public Json(final byte[] jsonBytes) {
        this.c = 0;
        if(jsonBytes==null){
        	a=new byte[0];
        }
        this.a = jsonBytes;
        this.b = a.length;
        
    }
    
    public Value parse() {
        try {
            return this.d = this.d(this.a, this.b, 0, new int[1]);
        }
        catch (Throwable t) {
            throw new RuntimeException("JSON error @line:" + this.c + " / " + t.getMessage(), t);
        }
    }
    
    public static Value parseFromBytes(final byte[] jsonBytes) {
        return new Json(jsonBytes).parse();
    }
    
    public static Value parseFromString(final String jsonString) {
        return new Json(jsonString.getBytes()).parse();
    }
    
    private static String a(final byte[] array, final int n, final int n2, final int[] array2) {
        int i = n2;
        StringBuffer sb = null;
        int n3 = n2;
        while (i < n) {
            switch ((char)(array[i] & 0xFF)) {
                case '\"': {
                    array2[0] = i + 1;
                    if (sb != null) {
                        if (i - 1 > n3) {
                            sb.append(new String(array, n3, i - 1 - n3));
                        }
                        return sb.toString();
                    }
                    return new String(array, n2, i - n2);
                }
                case '\\': {
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    if (i > n3) {
                        sb.append(new String(array, n3, i - n3));
                    }
                    if (++i < n) {
                        switch ((char)(array[i] & 0xFF)) {
                            case '\\': {
                                sb.append('\\');
                                break;
                            }
                            case '\"': {
                                sb.append('\"');
                                break;
                            }
                            case '/': {
                                sb.append('/');
                                break;
                            }
                            case 'b': {
                                sb.append('\b');
                                break;
                            }
                            case 'f': {
                                sb.append('\f');
                                break;
                            }
                            case 'n': {
                                sb.append('\n');
                                break;
                            }
                            case 'r': {
                                sb.append('\r');
                                break;
                            }
                            case 't': {
                                sb.append('\t');
                                break;
                            }
                            case 'u': {
                                throw new RuntimeException("parse string/unicode escape not supported");
                            }
                        }
                        n3 = i + 1;
                        break;
                    }
                    throw new RuntimeException("parse string/escape error");
                }
            }
            ++i;
        }
        throw new RuntimeException("parse string/illegal end");
    }
    
    private Value b(final byte[] array, final int n, final int n2, final int[] array2) {
        final ListMap<String, Value> hashMap = new ListMap<String, Value>();
        String a = null;
        int i = n2;
        final int[] array3 = { 0 };
        int n3 = 0;
        while (i < n) {
        Label_0134:
            while (i < n) {
                switch ((char)(array[i] & 0xFF)) {
                    case '\"': {
                        a = a(array, n, i + 1, array3);
                        i = array3[0];
                        n3 = 1;
                        break Label_0134;
                    }
                    case '}': {
                        array2[0] = i + 1;
                        return new Value(hashMap);
                    }
                    case ':': {
                        throw new RuntimeException("illegal ':' position");
                    }
                    default: {
                        ++i;
                        continue;
                    }
                }
            }
            if (n3 == 0) {
                throw new RuntimeException("key not found");
            }
            n3 = 0;
        Label_0242:
            while (i < n) {
                switch ((char)(array[i] & 0xFF)) {
                    case ':': {
                        n3 = 1;
                        ++i;
                        break Label_0242;
                    }
                    case '}': {
                        throw new RuntimeException("illegal '}' position");
                    }
                    case '\n': {
                        ++this.c;
                        break;
                    }
                }
                ++i;
            }
            if (n3 == 0) {
                throw new RuntimeException("':' not found");
            }
            final Value d = this.d(array, n, i, array3);
            i = array3[0];
            hashMap.put(a, d);
        Label_0376:
            while (i < n) {
                switch ((char)(array[i] & 0xFF)) {
                    case ',': {
                        break Label_0376;
                    }
                    case '}': {
                        array2[0] = i + 1;
                        return new Value(hashMap);
                    }
                    case '\n': {
                        ++this.c;
                        break;
                    }
                }
                ++i;
            }
            ++i;
        }
        throw new RuntimeException("illegal end of parseObject");
    }
    
    private Value c(final byte[] array, final int n, final int n2, final int[] array2) {
        final TArray<Value> o = new TArray<Value>();
        int i = n2;
        final int[] array3 = { 0 };
        while (i < n) {
            final Value d = this.d(array, n, i, array3);
            i = array3[0];
            o.add(d);
        Label_0136:
            while (i < n) {
                switch ((char)(array[i] & 0xFF)) {
                    case ',': {
                        break Label_0136;
                    }
                    case ']': {
                        array2[0] = i + 1;
                        return new Value(o);
                    }
                    case '\n': {
                        ++this.c;
                        break;
                    }
                }
                ++i;
            }
            ++i;
        }
        throw new RuntimeException("illegal end of parseObject");
    }
    
    private Value d(final byte[] array, final int n, final int n2, final int[] array2) {
        int i;
        for (i = n2; i < n; ++i) {
            switch ((char)(array[i] & 0xFF)) {
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    return new Value(new Double(loon.live2d.util.StringUtil.indexOf(array, n, i, array2)));
                }
                case '\"': {
                    return new Value(a(array, n, i + 1, array2));
                }
                case '[': {
                    return this.c(array, n, i + 1, array2);
                }
                case '{': {
                    return this.b(array, n, i + 1, array2);
                }
                case 'n': {
                    if (i + 3 < n) {
                        return null;
                    }
                    throw new RuntimeException("parse null / " + this.a(array, n, i));
                }
                case 't': {
                    if (i + 3 < n) {
                        return new Value(Boolean.TRUE);
                    }
                    throw new RuntimeException("parse true / " + this.a(array, n, i));
                }
                case 'f': {
                    if (i + 4 < n) {
                        return new Value(Boolean.FALSE);
                    }
                    throw new RuntimeException("parse false / " + this.a(array, n, i));
                }
                case ',': {
                    throw new RuntimeException("illegal ',' position / " + this.a(array, n, i));
                }
                case '\n': {
                    ++this.c;
                    break;
                }
            }
        }
        throw new RuntimeException("illegal end of value / " + this.a(array, n, i));
    }
    
    private String a(final byte[] array, final int n, final int n2) {
        int n3 = 0;
        int n4 = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == 10) {
                ++n3;
                n4 = i + 1;
            }
            if (i == n2) {
                break;
            }
        }
        int n5 = n4;
        for (int j = n4; j < array.length; ++j) {
            if (array[j] == 10) {
                n5 = j;
                break;
            }
        }
        return "line : " + n3 + " (" + new String(array, n4, n5 - n4) + ")";
    }
    
    public static final class Value
    {
        Object a;
        
        public Value(final Object o) {
            this.a = o;
        }
        
        @Override
        public String toString() {
            return this.toString("");
        }
        
        public String toString(final String indent) {
            if (this.a instanceof String) {
                return (String)this.a;
            }
            if (this.a instanceof TArray) {
                String s = String.valueOf(indent) + "[\n";
                final Iterator<Value> iterator = ((TArray)this.a).iterator();
                while (iterator.hasNext()) {
                    s = String.valueOf(s) + indent + "\t" + iterator.next().toString(String.valueOf(indent) + "\t") + "\n";
                }
                return String.valueOf(s) + indent + "]\n";
            }
            if (this.a instanceof ListMap) {
                String s2 = String.valueOf(indent) + "{\n";
                final ListMap map = (ListMap)this.a;
                for (final Object s3 : map.keys) {
                    s2 = String.valueOf(s2) + indent + "\t" + s3 + " : " + ((Value)map.get(s3)).toString(String.valueOf(indent) + "\t") + "\n";
                }
                return String.valueOf(s2) + indent + "}\n";
            }
            return new StringBuilder().append(this.a).toString();
        }
        
        public int toInt() {
            return this.toInt(0);
        }
        
        public int toInt(final int defaultV) {
            if (this.a instanceof Double) {
                return ((Double)this.a).intValue();
            }
            return defaultV;
        }
        
        public float toFloat() {
            return this.toFloat(0.0f);
        }
        
        public float toFloat(final float defaultV) {
            if (this.a instanceof Double) {
                return ((Double)this.a).floatValue();
            }
            return defaultV;
        }
        
        public double toDouble() {
            return this.toDouble(0.0);
        }
        
        public double toDouble(final double defaultV) {
            if (this.a instanceof Double) {
                return (double)this.a;
            }
            return defaultV;
        }
        
        public TArray getVector(final TArray defalutV) {
            if (this.a instanceof TArray) {
                return (TArray)this.a;
            }
            return defalutV;
        }
        
        public Value get(final int index) {
            if (this.a instanceof TArray) {
                return (Value) ((TArray)this.a).get(index);
            }
            return null;
        }
        
        public ListMap<String, Value> getMap(final ListMap<String, Value> defalutV) {
            if (this.a instanceof ListMap) {
                return (ListMap<String, Value>)this.a;
            }
            return defalutV;
        }
        
        public Value get(final String key) {
            if (this.a instanceof ListMap) {
                return (Value) ((ListMap)this.a).get(key);
            }
            return null;
        }
        
        public Object[] keySet() {
            if (this.a instanceof ListMap) {
                return ((ListMap)this.a).keys;
            }
            return null;
        }
        
        public boolean isNull() {
            return this.a == null;
        }
        
        public boolean isboolean() {
            return this.a instanceof Boolean;
        }
        
        public boolean isDouble() {
            return this.a instanceof Double;
        }
        
        public boolean isString() {
            return this.a instanceof String;
        }
        
        public boolean isArray() {
            return this.a instanceof TArray;
        }
        
        public boolean isMap() {
            return this.a instanceof ListMap;
        }
    }
}
