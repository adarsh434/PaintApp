package com.example.paintapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paintapp.widget.PaintView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    BottomNavigationView bottomNavigationView;
    SeekBar seekBar, seekBar1;
    Button button, button1;
    TextView dp, dp1;
    int previousColor = Color.RED;
    private static String fileName;

    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Paints");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paintView);
        bottomNavigationView = findViewById(R.id.bottomBar);
        seekBar = findViewById(R.id.brushStroke);
        seekBar1 = findViewById(R.id.eraserStroke);
        dp = findViewById(R.id.dp);
        dp1 = findViewById(R.id.dpEraser);
        button = findViewById(R.id.select);
        button1 = findViewById(R.id.selectEraser);

        askPermission();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.brush:
                        seekBar.setVisibility(View.VISIBLE);
                        dp.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                dp.setText(progress +" dp");
                                seekBar.setMax(50);
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) { }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) { }
                        });

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String number = dp.getText().toString();
                                paintView.setStroke(Integer.parseInt( number.substring(0, number.length() - 3)));
                                paintView.setColor(previousColor);

                                seekBar.setVisibility(View.INVISIBLE);
                                dp.setVisibility(View.INVISIBLE);
                                button.setVisibility(View.INVISIBLE);
                            }
                        });

                        break;

                    case R.id.colorLens:
                        new ColorPickerPopup.Builder(getApplicationContext())
                                .initialColor(Color.RED)
                                .enableBrightness(true)
                                .enableAlpha(true)
                                .okTitle("Choose")
                                .cancelTitle("Cancel")
                                .showIndicator(true)
                                .showValue(true)
                                .build()
                                .show(paintView, new ColorPickerPopup.ColorPickerObserver() {
                                    @Override
                                    public void onColorPicked(int color) {
                                        previousColor = color;
                                        paintView.setColor(color);
                                    }
                                });
                        break;

                    case R.id.delete:
                        seekBar1.setVisibility(View.VISIBLE);
                        dp1.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);

                        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                dp1.setText(progress +" dp");
                                seekBar1.setMax(50);


                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) { }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) { }
                        });

                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String number = dp1.getText().toString();
                                paintView.setStroke(Integer.parseInt( number.substring(0, number.length() - 3)));
                                paintView.setColor(Color.WHITE);

                                seekBar1.setVisibility(View.INVISIBLE);
                                dp1.setVisibility(View.INVISIBLE);
                                button1.setVisibility(View.INVISIBLE);
                            }
                        });
                        break;

                    case R.id.save:
                        try {
                            saveImage();
                            Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                        }

                        catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void saveImage() throws IOException {

        if(!path.exists()){
            path.mkdir();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = simpleDateFormat.format(new Date());
        fileName = path + "/" + date + ".png";

        File file = new File(fileName);

        paintView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(paintView.getDrawingCache());
        paintView.setDrawingCacheEnabled(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        byte[] bitmapData = baos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

    }

    public void askPermission(){
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.areAllPermissionsGranted()){
                            Toast.makeText(getApplicationContext(), "Granted!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Allow Storage Permission to save your Painting", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

}