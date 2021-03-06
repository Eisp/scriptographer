/*
 * Scriptographer
 *
 * This file is part of Scriptographer, a Scripting Plugin for Adobe Illustrator
 * http://scriptographer.org/
 *
 * Copyright (c) 2002-2010, Juerg Lehni
 * http://scratchdisk.com/
 *
 * All rights reserved. See LICENSE file for details.
 */

#include "stdHeaders.h"
#include "ScriptographerEngine.h"
#include "com_scriptographer_ai_Color.h"

/*
 * com.scriptographer.ai.Color
 */

/*
 * com.scriptographer.ai.Color nativeConvert(int type)
 */
JNIEXPORT jobject JNICALL Java_com_scriptographer_ai_Color_nativeConvert(
		JNIEnv *env, jobject obj, jint type) {
	try {
		AIColor col;
		AIReal alpha;
		gEngine->convertColor(env, obj, &col, &alpha);
		// type -> conversion tranlsation table:
		static AIColorConversionSpaceValue conversionTypes[] = {
			kAIRGBColorSpace,
			kAICMYKColorSpace,
			kAIGrayColorSpace,
			kAIMonoColorSpace,
			kAIARGBColorSpace,
			kAIACMYKColorSpace,
			kAIAGrayColorSpace,
			kAIMonoColorSpace
		};
		if (gEngine->convertColor(&col, conversionTypes[type], &col, alpha,
				&alpha) != NULL) {
			return gEngine->convertColor(env, &col, alpha);
		}
	} EXCEPTION_CONVERT(env);
	return NULL;
}

/*
 * java.awt.color.ICC_Profile nativeGetProfile(int whichSpace)
 */
JNIEXPORT jobject JNICALL Java_com_scriptographer_ai_Color_nativeGetProfile(
		JNIEnv *env, jclass cls, jint whichSpace) {
	jobject ret = NULL;
	try {
		AIColorProfile profile;
		AIErr err = sAIOverrideColorConversion->GetWSProfile(whichSpace,
				&profile);
		if (err == kNoErr) {
			ASUInt32 size;
			// First get the size...
			if (!sAIOverrideColorConversion->GetProfileData(profile, &size,
					NULL)) {
				// Bow the data:
				char *data = new char[size];
				if (!sAIOverrideColorConversion->GetProfileData(profile, &size,
						data)) {
					// Now use ICC_Profile.getInstance to create a ICC_Profile
					// from it:
					jbyteArray dataArray = env->NewByteArray(size); 
					env->SetByteArrayRegion(dataArray, 0, size, (jbyte *) data); 
					ret = gEngine->callStaticObjectMethod(env,
							gEngine->cls_awt_ICC_Profile,
							gEngine->mid_awt_ICC_Profile_getInstance, dataArray);
				}
				delete data;
			}
			sAIOverrideColorConversion->FreeProfile(profile);
		}
	} EXCEPTION_CONVERT(env);
	return ret;
}

/*
 * void nativeSetGradient(int pointer, int handle, com.scriptographer.ai.Point
 *     origin, double angle, double length, com.scriptographer.ai.Matrix matrix,
 *     double hiliteAngle, double hiliteLength)
 */
JNIEXPORT void JNICALL Java_com_scriptographer_ai_Color_nativeSetGradient(
		JNIEnv *env, jclass cls, jint pointer, jint handle, jobject origin,
		jdouble angle, jdouble length, jobject matrix, jdouble hiliteAngle,
		jdouble hiliteLength) {
	try {
		AIGradientStyle *style = (AIGradientStyle *) pointer;
		style->gradient = (AIGradientHandle) handle;
		// gradientOrigin is in page coordinates, ignoring the documetn rurler,
		// not in artboard coordinates which are relative to it(?)
		gEngine->convertPoint(env, kCurrentCoordinates, origin,
				&style->gradientOrigin);
		style->gradientAngle = angle;
		style->gradientLength = length;
		// TODO: Test if conersion is correct and sync with 
		// ScriptographerEngine::convertColor
		gEngine->convertMatrix(env, kArtboardCoordinates, kCurrentCoordinates,
				matrix, &style->matrix);
		style->hiliteAngle = hiliteAngle;
		style->hiliteLength = hiliteLength;
	} EXCEPTION_CONVERT(env);
}

/*
 * void nativeSetPattern(int pointer, int handle, com.scriptographer.ai.Matrix
 *     matrix)
 */
JNIEXPORT void JNICALL Java_com_scriptographer_ai_Color_nativeSetPattern(
		JNIEnv *env, jclass cls, jint pointer, jint handle, jobject matrix) {
	try {
		AIPatternStyle *style = (AIPatternStyle *) pointer;
		style->pattern = (AIPatternHandle) handle;
		// See the following declaration for more information about these values
		// here:
		// jobject ScriptographerEngine::convertColor(JNIEnv *env,
		//     AIColor *srcCol, AIReal alpha)
		style->shiftDist = 0;
		style->shiftAngle = 0;
		style->scale.h = 100;
		style->scale.v = 100;
		style->rotate = 0;
		style->reflect = false;
		style->reflectAngle = 0;
		style->shearAngle = 0;
		style->shearAxis = 0;
		// TODO: Test if conersion is correct and sync with 
		// ScriptographerEngine::convertColor
		gEngine->convertMatrix(env, kArtboardCoordinates, kCurrentCoordinates,
				matrix, &style->transform);
	} EXCEPTION_CONVERT(env);
}
