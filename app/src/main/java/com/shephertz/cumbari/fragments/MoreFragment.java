package com.moblyo.market.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.moblyo.market.PositionActivity;
import com.moblyo.market.R;
import com.moblyo.market.SettingsActivity;
import com.moblyo.market.adapter.MoreRecyclerViewAdapter;
import com.moblyo.market.utils.APPConstants;
import com.moblyo.market.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends ParentFragment {

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private ArrayList<String> moreArray;

    private MoreRecyclerViewAdapter mMoreRecyclerViewAdapter = null;
    private RecyclerView recyclerView;

    @SuppressWarnings("unused")
    public static MoreFragment newInstance(int columnCount) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public MoreFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMoreRecyclerViewAdapter != null){
            mMoreRecyclerViewAdapter.notifyData();
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

        moreArray =  new ArrayList<String>();
        moreArray.add(getActivity().getResources().getString(R.string.more_position));
        moreArray.add(getActivity().getResources().getString(R.string.more_setting));
        moreArray.add(getActivity().getResources().getString(R.string.more_feedback));
        moreArray.add(getActivity().getResources().getString(R.string.more_about_cumbari));

        mMoreRecyclerViewAdapter = new MoreRecyclerViewAdapter(getActivity(), moreArray);
        recyclerView.setAdapter(mMoreRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 0:
                        Intent positionScreen = new Intent(getActivity(), PositionActivity.class);
                        mActivity.startActivity(positionScreen);
                        break;
                    case 1:
                        Intent settingScreen = new Intent(getActivity(), SettingsActivity.class);
                        mActivity.startActivity(settingScreen);
                        mActivity.finish();
                        break;
                    case 2:
                        sendEmail();
                        break;
                    case 3:
                        aboutCumbari();
                        break;
                }
            }
        }));
        return view;
    }

    private void feedbackToMail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String[] recipients={APPConstants.Feedback_Mail};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.more_feedback));
        emailIntent.setType("text/plain");
        final PackageManager pm = mActivity.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for(final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                best = info;
            }
        }

        if (best != null) {
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            mActivity.startActivity(emailIntent);
        }else{
            Toast.makeText(getActivity(),mActivity.getResources().getString(R.string.no_mail_configured),Toast.LENGTH_SHORT).show();
        }
    }


    protected void sendEmail() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String[] recipients={APPConstants.Feedback_Mail};

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.more_feedback));
            try {
                mActivity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(),mActivity.getResources().getString(R.string.no_mail_configured),Toast.LENGTH_SHORT).show();
            }

    }

    private void aboutCumbari(){
        Uri uri = Uri.parse(APPConstants.CumbariURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(intent);
    }
}
