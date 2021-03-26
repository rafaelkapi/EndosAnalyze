package com.cactus.endosanalyze.histogram;

import android.graphics.Bitmap;
import android.graphics.Color;

public class HistogramCalculationUtils
{
        public static int[] getGrayBins(Bitmap bitmap1){
            int grayBins[]=new int[256];
            int[] values=new int[bitmap1.getWidth()*bitmap1.getHeight()];
            bitmap1.getPixels(values,0,bitmap1.getWidth(),0,0,bitmap1.getWidth(),bitmap1.getHeight());
            /* pixels = values - int: A matriz para receber as cores do bitmap
               offset - int: O primeiro índice a ser escrito em pixels []
               stride - int: O número de entradas em pixels [] para pular entre as linhas (deve ser> = largura do bitmap). Pode ser negativo.
               x - int: A coordenada x do primeiro pixel a ser lida do bitmap
               y - int: A coordenada y do primeiro pixel a ser lida do bitmap
               width - int: O número de pixels para ler em cada linha
               height - int: O número de linhas para ler             */


            for (int value:values){
                grayBins[roundToGray(value)]++;
            }
            return grayBins;
        }

        public static int[] getComulBins(Bitmap bitmap1){
            int comulBins[]=new int[256];
            int[] grayBins=getGrayBins(bitmap1);
            comulBins[0] =grayBins[0];
            for (int i=1;i<256;i++){
                comulBins[i]=grayBins[i]+comulBins[i-1];
            }
            return comulBins;
        }
        public static int[] getPondrBins(Bitmap bitmap1){
            int width=bitmap1.getWidth(),height=bitmap1.getHeight();
            if(height==width){
                int[] pondrBins=new int[256];
                int[] values=new int[bitmap1.getWidth()*bitmap1.getHeight()];
                bitmap1.getPixels(values,0,bitmap1.getWidth(),0,0,bitmap1.getWidth(),bitmap1.getHeight());
                for (int i=0;i<height;i++){
                    for (int j=0;j<width;j++){
                        float distX=j<height/2 ?height/2 -j : (j%(height/2));
                        float distY=i<width/2 ? width/2-i:(i%(width/2));
                        double x=distX/(width/2);
                        double y=distY/(height/2);
                        double m= Math.pow(x,2)+ Math.pow(y,2);
                        double dist= Math.sqrt(m);
                        int value=roundToGray(values[i*height+j]);
                        if (dist <= 1/3)
                            pondrBins[value]+= 3;
                        else if (dist <= 1)
                            pondrBins[value]+= 2;
                        else
                            pondrBins[value]++;
                    }
                }

                return pondrBins;

            }
            return new int[256];
            }
        private static int roundToGray(int pxl){
            double lum= 0.3* Color.red(pxl)+0.59* Color.green(pxl)+0.11* Color.blue(pxl);
            // Color.red - int: Retorna o componente vermelho no intervalo de 0 a 255 no espaço sRGB padrão.
            return ((int) Math.round(lum)); // Arredonda os valores
        }
}
