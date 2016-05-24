package com.shephertz.cumbari;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.shephertz.cumbari.adapter.SettingsRecyclerViewAdapter;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity
{
    private TextView title;
    private SettingsRecyclerViewAdapter mSettingsRecyclerViewAdapter;
    private ArrayList<String> settingsArray;
    private RecyclerView recyclerView;
    private ImageView map_icon;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        initialiseResources();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mSettingsRecyclerViewAdapter != null){
            mSettingsRecyclerViewAdapter.notifyData();
        }
    }

    private void initialiseResources() {
        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(getResources().getString(R.string.more_setting));

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        map_icon.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = (SearchView) findViewById(R.id.data_searchview);
        searchView.setVisibility(View.GONE);

        settingsArray = new ArrayList<String>();
        settingsArray.add(getResources().getString(R.string.language));
        settingsArray.add(getResources().getString(R.string.unit));
        settingsArray.add(getResources().getString(R.string.offers_in_list));
        settingsArray.add(getResources().getString(R.string.range));

        mSettingsRecyclerViewAdapter = new SettingsRecyclerViewAdapter(SettingsActivity.this, settingsArray);
        recyclerView.setAdapter(mSettingsRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(SettingsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == 0){
                    Intent moveToHomeScreen = new Intent(SettingsActivity.this,LanguageActivity.class);
                    startActivity(moveToHomeScreen);
                    finish();
                }else if(position == 1){
                    Intent moveToUnitScreen = new Intent(SettingsActivity.this,UnitActivity.class);
                    startActivity(moveToUnitScreen);
                }else if(position == 2){
                    Intent intent = new Intent(SettingsActivity.this,SeekBarSelectionOfferOrRangeActivity.class);
                    intent.putExtra("isRange",false);
                    startActivity(intent);
                }else if(position == 3){
                    Intent intent = new Intent(SettingsActivity.this,SeekBarSelectionOfferOrRangeActivity.class);
                    intent.putExtra("isRange",true);
                    startActivity(intent);
                }

            }
        }));
    }

    @Override
    public void onBackPressed() {
        Intent moveToHomeScreen = new Intent(SettingsActivity.this,HomeScreenActivity.class);
        moveToHomeScreen.putExtra("tabIndex",4);
        startActivity(moveToHomeScreen);
        finish();
    }
}
