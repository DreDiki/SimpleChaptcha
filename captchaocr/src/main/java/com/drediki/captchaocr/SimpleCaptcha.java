package com.drediki.captchaocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.IOException;

/**
 * Created by DreDiki on 2017/2/20.
 * Simple Hard Code Ocr For SDU Weihai
 * http://portal.wh.sdu.edu.cn/casServer/captcha.htm
 * JPEG 80*30
 */

public class SimpleCaptcha {
    private static final String LOG_TAG = "SimpleCaptcha";
    //    private Context context;
    private int[][] samples;

    public SimpleCaptcha(Context context) {
//        this.context = context;
        samples = new int[10][300];
        try {
            for (int i = 0; i < 10; i++) {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(i + ".png"));
                bitmap.getPixels(samples[i], 0, 15, 0, 0, 15, 20);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String captchaDecode(Bitmap bitmap){
        StringBuilder stringBuilder = new StringBuilder();
        Bitmap simplified = simplifyBitmap(bitmap);
        int[] singleChar = new int[300];
        simplified.getPixels(singleChar,0, 15, 0, 0, 15, 20);
        stringBuilder.append(test(singleChar));
        simplified.getPixels(singleChar,0, 15, 15, 0, 15, 20);
        stringBuilder.append(test(singleChar));
        simplified.getPixels(singleChar,0, 15, 30, 0, 15, 20);
        stringBuilder.append(test(singleChar));
        simplified.getPixels(singleChar,0, 15, 45, 0, 15, 20);
        stringBuilder.append(test(singleChar));
        return stringBuilder.toString();
    }
    private int test(int[] singleChar) {
        int[] count = new int[10];
        for (int j = 0; j < 10; j++) {//cycle less outside
            for (int i = 0; i < 300; i++) {
                if (singleChar[i] == samples[j][i]) count[j]++;
            }
        }
        int max =0,maxC = count[0];
        for(int j=1;j<10;j++){
            if(count[j]>maxC){
                maxC = count[j];
                max = j;
            }
        }
        return max;
    }

    public static Bitmap simplifyBitmap(Bitmap source) {
        int threshold = 145;//get this by ps manually
        int width = source.getWidth();
        int height = source.getHeight();
//        Log.i(LOG_TAG, width + ">" + height);
        if (width != 80 || height != 30) return null;
        int[] pixels = new int[1200];
        source.getPixels(pixels, 0, 60, 9, 4, 60, 20);
        int tempGrayColor;
        int tempCellColor;
        for (int i = 0; i < 1200; i++) {
            tempCellColor = pixels[i];
            tempGrayColor = (int) (0.11 * Color.red(tempCellColor) + 0.59 * Color.green(tempCellColor) + 0.3 * Color.blue(tempCellColor));
            if (tempGrayColor > threshold) tempGrayColor = 255;
            else tempGrayColor = 0;
//                Log.i(LOG_TAG,tempGrayColor+".");
            pixels[i] = Color.argb(255, tempGrayColor, tempGrayColor, tempGrayColor);
        }
        Bitmap result = Bitmap.createBitmap(60, 20, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, 60, 0, 0, 60, 20);
        return result;
    }
}
