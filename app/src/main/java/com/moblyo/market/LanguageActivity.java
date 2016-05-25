package com.moblyo.market;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.moblyo.market.adapter.PositionRecyclerViewAdapter;
import com.moblyo.market.sync.SyncApplicationData;
import com.moblyo.market.utils.RecyclerItemClickListener;
import com.moblyo.market.utils.SharedPrefKeys;

import java.util.ArrayList;

public class LanguageActivity extends BaseActivity
{
    private TextView title;
    private PositionRecyclerViewAdapter mPositionRecyclerViewAdapter;
    private ArrayList<String> languageArray;
    private RecyclerView recyclerView;
    private ImageView map_icon;
    private SearchView searchView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        appUtility.setLocale();
        initialiseResources();
    }

    private void initialiseResources() {
        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(getResources().getString(R.string.language));

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        map_icon.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = (SearchView) findViewById(R.id.data_searchview);
        searchView.setVisibility(View.GONE);

        languageArray = new ArrayList<String>();
        languageArray.add(getResources().getString(R.string.english));
        languageArray.add(getResources().getString(R.string.swedish));
        languageArray.add(getResources().getString(R.string.german));


        int selected = 0;
        if (sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG").equals("SWE")) {
            selected = 1;
        }else if (sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG").equals("GER")) {
            selected = 2;
        }
        mPositionRecyclerViewAdapter = new PositionRecyclerViewAdapter(LanguageActivity.this, languageArray, selected);
        recyclerView.setAdapter(mPositionRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(LanguageActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(appUtility.isNetworkAvailable()) {
                    mPositionRecyclerViewAdapter.indexSelected = position;
                    mPositionRecyclerViewAdapter.notifyData();
                    if (position == 0) {
                        sharedPreferenceUtil.saveData(SharedPrefKeys.LANGUAGE, "ENG");
                    } else if (position == 1) {
                        sharedPreferenceUtil.saveData(SharedPrefKeys.LANGUAGE, "SWE");
                    } else {
                        sharedPreferenceUtil.saveData(SharedPrefKeys.LANGUAGE, "GER");
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog = ProgressDialog.show(LanguageActivity.this, getResources().getString(R.string.progress_title), getResources().getString(R.string.progress_message), false);
                                    progressDialog.setCancelable(false);
                                }
                            });
                            new SyncApplicationData(sharedPreferenceUtil).syncData();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   progressDialog.dismiss();
                                    restartActivity();
                                }
                            });
                        }
                    }).start();
                }else{
                    alertMessage.alertBoxForNoConnection(null);
                }
            }
        }));
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        appUtility.setLocale();

        Intent i = new Intent(LanguageActivity.this,SettingsActivity.class);
        startActivity(i);
        finish();
    }
}
