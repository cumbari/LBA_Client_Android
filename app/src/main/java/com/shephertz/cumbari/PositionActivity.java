package com.shephertz.cumbari;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.shephertz.cumbari.adapter.PositionRecyclerViewAdapter;
import com.shephertz.cumbari.sync.SyncApplicationData;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;
import com.shephertz.cumbari.utils.SharedPrefKeys;

import java.util.ArrayList;

public class PositionActivity extends BaseActivity
{
    private TextView title;
    private PositionRecyclerViewAdapter mPositionRecyclerViewAdapter;
    private ArrayList<String> positionArray;
    private RecyclerView recyclerView;
    private ImageView map_icon;
    private SearchView searchView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        initialiseResources();
    }

    private void initialiseResources() {
        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(getResources().getString(R.string.more_position));

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        map_icon.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = (SearchView) findViewById(R.id.data_searchview);
        searchView.setVisibility(View.GONE);

        positionArray = new ArrayList<String>();
        positionArray.add(getResources().getString(R.string.current_position));
        positionArray.add(getResources().getString(R.string.new_position));

        int selected = 0;
        if (!sharedPreferenceUtil.getData(SharedPrefKeys.IS_CURRENT_POSITION, true)) {
            selected = 1;
        }
        mPositionRecyclerViewAdapter = new PositionRecyclerViewAdapter(PositionActivity.this, positionArray, selected);
        recyclerView.setAdapter(mPositionRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(PositionActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPositionRecyclerViewAdapter.indexSelected = position;
                mPositionRecyclerViewAdapter.notifyData();
                if (position == 0) {
                    sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LATITUDE, (float) getLocationFromGoogleClient.mCurrentLocation.getLatitude());
                    sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LONGITUDE, (float) getLocationFromGoogleClient.mCurrentLocation.getLongitude());
                    sharedPreferenceUtil.saveData(SharedPrefKeys.IS_CURRENT_POSITION, true);
                    fetchDataAgain();
                } else {
                    sharedPreferenceUtil.saveData(SharedPrefKeys.IS_CURRENT_POSITION, false);
                    Intent intent = new Intent(PositionActivity.this, SettingsMapActivity.class);
                    startActivity(intent);
                }
            }
        }));
    }


    private void fetchDataAgain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = ProgressDialog.show(PositionActivity.this, getResources().getString(R.string.progress_title), getResources().getString(R.string.progress_message), false);
                        progressDialog.setCancelable(false);
                    }
                });
                new SyncApplicationData(sharedPreferenceUtil).syncData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
}
