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
package loon.geom;


/**
 * Implements some code shared by the various {@link Transform} implementations.
 */
public abstract class AbstractTransform implements Transform
{
	public Object tag;
	
    @Override // from Transform
    public Vector2f scale () {
        return new Vector2f(scaleX(), scaleY());
    }

    @Override // from Transform
    public Vector2f translation () {
        return new Vector2f(tx(), ty());
    }

    @Override // from Transform
    public Transform setUniformScale (float scale) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setScale (float scaleX, float scaleY) {
        setScaleX(scaleX);
        setScaleY(scaleY);
        return this;
    }

    @Override // from Transform
    public Transform setScaleX (float scaleX) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setScaleY (float scaleY) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setRotation (float angle) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTranslation (float tx, float ty) {
        setTx(tx);
        setTy(ty);
        return this;
    }

    @Override // from Transform
    public Transform uniformScale (float scale) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform scale (float scaleX, float scaleY) {
        scaleX(scaleX);
        scaleY(scaleY);
        return this;
    }

    @Override // from Transform
    public Transform scaleX (float scaleX) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform scaleY (float scaleY) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform rotate (float angle) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform translate (float tx, float ty) {
        translateX(tx);
        translateY(ty);
        return this;
    }

    @Override // from Transform
    public Transform translateX (float tx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform translateY (float ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform shear (float sx, float sy) {
        shearX(sx);
        shearY(sy);
        return this;
    }

    @Override // from Transform
    public Transform shearX (float sx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform shearY (float sy) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTx (float tx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTy (float ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTransform (float m00, float m01, float m10, float m11, float tx, float ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public abstract Transform cpy ();
}
