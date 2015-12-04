package loon.live2d.id;

import java.util.*;

import loon.utils.ListMap;

public class BaseDataID extends ID
{
    static BaseDataID a;
    static ListMap b;
    
    static {
        BaseDataID.a = null;
        BaseDataID.b = new ListMap();
    }
    
    private BaseDataID() {
    }
    
    private BaseDataID(final String idstr) {
        this.c = idstr;
    }
    
    public static BaseDataID DST_BASE_ID() {
        if (BaseDataID.a == null) {
            BaseDataID.a = getID("DST_BASE");
        }
        return BaseDataID.a;
    }
    
    public BaseDataID createIDForSerialize() {
        return new BaseDataID();
    }
    
    static void clear() {
        BaseDataID.b.clear();
        BaseDataID.a = null;
    }
    
    public static BaseDataID getID(final String tmp_idstr) {
        BaseDataID baseDataID = (BaseDataID) BaseDataID.b.get(tmp_idstr);
        if (baseDataID == null) {
            baseDataID = new BaseDataID(tmp_idstr);
            BaseDataID.b.put(tmp_idstr, baseDataID);
        }
        return baseDataID;
    }
}
