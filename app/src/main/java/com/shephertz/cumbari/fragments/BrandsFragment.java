package com.shephertz.cumbari.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.shephertz.cumbari.CouponsInSelectedBrandsActivity;
import com.shephertz.cumbari.HomeScreenActivity;
import com.shephertz.cumbari.R;
import com.shephertz.cumbari.adapter.BrandsRecyclerViewAdapter;
import com.shephertz.cumbari.model.ListOfBrandHits;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;

import java.util.ArrayList;

public class BrandsFragment extends ParentFragment {

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private ArrayList<ListOfBrandHits> brandsArray;

    private RecyclerView recyclerView;
    private BrandsRecyclerViewAdapter mBrandsRecyclerViewAdapter;

    @SuppressWarnings("unused")
    public static BrandsFragment newInstance(int columnCount) {
        BrandsFragment fragment = new BrandsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public BrandsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBrandsRecyclerViewAdapter != null) {
            setData();
            mBrandsRecyclerViewAdapter = new BrandsRecyclerViewAdapter(getActivity(),brandsArray);
            recyclerView.setAdapter(mBrandsRecyclerViewAdapter);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        SearchView searchView = (SearchView) view.findViewById(R.id.data_searchview);
       searchView.setVisibility(View.GONE);

        brandsArray = new ArrayList<ListOfBrandHits>();
        setData();

        mBrandsRecyclerViewAdapter = new BrandsRecyclerViewAdapter(getActivity(),brandsArray);
        recyclerView.setAdapter(mBrandsRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ListOfBrandHits item = brandsArray.get(position);
                Intent moveToCategorization = new Intent(mActivity,CouponsInSelectedBrandsActivity.class);
                moveToCategorization.putExtra("brandName",item.getBrandName());
                mActivity.startActivity(moveToCategorization);
            }
        }));
        return view;
    }

    private void setData() {
        try {
            if(brandsArray == null){
                brandsArray = new ArrayList<ListOfBrandHits>();
            }

            if (((HomeScreenActivity) mActivity).getCouponsData != null
                    && ((HomeScreenActivity) mActivity).getCouponsData.getListOfBrandHits() != null) {
                brandsArray = ((HomeScreenActivity) mActivity).getCouponsData.getListOfBrandHits();
                brandsArray = removeDuplicacy(brandsArray);
            }
        }catch (Exception e){

        }
    }

    private ArrayList<ListOfBrandHits> removeDuplicacy(ArrayList<ListOfBrandHits> listOfData) {
        ArrayList<ListOfBrandHits> uniqueList = new ArrayList<ListOfBrandHits>();
        if (listOfData.size() > 0) {
            ArrayList<ListOfBrandHits> array = new ArrayList<ListOfBrandHits>();
            for (int i = 0; i < listOfData.size(); i++)
                array.add(listOfData.get(i));
            uniqueList.add(array.get(0));
            for (int i = 0; i < array.size(); i++) {
                ListOfBrandHits items = array.get(i);
                boolean add = false;
                for (int j = 0; j < uniqueList.size(); j++) {
                    if (items.getBrandName().equalsIgnoreCase(uniqueList.get(j).getBrandName()))
                        add = true;
                }
                if (!add)
                    uniqueList.add(items);
            }
        }
        return uniqueList;
    }

    @Override
    public void isMapSelectedToFragment(int mapSelectedIndex) {
        if(mapSelectedIndex == 2){

        }
    }
}
