/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.action.map.tmx;

public class TMXMapLayer
{
    protected static int nextParseOrder = 0;

    protected TMXMap map;
    protected String name;

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zOrder;
    protected int parseOrder;

    protected float        opacity;
    protected boolean      visible;
    protected TmxLayerType type;

    protected TMXProperties properties;

    public TMXMapLayer(TMXMap map, String name,
                       int x, int y, int width, int height,
                       float opacity, boolean visible, TmxLayerType type)
    {
        this.map = map;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.opacity = opacity;
        this.visible = visible;
        this.type = type;

        properties = new TMXProperties();

        ++nextParseOrder;
    }

    public TMXMap getMap()
    {
        return map;
    }

    public String getName()
    {
        return name;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getZOrder()
    {
        return zOrder;
    }

    public void setZOrder(int zOrder)
    {
        this.zOrder = zOrder;
    }

    public int getParseOrder()
    {
        return parseOrder;
    }

    public float getOpacity()
    {
        return opacity;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public TmxLayerType getType()
    {
        return type;
    }

    public TMXProperties getProperties()
    {
        return properties;
    }

    public enum TmxLayerType
    {
        TILE, OBJECT, IMAGE
    }
}
