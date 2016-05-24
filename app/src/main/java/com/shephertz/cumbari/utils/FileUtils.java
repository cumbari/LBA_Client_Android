/*
 * Added by Amit Sharma.
 */
package com.shephertz.cumbari.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * The Class FileUtils
 */
public class FileUtils {

	/**
	 * Copy file.
	 * 
	 * @param currentDB
	 *            the current db
	 * @param backupDB
	 *            the backup db
	 */
	public static void copyFile(File currentDB, File backupDB) {
		if (currentDB.exists()) {
			FileChannel src = null;
			FileChannel dst = null;
			try {
				src = new FileInputStream(currentDB).getChannel();
				dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (src != null)
						src.close();
					if (dst != null)
						dst.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

}
