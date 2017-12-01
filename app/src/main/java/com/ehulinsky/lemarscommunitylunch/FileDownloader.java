package com.ehulinsky.lemarscommunitylunch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ethan on 11/29/17.
 */

public class FileDownloader extends AsyncTask<String, Void, String> {

    Context context;
    FileDownloadedListener fileDownloadedListener;
    public FileDownloader(Context ctx) {
        context=ctx;
        fileDownloadedListener=(FileDownloadedListener) ctx;
    }
    final static int MEGABYTE=1024*1024;

    @Override
    protected void onPostExecute(String result) {

        fileDownloadedListener.fileDownloaded(result);
    }

    @Override
    protected String doInBackground(String... strings) {
        String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
        String fileName = strings[1];  // -> maven.pdf
        String pdfFile = context.getFilesDir()+"/lunch.pdf";
        FileDownloader.downloadFile(fileUrl, pdfFile);
        return pdfFile;
    }

    public static void downloadFile(String fileUrl, String directory){
        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while((bufferLength = inputStream.read(buffer))>0 ){
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

