package loon.live2d.id;

import loon.utils.ListMap;

public class PartsDataID extends ID
{
    static ListMap<String,PartsDataID> ids;
    
    static {
        PartsDataID.ids = new ListMap<String,PartsDataID>();
    }
    
    private PartsDataID() {
    }
    
    private PartsDataID(final String str) {
        this.c = str;
    }
    
    public PartsDataID createIDForSerialize() {
        return new PartsDataID();
    }
    
    static void clear() {
        PartsDataID.ids.clear();
    }
    
    public static PartsDataID getID(final String tmp_idstr) {
        PartsDataID partsDataID = PartsDataID.ids.get(tmp_idstr);
        if (partsDataID == null) {
            partsDataID = new PartsDataID(tmp_idstr);
            PartsDataID.ids.put(tmp_idstr, partsDataID);
        }
        return partsDataID;
    }
}
