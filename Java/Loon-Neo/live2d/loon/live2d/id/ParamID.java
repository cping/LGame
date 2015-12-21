package loon.live2d.id;

import loon.utils.ListMap;

public class ParamID extends ID
{
    static ListMap<String,ParamID> ids;
    
    static {
        ParamID.ids = new ListMap<String,ParamID>();
    }
    
    private ParamID() {
    }
    
    private ParamID(final String id_str) {
        this.c = id_str;
    }
    
    public ParamID createIDForSerialize() {
        return new ParamID();
    }
    
    static void clear() {
        ParamID.ids.clear();
    }
    
    public static ParamID getID(final String tmp_idstr) {
        ParamID paramID = ParamID.ids.get(tmp_idstr);
        if (paramID == null) {
            paramID = new ParamID(tmp_idstr);
            ParamID.ids.put(tmp_idstr, paramID);
        }
        return paramID;
    }
}
