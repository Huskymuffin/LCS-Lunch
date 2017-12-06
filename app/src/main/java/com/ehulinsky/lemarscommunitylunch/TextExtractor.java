package com.ehulinsky.lemarscommunitylunch;

import android.os.AsyncTask;

import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by ethan on 12/3/17.
 */

public class TextExtractor extends AsyncTask<String,Void,String> {

    TextExtractedListener textExtractedListener;

    public TextExtractor(TextExtractedListener listener)
    {
        textExtractedListener=listener;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... strings) {
        String parsedText="";
        PDFTextStripper pdfStripper;
        PDDocument pdDoc;
        COSDocument cosDoc;
        File file = new File(strings[0]);
        try {
            PDFParser parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(1);
            parsedText=pdfStripper.getText(pdDoc);


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return parsedText;
    }

    @Override
    protected void onPostExecute(String result) {
        textExtractedListener.textExtracted(result);
    }
}
