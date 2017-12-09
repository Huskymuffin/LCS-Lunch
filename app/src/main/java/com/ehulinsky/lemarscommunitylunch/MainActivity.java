package com.ehulinsky.lemarscommunitylunch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
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

import org.json.JSONStringer;
import org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends FragmentActivity implements DownloadCompleteListener, FileDownloadedListener, TextExtractedListener{

    ProgressDialog progressDialog;
    final String monthInDatabasePreference="monthInDatabase";
    final String hasAppLoadedPreference="hasAppLoaded";
    final String TAG="MainActivity";
    Calendar today=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        today.setTime(new Date());
        Log.v(TAG,String.valueOf(App.get().getSP().getInt(monthInDatabasePreference,today.get(Calendar.MONTH))));
        Log.v(TAG,String.valueOf(App.get().getSP().getBoolean(hasAppLoadedPreference,false)));
        if(App.get().getSP().getInt(monthInDatabasePreference,today.get(Calendar.MONTH))!=today.get(Calendar.MONTH)||
                !App.get().getSP().getBoolean(hasAppLoadedPreference,false))
        {
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
        else
        {
            updateFromDb();
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
        Log.v(TAG,"startDownload");
        new DownloadTask(this).execute("http://lemarscsd.org");
    }





    @Override
    public void downloadComplete(String str) {
        FileDownloader downloader=new  FileDownloader(this);

        String month=ThingyThatTurnsAMonthIntoAString.monthToString(today.get(Calendar.MONTH));
        String link;
        try {
            link = LinkFinder.findLink(str, month + " Lunch Calendar");
        }
        catch (MenuHasWrongDateException e)
        {
            e.printStackTrace();
            new AlertDialog.Builder(this)
            .setTitle("Error")
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            progressDialog.hide();
            return;
        }

        if (progressDialog != null) {
            progressDialog.setMessage("Downloading PDF...");
        }
        downloader.execute(link,"lunch.pdf");


    }

    @Override
    public void fileDownloaded(String filepath) {
        if (progressDialog != null) {
            progressDialog.setMessage("Extracting text from PDF...");
        }
        TextExtractor extractor=new TextExtractor(this);
        extractor.execute(filepath);

    }

    @Override
    public void textExtracted(final String text) {
        Log.v(TAG,"textExtracted");
        Thread t=new Thread(new Runnable() {
            ExtractedTextParser textParser=new ExtractedTextParser();
            Calendar c=Calendar.getInstance();
            String output;
            ArrayList<Menu> menus=new ArrayList<Menu>();
            Menu menu;

            @Override
            public void run() {
                c.setTime(new Date());
                ArrayList<String> parsed = textParser.parse(text);
                for(String s:parsed)
                {
                    menu=new Menu();
                    menu.setItems(s);
                    menus.add(menu);
                }


                App.get().getDB().menuDao().insertAll(menus);
                App.get().getSP().edit()
                        .putInt(monthInDatabasePreference, today.get(Calendar.MONTH))
                        .putBoolean(hasAppLoadedPreference, true)
                        .apply();

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        progressDialog.hide();
        updateFromDb();

    }

    public int getCurrentWeekday() {
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY&&c.get(Calendar.DAY_OF_MONTH)!=1)
        {
            c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH)-1);
            //make it saturday, because it works for saturday...
            // TODO: make this less stupid
        }
        return c.get(Calendar.DAY_OF_MONTH)-((c.get(Calendar.WEEK_OF_MONTH)-1)*2);


    }

    public void updateFromDb() {
        Log.v(TAG,"updateFromDb");
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                updateUI(new ArrayList<Menu>(App.get().getDB().menuDao().getAll()));
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    public void updateUI(final ArrayList<Menu> menus) {
        runOnUiThread(new Runnable() {
            String output="";
            @Override
            public void run() {
                TextView textView=findViewById(R.id.result);
                /*for(Menu m:menus)
                {
                    output+=m.getId()+"\n"+m.getItems()+"\n\n";
                }*/
                Calendar c=Calendar.getInstance();
                c.setTime(new Date());
                output=menus.get(getCurrentWeekday()-1).getItems();
                textView.setText(output);
            }
        });

    }

}
