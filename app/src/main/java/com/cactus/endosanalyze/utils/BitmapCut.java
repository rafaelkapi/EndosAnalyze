package com.cactus.endosanalyze.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
    /*
       A classe Canvas contém as chamadas "desenhar".
       Para desenhar algo, você precisa de 4 componentes básicos:
       Um bitmap para armazenar os pixels, um Canvas para hospedar as chamadas de desenho (escrever no bitmap),
       um desenho primitivo (por exemplo, Rect, Path, text, Bitmap)
       e um paint (para descrever as cores e estilos do desenho).
 */
import android.graphics.Color;
import android.graphics.Paint;
// A classe Paint contém informações de estilo e cor sobre como desenhar geometrias, texto e bitmaps.
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import com.cactus.endosanalyze.camera.MediaHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
        /*
           Rect contém quatro coordenadas inteiras para um retângulo.
           O retângulo é representado pelas coordenadas de suas 4 arestas (esquerda, superior, direita inferior).
           Esses campos podem ser acessados ​​diretamente. Use largura ()
           e altura () para recuperar a largura e a altura do retângulo.
           Nota: a maioria dos métodos não verifica se as coordenadas estão
           classificadas corretamente (ou seja, esquerda <= direita e superior <= inferior).

         */

public class BitmapCut {

    private static final int CAPTURED_COLOR =400;


    public static Bitmap Circle(Bitmap bitmap) {

        int [] coordCut = circleLocation(bitmap);
        if (coordCut[2]>1 && coordCut[3]>1) {

            int aux = coordCut[1]+coordCut[3]-coordCut[2];

            Bitmap result = Bitmap.createBitmap(bitmap, coordCut[0] , aux ,
                    coordCut[2] ,
                    coordCut[2] , null, true);

            Bitmap output = Bitmap.createBitmap(result.getWidth(),
                    result.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);

            final int color = Color.BLUE;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());

            paint.setAntiAlias(true);
            // Auxiliar para setFlags (), definindo ou limpando o bit ANTI_ALIAS_FLAG O anti-aliasing suaviza as bordas do que está sendo desenhado, mas não tem impacto no interior da forma.
//        canvas.drawARGB(0, 255, 0, 0); // preenche o canvas (tela) com as cores especificadas, a: alpha, r:red ...
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(result.getWidth()/2, result.getHeight()/2, // cx, cy - coordenadas de centro
                    result.getWidth()/2 , paint);
        /*
           Desenhe o círculo especificado usando a tinta especificada.
           Se o raio for <= 0, nada será desenhado.
           O círculo será preenchido ou emoldurado com base no estilo da pintura.
         */
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /*
            Defina ou desmarque o objeto do modo de transferência.
            Um modo de transferência define como os pixels de origem (gerados por um comando de desenho)
            são compostos com os pixels de destino (conteúdo do destino de renderização).
         */
            canvas.drawBitmap(result, rect, rect, paint);
            return output;
        }
        return bitmap;
    }

    private static int [] circleLocation(Bitmap bitmap)  {

        Boolean breakForHor = false;
        Boolean breakForVer = false;
        Boolean breakForHor2 = false;
        Boolean breakForVer2 = false;

        int  coordCut [] = {0,0,0,0}; // {x,y,width,height}

        Bitmap output = Bitmap.createBitmap(bitmap);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();

        paint.setColor(Color.GREEN);

        int[] bitWidht = new int[output.getWidth()];
        int[] bitHeight = new int[output.getHeight()];

        int prevPix = bitmap.getPixel(0, 0);

        for (int y = 0; y < bitHeight.length ; y++) {
            if(breakForHor) break;

            for (int x = 0; x < bitWidht.length ; x++) {
                int colorPix = bitmap.getPixel(x, y);
                int tone = Color.red(colorPix) + Color.green(colorPix) + Color.blue(colorPix);
                if( tone>=CAPTURED_COLOR ) {
                    coordCut[1] = y; // initial coordinate height
                    Log.d("teste", " Horizontal  X: " +x+ "   " + "Y: " +y);
                    breakForHor = true;
                    break;
                }
            }
        }

        for (int x = 0; x < bitWidht.length ; x++) {
            if(breakForVer) break;

            for (int y = 0; y < bitHeight.length ; y++) {
                int colorPix = bitmap.getPixel(x, y);
//                int prevPix = bitmap.getPixel(x, y);
//                if(y==0) colorPix = prevPix;
//
//                if( Color.red(colorPix) >(prevPix+10) && Color.green(colorPix) > (prevPix+10) && Color.blue(colorPix) >(prevPix+10)) {
                int tone = Color.red(colorPix) + Color.green(colorPix) + Color.blue(colorPix);
                if( tone>=CAPTURED_COLOR ){

                    coordCut[0]= x; // initial coordinate widht
                    Log.d("teste", " Vertical  X: " +x+ "   " + "Y: " +y);
                    breakForVer = true;
                    break;

                }
            }
        }


        for (int x =  bitWidht.length-1; x > 0 ; x--) {
            if(breakForVer2) break;

            for (int y = 0; y < bitHeight.length ; y++) {
                int colorPix = bitmap.getPixel(x, y);
                int tone = Color.red(colorPix) + Color.green(colorPix) + Color.blue(colorPix);
                if( tone>=CAPTURED_COLOR ){
                    canvas.drawLine(x, 0, x, bitmap.getHeight(), paint);
                    coordCut[2] = x - coordCut[0];
                    Log.d("teste", " Vertical  X: " +x+ "   " + "Y: " +y);
                    breakForVer2 = true;
                    break;
                }
            }
        }


        for (int y =  bitHeight.length-1; y > 0 ; y--) {
            if(breakForHor2) break;

            for (int x =  bitWidht.length-1; x > 0 ; x--) {
                int colorPix = bitmap.getPixel(x, y);
                int tone = Color.red(colorPix) + Color.green(colorPix) + Color.blue(colorPix);
                if( tone>=CAPTURED_COLOR ){
                    canvas.drawLine(0, y, bitmap.getWidth(), y, paint);
                    coordCut[3] = y - coordCut[1];
                    Log.d("teste", " Horizontal  X: " +x+ "   " + "Y: " +y);
                    breakForHor2 = true;
                    break;

                }
            }
        }

        canvas.drawLine(coordCut[0], 0, coordCut[0], bitmap.getHeight(), paint);
        canvas.drawLine(0, coordCut[1], bitmap.getWidth(), coordCut[1], paint);

        File outputMediaFile = MediaHelper.createCameraFile(true);
        try {
            FileOutputStream fos = new FileOutputStream(outputMediaFile); // Cria a alocação pro registro na memória
            output.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return coordCut;
    }

}
