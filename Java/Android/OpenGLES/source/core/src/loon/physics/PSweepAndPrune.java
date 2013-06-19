/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.physics;

import loon.utils.CollectionUtils;

public class PSweepAndPrune
{

    boolean checkX;
    int numObject;
    private PSortableObject[] objsX;
    private PSortableObject[] objsY;
    
    public PSweepAndPrune()
    {
        objsX = new PSortableObject[1024];
        objsY = new PSortableObject[1024];
    }

    void addObject(PSortableObject ox, PSortableObject oy)
    {
        if(numObject + 1 >= objsX.length)
        {
            objsX = (PSortableObject[])CollectionUtils.copyOf(objsX, objsX.length * 2);
            objsY = (PSortableObject[])CollectionUtils.copyOf(objsY, objsY.length * 2);
        }
        objsX[numObject] = ox;
        objsY[numObject] = oy;
        numObject++;
    }

    void removeObject(PSortableObject ox, PSortableObject oy)
    {
        int indexX = -1;
        int indexY = -1;
        for(int i = 0; i < numObject; i++)
        {
            if(objsX[i] != ox)
                continue;
            indexX = i;
            break;
        }

        if(indexX != -1 && indexX != numObject - 1)
            System.arraycopy(objsX, indexX + 1, objsX, indexX, numObject - indexX - 1);
        for(int i = 0; i < numObject; i++)
        {
            if(objsY[i] != oy)
                continue;
            indexY = i;
            break;
        }

        if(indexY != -1 && indexY != numObject - 1)
            System.arraycopy(objsY, indexY + 1, objsY, indexY, numObject - indexY - 1);
        numObject--;
    }

    PSortableObject[] sort()
    {
        PInsertionSorter.sort(objsX, numObject);
        PInsertionSorter.sort(objsY, numObject);
        int stack = 0;
        int overlapX = 0;
        int overlapY = 0;
        for(int i = 0; i < numObject; i++)
            if(objsX[i].begin)
            {
                stack++;
                overlapX += stack;
            } else
            {
                stack--;
            }

        stack = 0;
        for(int i = 0; i < numObject; i++)
            if(objsY[i].begin)
            {
                stack++;
                overlapY += stack;
            } else
            {
                stack--;
            }

        return (checkX = overlapX < overlapY) ? objsX : objsY;
    }

}
