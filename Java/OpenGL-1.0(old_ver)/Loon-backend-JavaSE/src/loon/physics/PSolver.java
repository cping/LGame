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

public class PSolver
{

    PBody b1;
    PBody b2;
    PContact[] cs;
    float fric;
    int numContacts;
    boolean rem;
    float rest;
    PShape s1;
    PShape s2;
    
    public PSolver(PShape shape1, PShape shape2, PContact contacts[], int num)
    {
        s1 = shape1;
        s2 = shape2;
        b1 = s1._parent;
        b2 = s2._parent;
        fric = (float)Math.sqrt(s1._fric * s2._fric);
        rest = (float)Math.sqrt(s1._rest * s2._rest);
        cs = contacts;
        numContacts = num;
        for(int i = 0; i < numContacts; i++)
        {
            PContact c = cs[i];
            c.rel1 = c.pos.sub(b1.pos);
            c.rel2 = c.pos.sub(b2.pos);
            c.massN = PTransformer.calcEffectiveMass(b1, b2, c.rel1, c.rel2, c.normal);
            c.massT = PTransformer.calcEffectiveMass(b1, b2, c.rel1, c.rel2, c.tangent);
            c.relVel = PTransformer.calcRelativeVelocity(b1, b2, c.rel1, c.rel2);
            float rvn = c.relVel.dot(c.normal);
            if(rvn < -0.5F)
                c.targetVelocity = Math.max(rest * -rvn, 0.0F);
            else
                c.targetVelocity = 0.0F;
            c.tangent.set(c.normal.y, -c.normal.x);
            c.localRel1.set(c.rel1.x, c.rel1.y);
            c.localRel2.set(c.rel2.x, c.rel2.y);
            b1.mAng.transpose().mulEqual(c.localRel1);
            b2.mAng.transpose().mulEqual(c.localRel2);
        }

    }

    private float clamp(float v, float min, float max)
    {
        return v <= max ? v >= min ? v : min : max;
    }

    public PContact[] getContacts()
    {
        PContact c[] = new PContact[numContacts];
        for(int i = 0; i < numContacts; i++)
            c[i] = cs[i];

        return c;
    }

    private float max(float v, float max)
    {
        return v >= max ? v : max;
    }

    void preSolve()
    {
        rem = true;
        for(int i = 0; i < numContacts; i++)
        {
            PContact c = cs[i];
            b1.applyImpulse(c.normal.x * c.norI + c.tangent.x * c.tanI, c.normal.y * c.norI + c.tangent.y * c.tanI, c.pos.x, c.pos.y);
            b2.applyImpulse(c.normal.x * -c.norI + c.tangent.x * -c.tanI, c.normal.y * -c.norI + c.tangent.y * -c.tanI, c.pos.x, c.pos.y);
            c.corI = 0.0F;
        }

    }

    void solvePosition()
    {
        for(int i = 0; i < numContacts; i++)
        {
            PContact c = cs[i];
            c.relPosVel = PTransformer.calcRelativeCorrectVelocity(b1, b2, c.rel1, c.rel2);
            float rvn = c.normal.dot(c.relPosVel);
            float subCorrectI = -c.massN * 0.2F * (rvn + c.overlap + 0.002F);
            float newCorrectI = max(c.corI + subCorrectI, 0.0F);
            subCorrectI = newCorrectI - c.corI;
            float forceX = c.normal.x * subCorrectI;
            float forceY = c.normal.y * subCorrectI;
            b1.positionCorrection(forceX, forceY, c.pos.x, c.pos.y);
            b2.positionCorrection(-forceX, -forceY, c.pos.x, c.pos.y);
            c.corI = newCorrectI;
        }

    }

    void solveVelocity()
    {
        for(int i = 0; i < numContacts; i++)
        {
            PContact c = cs[i];
            c.relVel = PTransformer.calcRelativeVelocity(b1, b2, c.rel1, c.rel2);
            float rvn = c.normal.x * c.relVel.x + c.normal.y * c.relVel.y;
            float subNormalI = -c.massN * (rvn - c.targetVelocity);
            float newNormalI = max(c.norI + subNormalI, 0.0F);
            subNormalI = newNormalI - c.norI;
            float forceX = c.normal.x * subNormalI;
            float forceY = c.normal.y * subNormalI;
            b1.applyImpulse(forceX, forceY, c.pos.x, c.pos.y);
            b2.applyImpulse(-forceX, -forceY, c.pos.x, c.pos.y);
            c.norI = newNormalI;
        }

        for(int i = 0; i < numContacts; i++)
        {
            PContact c = cs[i];
            c.relVel = PTransformer.calcRelativeVelocity(b1, b2, c.rel1, c.rel2);
            float rvt = c.tangent.x * c.relVel.x + c.tangent.y * c.relVel.y;
            float maxFriction = c.norI * fric;
            float subTangentI = c.massT * -rvt;
            float newTangentI = clamp(c.tanI + subTangentI, -maxFriction, maxFriction);
            subTangentI = newTangentI - c.tanI;
            float forceX = c.tangent.x * subTangentI;
            float forceY = c.tangent.y * subTangentI;
            b1.applyImpulse(forceX, forceY, c.pos.x, c.pos.y);
            b2.applyImpulse(-forceX, -forceY, c.pos.x, c.pos.y);
            c.tanI = newTangentI;
        }

    }

    void update(PContact contacts[], int num)
    {
        PContact old[] = cs;
        int oldNumContacts = numContacts;
        fric = (float)Math.sqrt(s1._fric * s2._fric);
        rest = (float)Math.sqrt(s1._rest * s2._rest);
        cs = contacts;
        numContacts = num;
        for(int i = 0; i < numContacts; i++)
        {
            PContact c = cs[i];
            c.rel1 = c.pos.sub(b1.pos);
            c.rel2 = c.pos.sub(b2.pos);
            c.massN = PTransformer.calcEffectiveMass(b1, b2, c.rel1, c.rel2, c.normal);
            c.massT = PTransformer.calcEffectiveMass(b1, b2, c.rel1, c.rel2, c.tangent);
            c.tangent.set(c.normal.y, -c.normal.x);
            c.localRel1.set(c.rel1.x, c.rel1.y);
            c.localRel2.set(c.rel2.x, c.rel2.y);
            b1.mAng.transpose().mulEqual(c.localRel1);
            b2.mAng.transpose().mulEqual(c.localRel2);
        }

        for(int i = 0; i < oldNumContacts; i++)
        {
            for(int j = 0; j < numContacts; j++)
                if(old[i].data.id == cs[j].data.id && old[i].data.flip == cs[j].data.flip)
                {
                    cs[j].norI = old[i].norI;
                    cs[j].tanI = old[i].tanI;
                }

        }

        rem = false;
    }

}
