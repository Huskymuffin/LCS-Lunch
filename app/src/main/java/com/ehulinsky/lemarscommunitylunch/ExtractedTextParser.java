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

    public ArrayList<String> parse(String str) {
        ArrayList<String> meals = new ArrayList<>();
        Date d = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(d);
        String entree="HS Entrée";
        String date=ThingyThatTurnsAMonthIntoAString.monthToString(c.get(Calendar.MONTH)).toUpperCase()+
                " "+c.get(Calendar.YEAR);
        String[] replace={
                date,
                "LE MARS COMMUNITY SCHOOLS",
                "“This is an Equal Opportunity Provider”",
                "Menu is subject to change without notice",
                "CALORIE TARGET",
                "K-5 550-650",
                "6-8 600-700",
                "9-12 750-850"
        };

        String[] noSchool= { "No School","Christmas Holiday"};

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
        boolean isNoSchool=false;
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
                    for(String s: noSchool) {
                        if(line.equals(s))
                        {
                            meal+=line;
                            meals.add(meal);
                            meal="";
                            line="";
                            isNoSchool=true;
                            break;
                        }
                    }

                    if(!isNoSchool) {
                        if (!line.equals("")) {
                            meal+=line+"\n";
                            line = "";
                        }
                    }
                    isNoSchool=false;
                }
            }
            else
            {
                line+=str.charAt(i);
            }
        }
        Log.v(TAG,meals.toString());
        return meals;


    }



    public boolean continuesToNextLine(String line) {
        if(line.endsWith("String")||line.endsWith("&"))
        {
            return true;
        }
        return false;
    }

}
