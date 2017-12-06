package com.example.android.themoviedb;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;



public class Utils {

    private static String language="&language=en-US";
    private final static String API_KEY="d1ed76b7307ba5cec012b3685dc37dd3";

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        Utils.language = language;
    }

    public static void saveMedia(Context context, MenuItem item, String id, int mediaType){
        //Add to favorite
        String posMsg=context.getString(R.string.movie_added);
        String negMsg=context.getString(R.string.movie_removed);
        try{
            if(mediaType>2 || mediaType<0){
                Log.v("mytag","iwas return");
                return;
            }
            else
            {
                switch(mediaType){
                    case 0:
                        break;
                    case 1:
                        posMsg=context.getString(R.string.show_added);
                        negMsg=context.getString(R.string.show_removed);
                        break;
                    case 2:
                        posMsg=context.getString(R.string.person_added);
                        negMsg=context.getString(R.string.person_added);
                        break;
                    default:
                        return;
                }
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (!sharedPref.contains(id)){
                    editor.putInt(id,mediaType);
                    editor.apply();
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    Toast.makeText(context,posMsg,Toast.LENGTH_SHORT).show();
                }
                else{
                    editor.remove(id);
                    editor.apply();
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    Toast.makeText(context,negMsg,Toast.LENGTH_SHORT).show();
                }
                Log.v("my tag","iwas here");
            }
        }catch (NumberFormatException e){
            Log.v("my tag","iwas catch");
        }
    }

    public static ArrayList<String> getMedia(Context context, int MediaType){
        ArrayList<String> ids=new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> entry :  sharedPref.getAll().entrySet())
        {
            if(MediaType == (Integer)entry.getValue()){
                ids.add(entry.getKey());
        }
      }
      return ids;
    }
    public static int getAppLanguage(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key2), Context.MODE_PRIVATE);
        int language = sharedPref.getInt("language",0);
        if(language==0){
            setLanguage("&language=en-US");
        }
        else{
            setLanguage("&language=fr-FR");
        }
         return language;
    }
    public static void setAppLanguage(Context context, int Value){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key2), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("language",Value);
        editor.apply();
    }
}
