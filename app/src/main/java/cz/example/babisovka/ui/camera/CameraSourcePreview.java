/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file was modified for purposes of the application Babisovka.
 * Tento soubor byl modifikovany pro ucely aplikace Babisovka.
 */

package cz.example.babisovka.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.RequiresPermission;

import com.google.android.gms.common.images.Size;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    final private Context mContext;
    final private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;

    private GraphicOverlay<?> mOverlay;

    private int oknoSirka = 300;
    private int oknoVyska = 500;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource) throws IOException, SecurityException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
            SetPreviewSize();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource, GraphicOverlay<?> overlay) throws IOException, SecurityException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null) {

            // workaround, oprava zhasinani blesku, aby se zhasl vzdy
            // bez toho se zhasne s kamerou a zapamatuje si jestli ma svitit nebo ne a pripadne prenastaveni ignoruje
            if (mCameraSource.getFlashMode()!=null)
                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startIfReady() throws IOException, SecurityException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
                SetPreviewSize();
            } catch (SecurityException se) {
                Log.e(TAG,"Do not have permission to start the camera", se);
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        oknoSirka=right-left;
        oknoVyska= bottom-top;
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }

    // atributy - umisteni komponenty CameraSourcePreview (levy horni roh a pravy spodni)
    private void SetPreviewSize(){
        int width = 320;    // nastrel rozliseni kamery, dokud se nepodari nacist realna hodnota
        int height = 240;   // nastrel rozliseni kamery, dokud se nepodari nacist realna hodnota
        if (mCameraSource != null) {

            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            //noinspection SuspiciousNameCombination
            width = height;
            height = tmp;
        }

        // Computes height and width for potentially doing fit width.
        int childWidth;// = oknoSirka;
        int childHeight = (int)(((float) oknoSirka / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > oknoVyska) {
            childHeight = (int)(((float) oknoSirka / (float) width) * height);
            childWidth = oknoSirka;
        }
        else
        {
            childHeight = oknoVyska;
            childWidth = (int)(((float) oknoVyska / (float) height) * width);
        }
        // o kolik potrebujeme posunout obraz, aby byl vyrez vyskove na stred proti kamere
        // sirka je stejna jako sirka komponenty CameraSourcePreview
        int childXOffset = (childWidth - oknoSirka)/2;
        int childYOffset = (childHeight - oknoVyska)/2;

        // umisteni obrazu kamery do CameraSourcePreview
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(-childXOffset, -childYOffset, childWidth-childXOffset, childHeight-childYOffset);
        }
    }
}