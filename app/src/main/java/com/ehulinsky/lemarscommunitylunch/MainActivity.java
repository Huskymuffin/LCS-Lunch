package com.ehulinsky.lemarscommunitylunch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends FragmentActivity implements DownloadCompleteListener, FileDownloadedListener, TextExtractedListener {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (isNetworkConnected()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Finding link to lunch menu...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            startDownload();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setMessage("It looks like your internet connection is off. Please turn it " +
                            "on and try again.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }



    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }


    public void startDownload() {
        new DownloadTask(this).execute("http://lemarscsd.org");
    }

    public void fileDownloaded(String filepath) {
        if (progressDialog != null) {
            progressDialog.setMessage("Extracting text from PDF...");
        }
        TextExtractor extractor=new TextExtractor(this);
        extractor.execute(filepath);

    }

    @Override
    public void textExtracted(String text) {
        ExtractedTextParser textParser=new ExtractedTextParser();
        TextView textView= findViewById(R.id.result);
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        textView.setText(textParser.parse(text).get(getCurrentWeekday()-1));

        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    public void downloadComplete(String str) {
        FileDownloader downloader=new  FileDownloader(this);
        if (progressDialog != null) {
            progressDialog.setMessage("Downloading PDF...");
        }
        downloader.execute(LinkFinder.findLink(str,"Lunch Calendar"),"lunch.pdf");


    }

    public int getCurrentWeekday() {
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH,29);
        int day=c.get(Calendar.DAY_OF_MONTH);
        if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
        {
            day+=1;
        }
        else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)
        {
            day+=2;
        }

        return day-((c.get(Calendar.WEEK_OF_MONTH)-1)*2);


    }

}
