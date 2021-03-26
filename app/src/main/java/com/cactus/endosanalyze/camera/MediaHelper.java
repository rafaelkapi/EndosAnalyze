package com.cactus.endosanalyze.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import androidx.exifinterface.media.ExifInterface;
import android.util.Log;

import com.cactus.endosanalyze.utils.BitmapCut;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MediaHelper {

  private static Context context;

  public static Uri saveCameraFile( Context activity, byte[] data) {
    context = activity;
    File pictureFile = createCameraFile(true);

    if (pictureFile == null) {
      Log.d("Teste", "Error createing media file, check storage permission");
      return null;
    }

    File outputMediaFile = null;
    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      /*
         Usado para alocar um espaço pra salvar na memória um arquivo.
         Destina-se a gravar streams de bytes raw(brutos), como dados de imagem.
          Para escrever streams de caracteres, considere usar FileWriter.
       */

      Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

      /*
          data - byte: matriz de bytes de dados de imagem compactados
          offset - int: deslocamento em imageData para onde o decodificador deve começar a analisar.
       */

      ExifInterface exif = new ExifInterface(pictureFile.toString());
      /*
          Usamos pra saber informações da imagem, nesse caso para saber a orientação em termos de linha e col
          Esta é uma classe para ler e gravar tags Exif em vários formatos de arquivo de imagem.
       */

      Log.d("Teste", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
      if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
        realImage = rotate(realImage, 90);
      } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
        realImage = rotate(realImage, 270);
      } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
        realImage = rotate(realImage, 180);
      } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")) {
        realImage = rotate(realImage, 90);
      }

      realImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

      fos.close();

      Matrix matrix = new Matrix();
      outputMediaFile = createCameraFile( false);

      if (outputMediaFile == null) {
        Log.d("Teste", "Error creating media file, check storage permissions");
        return null;

      }

      Log.i("Teste", realImage.getWidth() + " x " + realImage.getHeight());
//      Bitmap result = Bitmap.createBitmap(realImage, 0, 0,
//              realImage.getWidth(),
//              (int)(realImage.getHeight()*1), matrix, true);

      fos = new FileOutputStream(outputMediaFile); // Cria a alocação pro registro na memória
      Bitmap bitmapCircle = BitmapCut.Circle(realImage);
      bitmapCircle.compress(Bitmap.CompressFormat.PNG, 100, fos);
      fos.close();






    } catch (FileNotFoundException e) {
      Log.e("Teste", e.getLocalizedMessage(), e);
    } catch (IOException e) {
      Log.e("Teste", e.getLocalizedMessage(), e);
    }

    return Uri.fromFile(outputMediaFile);
  }

  private static Bitmap rotate(Bitmap bitmap, int degree) {
    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    Matrix matrix = new Matrix();
    matrix.setRotate(degree);

    return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
  }

  public static File createCameraFile( boolean temp) {
    if (context== null) return null;

    File mediaStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (mediaStorageDir != null && !mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("Teste", "failed to create directory");
        return null;
      }
    }

    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    return new File(mediaStorageDir.getPath() + File.separator + (temp ? "TEMP_" : "IMG_")
            + timestamp + ".png");
  }






}

