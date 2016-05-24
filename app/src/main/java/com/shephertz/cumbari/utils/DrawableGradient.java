package com.shephertz.cumbari.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class DrawableGradient extends GradientDrawable {
	public DrawableGradient(int[] colors, int cornerRadius, String strokeColor,
			int strokeWidth) {
		super(Orientation.TOP_BOTTOM, colors);

		try {
			this.setShape(GradientDrawable.RECTANGLE);
			this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
			this.setStroke(strokeWidth, Color.parseColor(strokeColor));
			this.setCornerRadius(cornerRadius);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}