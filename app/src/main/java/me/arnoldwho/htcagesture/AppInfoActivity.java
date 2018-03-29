package me.arnoldwho.htcagesture;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class AppInfoActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_app_info);
        ListView listView = (ListView) findViewById(R.id.applist);
       // AppAdapter mBaseAdapter = new AppAdapter(getApplicationContext());
        AppAdapter mBaseAdapter = new AppAdapter(this);
        listView.setAdapter(mBaseAdapter);
        mBaseAdapter.getAppInfo();
    }
}
