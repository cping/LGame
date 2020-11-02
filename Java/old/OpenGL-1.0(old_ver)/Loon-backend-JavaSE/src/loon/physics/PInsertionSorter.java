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

public class PInsertionSorter
{

    public static final void sort(PSortableObject objs[], int num)
    {
        if(num == 1){
            return;
        }
        for(int i = 1; i < num; i++)
        {
            PSortableObject s = objs[i];
            if(objs[i - 1].value > s.value)
            {
                int j = i;
                do{
                    objs[j] = objs[j - 1];
                }while(--j > 0 && objs[j - 1].value > s.value);
                objs[j] = s;
            }
        }

    }

    private PInsertionSorter()
    {
    }
}
