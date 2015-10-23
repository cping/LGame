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
 * Represents a geometric transform. Specialized implementations exist for identity, rigid body,
 * uniform, non-uniform, and affine transforms.
 */
public interface Transform
{
    /** Returns the uniform scale applied by this transform. The uniform scale will be approximated
     * for non-uniform transforms. */
    float uniformScale ();

    /** Returns the scale vector for this transform. */
    Vector2f scale ();

    /** Returns the x-component of the scale applied by this transform. Note that this will be
     * extracted and therefore approximate for affine transforms. */
    float scaleX ();

    /** Returns the y-component of the scale applied by this transform. Note that this will be
     * extracted and therefore approximate for affine transforms. */
    float scaleY ();

    /** Returns the rotation applied by this transform. Note that the rotation is extracted and
     * therefore approximate for affine transforms.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    float rotation ();

    /** Returns the translation vector for this transform. */
    Vector2f translation ();

    /** Returns the x-coordinate of the translation component. */
    float tx ();

    /** Returns the y-coordinate of the translation component. */
    float ty ();

    /** Copies the affine transform matrix into the supplied array.
     * @param matrix the array which receives {@code m00, m01, m10, m11, tx, ty}. */
    void get (float[] matrix);

    /** Sets the uniform scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not uniform or greater. */
    Transform setUniformScale (float scale);

    /** Sets the x and y scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if either supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform setScale (float scaleX, float scaleY);

    /** Sets the x scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform setScaleX (float scaleX);

    /** Sets the y scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform setScaleY (float scaleY);

    /** Sets the rotation component of this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setRotation (float angle);

    /** Sets the translation component of this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setTranslation (float tx, float ty);

    /** Sets the x-component of this transform's translation.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setTx (float tx);

    /** Sets the y-component of this transform's translation.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setTy (float ty);

    /** Sets the affine transform matrix.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not affine or greater. */
    Transform setTransform (float m00, float m01, float m10, float m11,
                            float tx, float ty);

    /** Scales this transform in a uniform manner by the specified amount.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not uniform or greater. */
    Transform uniformScale (float scale);

    /** Scales this transform by the specified amount in the x and y dimensions.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if either supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform scale (float scaleX, float scaleY);

    /** Scales this transform by the specified amount in the x dimension.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform scaleX (float scaleX);

    /** Scales this transform by the specified amount in the y dimension.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform scaleY (float scaleY);

    /** Rotates this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform rotate (float angle);

    /** Translates this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform translate (float tx, float ty);

    /** Translates this transform in the x dimension.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform translateX (float tx);

    /** Translates this transform in the y dimension.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform translateY (float ty);

    /** Shears this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not affine or greater. */
    Transform shear (float tx, float ty);

    /** Shears this transform in the x dimension.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not affine or greater. */
    Transform shearX (float tx);

    /** Shears this transform in the y dimension.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not affine or greater. */
    Transform shearY (float ty);

    /** Returns a new transform that represents the inverse of this transform.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    Transform invert ();

    /** Returns a new transform comprised of the concatenation of {@code other} to this transform
     * (i.e. {@code this x other}). */
    Transform concatenate (Transform other);

    /** Returns a new transform comprised of the concatenation of this transform to {@code other}
     * (i.e. {@code other x this}). */
    Transform preConcatenate (Transform other);

    /** Returns a new transform comprised of the linear interpolation between this transform and
     * the specified other. */
    Transform lerp (Transform other, float t);

    /** Transforms the supplied points.
     * @param src the points to be transformed.
     * @param srcOff the offset into the {@code src} array at which to start.
     * @param dst the points into which to store the transformed points. May be {@code src}.
     * @param dstOff the offset into the {@code dst} array at which to start.
     * @param count the number of points to transform. */
    void transform (Vector2f[] src, int srcOff, Vector2f[] dst, int dstOff, int count);

    /** Transforms the supplied points.
     * @param src the points to be transformed (as {@code [x, y, x, y, ...]}).
     * @param srcOff the offset into the {@code src} array at which to start.
     * @param dst the points into which to store the transformed points. May be {@code src}.
     * @param dstOff the offset into the {@code dst} array at which to start.
     * @param count the number of points to transform. */
    void transform (float[] src, int srcOff, float[] dst, int dstOff, int count);

    /** Transforms the supplied vector as a point (accounting for translation), writing the result
     * into {@code into}.
     * @param into a vector into which to store the result, may be the same object as {@code v}.
     * @return {@code into}, for chaining. */
    Vector2f transformPoint (Vector2f v, Vector2f into);

    /** Transforms the supplied vector, writing the result into {@code into}.
     * @param into a vector into which to store the result, may be the same object as {@code v}.
     * @return {@code into}, for chaining. */
    Vector2f transform (Vector2f v, Vector2f into);

    /** Inverse transforms the supplied vector, writing the result into {@code into}.
     * @param into a vector into which to store the result, may be the same object as {@code v}.
     * @return {@code into}, for chaining.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    Vector2f inverseTransform (Vector2f v, Vector2f into);

    /** @deprecated Use {@link #copy}. */
    @Deprecated Transform clone ();

    /** Returns a copy of this transform. */
    Transform cpy ();

    /** Returns an integer that increases monotonically with the generality of the transform
     * implementation. Used internally when combining transforms. */
    int generality ();
}
