#include <jni.h>
#ifndef _Included_loon_jni_NativeSupport
#define _Included_loon_jni_NativeSupport
#ifdef __cplusplus
extern "C" {
#endif
	
    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jniencode(JNIEnv *, jclass, jbyteArray, jint,jint);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnimul(JNIEnv *, jclass, jfloatArray, jfloatArray);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnimulVec___3F_3F(JNIEnv *, jclass, jfloatArray, jfloatArray);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnimulVec___3F_3FIII(JNIEnv *, jclass, jfloatArray, jfloatArray, jint, jint, jint);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jniprj___3F_3F(JNIEnv *, jclass, jfloatArray, jfloatArray);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jniprj___3F_3FIII(JNIEnv *, jclass, jfloatArray, jfloatArray, jint, jint, jint);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnirot___3F_3F(JNIEnv *, jclass, jfloatArray, jfloatArray);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_jnirot___3F_3FIII(JNIEnv *, jclass, jfloatArray, jfloatArray, jint, jint, jint);

    JNIEXPORT jboolean JNICALL Java_loon_jni_NativeSupport_jniinv(JNIEnv *, jclass, jfloatArray);

    JNIEXPORT jfloat JNICALL Java_loon_jni_NativeSupport_jnidet(JNIEnv *, jclass, jfloatArray);
	
    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_updateFractions(JNIEnv *, jclass,jint,jfloatArray,
			jint, jint,jintArray,jint);
	
    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_updateArray(JNIEnv *, jclass,jint,jint,jint,
			 jintArray,  jintArray,  jintArray,jint,jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3FLjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jfloatArray, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3BILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jbyteArray, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3CILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jcharArray, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3SILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jshortArray, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3IILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jintArray, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3JILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jlongArray, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3FILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jfloatArray, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL  Java_loon_jni_NativeSupport_bufferCopy___3DILjava_nio_Buffer_2II
		  (JNIEnv *, jclass, jdoubleArray, jint, jobject, jint, jint);

    JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferCopy__Ljava_nio_Buffer_2ILjava_nio_Buffer_2II
    (JNIEnv *, jclass, jobject, jint, jobject, jint, jint);

	JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferPut(JNIEnv *, jclass, jobject, jfloatArray, jint, jint);

	JNIEXPORT jobject JNICALL Java_loon_jni_NativeSupport_bufferDirect(JNIEnv *, jclass, jint);

	JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferClear(JNIEnv *, jclass, jobject, jint);

	JNIEXPORT void JNICALL Java_loon_jni_NativeSupport_bufferFreeDirect(JNIEnv *, jclass, jobject);

	JNIEXPORT jintArray JNICALL Java_loon_jni_NativeSupport_getGray(JNIEnv *env, jclass, jintArray, jint, jint);

	JNIEXPORT jintArray JNICALL Java_loon_jni_NativeSupport_setColorKey(JNIEnv *env, jclass, jintArray, jint);

	JNIEXPORT jintArray JNICALL Java_loon_jni_NativeSupport_setColorKeys(JNIEnv *env, jclass, jintArray, jintArray);

	JNIEXPORT jintArray JNICALL Java_loon_jni_NativeSupport_setColorKeyLimit(JNIEnv *env, jclass, jintArray, jint, jint);

#ifdef __cplusplus
}
#endif
#endif