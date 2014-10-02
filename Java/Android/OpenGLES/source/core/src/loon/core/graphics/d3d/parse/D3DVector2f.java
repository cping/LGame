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

public class D3DVector2f {
	
	float mT[];
	
	public D3DVector2f(){
		mT = new float[2];
	}
	
	public D3DVector2f(float x, float y){
		this();
		set(x,y);
	}	
	
	public void set(float x, float y)
	{
		mT[0] = x;
		mT[1] = y;
	}
	
	public float getX()
	{
		return mT[0];
	}
	
	public float getY()
	{
		return mT[1];
	}
	
	public D3DVector2f clone()
	{
		return new D3DVector2f(this.getX(),this.getY());
	}
	
	public void mul(float f)
	{
		mT[0] = mT[0] * f;
	    mT[1] = mT[1] * f;
	}
	
	public void sub(D3DVector2f v)
	{
		this.set(this.getX() - v.getX(), this.getY() - v.getY());
	}
	
	public void add(D3DVector2f v)
	{
		this.set(this.getX() + v.getX(), this.getY() + v.getY());
	}
	
	public float length()
	{
		return (float)java.lang.Math.sqrt(this.getX()*this.getX()+(this.getY()*this.getY()));
	}
	
	public static void sub(D3DVector2f res,D3DVector2f v1,D3DVector2f v2)
	{
		res.set(v1.getX()-v2.getX(),v1.getY()-v2.getY());
	}	
	
	public static float distance(D3DVector2f v1,D3DVector2f v2)
	{
		return (float)java.lang.Math.sqrt( (v2.getX()-v1.getX())*(v2.getX()-v1.getX())+(v2.getY()-v1.getY())*(v2.getY()-v1.getY()));
	}		
	
	public static float dotProduct(D3DVector2f v1,D3DVector2f v2)
	{
		return v1.getX()*v2.getX()+v1.getY()*v2.getY();
	}
	
	public void setFromTexCoords(FloatBuffer fb,int indice)
	{
		mT[0] = fb.get(indice*D3DMesh.nbFloatPerVertex+6);
		mT[1] = fb.get(indice*D3DMesh.nbFloatPerVertex+7);		
	}	
	
	public void normalize() {
		float l = length();
		mT[0] = mT[0] / l;
		mT[1] = mT[1] / l;
	}
}
