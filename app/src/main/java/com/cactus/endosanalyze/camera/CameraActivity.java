package com.cactus.endosanalyze.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.cactus.endosanalyze.main.MainActivity;
import com.cactus.endosanalyze.R;

public class CameraActivity extends AppCompatActivity {

    private Camera camera = null;
    private Uri uri;
    private Button buttonFoto;
    private FrameLayout preview;
    private Context localContext;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        localContext = this;
        buttonFoto = (Button) findViewById(R.id.camera_image_view_picture);
        preview = (FrameLayout) findViewById(R.id.camera_surface);
        progressBar = (ProgressBar) findViewById(R.id.cam_progressbar);

        buttonFoto.setVisibility(View.INVISIBLE);
        camera = getCameraInstance(this);

        if (camera != null) {
            startCam();
            buttonFoto.setVisibility(View.VISIBLE);
        }


        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview.removeAllViews();
                buttonFoto.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                camera.takePicture(null, null, (bytes, camera) -> {
                    uri = MediaHelper.saveCameraFile(localContext, bytes);
                    if (uri != null) {
                        onImageLoad(uri);
                    }
                });
            }
        });
    }


    public void startCam() {

        if (checkCameraHardware(this)) {
            camera = Camera.open();
            // set Camera parameters
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(params);
            // Crie nossa preview de view e defina-a como o conteúdo de nossa atividade
            CameraPreview cameraPreview = new CameraPreview(this, camera);

            preview.addView(cameraPreview);
        }
    }

    public Camera getCameraInstance(Context context) {
        Camera camera = null;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
        };
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 300);
                return null;
            }
            camera = Camera.open();
        } catch (Exception e) {
        }
        return camera;
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    private void onImageLoad(Uri uri) {
        MainActivity.launch(this, uri);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 300) {
            startCam();
            buttonFoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
        }
    }



//    public void chargeUri(byte[] bytes) {
//        uri = MediaHelper.saveCameraFile(localContext, bytes);
//        if (uri != null) {
//            onImageLoad(uri);
//        }
//    }
//
//
//    public static byte[] getBytes(Context ctx) {
//        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        String str = prefs.getString(DATA_NAME, null);
//        if (str != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                return str.getBytes(StandardCharsets.ISO_8859_1);
//            }
//        }
//        return null;
//    }
//
//    public static void setBytes(Context ctx, byte[] bytes) {
//        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor e = prefs.edit();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            e.putString(DATA_NAME, new String(bytes, StandardCharsets.ISO_8859_1));
//        }
//        e.apply();
//    }


}