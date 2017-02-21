package com.drediki.captchatest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.drediki.captchaocr.SimpleCaptcha;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView result;
    private ImageView before;
    private ImageView after;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);
        result = (TextView) findViewById(R.id.result);
        before = (ImageView) findViewById(R.id.source);
        after = (ImageView) findViewById(R.id.after);
        try {
            Random random = new Random();
            Bitmap bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("captcha"+random.nextInt(6)+".jpg"));
            after.setImageBitmap(SimpleCaptcha.simplifyBitmap(bitmap));
            before.setImageBitmap(bitmap);
            SimpleCaptcha simpleCaptcha = new SimpleCaptcha(this);
            result.setText(simpleCaptcha.captchaDecode(bitmap));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello 2017", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onClick(View v) {
        RequireWeb requireWeb = new RequireWeb();
        requireWeb.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class RequireWeb extends AsyncTask<Void,Void,Void>{

        private Bitmap source;
        private Bitmap afterData;
        private String resultData;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection.Response response = Jsoup.connect("http://portal.wh.sdu.edu.cn/casServer/captcha.htm").ignoreContentType(true).execute();
                byte[] bytes = response.bodyAsBytes();
                source = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                afterData = SimpleCaptcha.simplifyBitmap(source);
                SimpleCaptcha simpleCaptcha = new SimpleCaptcha(MainActivity.this);
                resultData = simpleCaptcha.captchaDecode(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            result.setText(resultData);
            before.setImageBitmap(source);
            after.setImageBitmap(afterData);
        }
    }
}
