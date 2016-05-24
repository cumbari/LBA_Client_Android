package com.shephertz.cumbari;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.shephertz.cumbari.adapter.PositionRecyclerViewAdapter;
import com.shephertz.cumbari.location.Coordinate;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;
import com.shephertz.cumbari.utils.SharedPrefKeys;

import java.util.ArrayList;

public class UnitActivity extends BaseActivity
{
    private TextView title;
    private PositionRecyclerViewAdapter mPositionRecyclerViewAdapter;
    private ArrayList<String> unitArray;
    private RecyclerView recyclerView;
    private ImageView map_icon;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        initialiseResources();
    }

    private void initialiseResources() {
        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(getResources().getString(R.string.unit));

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        map_icon.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = (SearchView) findViewById(R.id.data_searchview);
        searchView.setVisibility(View.GONE);

        unitArray = new ArrayList<String>();
        unitArray.add(getResources().getString(R.string.meter));
        unitArray.add(getResources().getString(R.string.mile));


        int selected = 0;
       if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Miles.getValue()) {
           selected = 1;
       }


        mPositionRecyclerViewAdapter = new PositionRecyclerViewAdapter(UnitActivity.this, unitArray, selected);
        recyclerView.setAdapter(mPositionRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(UnitActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    mPositionRecyclerViewAdapter.indexSelected = position;
                    mPositionRecyclerViewAdapter.notifyData();
                    if (position == 0) {
                        sharedPreferenceUtil.saveData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue());
                    } else if (position == 1) {
                        sharedPreferenceUtil.saveData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Miles.getValue());
                    }
            }
        }));
    }

}
