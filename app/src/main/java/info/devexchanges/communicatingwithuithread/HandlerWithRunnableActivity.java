package info.devexchanges.communicatingwithuithread;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class HandlerWithRunnableActivity extends AppCompatActivity {

    private Handler handler;
    private ImageView imageView;
    private Button btnDownload;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_handler_demo);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnDownload = (Button)findViewById(R.id.btn_download);
        btnDownload.setText("Download image by Runnable");

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(HandlerWithRunnableActivity.this, "Loading", "Loading data...");

                //defining a new Thread with Runnable
                new Thread(new Runnable() {
                    public void run() {
                        bitmap = downloadBitmap("http://i.imgur.com/HR5QMOY.jpg");
                        handler.post(updateUI);
                    }
                }).start();
            }
        });
    }

    //Update download result (bitmap) to ImageView
    final Runnable updateUI = new Runnable() {
        public void run() {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                progressDialog.dismiss();
                Toast.makeText(HandlerWithRunnableActivity.this, "Download Successful!", Toast.LENGTH_SHORT).show();
            }
        }
    };

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
}