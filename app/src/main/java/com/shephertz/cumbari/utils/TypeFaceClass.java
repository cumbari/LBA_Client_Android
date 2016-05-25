package com.moblyo.market.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;
import android.widget.TextView;

public class TypeFaceClass 
{
	private Typeface tfNormal = null,tfBold=null,tfMed=null;
	public TypeFaceClass(Context con) {
		tfNormal = Typeface.createFromAsset(con.getAssets(), "HelveticaNeue.ttf");
		tfBold = Typeface.createFromAsset(con.getAssets(), "HelveticaNeue_Bold.ttf");
		tfMed = Typeface.createFromAsset(con.getAssets(), "HelveticaNeue_Med.ttf");
	}
	
	public void  setTypefaceNormal(TextView txt){
		txt.setTypeface(tfNormal);
	}
	
	public void  setTypefaceBold(TextView txt){
		txt.setTypeface(tfBold);
	}
	
	public void  setTypefaceMed(TextView txt){
		txt.setTypeface(tfMed);
	}
	
	public void  setTypefaceNormal(EditText txt){
		txt.setTypeface(tfNormal);
	}
	
	public void  setTypefaceBold(EditText txt){
		txt.setTypeface(tfBold);
	}
	
	public void  setTypefaceMed(EditText txt){
		txt.setTypeface(tfMed);
	}
	
}
