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

public abstract class PJoint
{

    boolean rem;
    PJointType type;
    
    public PJoint()
    {
        type = PJointType.NULL_JOINT;
    }

    public PJointType getJointType()
    {
        return type;
    }

    abstract void preSolve(float f);

    public void remove()
    {
        rem = true;
    }

    abstract void solvePosition();

    abstract void solveVelocity(float f);

    abstract void update();

}
