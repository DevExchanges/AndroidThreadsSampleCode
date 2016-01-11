package info.devexchanges.communicatingwithuithread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AsyncTaskActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View btnDownload;
    private AsyncTask asyncTask;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asynctask);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnDownload = findViewById(R.id.btn_download);
        progressBar = (ProgressBar) findViewById(R.id.loading);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTask = new DownloadTask().execute();
                Toast.makeText(AsyncTaskActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                btnDownload.setEnabled(false);
            }
        });
    }

    private class DownloadTask extends AsyncTask<Void, String, Bitmap> {
        /**
         * Downloading file in background thread
         */
        @Override
        protected Bitmap doInBackground(Void... params) {
            int count;
            Bitmap bitmap = null;
            try {
                URL url = new URL("http://i.imgur.com/3CBxbOj.jpg");
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream inputStream = url.openStream();
                
                //this input stream used for caculate download percentages
                InputStream inputStream1 = url.openStream();
                //decode Bitmap object from input stream
                bitmap = BitmapFactory.decodeStream(inputStream);

                byte data[] = new byte[1024];
                long total = 0;
                // getting file length
                int lenghtOfFile = connection.getContentLength();

                while ((count = inputStream1.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            progressBar.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            Toast.makeText(AsyncTaskActivity.this, "Download successful!", Toast.LENGTH_SHORT).show();
            btnDownload.setEnabled(true);
        }
    }
}