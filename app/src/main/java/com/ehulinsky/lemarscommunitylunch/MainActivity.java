package com.ehulinsky.lemarscommunitylunch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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


public class MainActivity extends FragmentActivity implements DownloadCompleteListener, FileDownloadedListener {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (isNetworkConnected()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Getting link to lunch menu...");
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
            progressDialog.setMessage("Extracting text from pdf...");
        }
        ExtractedTextParser textParser=new ExtractedTextParser();
        TextView textView= findViewById(R.id.result);
       // textView.setText(filepath);
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        File file = new File(filepath);
        try {
            PDFParser parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(1);
            String parsedText = pdfStripper.getText(pdDoc);

            textView.setText(textParser.parse(parsedText));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

}
