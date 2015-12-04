package loon.live2d.id;

public class ID
{
    protected String c;
    
    @Override
    public String toString() {
        return this.c;
    }
    
    public static void releaseStored_notForClientCall() {
        ParamID.clear();
        BaseDataID.clear();
        DrawDataID.clear();
        PartsDataID.clear();
    }
}
