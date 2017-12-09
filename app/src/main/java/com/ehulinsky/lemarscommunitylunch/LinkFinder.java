package com.ehulinsky.lemarscommunitylunch;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ethan on 11/28/17.
 */

public class LinkFinder {
    private final static String TAG="LinkFinder";
    private LinkFinder() {
    }

    public static String findLink(String html,String linkText) throws MenuHasWrongDateException {
        StringBuilder link=new StringBuilder();
        boolean foundStartTag=false;
        int startTagIndex=0;
        int endTagIndex=0;
        StringBuilder anchorTag=new StringBuilder();
        int textIndex=html.indexOf(linkText);
        Calendar today=Calendar.getInstance();
        today.setTime(new Date());
        String month=ThingyThatTurnsAMonthIntoAString.monthToString(today.get(Calendar.MONTH));
        if(textIndex==-1)
        {
            throw new MenuHasWrongDateException("Could not find calendar for "+month);
        }
        for(int i=textIndex-1;i>=0;i--)
        {
            if(html.charAt(i)=='<')
            {
                startTagIndex=i;
                foundStartTag=true;
                break;
            }
        }
        if(!foundStartTag)
        {
            Log.v(TAG,"didnt find start tag");
            return "";
        }

        if(html.charAt(startTagIndex+1)!='a')
        {
            Log.v(TAG,"not a link");
            return "";
        }

        for(int j=startTagIndex+2;j<html.length();j++)
        {
            if(html.charAt(j)=='>')
            {
                endTagIndex=j;
                break;
            }
        }

        for(int k=startTagIndex;k<=endTagIndex;k++)
        {
            anchorTag.append(html.charAt(k));
        }
        boolean foundStringStart=false;
        for(int l=anchorTag.indexOf("href")+4;l<anchorTag.length();l++)
        {
            if(anchorTag.charAt(l)=='"')
            {
                if(!foundStringStart)
                {
                    foundStringStart=true;
                }
                else
                {
                    break;
                }
            }

            else if(foundStringStart)
            {
                link.append(anchorTag.charAt(l));
            }
        }
        return link.toString();
    }
}
