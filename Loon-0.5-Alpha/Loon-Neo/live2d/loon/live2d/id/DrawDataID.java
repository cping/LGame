package loon.live2d.id;


import loon.utils.ListMap;

public class DrawDataID extends ID
{
    static ListMap ids;
    
    static {
        DrawDataID.ids = new ListMap();
    }
    
    private DrawDataID() {
    }
    
    private DrawDataID(final String idstr) {
        this.c = idstr;
    }
    
    public DrawDataID createIDForSerialize() {
        return new DrawDataID();
    }
    
    static void clear() {
        DrawDataID.ids.clear();
    }
    
    public static DrawDataID getID(final String tmp_idstr) {
        DrawDataID drawDataID = (DrawDataID) DrawDataID.ids.get(tmp_idstr);
        if (drawDataID == null) {
            drawDataID = new DrawDataID(tmp_idstr);
            DrawDataID.ids.put(tmp_idstr, drawDataID);
        }
        return drawDataID;
    }
}
