#include "LPlus.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

	#define M00 0
	#define M01 4
	#define M02 8
	#define M03 12
	#define M10 1
	#define M11 5
	#define M12 9
	#define M13 13
	#define M20 2
	#define M21 6
	#define M22 10
	#define M23 14
	#define M30 3
	#define M31 7
	#define M32 11
	#define M33 15

	static inline void matrix4_mul(float* mata, float* matb) {
		float tmp[16];
		tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20] + mata[M03] * matb[M30];
		tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21] + mata[M03] * matb[M31];
		tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22] + mata[M03] * matb[M32];
		tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23] + mata[M03] * matb[M33];
		tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20] + mata[M13] * matb[M30];
		tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
		tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
		tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];
		tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20] + mata[M23] * matb[M30];
		tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
		tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
		tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];
		tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20] + mata[M33] * matb[M30];
		tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
		tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
		tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];
		memcpy(mata, tmp, sizeof(float) *  16);
	}
	
	static inline float matrix4_det(float* val) {
		return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03] - val[M30] * val[M11]
				* val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03] + val[M20] * val[M11] * val[M32] * val[M03] - val[M10]
				* val[M21] * val[M32] * val[M03] - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
				+ val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13] - val[M20] * val[M01] * val[M32]
				* val[M13] + val[M00] * val[M21] * val[M32] * val[M13] + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31]
				* val[M02] * val[M23] - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23] + val[M10]
				* val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23] - val[M20] * val[M11] * val[M02] * val[M33]
				+ val[M10] * val[M21] * val[M02] * val[M33] + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12]
				* val[M33] - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
	}
	
	static inline bool matrix4_inv(float* val) {
		float tmp[16];
		float l_det = matrix4_det(val);
		if (l_det == 0) return false;
		tmp[M00] = val[M12] * val[M23] * val[M31] - val[M13] * val[M22] * val[M31] + val[M13] * val[M21] * val[M32] - val[M11]
			* val[M23] * val[M32] - val[M12] * val[M21] * val[M33] + val[M11] * val[M22] * val[M33];
		tmp[M01] = val[M03] * val[M22] * val[M31] - val[M02] * val[M23] * val[M31] - val[M03] * val[M21] * val[M32] + val[M01]
			* val[M23] * val[M32] + val[M02] * val[M21] * val[M33] - val[M01] * val[M22] * val[M33];
		tmp[M02] = val[M02] * val[M13] * val[M31] - val[M03] * val[M12] * val[M31] + val[M03] * val[M11] * val[M32] - val[M01]
			* val[M13] * val[M32] - val[M02] * val[M11] * val[M33] + val[M01] * val[M12] * val[M33];
		tmp[M03] = val[M03] * val[M12] * val[M21] - val[M02] * val[M13] * val[M21] - val[M03] * val[M11] * val[M22] + val[M01]
			* val[M13] * val[M22] + val[M02] * val[M11] * val[M23] - val[M01] * val[M12] * val[M23];
		tmp[M10] = val[M13] * val[M22] * val[M30] - val[M12] * val[M23] * val[M30] - val[M13] * val[M20] * val[M32] + val[M10]
			* val[M23] * val[M32] + val[M12] * val[M20] * val[M33] - val[M10] * val[M22] * val[M33];
		tmp[M11] = val[M02] * val[M23] * val[M30] - val[M03] * val[M22] * val[M30] + val[M03] * val[M20] * val[M32] - val[M00]
			* val[M23] * val[M32] - val[M02] * val[M20] * val[M33] + val[M00] * val[M22] * val[M33];
		tmp[M12] = val[M03] * val[M12] * val[M30] - val[M02] * val[M13] * val[M30] - val[M03] * val[M10] * val[M32] + val[M00]
			* val[M13] * val[M32] + val[M02] * val[M10] * val[M33] - val[M00] * val[M12] * val[M33];
		tmp[M13] = val[M02] * val[M13] * val[M20] - val[M03] * val[M12] * val[M20] + val[M03] * val[M10] * val[M22] - val[M00]
			* val[M13] * val[M22] - val[M02] * val[M10] * val[M23] + val[M00] * val[M12] * val[M23];
		tmp[M20] = val[M11] * val[M23] * val[M30] - val[M13] * val[M21] * val[M30] + val[M13] * val[M20] * val[M31] - val[M10]
			* val[M23] * val[M31] - val[M11] * val[M20] * val[M33] + val[M10] * val[M21] * val[M33];
		tmp[M21] = val[M03] * val[M21] * val[M30] - val[M01] * val[M23] * val[M30] - val[M03] * val[M20] * val[M31] + val[M00]
			* val[M23] * val[M31] + val[M01] * val[M20] * val[M33] - val[M00] * val[M21] * val[M33];
		tmp[M22] = val[M01] * val[M13] * val[M30] - val[M03] * val[M11] * val[M30] + val[M03] * val[M10] * val[M31] - val[M00]
			* val[M13] * val[M31] - val[M01] * val[M10] * val[M33] + val[M00] * val[M11] * val[M33];
		tmp[M23] = val[M03] * val[M11] * val[M20] - val[M01] * val[M13] * val[M20] - val[M03] * val[M10] * val[M21] + val[M00]
			* val[M13] * val[M21] + val[M01] * val[M10] * val[M23] - val[M00] * val[M11] * val[M23];
		tmp[M30] = val[M12] * val[M21] * val[M30] - val[M11] * val[M22] * val[M30] - val[M12] * val[M20] * val[M31] + val[M10]
			* val[M22] * val[M31] + val[M11] * val[M20] * val[M32] - val[M10] * val[M21] * val[M32];
		tmp[M31] = val[M01] * val[M22] * val[M30] - val[M02] * val[M21] * val[M30] + val[M02] * val[M20] * val[M31] - val[M00]
			* val[M22] * val[M31] - val[M01] * val[M20] * val[M32] + val[M00] * val[M21] * val[M32];
		tmp[M32] = val[M02] * val[M11] * val[M30] - val[M01] * val[M12] * val[M30] - val[M02] * val[M10] * val[M31] + val[M00]
			* val[M12] * val[M31] + val[M01] * val[M10] * val[M32] - val[M00] * val[M11] * val[M32];
		tmp[M33] = val[M01] * val[M12] * val[M20] - val[M02] * val[M11] * val[M20] + val[M02] * val[M10] * val[M21] - val[M00]
			* val[M12] * val[M21] - val[M01] * val[M10] * val[M22] + val[M00] * val[M11] * val[M22];
	
		float inv_det = 1.0f / l_det;
		val[M00] = tmp[M00] * inv_det;
		val[M01] = tmp[M01] * inv_det;
		val[M02] = tmp[M02] * inv_det;
		val[M03] = tmp[M03] * inv_det;
		val[M10] = tmp[M10] * inv_det;
		val[M11] = tmp[M11] * inv_det;
		val[M12] = tmp[M12] * inv_det;
		val[M13] = tmp[M13] * inv_det;
		val[M20] = tmp[M20] * inv_det;
		val[M21] = tmp[M21] * inv_det;
		val[M22] = tmp[M22] * inv_det;
		val[M23] = tmp[M23] * inv_det;
		val[M30] = tmp[M30] * inv_det;
		val[M31] = tmp[M31] * inv_det;
		val[M32] = tmp[M32] * inv_det;
		val[M33] = tmp[M33] * inv_det;
		return true;
	}
	
	static inline void matrix4_mulVec(float* mat, float* vec) {
		float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03];
		float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13];
		float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23];
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;
	}
	
	static inline void matrix4_proj(float* mat, float* vec) {
		float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2] * mat[M32] + mat[M33]);
		float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03]) * inv_w;
		float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13]) * inv_w; 
		float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23]) * inv_w;
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;
	}
	
	static inline void matrix4_rot(float* mat, float* vec) {
		float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
		float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
		float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;
	}
	

   JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jniencode(JNIEnv* env, jclass clazz, jbyteArray bytes, jint size,jint tag) {
	jbyte* data = (jbyte*)env->GetPrimitiveArrayCritical(bytes, 0);
			for (int i = 0; i < size; i++) {
				data[i] ^= tag;
			}
	env->ReleasePrimitiveArrayCritical(bytes, data, 0);
	 }


	 JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnimul(JNIEnv* env, jclass clazz, jfloatArray obj_mata, jfloatArray obj_matb) {
	float* mata = (float*)env->GetPrimitiveArrayCritical(obj_mata, 0);
	float* matb = (float*)env->GetPrimitiveArrayCritical(obj_matb, 0);

		matrix4_mul(mata, matb);

	
	env->ReleasePrimitiveArrayCritical(obj_mata, mata, 0);
	env->ReleasePrimitiveArrayCritical(obj_matb, matb, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnimulVec___3F_3F(JNIEnv* env, jclass clazz, jfloatArray obj_mat, jfloatArray obj_vec) {
	float* mat = (float*)env->GetPrimitiveArrayCritical(obj_mat, 0);
	float* vec = (float*)env->GetPrimitiveArrayCritical(obj_vec, 0);

		matrix4_mulVec(mat, vec);

	
	env->ReleasePrimitiveArrayCritical(obj_mat, mat, 0);
	env->ReleasePrimitiveArrayCritical(obj_vec, vec, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnimulVec___3F_3FIII(JNIEnv* env, jclass clazz, jfloatArray obj_mat, jfloatArray obj_vecs, jint offset, jint numVecs, jint stride) {
	float* mat = (float*)env->GetPrimitiveArrayCritical(obj_mat, 0);
	float* vecs = (float*)env->GetPrimitiveArrayCritical(obj_vecs, 0);


		float* vecPtr = vecs + offset;

		for(int i = 0; i < numVecs; i++) {

			matrix4_mulVec(mat, vecPtr);

			vecPtr += stride;

		}

	
	env->ReleasePrimitiveArrayCritical(obj_mat, mat, 0);
	env->ReleasePrimitiveArrayCritical(obj_vecs, vecs, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jniprj___3F_3F(JNIEnv* env, jclass clazz, jfloatArray obj_mat, jfloatArray obj_vec) {
	float* mat = (float*)env->GetPrimitiveArrayCritical(obj_mat, 0);
	float* vec = (float*)env->GetPrimitiveArrayCritical(obj_vec, 0);


		matrix4_proj(mat, vec);

	
	env->ReleasePrimitiveArrayCritical(obj_mat, mat, 0);
	env->ReleasePrimitiveArrayCritical(obj_vec, vec, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jniprj___3F_3FIII(JNIEnv* env, jclass clazz, jfloatArray obj_mat, jfloatArray obj_vecs, jint offset, jint numVecs, jint stride) {
	float* mat = (float*)env->GetPrimitiveArrayCritical(obj_mat, 0);
	float* vecs = (float*)env->GetPrimitiveArrayCritical(obj_vecs, 0);

		float* vecPtr = vecs + offset;

		for(int i = 0; i < numVecs; i++) {

			matrix4_proj(mat, vecPtr);

			vecPtr += stride;

		}

	
	env->ReleasePrimitiveArrayCritical(obj_mat, mat, 0);
	env->ReleasePrimitiveArrayCritical(obj_vecs, vecs, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnirot___3F_3F(JNIEnv* env, jclass clazz, jfloatArray obj_mat, jfloatArray obj_vec) {
	float* mat = (float*)env->GetPrimitiveArrayCritical(obj_mat, 0);
	float* vec = (float*)env->GetPrimitiveArrayCritical(obj_vec, 0);

		matrix4_rot(mat, vec);

	
	env->ReleasePrimitiveArrayCritical(obj_mat, mat, 0);
	env->ReleasePrimitiveArrayCritical(obj_vec, vec, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnirot___3F_3FIII(JNIEnv* env, jclass clazz, jfloatArray obj_mat, jfloatArray obj_vecs, jint offset, jint numVecs, jint stride) {
	float* mat = (float*)env->GetPrimitiveArrayCritical(obj_mat, 0);
	float* vecs = (float*)env->GetPrimitiveArrayCritical(obj_vecs, 0);

		float* vecPtr = vecs + offset;

		for(int i = 0; i < numVecs; i++) {

			matrix4_rot(mat, vecPtr);

			vecPtr += stride;

		}

	
	env->ReleasePrimitiveArrayCritical(obj_mat, mat, 0);
	env->ReleasePrimitiveArrayCritical(obj_vecs, vecs, 0);

}

static inline jboolean wrapped_inv
(JNIEnv* env, jclass clazz, jfloatArray obj_values, float* values) {

		return matrix4_inv(values);
}

JNIEXPORT jboolean JNICALL Java_loon_jni_NativeSupport_jniinv(JNIEnv* env, jclass clazz, jfloatArray obj_values) {
	float* values = (float*)env->GetPrimitiveArrayCritical(obj_values, 0);

	jboolean JNI_returnValue = wrapped_inv(env, clazz, obj_values, values);

	env->ReleasePrimitiveArrayCritical(obj_values, values, 0);

	return JNI_returnValue;
}

static inline jfloat wrapped_det
(JNIEnv* env, jclass clazz, jfloatArray obj_values, float* values) {

		return matrix4_det(values);
	
}

JNIEXPORT jfloat JNICALL Java_loon_jni_NativeSupport_jnidet(JNIEnv* env, jclass clazz, jfloatArray obj_values) {
	float* values = (float*)env->GetPrimitiveArrayCritical(obj_values, 0);

	jfloat JNI_returnValue = wrapped_det(env, clazz, obj_values, values);

	env->ReleasePrimitiveArrayCritical(obj_values, values, 0);

	return JNI_returnValue;
}


JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_updateFractions(JNIEnv *env, jclass,jint size,jfloatArray src,
			jint width, jint height, jintArray dst,jint numElements) {

		jfloat* jniSrc=env->GetFloatArrayElements(src,NULL);
		jint* jniDst=env->GetIntArrayElements(dst,NULL);
	    jint x, y;
		jint idx = 0;
		for (jint j = 0; j < size; j++) {
			idx = j * numElements;
			if (jniSrc[idx + 4] != 0xffffff) {
				if (jniSrc[idx + 5] <= 0) {
					jniSrc[idx + 0] += jniSrc[idx + 2];
					jniSrc[idx + 1] += jniSrc[idx + 3];
					jniSrc[idx + 3] += 0.1;
				} else {
					jniSrc[idx + 5]--;
				}
				x = jniSrc[idx + 0];
				y = jniSrc[idx + 1];
				if (x > -1 && y > -1 && x < width && y < height) {
					jniDst[x + y * width] = jniSrc[idx + 4];
				}
			}
		}
	   env->SetIntArrayRegion(dst,0,size,jniDst);
	   env->ReleaseFloatArrayElements(src, jniSrc, 0);
       env->ReleaseIntArrayElements(dst, jniDst, 0);

}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_updateArray(JNIEnv *env, jclass,jint maxPixel,  jint pixelStart,  jint pixelEnd,
			 jintArray src,  jintArray dst,  jintArray colors,jint c1,jint c2) {

  jboolean b;

  jsize length = env->GetArrayLength(src);

  jint* jniSrc=env->GetIntArrayElements(src,&b);
  jint* jniDst=env->GetIntArrayElements(dst,&b);
  jint* jniColors=env->GetIntArrayElements(colors,&b);

				if (pixelStart < pixelEnd)
				{

					const int start = pixelStart + 1;
					const int end = pixelEnd + 1;
					if (end > maxPixel)
					{
						return;
					}
					for (int i = 0; i < length; i++)
					{
						if (jniDst[i] != 0xffffff)
						{
							for (int pixIndex = start; pixIndex < end; pixIndex++)
							{
								if (jniColors[pixIndex] == jniSrc[i])
								{
									jniDst[i] = 0xffffff;
								}
								else if (jniSrc[i] == c1)
								{
									jniDst[i] = 0xffffff;
								}
							}
						}
					}
				}
				else
				{
					const int start = pixelEnd - 1;
					const int end = pixelStart;
					if (start < 0)
					{
						return;
					}
					for (int i = 0; i < length; i++)
					{
						if (jniDst[i] != 0xffffff)
						{
							for (int pixIndex = start; pixIndex < end; pixIndex++)
							{
								if (jniColors[pixIndex] == jniSrc[i])
								{
									jniDst[i] = 0xffffff;
								}
								else if (jniSrc[i] == c2)
								{
									jniDst[i] = 0xffffff;
								}
							}
						}
					}
			}	
     env->SetIntArrayRegion(dst,0,length,jniDst);
     env->ReleaseIntArrayElements(src, jniSrc, 0);
     env->ReleaseIntArrayElements(dst, jniDst, 0);
     env->ReleaseIntArrayElements(colors, jniColors, 0);
}

static inline void copy(JNIEnv* env, jarray src, int srcOffset, jobject dst, int dstOffset, int numBytes) {
	char* pDst = (char*)env->GetDirectBufferAddress( dst );
	char* pSrc = (char*)env->GetPrimitiveArrayCritical(src, 0);
	memcpy( pDst + dstOffset, pSrc + srcOffset, numBytes);
	env->ReleasePrimitiveArrayCritical(src, pSrc, 0);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3FLjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jfloatArray src, jobject dst, jint numFloats, jint offset )
{
	float* pDst = (float*)env->GetDirectBufferAddress( dst );
	float* pSrc = (float*)env->GetPrimitiveArrayCritical(src, 0);
	
	memcpy( pDst, pSrc + offset, numFloats << 2 );
	env->ReleasePrimitiveArrayCritical(src, pSrc, 0);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3BILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jbyteArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
	copy(env, src, srcOffset, dst, dstOffset, numBytes);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3CILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jcharArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
	copy(env, src, srcOffset, dst, dstOffset, numBytes);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3SILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jshortArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
	copy(env, src, srcOffset, dst, dstOffset, numBytes); 
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3IILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jintArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
  	copy(env, src, srcOffset, dst, dstOffset, numBytes);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3JILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jlongArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
  	copy(env, src, srcOffset, dst, dstOffset, numBytes);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3FILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jfloatArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
	copy(env, src, srcOffset, dst, dstOffset, numBytes);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3DILjava_nio_Buffer_2II
  (JNIEnv * env, jclass, jdoubleArray src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
	copy(env, src, srcOffset, dst, dstOffset, numBytes);
}

JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy__Ljava_nio_Buffer_2ILjava_nio_Buffer_2II
  (JNIEnv *env, jclass, jobject src, jint srcOffset, jobject dst, jint dstOffset, jint numBytes) {
	char* pSrc = (char*)env->GetDirectBufferAddress( src );  
	char* pDst = (char*)env->GetDirectBufferAddress( dst );
	memcpy( pDst + dstOffset, pSrc + srcOffset, numBytes);
}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferPut(JNIEnv *env, jclass, jobject pBuffer, jfloatArray pData, jint pLength, jint pOffset) {
	unsigned char* bufferAddress = (unsigned char*)env->GetDirectBufferAddress(pBuffer);
	float* dataAddress = (float*)env->GetPrimitiveArrayCritical(pData, 0);
	memcpy(bufferAddress, dataAddress + pOffset, pLength << 2);
	env->ReleasePrimitiveArrayCritical(pData, dataAddress, 0);
}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferClear(JNIEnv *env, jclass, jobject buffer, jint count) {
	unsigned char *pBuffer = (unsigned char *)env->GetDirectBufferAddress(buffer);
	memset(pBuffer, 0, count);
}

JNIEXPORT jobject JNICALL  Java_loon_jni_NativeSupport_bufferDirect(JNIEnv *env, jclass, jint numBytes) {
	    char* ptr = (char*)malloc(numBytes);
		return env->NewDirectByteBuffer(ptr, numBytes);
}

JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferFreeDirect(JNIEnv* env, jclass, jobject pBuffer) {
	unsigned char* bufferAddress = (unsigned char*)env->GetDirectBufferAddress(pBuffer);
	free(bufferAddress);
}
