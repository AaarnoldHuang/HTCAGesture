package me.arnoldwho.htcagesture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        TextView appnameTextview = (TextView) findViewById(R.id.appname);
        TextView chooseapp = (TextView) findViewById(R.id.chooseapp) ;
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide);
        chooseapp.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                /*ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Loading Apps...");
                progressDialog.setMessage("Laoding...");
                progressDialog.setCancelable(false);
                progressDialog.show();*/
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AppInfoActivity.class);
                startActivityForResult(intent, 1);
                //progressDialog.dismiss();
            }
        });
        getWindow().setExitTransition(slide);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        String appname = preferences.getString("packagename","Spotify");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String packagename = data.getStringExtra("packagename");
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("packagename", packagename);
                    editor.apply();
                }
                break;
                default:
        }
    }

}
