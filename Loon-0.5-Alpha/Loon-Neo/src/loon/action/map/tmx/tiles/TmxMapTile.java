package loon.action.map.tmx.tiles;

import loon.LSystem;
import loon.action.map.tmx.TMXMap;

public class TmxMapTile
{
    private int tileSetID;
    private int id;
    private int gid;

    private boolean flippedHorizontally;
    private boolean flippedVertically;
    private boolean flippedDiagonally;

    public TmxMapTile(int gid, int tileSetFirstID, int tileSetID)
    {

        this.tileSetID = tileSetID;

        flippedHorizontally = (gid & TMXMap.FLIPPED_HORIZONTALLY_FLAG) != 0;
        flippedVertically = (gid & TMXMap.FLIPPED_VERTICALLY_FLAG) != 0;
        flippedDiagonally = (gid & TMXMap.FLIPPED_DIAGONALLY_FLAG) != 0;

        this.gid = (int) (gid & ~(TMXMap.FLIPPED_HORIZONTALLY_FLAG | TMXMap.FLIPPED_VERTICALLY_FLAG | TMXMap.FLIPPED_DIAGONALLY_FLAG));
        this.id = gid - tileSetFirstID;
    }

    public int getTileSetID()
    {
        return tileSetID;
    }

    public int getID()
    {
        return id;
    }

    public int getGID()
    {
        return gid;
    }

    public boolean isFlippedHorizontally()
    {
        return flippedHorizontally;
    }

    public boolean isFlippedVertically()
    {
        return flippedVertically;
    }

    public boolean isFlippedDiagonally()
    {
        return flippedDiagonally;
    }
    
    @Override
    public int hashCode(){
    	int result = id;
    	result = LSystem.unite(result, gid);
    	result = LSystem.unite(result, tileSetID);
    	result = LSystem.unite(result, flippedHorizontally);
    	result = LSystem.unite(result, flippedVertically);
    	result = LSystem.unite(result, flippedDiagonally);
    	return result;
    }
}
