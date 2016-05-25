
package com.moblyo.market.utils;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * The Class TimeIgnoringComparator.
 */
public class TimeIgnoringComparator implements Comparator<Date> {

	
	public int compare(Date d1, Date d2) {
		// convert Date objects to Calendar objects, as a large number
		// of Date methods like getMonth(), getYear() etc. are deprecated.
		// According to documentation it is recommended to use Calendar objects

		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
			return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);

		if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
			return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);

		return c1.get(Calendar.DATE) - c2.get(Calendar.DATE);
	}
}