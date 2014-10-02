/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.core.graphics.d3d.parse;

import java.nio.FloatBuffer;

public class D3DVector {
	
	float mT[];
	
	public D3DVector(){
		mT = new float[3];
	}
	
	public D3DVector(float x, float y, float z)
	{
		this();
		this.set(0, x);
		this.set(1, y);
		this.set(2, z);
	}
	
	public D3DVector(float[] v) {
		this();
		this.set(v);
	}
	
	public D3DVector clone()
	{
		return new D3DVector(this.get(0),this.get(1),this.get(2));
	}
	
	public D3DVector(FloatBuffer fb,int indice,int nbFloatPerVertex)
	{
		mT[0] = fb.get(indice*nbFloatPerVertex);
		mT[1] = fb.get(indice*nbFloatPerVertex+1);
		mT[2] = fb.get(indice*nbFloatPerVertex+2);		
	}
	
	public void set(D3DVector v)
	{
		this.set(0,v.get(0));
		this.set(1,v.get(1));
		this.set(2,v.get(2));
	}
	
	public void set(float v[])
	{
		this.set(0, v[0]);
		this.set(1, v[1]);
		this.set(2, v[2]);		
	}
	
	public void setFromVertice(FloatBuffer fb,int indice)
	{
		mT[0] = fb.get(indice*D3DMesh.nbFloatPerVertex);
		mT[1] = fb.get(indice*D3DMesh.nbFloatPerVertex+1);
		mT[2] = fb.get(indice*D3DMesh.nbFloatPerVertex+2);			
	}
	
	public void setFromNormal(FloatBuffer fb,int indice)
	{
		mT[0] = fb.get(indice*D3DMesh.nbFloatPerVertex+3);
		mT[1] = fb.get(indice*D3DMesh.nbFloatPerVertex+4);
		mT[2] = fb.get(indice*D3DMesh.nbFloatPerVertex+5);			
	}	
	
	public float get(int index)
	{
		return mT[index];
	}
	
	public void set(int index,float value)
	{
		mT[index] = value;
	}
	
	public void mul(float f)
	{
		mT[0] = mT[0] * f;
		mT[1] = mT[1] * f;
		mT[2] = mT[2] * f;
	}
	
	public void add (float v[])
	{
		mT[0] = mT[0] + v[0];
		mT[1] = mT[1] + v[1];
		mT[2] = mT[2] + v[2];			
	}
	
	public void add(D3DVector v)
	{
		mT[0] = mT[0] + v.get(0);
		mT[1] = mT[1] + v.get(1);
		mT[2] = mT[2] + v.get(2);		
	}
	
	public static void sub(D3DVector res,D3DVector v1,D3DVector v2)
	{
		res.set(0,v1.get(0)-v2.get(0));
		res.set(1,v1.get(1)-v2.get(1));
		res.set(2,v1.get(2)-v2.get(2));		
	}		
	
	public static void add(D3DVector res,D3DVector v1,D3DVector v2)
	{
		res.set(0,v1.get(0)+v2.get(0));
		res.set(1,v1.get(1)+v2.get(1));
		res.set(2,v1.get(2)+v2.get(2));		
	}	
	
	public static float dot(D3DVector v1,D3DVector v2)
	{
		return v1.get(0)*v2.get(0)+v1.get(1)*v2.get(1)+v1.get(2)*v2.get(2);
	}
	
	public static void cross(D3DVector res,D3DVector v1,D3DVector v2)
	{
		res.set(0,v1.get(1) * v2.get(2) - v1.get(2) * v2.get(1));
		res.set(1,v1.get(2) * v2.get(0) - v1.get(0) * v2.get(2));
		res.set(2,v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0)); 		
	}
	public static float distance(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		return (float)java.lang.Math.sqrt( (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+(z2-z1)*(z2-z1));
	}		
	
	public static float distance(D3DVector v1,D3DVector v2)
	{
		return distance(v1.get(0),v1.get(1),v1.get(2),v2.get(0),v2.get(1),v2.get(2));
	}		
	
	public float length()
	{
		return distance(0f,0f,0f,this.get(0),this.get(1),this.get(2));		
	}
	
	public void normalize()
	{
		float d = distance(mT[0],mT[1],mT[2],0.f,0.f,0.f);
		if (d != 0.f)
		{
			mT[0] = mT[0]/d;
			mT[1] = mT[1]/d;
			mT[2] = mT[2]/d;			
		}
	}
	
	public static double collideSphere(D3DVector origin, D3DVector direction, float sphereRadius, D3DVector spherePosition) {
		double r2 = sphereRadius * sphereRadius;
	
		double a = D3DVector.dot(direction, direction);
		D3DVector oc = new D3DVector();
		D3DVector.sub(oc, origin,spherePosition);
		double b = D3DVector.dot(oc, direction);
		b = b *2;
		oc = origin.clone();
		D3DVector.sub(oc, origin,spherePosition);
		double c = D3DVector.dot(oc, oc) - r2;

		return b*b - 4 * a * c;
	}		
}
