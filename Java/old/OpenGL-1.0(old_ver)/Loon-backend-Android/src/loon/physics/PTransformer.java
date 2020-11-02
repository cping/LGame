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

import loon.core.geom.Vector2f;

public class PTransformer
{

    public float e00;
    public float e01;
    public float e10;
    public float e11;
    
    public PTransformer()
    {
        setIdentity();
    }

    public PTransformer(float e00, float e01, float e10, float e11)
    {
        set(e00, e01, e10, e11);
    }

    public PTransformer add(PTransformer m)
    {
    	
        return new PTransformer(e00 + m.e00, e01 + m.e01, e10 + m.e10, e11 + m.e11);
    }

    public void addEqual(PTransformer m)
    {
        e00 += m.e00;
        e01 += m.e01;
        e10 += m.e10;
        e11 += m.e11;
    }

    @Override
	public PTransformer clone()
    {
        return new PTransformer(e00, e01, e10, e11);
    }

    public PTransformer invert()
    {
        float d = e00 * e11 - e10 * e01;
        if(d != 0.0F)
            d = 1.0F / d;
        else
            return new PTransformer();
        PTransformer ret = new PTransformer(d * e11, -d * e01, -d * e10, d * e00);
        return ret;
    }

    public void invertEqual()
    {
        float d = e00 * e11 - e10 * e01;
        if(d != 0.0F)
            d = 1.0F / d;
        else
            setIdentity();
        set(d * e11, -d * e01, -d * e10, d * e00);
    }

    public PTransformer mul(PTransformer m)
    {
        float t11 = e00 * m.e00 + e01 * m.e10;
        float t12 = e00 * m.e01 + e01 * m.e11;
        float t13 = e10 * m.e00 + e11 * m.e10;
        float t14 = e10 * m.e01 + e11 * m.e11;
        return new PTransformer(t11, t12, t13, t14);
    }

    public Vector2f mul(Vector2f v)
    {
        return new Vector2f(e00 * v.x + e10 * v.y, e01 * v.x + e11 * v.y);
    }

    public void mulEqual(PTransformer m)
    {
        float t11 = e00 * m.e00 + e01 * m.e10;
        float t12 = e00 * m.e01 + e01 * m.e11;
        float t13 = e10 * m.e00 + e11 * m.e10;
        float t14 = e10 * m.e01 + e11 * m.e11;
        set(t11, t12, t13, t14);
    }

    public void mulEqual(Vector2f v)
    {
        v.set(e00 * v.x + e10 * v.y, e01 * v.x + e11 * v.y);
    }

    public void set(float e00, float e01, float e10, float e11)
    {
        this.e00 = e00;
        this.e01 = e01;
        this.e10 = e10;
        this.e11 = e11;
    }

    public void setIdentity()
    {
        e01 = e10 = 0.0F;
        e00 = e11 = 1.0F;
    }

    public void setRotate(float theta)
    {
        float sin = (float)Math.sin(theta);
        float cos = (float)Math.cos(theta);
        e00 = cos;
        e01 = sin;
        e10 = -sin;
        e11 = cos;
    }

    @Override
	public String toString()
    {
        return (new StringBuilder("[[")).append(e00).append(", ").append(e01).append("], [").append(e10).append(", ").append(e11).append("]]").toString();
    }

    public PTransformer transpose()
    {
        return new PTransformer(e00, e10, e01, e11);
    }

    public void transposeEqual()
    {
        set(e00, e10, e01, e11);
    }

    public static PTransformer calcEffectiveMass(PBody b1, PBody b2, Vector2f r1, Vector2f r2)
    {
        PTransformer mass = new PTransformer(b1.invM + b2.invM + b1.invI * r1.y * r1.y + b2.invI * r2.y * r2.y, -b1.invI * r1.x * r1.y - b2.invI * r2.x * r2.y, -b1.invI * r1.x * r1.y - b2.invI * r2.x * r2.y, b1.invM + b2.invM + b1.invI * r1.x * r1.x + b2.invI * r2.x * r2.x);
        mass.invertEqual();
        return mass;
    }

    public static float calcEffectiveMass(PBody b1, PBody b2, Vector2f r1, Vector2f r2, Vector2f normal)
    {
        float rn1 = normal.dot(r1);
        float rn2 = normal.dot(r2);
        return 1.0F / (b1.invM + b2.invM + b1.invI * ((r1.x * r1.x + r1.y * r1.y) - rn1 * rn1) + b2.invI * ((r2.x * r2.x + r2.y * r2.y) - rn2 * rn2));
    }

    public static PTransformer calcEffectiveMass(PBody b, Vector2f r)
    {
        PTransformer mass = new PTransformer(b.invM + b.invI * r.y * r.y, -b.invI * r.x * r.y, -b.invI * r.x * r.y, b.invM + b.invI * r.x * r.x);
        mass.invertEqual();
        return mass;
    }

    public static Vector2f calcRelativeCorrectVelocity(PBody b1, PBody b2, Vector2f r1, Vector2f r2)
    {
        Vector2f relVel = b1.correctVel.clone();
        relVel.x -= b2.correctVel.x;
        relVel.y -= b2.correctVel.y;
        relVel.x += -b1.correctAngVel * r1.y;
        relVel.y += b1.correctAngVel * r1.x;
        relVel.x -= -b2.correctAngVel * r2.y;
        relVel.y -= b2.correctAngVel * r2.x;
        return relVel;
    }

    public static Vector2f calcRelativeVelocity(PBody b1, PBody b2, Vector2f r1, Vector2f r2)
    {
        Vector2f relVel = b1.vel.clone();
        relVel.x -= b2.vel.x;
        relVel.y -= b2.vel.y;
        relVel.x += -b1.angVel * r1.y;
        relVel.y += b1.angVel * r1.x;
        relVel.x -= -b2.angVel * r2.y;
        relVel.y -= b2.angVel * r2.x;
        return relVel;
    }

}
