package me.arnoldwho.htcagesture;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class Appinfo {
    public String appName="";
    public String packageName="";
    public Drawable appIcon=null;
    public void print()
    {
        Log.v("app","Name:"+appName+" Package:"+packageName);
    }
}
