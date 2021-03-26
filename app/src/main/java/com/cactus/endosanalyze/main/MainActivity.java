package com.cactus.endosanalyze.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cactus.endosanalyze.R;
import com.cactus.endosanalyze.camera.CameraActivity;
import com.cactus.endosanalyze.histogram.HistogramCalculationUtils;
import com.cactus.endosanalyze.utils.DefectAnalyze;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static void launch(Context context, Uri uri) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("uri", uri);
        context.startActivity(intent);

    }
    private ImageView source_img;
    private TextView text_view;
    private GraphView graph;
    private Bitmap bitmap;
    private Parcelable uri;
    private final List<Integer> pixDefect = new ArrayList<>();
    private String qntdDefect = "0";
    private enum Defect {CRACK, UNDEFINED, NODEFECT}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        source_img = (ImageView) findViewById(R.id.source_img);
        text_view = (TextView) findViewById(R.id.text_view);
        graph = (GraphView) findViewById(R.id.graph);

        uri = getIntent().getExtras().getParcelable("uri");
        source_img.setImageURI((Uri) uri);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);
        }

        switch (checkDefect()){
            case CRACK: percentDamage()
                    .showResultAnalizedText(Defect.CRACK)
                    .showGraph()
                    .highlightDefects();
                break;
            case UNDEFINED: percentDamage()
                    .showResultAnalizedText(Defect.UNDEFINED)
                    .showGraph()
                    .highlightDefects();
                break;
            case NODEFECT: showResultAnalizedText(Defect.NODEFECT)
                    .showGraph()
                    .highlightDefects();
                break;
        }
    }
    

    private Defect checkDefect () {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pixDefect.addAll(DefectAnalyze.analyzer(bitmap));
        if (!pixDefect.isEmpty()) {
            boolean crackDefect = DefectAnalyze.crack(pixDefect);
            if(crackDefect) return Defect.CRACK;
            else return Defect.UNDEFINED;
        }
          else return Defect.NODEFECT;
    }



    private MainActivity percentDamage() {

        DecimalFormat formatador = new DecimalFormat("0.0");
        double aux = (100 * (pixDefect.size() / 2) / (Math.PI*(Math.pow((bitmap.getWidth()/2),2))));
        qntdDefect = formatador.format(aux);

        Log.d("Teste DefectAnalyze", "qntdDefect: " + qntdDefect + " %");
        return this;
    }


    private MainActivity showResultAnalizedText ( Defect defect) {

        switch (defect){
            case CRACK:
                String text = (" Defeito localizado \n" + " Possível Rachadura nas lentes"
                        + " \n Comprometimento da imagem: " + qntdDefect + " %");
                text_view.setText(text);
                break;
            case UNDEFINED:
                text = (" Defeito localizado \n" + " Comprometimento da imagem: " + qntdDefect + " %");
                text_view.setText(text);
                break;
            case NODEFECT:
                text = (" Nenhum defeito foi localizado ");
                text_view.setText(text);
                break;
        }
        return this;
    }


    private MainActivity showGraph() {

        int[] bins = HistogramCalculationUtils.getGrayBins(bitmap);
        initViewGraph();
        insertDataInGraph(bins);
        graph.setVisibility(View.VISIBLE);

        return this;
    }

    private void highlightDefects () {

        float[] drawMatrix = new float[pixDefect.size()];
        for (int i = 0; i < pixDefect.size(); i++) {
            drawMatrix[i] = pixDefect.get(i);
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        canvas.drawBitmap(bitmap, rect, rect, paint);
        paint.setColor(Color.RED);

        canvas.drawPoints(drawMatrix, paint);
        source_img.setImageBitmap(output);

    }

    private void initViewGraph() {

        graph.setVisibility(View.INVISIBLE);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(255);
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling

    }

    private void insertDataInGraph(int[] bins) {

        DataPoint[] dataPoints = new DataPoint[256];
        for (int j = 0; j < 256; j++) {
            dataPoints[j] = new DataPoint(j, bins[j]);
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
        graph.removeAllSeries();
        series.setSpacing(0);
        graph.addSeries(series);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, CameraActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}