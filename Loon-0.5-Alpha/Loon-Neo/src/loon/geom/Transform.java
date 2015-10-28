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

public interface Transform
{

    float uniformScale ();

    Vector2f scale ();

    float scaleX ();

    float scaleY ();

    float rotation ();

    Vector2f translation ();

    float tx ();

    float ty ();

    void get (float[] matrix);

    Transform setUniformScale (float scale);

    Transform setScale (float scaleX, float scaleY);

    Transform setScaleX (float scaleX);

    Transform setScaleY (float scaleY);

    Transform setRotation (float angle);

    Transform setTranslation (float tx, float ty);

    Transform setTx (float tx);

    Transform setTy (float ty);

    Transform setTransform (float m00, float m01, float m10, float m11,
                            float tx, float ty);

    Transform uniformScale (float scale);

    Transform scale (float scaleX, float scaleY);

    Transform scaleX (float scaleX);

    Transform scaleY (float scaleY);

    Transform rotate (float angle);

    Transform translate (float tx, float ty);

    Transform translateX (float tx);

    Transform translateY (float ty);

    Transform shear (float tx, float ty);

    Transform shearX (float tx);

    Transform shearY (float ty);

    Transform invert ();

    Transform concatenate (Transform other);

    Transform preConcatenate (Transform other);

    Transform lerp (Transform other, float t);

    void transform (Vector2f[] src, int srcOff, Vector2f[] dst, int dstOff, int count);

    void transform (float[] src, int srcOff, float[] dst, int dstOff, int count);

    Vector2f transformPoint (Vector2f v, Vector2f into);

    Vector2f transform (Vector2f v, Vector2f into);

    Vector2f inverseTransform (Vector2f v, Vector2f into);

    Transform cpy ();

    int generality ();
}
