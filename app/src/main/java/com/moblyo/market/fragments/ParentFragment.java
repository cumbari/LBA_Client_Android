package com.moblyo.market.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moblyo.market.interfaces.OnFragmentAttachedListener;
import com.moblyo.market.interfaces.OnPassValueToFragmentListener;
import com.moblyo.market.utils.AppUtility;
import com.moblyo.market.utils.SharedPreferenceUtil;
import com.moblyo.market.utils.TypeFaceClass;

public class ParentFragment extends Fragment implements OnPassValueToFragmentListener {
	protected OnFragmentAttachedListener mListener;
	protected TypeFaceClass typeFace = null;
	protected SharedPreferenceUtil sharedPreferenceUtil = null;
	protected AppUtility appUtility = null;

	protected Activity mActivity = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		typeFace=new TypeFaceClass(getActivity());
		sharedPreferenceUtil = SharedPreferenceUtil.getInstance(activity);
		appUtility = new AppUtility(mActivity);
		try {
			mListener = (OnFragmentAttachedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnPassValueToFragmentListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mActivity= null;
		mListener = null;
		typeFace = null;
		appUtility = null;
		sharedPreferenceUtil = null;
	}

	@Override
	public void isEditEnabledPassToFragment(boolean isEnabled) {
		mListener.isEditEnabled(isEnabled);
	}

	@Override
	public void isMapSelectedToFragment(int mapSelectedIndex) {
		mListener.isMapTapped(mapSelectedIndex);

	}

	@Override
	public void sortData(boolean sortData) {
		mListener.isDataSort(sortData);
	}
}
