package com.cactus.endosanalyze.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DefectAnalyze {

    private static List<Integer> pixDefect = new ArrayList<>();
    private static List<Double> listAux = new ArrayList<>();
    private static List<Double> neighborsList = new ArrayList<>();

    public static List<Integer> analyzer(Bitmap bitmap) {

        List<Integer> pixDefect = new ArrayList<>();


        for (int y = 0; y < bitmap.getHeight() ; y++) {

            for (int x = 0; x < bitmap.getWidth() ; x++) {
                int colorPix = bitmap.getPixel(x, y);
                int tone = Color.red(colorPix) + Color.green(colorPix) + Color.blue(colorPix);
                if( tone<=178 && Color.alpha(colorPix)>=10) {
                    pixDefect.add(x);
                    pixDefect.add(y);
                }
            }
        }

        return pixDefect;
    }

    public static boolean crack(List<Integer> pixDefectIn) {

        pixDefect.clear();
        pixDefect.addAll(pixDefectIn);
        List<Double> coordDouble = new ArrayList<>();
        List<Integer> fragments = new ArrayList<>();
        listAux = new ArrayList<>();
        int previus =0;

        for (int i = 0; i < pixDefect.size() ; i=i+2) {

            double alphaX = pixDefect.get(i);
            double alphaY = pixDefect.get(i+1);
            String aux = Integer.toString((int)alphaY);
            double a = Math.pow(10, -aux.length());
            double partY =  alphaY*a;
            double coordDou = alphaX+partY;

            coordDouble.add(coordDou);
        }

        listAux.clear();
        listAux.addAll(coordDouble);

//        Log.d("Teste conv. list", "pixdefect: "+ pixDefect.size()/2+"   listAux: "+listAux.size());

        for (int i = 0; i <listAux.size() ; i++) {

            neighbors(listAux.get(i));
            if(i==0) {
                fragments.add(neighborsList.size());
                previus = neighborsList.size();

            } else {


                fragments.add(neighborsList.size()-previus);
                if(neighborsList.size()-previus > 12) return true;
                previus = neighborsList.size();
            }
            if(neighborsList.size()>=10000) break;
        }

//        testCrack(fragments);

        return false;
    }


    public static void neighbors(Double coordDouble) {

       double coord = coordDouble;
       int x = (int)coord;
       String aux2 = Double.toString(coord);
       int y = Integer.parseInt(aux2.substring(aux2.indexOf(".")).replace(".", "").trim());

           int
                   x3= x-1,  y3= y,
                   x4= x+1,  y4= y;


           Double alphaCoord3 = Double.parseDouble(x3 + "." + y3);
           Double alphaCoord4 = Double.parseDouble(x4 + "." + y4);

           if(listAux.contains(alphaCoord3)){
               neighborsList.add(alphaCoord3);
               listAux.remove(alphaCoord3);
               if(neighborsList.size() >= 10000) return ;
               neighbors(alphaCoord3);
           }

       if(listAux.contains(alphaCoord4)){
           neighborsList.add(alphaCoord4);
           listAux.remove(alphaCoord4);
           if(neighborsList.size() >= 10000) return;
           neighbors(alphaCoord4);
           }


    }

//    private static void testCrack (List<Integer> fragments) {
//
//        Log.d("Teste Crack"," Qntd. vizinhos: "+fragments.size());
//
//        for (int i = 0; i <50 ; i++) {
//
//            Log.d("Teste NeighborsList", "  neighborsList: "+neighborsList.get(i));
//        }
//
//
//        Log.d("Teste fragments", String.valueOf(fragments));
//
//        Log.d("Teste conv. list", "   listAux: "+listAux.size());
//
//    }
}
