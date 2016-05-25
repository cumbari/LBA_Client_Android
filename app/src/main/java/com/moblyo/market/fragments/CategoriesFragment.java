package com.moblyo.market.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.moblyo.market.CouponsInSelectedCategoryActivity;
import com.moblyo.market.HomeScreenActivity;
import com.moblyo.market.R;
import com.moblyo.market.adapter.CategoriesRecyclerViewAdapter;
import com.moblyo.market.model.ListOfCategories;
import com.moblyo.market.utils.RecyclerItemClickListener;

import java.util.ArrayList;

public class CategoriesFragment extends ParentFragment {

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private ArrayList<ListOfCategories> categoriesArray;

    private RecyclerView recyclerView;
    private CategoriesRecyclerViewAdapter mCategoriesRecyclerViewAdapter;

    @SuppressWarnings("unused")
    public static CategoriesFragment newInstance(int columnCount) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoriesFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCategoriesRecyclerViewAdapter != null) {
            setData();
            mCategoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(getActivity(),categoriesArray);
            mCategoriesRecyclerViewAdapter.notifyData();
            recyclerView.setAdapter(mCategoriesRecyclerViewAdapter);
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

        categoriesArray =  new ArrayList<ListOfCategories>();
        setData();

        mCategoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(getActivity(), categoriesArray);
        recyclerView.setAdapter(mCategoriesRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ListOfCategories item = categoriesArray.get(position);
                Intent moveToCategorization = new Intent(mActivity,CouponsInSelectedCategoryActivity.class);
                moveToCategorization.putExtra("categoryID",item.getCategoryId());
                moveToCategorization.putExtra("categoryName",item.getCategoryName());
                mActivity.startActivity(moveToCategorization);
            }
        }));
        return view;
    }

    private void setData() {
        try{
        if(categoriesArray == null){
            categoriesArray =  new ArrayList<ListOfCategories>();
        }

        if(((HomeScreenActivity)mActivity).getCategoriesData != null
                && ((HomeScreenActivity)mActivity).getCategoriesData.getListOfCategories() != null) {
            categoriesArray = ((HomeScreenActivity)mActivity).getCategoriesData.getListOfCategories();
        }
        }catch (Exception e){

        }
    }
}
