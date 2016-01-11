package info.devexchanges.communicatingwithuithread;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

public class HandlerDemoActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private String url = "http://i.imgur.com/h5YqScl.jpg";
    private Bitmap bitmap = null;
    private ImageHandler imageHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_demo);
        imageHandler = new ImageHandler(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDownload = (Button) findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                progressDialog = ProgressDialog.show(HandlerDemoActivity.this, "Loading", "Loading data...");
                new Thread() {
                    public void run() {
                        bitmap = downloadBitmap(url);
                        imageHandler.sendEmptyMessage(0);
                    }
                }.start();
            }
        });
    }

    private static class ImageHandler extends Handler {
        private final WeakReference<HandlerDemoActivity> weakRef;
        private HandlerDemoActivity activity;

        public ImageHandler(HandlerDemoActivity activity) {
            weakRef = new WeakReference<>(activity);
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.imageView.setImageBitmap(activity.bitmap);
            activity.progressDialog.dismiss();
            Toast.makeText(activity, "Download Successful!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap downloadBitmap(String urlString) {
        try {
            java.net.URL url = new java.net.URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.runnable) {
            Intent intent = new Intent(this, HandlerWithRunnableActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.asynctask) {
            Intent intent = new Intent(this, AsyncTaskActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}