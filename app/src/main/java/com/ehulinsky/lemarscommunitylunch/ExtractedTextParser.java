package com.ehulinsky.lemarscommunitylunch;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ethan on 11/30/17.
 */

public class ExtractedTextParser {
    public static final String TAG="ExtractedTextParser";
    public ExtractedTextParser() {

    }

    public String parse(String str) {
        ArrayList<String> meals = new ArrayList<>();
        Date d = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(d);
        String entree="HS Entrée";
        String[] replace={monthToString(c.get(Calendar.MONTH)).toUpperCase()+" "+c.get(Calendar.YEAR),
                "LE MARS COMMUNITY SCHOOLS",
                "“This is an Equal Opportunity Provider”",
                "Menu is subject to change without notice",
                "CALORIE TARGET",
                "K-5 550-650",
                "6-8 600-700",
                "9-12 750-850"
        };

        String[] noSchool= { "No School","Christmas Break"};

        for (String s : replace) {
            str = str.replace(s, "");
        }


        //these are one line so they all get smashed together
        for(String s: noSchool)
        {
            str = str.replaceAll(s,"\n"+s);
        }

        str=str.replaceAll(" *\\n *","\n").replaceAll(" *$","").replaceAll("^ *","");

        String line="";
        String meal="";
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i)=='\n') {
                if(continuesToNextLine(line))
                {
                    line+=" "; //so they dont get smashed together
                }
                else if (line.contains(entree) || line.indexOf("Or ")==0) {
                    meal+=line;
                    meals.add(meal);
                    meal="";
                    line="";
                }
                else
                {
                    if(!line.equals(""))
                    {
                        meal+=line+"\n";
                        line="";
                    }
                }
            }
            else
            {
                line+=str.charAt(i);
            }
        }
        Log.v(TAG,meals.toString());
        str="";
        for(String s:meals)
        {
            str+=s+"\n\n";
        }
        return str;


    }

    private String monthToString(int month) {
        switch (month)
        {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "Notamonthotember";
        }
    }

    public boolean continuesToNextLine(String line) {
        if(line.endsWith("String")||line.endsWith("&"))
        {
            return true;
        }
        return false;
    }

}
