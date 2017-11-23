/*
 * Copyright (C) 2011 Google Inc.
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

package com.googlecode.eyesfree.textdetect;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Pixa;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author alanv@google.com (Alan Viverette)
 */
@SuppressWarnings("unused")
public class HydrogenTextDetector {
    private final long mNative;
    private final String TAG = "HydrogenTextDetector";

    static {
        System.loadLibrary("jpgt");
        System.loadLibrary("pngt");
        System.loadLibrary("lept");
        System.loadLibrary("hydrogen");
    }

    private Parameters mParams;

    public HydrogenTextDetector() {
        mNative = nativeConstructor();

        mParams = new Parameters();
        setParameters(mParams);
    }

    public void setSize(int width, int height) {
        // TODO(alanv): Set up native buffers
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            nativeDestructor(mNative);
        } finally {
            super.finalize();
        }
    }

    public void setParameters(Parameters params) {
        mParams = params;

        nativeSetParameters(mNative, mParams);
    }

    public Parameters getParameters() {
        return mParams;
    }

    public static Parameters getNewParameters(){
        return new Parameters();
    }

    public Pixa getTextAreas() {
        long nativePixa = nativeGetTextAreas(mNative);

        if (nativePixa == 0) {
            return null;
        }

        int width = nativeGetSourceWidth(mNative);
        int height = nativeGetSourceHeight(mNative);

        return new Pixa(nativePixa, width, height);
    }

    public float getSkewAngle() {
        return nativeGetSkewAngle(mNative);
    }

    public float[] getTextConfs() {
        return nativeGetTextConfs(mNative);
    }

    public Pix getSourceImage() {
        long nativePix = nativeGetSourceImage(mNative);

        if (nativePix == 0) {
            return null;
        }

        return new Pix(nativePix);
    }

    /**
     * Sets the text detection source image to be a clone of the supplied source
     * image. The supplied image may be recycled after calling this method.
     *
     * @param pixs The source image on which to perform text detection.
     */
    public void setSourceImage(Pix pixs) {
        nativeSetSourceImage(mNative, pixs.getNativePix());
    }

    public void detectText() {
        if(mParams.debug){
            Log.d(TAG, mParams.toString());
        }
        nativeDetectText(mNative);
    }

    public void clear() {
        nativeClear(mNative);
    }

    // ******************
    // * PUBLIC CLASSES *
    // ******************

    public static class Parameters implements Parcelable {

        public boolean debug;

        public String out_dir;

        // Edge-based thresholding
        public int edge_tile_x;

        public int edge_tile_y;

        public int edge_thresh;

        public int edge_avg_thresh;

        // Skew angle correction
        public boolean skew_enabled;

        public float skew_min_angle;

        public float skew_sweep_range;

        public float skew_sweep_delta;

        public int skew_sweep_reduction;

        public int skew_search_reduction;

        public float skew_search_min_delta;

        // Singleton filter
        public float single_min_aspect;

        public float single_max_aspect;

        public int single_min_area;

        public float single_min_density;

        // Quick pair filter
        public float pair_h_ratio;

        public float pair_d_ratio;

        public float pair_h_dist_ratio;

        public float pair_v_dist_ratio;

        public float pair_h_shared;

        // Cluster pair filter
        public int cluster_width_spacing;

        public float cluster_shared_edge;

        public float cluster_h_ratio;

        public int cluster_min_height;

        // Finalized cluster filter
        public int cluster_min_blobs;

        public float cluster_min_aspect;

        public float cluster_min_fdr;

        public int cluster_min_edge;

        public int cluster_min_edge_avg;

        public Parameters() {
            debug = false;
            out_dir = Environment.getExternalStorageDirectory().toString();

            // Edge-based thresholding
            edge_tile_x = 32;
            edge_tile_y = 64;
            edge_thresh = 64;
            edge_avg_thresh = 4;

            // Skew angle correction
            skew_enabled = true;
            skew_min_angle = 1.0f;
            skew_sweep_range = 30.0f;
            skew_sweep_delta = 5.0f;
            skew_sweep_reduction = 8;
            skew_search_reduction = 4;
            skew_search_min_delta = 0.01f;

            // Singleton filter
            single_min_aspect = 0.1f;
            single_max_aspect = 4.0f;
            single_min_area = 4;
            single_min_density = 0.2f;

            // Quick pair filter
            pair_h_ratio = 1.0f;
            pair_d_ratio = 1.5f;
            pair_h_dist_ratio = 2.0f;
            pair_v_dist_ratio = 0.25f;
            pair_h_shared = 0.25f;

            // Cluster pair filter
            cluster_width_spacing = 2;
            cluster_shared_edge = 0.5f;
            cluster_h_ratio = 1.0f;
            cluster_min_height = 5;

            // Finalized cluster filter
            cluster_min_blobs = 5;
            cluster_min_aspect = 2;
            cluster_min_fdr = 2.5f;
            cluster_min_edge = 32;
            cluster_min_edge_avg = 1;
        }

        /*
        *   Clone from other instance
        * */
        public Parameters(Parameters toClone) {
            debug = toClone.debug;
            out_dir = toClone.out_dir;

            // Edge-based thresholding
            edge_tile_x = toClone.edge_tile_x;
            edge_tile_y = toClone. edge_tile_y;
            edge_thresh = toClone.edge_thresh;
            edge_avg_thresh = toClone.edge_avg_thresh;

            // Skew angle correction
            skew_enabled = toClone.skew_enabled = true;
            skew_min_angle = toClone.skew_min_angle;
            skew_sweep_range = toClone.skew_sweep_range;
            skew_sweep_delta = toClone.skew_sweep_delta;
            skew_sweep_reduction = toClone.skew_sweep_reduction;
            skew_search_reduction = toClone.skew_search_reduction;
            skew_search_min_delta = toClone.skew_search_min_delta;

            // Singleton filter
            single_min_aspect = toClone.single_min_aspect;
            single_max_aspect = toClone.single_max_aspect;
            single_min_area = toClone.single_min_area;
            single_min_density = toClone.single_min_density;

            // Quick pair filter
            pair_h_ratio = toClone.pair_h_ratio;
            pair_d_ratio = toClone.pair_d_ratio;
            pair_h_dist_ratio = toClone.pair_h_dist_ratio;
            pair_v_dist_ratio = toClone.pair_v_dist_ratio;
            pair_h_shared = toClone.pair_h_shared;

            // Cluster pair filter
            cluster_width_spacing = toClone.cluster_width_spacing;
            cluster_shared_edge = toClone.cluster_shared_edge;
            cluster_h_ratio = toClone.cluster_h_ratio;
            cluster_min_height = toClone.cluster_min_height;

            // Finalized cluster filter
            cluster_min_blobs = toClone.cluster_min_blobs;
            cluster_min_aspect = toClone.cluster_min_aspect;
            cluster_min_fdr = toClone.cluster_min_fdr;
            cluster_min_edge = toClone.cluster_min_edge;
            cluster_min_edge_avg = toClone.cluster_min_edge_avg;
        }

        private Parameters(Parcel src) {
            readFromParcel(src);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameters:")
                    .append('\n' + "debug: " + debug)
                    .append('\n' + "out_dir: " + out_dir)
                    .append('\n' + "edge_tile_x: " + edge_tile_x)
                    .append('\n' + "edge_tile_y: " + edge_tile_y)
                    .append('\n' + "edge_thresh: " + edge_thresh)
                    .append('\n' + "edge_avg_thresh: " + edge_avg_thresh)

                    .append('\n' + "skew_enabled: " + skew_enabled)
                    .append('\n' + "skew_min_angle: " + skew_min_angle)
                    .append('\n' + "skew_sweep_range: " + skew_sweep_range)
                    .append('\n' + "skew_sweep_delta: " + skew_sweep_delta)
                    .append('\n' + "skew_sweep_reduction: " + skew_sweep_reduction)
                    .append('\n' + "skew_search_reduction: " + skew_search_reduction)
                    .append('\n' + "skew_search_min_delta: " + skew_search_min_delta)

                    .append('\n' + "single_min_aspect: " + single_min_aspect)
                    .append('\n' + "single_max_aspect: " + single_max_aspect)
                    .append('\n' + "single_min_area: " + single_min_area)
                    .append('\n' + "single_min_density: " + single_min_density)

                    .append('\n' + "pair_h_ratio: " + pair_h_ratio)
                    .append('\n' + "pair_d_ratio: " + pair_d_ratio)
                    .append('\n' + "pair_h_dist_ratio: " + pair_h_dist_ratio)
                    .append('\n' + "pair_v_dist_ratio: " + pair_v_dist_ratio)
                    .append('\n' + "pair_h_shared: " + pair_h_shared)

                    .append('\n' + "cluster_width_spacing: " + cluster_width_spacing)
                    .append('\n' + "cluster_shared_edge: " + cluster_shared_edge)
                    .append('\n' + "cluster_h_ratio: " + cluster_h_ratio)
                    .append('\n' + "cluster_min_height: " + cluster_min_height)

                    .append('\n' + "cluster_min_blobs: " + cluster_min_blobs)
                    .append('\n' + "cluster_min_aspect: " + cluster_min_aspect)
                    .append('\n' + "cluster_min_fdr: " + cluster_min_fdr)
                    .append('\n' + "cluster_min_edge: " + cluster_min_edge)
                    .append('\n' + "cluster_min_edge_avg: " + cluster_min_edge_avg);

            return sb.toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            boolean[] bA = new boolean[1];
            bA[0] = debug;
            dest.writeBooleanArray(bA);
            dest.writeString(out_dir);

            // Edge-based thresholding
            dest.writeInt(edge_tile_x);
            dest.writeInt(edge_tile_y);
            dest.writeInt(edge_thresh);
            dest.writeInt(edge_avg_thresh);

            // Skew angle correction
            boolean[] sA = new boolean[1];
            sA[0] = skew_enabled;
            dest.writeBooleanArray(sA);
            dest.writeFloat(skew_min_angle);
            dest.writeFloat(skew_sweep_range);
            dest.writeFloat(skew_sweep_delta);
            dest.writeInt(skew_sweep_reduction);
            dest.writeInt(skew_search_reduction);
            dest.writeFloat(skew_search_min_delta);

            // Singleton filter
            dest.writeFloat(single_min_aspect);
            dest.writeFloat(single_max_aspect);
            dest.writeInt(single_min_area);
            dest.writeFloat(single_min_density);

            // Quick pair filter
            dest.writeFloat(pair_h_ratio);
            dest.writeFloat(pair_d_ratio);
            dest.writeFloat(pair_h_dist_ratio);
            dest.writeFloat(pair_v_dist_ratio);
            dest.writeFloat(pair_h_shared);

            // Cluster pair filter
            dest.writeInt(cluster_width_spacing);
            dest.writeFloat(cluster_shared_edge);
            dest.writeFloat(cluster_h_ratio);
            dest.writeInt(cluster_min_height);

            // Finalized cluster filter
            dest.writeInt(cluster_min_blobs);
            dest.writeFloat(cluster_min_aspect);
            dest.writeFloat(cluster_min_fdr);
            dest.writeInt(cluster_min_edge);
            dest.writeInt(cluster_min_edge_avg);
//            Field[] fields = HydrogenTextDetector.Parameters.class.getDeclaredFields();
//            for(Field f : fields){
//                try {
//                    Type t = f.getGenericType();
//                    if(t.equals(boolean.class)){
//                        boolean[] bA = new boolean[0];
//                        bA[0] = ((Boolean)f.get(this));
//                        dest.writeBooleanArray(bA);
//                    }else if(t.equals(int.class)){
//                        dest.writeInt((int)f.get(this));
//                    }else if(t.equals(float.class)){
//                        dest.writeFloat((float)f.get(this));
//                    }else if(t.equals(String.class)){
//                        dest.writeString((String)f.get(this));
//                    }else{
//                        Log.w(TAG, "Setting params exception " + f.getName());
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (IllegalArgumentException iae){
//                    iae.printStackTrace();
//                }
//            }
        }

        private void readFromParcel(Parcel src) {
            boolean[] dA = new boolean[1];
            src.readBooleanArray(dA);
            debug = dA[0];
            out_dir = src.readString();

            // Edge-based thresholding
            edge_tile_x = src.readInt();
            edge_tile_y = src.readInt();
            edge_thresh = src.readInt();
            edge_avg_thresh = src.readInt();

            // Skew angle correction
            boolean[] sA = new boolean[1];
            src.readBooleanArray(sA);
            skew_enabled = sA[0];
            skew_min_angle = src.readFloat();
            skew_sweep_range = src.readFloat();
            skew_sweep_delta = src.readFloat();
            skew_sweep_reduction = src.readInt();
            skew_search_reduction = src.readInt();
            skew_search_min_delta = src.readFloat();

            // Singleton filter
            single_min_aspect = src.readFloat();
            single_max_aspect = src.readFloat();
            single_min_area = src.readInt();
            single_min_density = src.readFloat();

            // Quick pair filter
            pair_h_ratio = src.readFloat();
            pair_d_ratio = src.readFloat();
            pair_h_dist_ratio = src.readFloat();
            pair_v_dist_ratio = src.readFloat();
            pair_h_shared = src.readFloat();

            // Cluster pair filter
            cluster_width_spacing = src.readInt();
            cluster_shared_edge = src.readFloat();
            cluster_h_ratio = src.readFloat();
            cluster_min_height = src.readInt();

            // Finalized cluster filter
            cluster_min_blobs = src.readInt();
            cluster_min_aspect = src.readInt();
            cluster_min_fdr = src.readFloat();
            cluster_min_edge = src.readInt();
            cluster_min_edge_avg = src.readInt();
//            Field[] fields = HydrogenTextDetector.Parameters.class.getDeclaredFields();
//            for(Field f : fields){
//                try {
//                    Type t = f.getGenericType();
//                    if(t.equals(boolean.class)){
//                        boolean[] bA = new boolean[0];
//                        bA[0] = ((Boolean)f.get(this));
//                        dest.writeBooleanArray(bA);
//                    }else if(t.equals(int.class)){
//                        dest.writeInt((int)f.get(this));
//                    }else if(t.equals(float.class)){
//                        dest.writeFloat((float)f.get(this));
//                    }else if(t.equals(String.class)){
//                        dest.writeString((String)f.get(this));
//                    }else{
//                        Log.w(TAG, "Setting params exception " + f.getName());
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (IllegalArgumentException iae){
//                    iae.printStackTrace();
//                }
//            }
        }

        public static final Creator<Parameters> CREATOR = new Creator<Parameters>() {
            @Override
            public Parameters createFromParcel(Parcel in) {
                return new Parameters(in);
            }

            @Override
            public Parameters[] newArray(int size) {
                return new Parameters[size];
            }
        };
    }

    // ******************
    // * NATIVE METHODS *
    // ******************

    private static native long nativeConstructor();

    private static native void nativeDestructor(long nativePtr);

    private static native void nativeSetParameters(long nativePtr, Parameters params);

    private static native long nativeGetTextAreas(long nativePtr);

    private static native float nativeGetSkewAngle(long nativePtr);

    private static native int nativeGetSourceWidth(long nativePtr);

    private static native int nativeGetSourceHeight(long nativePtr);

    private static native float[] nativeGetTextConfs(long nativePtr);

    private static native long nativeGetSourceImage(long nativePtr);

    private static native void nativeSetSourceImage(long nativePtr, long nativePix);

    private static native void nativeDetectText(long nativePtr);

    private static native void nativeClear(long nativePtr);
}
