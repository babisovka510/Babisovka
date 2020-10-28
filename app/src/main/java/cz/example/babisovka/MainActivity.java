package cz.example.babisovka;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;

import cz.example.babisovka.helpers.DatafilesHelper;
import cz.example.babisovka.helpers.SettingsHelper;
import cz.example.babisovka.ui.camera.CameraSource;
import cz.example.babisovka.ui.camera.CameraSourcePreview;
import cz.example.babisovka.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Date;

// Hlavni trida aplikace. Zde vsechno zacina...
public class MainActivity extends AppCompatActivity implements BarcodeGraphicTracker.BarcodeUpdateListener {

    private static final String TAG = "BarcodeMain";
    private static final int requestSettingsCode = 1;
    private static final int requestDownload = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long zacatekOnCreate = new Date().getTime();


        long zacatekA = new Date().getTime();
        super.onCreate(savedInstanceState);
        long konecA = new Date().getTime();
        long celkem0 = konecA-zacatekA;


        zacatekA = new Date().getTime();
        setContentView(R.layout.activity_main);
        konecA = new Date().getTime();
        long celkem1 = konecA-zacatekA;


        // povolit/zakazat otaceni displeje
        if (SettingsHelper.getSettingInt(this, SettingsHelper.Preference.DISPLAY_ORIENTATION) == 1)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if (SettingsHelper.getSettingInt(this, SettingsHelper.Preference.DISPLAY_ORIENTATION) == 2)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        zacatekA = new Date().getTime();
        // kontrola datovych souboru a pripadne vytvoreni z default
        DatafilesHelper.initDataFiles(this, true);
        konecA = new Date().getTime();
        long celkemE = konecA-zacatekA;

        zacatekA = new Date().getTime();
        // --- inicializace seznamu firem ---
        BarcodeDecoding barcodeDecoding = new BarcodeDecoding();
        barcodeDecoding.init(this, false);

        konecA = new Date().getTime();
        long celkemA = konecA-zacatekA;

        zacatekA = new Date().getTime();
        // --- inicializace kamery + osvetleni ---
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        konecA = new Date().getTime();
        long celkemB = konecA-zacatekA;


        // read parameters from the intent used to launch the activity.
        // boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        zacatekA = new Date().getTime();
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false);
        } else {
            requestCameraPermission();
        }
        konecA = new Date().getTime();
        long celkemC = konecA-zacatekA;


        zacatekA = new Date().getTime();
        // --- inicializace gest na displeji ---
        // gestureDetector = new GestureDetector(this, new MainActivity.CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new MainActivity.ScaleListener());
        konecA = new Date().getTime();
        long celkemD = konecA-zacatekA;

        // iniciace toolbaru
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        /*
        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();
         */

        zacatekA = new Date().getTime();
        // obnoveni dat ve formulari, napr.  po otoceni
        restoreFormData(savedInstanceState);
        konecA = new Date().getTime();
        long celkemF = konecA-zacatekA;

        zacatekA = new Date().getTime();
        // ------- rucni zadavni  -------
        Switch swManualEntry = (Switch)this.findViewById(R.id.swManualEntry);
        final ConstraintLayout layManual = (ConstraintLayout) findViewById(R.id.layManual);
        final ImageView imgStatus = (ImageView) findViewById(R.id.imgStatus);
        final TextView txtBarCode = (TextView)this.findViewById(R.id.txtBarCode);
        imgStatus.bringToFront();
        swManualEntry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked && mPreview != null) {
                        layManual.bringToFront();
                        mPreview.stop();
                    }
                    else if (!isChecked && mPreview != null) {
                        startCameraSource();
                        mPreview.bringToFront();
                        txtBarCode.bringToFront();
                    }
                    imgStatus.bringToFront();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Chyba: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                fadeImage();
            }
        });

        // ------- inicializace blesku -------
        Switch swUseFlash = (Switch)this.findViewById(R.id.swUseFlash);
        swUseFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                if (mCameraSource.getFlashMode() == null)
                {
                    Toast.makeText(getApplicationContext(), "Osvětlení nelze zapnout. Pravděpodobně není v zařízení nainstalované.", Toast.LENGTH_LONG).show();
                    buttonView.setChecked(false);
                }
                else {
                    startCameraSource();
                }
            } catch (Exception e) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Chyba: osvětlení nelze zapnout. Pravděpodobně není v zařízení nainstalované.", Toast.LENGTH_LONG).show();
                    buttonView.setChecked(false);
                }
            }
            fadeImage();
            }
        });
        konecA = new Date().getTime();
        long celkemG = konecA-zacatekA;

        long konecOnCreate = new Date().getTime();
        long celkemOnCreate = konecOnCreate-zacatekOnCreate;

        // Toast.makeText(getApplicationContext(), String.format("Vse %s ms.\n0: %s ms\n1: %s ms\nA: %s ms\nB: %s ms\nC: %s ms\nD: %s ms\nE: %s ms\nF: %s ms", celkemOnCreate, celkem0, celkem1, celkemA, celkemB, celkemC, celkemD, celkemE, celkemF), Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_nastaveni) {
            Intent itnt = new Intent(this, SettingsActivity.class);
            this.startActivityForResult(itnt, requestSettingsCode);
            return true;
        }
        else if (item.getItemId() == R.id.menu_aktualizace) {
            Intent itnt = new Intent(this, DownloadDataActivity.class);
            this.startActivityForResult(itnt, requestDownload);
            return true;
        }
        else if (item.getItemId() == R.id.menu_oaplikaci) {
            about();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    // zachrana hodnot pri otoceni displeje
    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        TextView txtBarCode = (TextView)this.findViewById(R.id.txtBarCode);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        TextView txtProducerLabel = (TextView)this.findViewById(R.id.txtProducerLabel);
        outState.putString("txtProducerLabel", txtProducerLabel.getText().toString());
        TextView txtProducer = (TextView)this.findViewById(R.id.txtProducer);
        outState.putString("txtProducer", txtProducer.getText().toString());
        TextView txtCountry = (TextView)this.findViewById(R.id.txtNote);
        outState.putString("txtCountry", txtCountry.getText().toString());

        ImageView img = (ImageView) this.findViewById(R.id.imgStatus);
        Object imgTag = img.getTag();
        if (imgTag!=null)
            outState.putInt("imgStatus", (int)img.getTag());

        img = (ImageView) this.findViewById(R.id.imgBarcode);
        outState.putBoolean("imgBarcode", img.getAnimation() != null || img.getVisibility() == View.GONE);
    }
    private void restoreFormData(Bundle savedInstanceState){
        if(savedInstanceState!=null)
        {
            TextView txtBarCode = (TextView)this.findViewById(R.id.txtBarCode);
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            TextView txtProducerLabel = (TextView)this.findViewById(R.id.txtProducerLabel);
            txtProducerLabel.setText(savedInstanceState.getString("txtProducerLabel"));
            TextView txtProducer = (TextView)this.findViewById(R.id.txtProducer);
            txtProducer.setText(savedInstanceState.getString("txtProducer"));
            TextView txtCountry = (TextView)this.findViewById(R.id.txtNote);
            txtCountry.setText(savedInstanceState.getString("txtCountry"));

            ImageView img = (ImageView) this.findViewById(R.id.imgStatus);
            int imgTag=savedInstanceState.getInt("imgStatus", 0);
            if(imgTag != 0) {
                img.setImageResource(imgTag);
                img.setTag(imgTag);
            }

            img = (ImageView) this.findViewById(R.id.imgBarcode);
            if (savedInstanceState.getBoolean("imgBarcode"))
                img.setVisibility(View.GONE);
        }
    }

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    // private GestureDetector gestureDetector;

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;


        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.ok, dialogListener)
                .show();
        /*
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
         */
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
       boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = false; // gestureDetector.onTouchEvent(e);  // toto zpusobovalo pad aplikace pri skenovani kodu a zaroven sahnuti na displej

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    @SuppressWarnings("SameParameterValue")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // povolit/zakazat otaceni displeje, pokud se zmenilo nastaveni
        int displayOrientation = SettingsHelper.getSettingInt(this, SettingsHelper.Preference.DISPLAY_ORIENTATION);
        int displayCurrentOrientation = getRequestedOrientation();
        if (displayCurrentOrientation != displayOrientation && displayOrientation == 0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        else if (displayCurrentOrientation != displayOrientation && displayOrientation == 1)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if (displayCurrentOrientation != displayOrientation && displayOrientation == 2)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // spustit kameru
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(true, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        // zjistit, jestli lze kameru spustit
        Switch swManualEntry = (Switch)this.findViewById(R.id.swManualEntry);
        if(swManualEntry.isChecked()) {
            return;
        }
        // neni potreba vypnout blesk?
        Switch swUseFlash = (Switch)this.findViewById(R.id.swUseFlash);
        if (!swUseFlash.isChecked() && mCameraSource !=null && mCameraSource.getFlashMode() != null) {
            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }


        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);

                // neni potreba zaponout blesk?
                if (swUseFlash.isChecked() && mCameraSource.getFlashMode()!=null) {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            Intent data = new Intent();
            data.putExtra(BarcodeObject, best);
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
            return true;
        }
        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    public void about () {
        // vytvorime instanci tridy AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // pripravime si formular
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.activity_main_about, null);

        TextView txtNewName = alertLayout.findViewById(R.id.txtVersion);
        Resources res = getBaseContext().getResources();
        try {
            txtNewName.setText(String.format("Verze: %s",  getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        }
        catch (Exception e){
            String err = e.getMessage();
        }


        builder.setTitle(res.getString(R.string.main_about_title));
        builder.setPositiveButton("OK", null);
        builder.setView(alertLayout);
        builder.create().show();
    }

    public void goToFaceBook(View view) {
        try {
            // FB aplikace
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://groups/3382356618517340"));
            startActivity(intent);
        } catch (Exception e) {
            // FB browser
            Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/3382356618517340"));
            startActivity(intent);
        }
    }

    /*
    // Aktivace Developers Menu
    private int hiddenMenuCounter = 0;
    private long hiddenMenuTime = System.currentTimeMillis();
    public void openHiddenMenu(View view) {
        long timeNow = System.currentTimeMillis();
        if (timeNow - hiddenMenuTime < 1000) {
            if(hiddenMenuCounter > 3) {
                toolBarMenu.findItem(R.id.devMenu0).setVisible(true);
                toolBarMenu.findItem(R.id.devMenu1).setVisible(true);
                toolBarMenu.findItem(R.id.devMenu2).setVisible(true);
                Toast.makeText(this, "Developers menu has been activated.", Toast.LENGTH_SHORT).show();
            }
            hiddenMenuCounter ++;
        }
        else {
            hiddenMenuCounter = 0;
        }
        hiddenMenuTime = timeNow;
    }
    */

    // zneviditelni obrazek ilustracniho caroveho kodu
    private void fadeImage()
    {
        final ImageView img = (ImageView) this.findViewById(R.id.imgBarcode);

        // pouze pokud je zobrazeny
        if (img.getAnimation() != null || img.getVisibility() == View.GONE)
            return;

        Animation fadeOut = new AlphaAnimation(1, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1500);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }

    // udalost spustena tlacitkem po rucnim zadani caroveho kodu
    public void onBarcodeTyped(View view) {

        // schovat virtualni klavesnici
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        
        // najit kod
        EditText txtManualCode = (EditText)findViewById(R.id.txtManualCode);
        Barcode barcode = new Barcode();
        if(txtManualCode.getText().length()>8)
            barcode.format = Barcode.EAN_13;
        else
            barcode.format = Barcode.EAN_8;
        barcode.displayValue=txtManualCode.getText().toString();
        barcode.rawValue=txtManualCode.getText().toString();
        onBarcodeDetected(barcode);
    }

    // udalost volana pri rozpoznani barcode
    @Override
    public void onBarcodeDetected(Barcode barcode) {

        // zneviditelni obrazek ilustracniho caroveho kodu, pokud je viditelny
        fadeImage();

        // cteme pouze kody EAN-8 a EAN-13
        if(barcode.format != Barcode.EAN_8 && barcode.format != Barcode.EAN_13)
            return;

        // upozornit pipnutim nebo zavibrovanim (pouze pokud neni kod zadan rucne)
        if(barcode.isRecognized) {
            doHapticNotification();
        }

        // vypsat kod na displej
        TextView txtBarCode = (TextView)this.findViewById(R.id.txtBarCode);
        setText(txtBarCode, String.format("Kód: %s", barcode.displayValue));

        // vypsat zemi na displej
        BarcodeDecoding decoder = new BarcodeDecoding();

        TextView txtProducerLabel = (TextView) this.findViewById(R.id.txtProducerLabel);
        setText(txtProducerLabel, "Výrobce");

        TextView txtProducer = (TextView) this.findViewById(R.id.txtProducer);
        ResultData producer = decoder.getResultData(this, barcode.displayValue);
        setText(txtProducer, producer.nazev);

        TextView txtNote = (TextView) this.findViewById(R.id.txtNote);
        setText(txtNote, producer.dodatek);

        // zobrazit ikonku
        showIcon(producer.holding);

        /*
        TextView txtCountry = (TextView) this.findViewById(R.id.txtCountry);
        String strCountry = decoder.getResultData(barcode.displayValue).nazev;
        setText(txtCountry, String.format("Původ výrobce: %s", strCountry));
*/

        /*
        // vypsat vyrobce na displej
        TextView txtProducer = (TextView) this.findViewById(R.id.txtProducer);
        if (barcode.displayValue.length()==13) {
            FirmaData producer = decoder.getCompanyData(barcode.displayValue);
            if(producer == null) {
                setText(txtProducer, "Neznámý výrobce");
            }
            else {
                String strAgro = producer.agro.equals("1") ? "AGROFERT, " : "";
                // txtProducer.setText(String.format("Výrobce: %s%s", strAgro, producer.nazev));
                setText(txtProducer, String.format("Výrobce: %s%s", strAgro, producer.nazev));
            }
        }
        else
        {
            setText(txtProducer, "Výrobce nelze určit.");
        }
         */
        // Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
        //do something with barcode data returned

    }

    // zobrazi ikonu holding/neholding/nejiste
    private void showIcon(Companies.Kategorie kategorie){
        ImageView img = (ImageView) findViewById(R.id.imgStatus);
        int ico = R.drawable.nejiste;
        if (kategorie == Companies.Kategorie.HOLDING)
            ico = R.drawable.holding;
        else if (kategorie == Companies.Kategorie.MIMOHOLDING)
            ico = R.drawable.neholding;

        setImg(img, ico);
        // img.setImageResource(ico);
        // img.setTag(ico);
        // img.bringToFront();
    }

    // zapis do UI ve spravnem vlakne (na starsich telefonech primy zapis zlobil)
    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    private void setImg(final ImageView img,final int value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img.setImageResource(value);
                img.setTag(value);
                img.bringToFront();
            }
        });
    }

    // pipne/zavibruje
    private void doHapticNotification() {
        if (SettingsHelper.getSettingBoolean(this, SettingsHelper.Preference.BARCODE_BEEP)) {
            MediaPlayer prehravac = MediaPlayer.create(this, R.raw.beep2);
            prehravac.start();
        }
        if (SettingsHelper.getSettingBoolean(this, SettingsHelper.Preference.BARCODE_VIBRATE)) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(60);
            }
        }
    }
}
