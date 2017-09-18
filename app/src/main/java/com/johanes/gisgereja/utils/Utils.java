package com.johanes.gisgereja.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class Utils {

	public static String URL = "http://10.0.2.2/mtower/";
	//public static String URL = "http://192.168.228.1/mtower/"; 
	
	public static InputStream getCoverFromCache(Context ctx, String fullUrl)
			throws Exception {
		if (sNVL(fullUrl).length() < 1) {
			// Log.e("Epic", "empty URL");
			InputStream is = ctx.getAssets().open("nophoto.png"); // guarantee
			// exist
			return is;
		}

		String fileNameOnly = getFileName(fullUrl);

		File aCacheFile = new File(ctx.getCacheDir(), fileNameOnly);

		if (aCacheFile.exists()) {
			return new FileInputStream(aCacheFile);
		}

		Log.e("eric", "cache not found for " + aCacheFile.getPath()
				+ "->URL is " + fullUrl);

		InputStream is = ctx.getAssets().open("nophoto.png"); // guarantee exist

		return is;
	}

	public static String getFileName(String url) {
		try {
			return getFileName(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String getFileName(java.net.URL extUrl) {
		String filename = "";

		String path = extUrl.getPath();

		String[] pathContents = path.split("[\\\\/]");
		if (pathContents != null) {
			int pathContentsLength = pathContents.length;

			for (int i = 0; i < pathContents.length; i++)
				;
			String lastPart = pathContents[(pathContentsLength - 1)];
			String[] lastPartContents = lastPart.split("\\.");
			if ((lastPartContents != null) && (lastPartContents.length > 1)) {
				int lastPartContentLength = lastPartContents.length;

				String name = "";
				for (int i = 0; i < lastPartContentLength; i++) {
					if (i < lastPartContents.length - 1) {
						name = name + lastPartContents[i];
						if (i < lastPartContentLength - 2) {
							name = name + ".";
						}
					}
				}
				String extension = lastPartContents[(lastPartContentLength - 1)];
				filename = name + "." + extension;
			}

		}

		return filename;
	}

	public static String sNVL(String param) {
		return sNVL(param, "");
	}

	public static String sNVL(String param, String _default) {
		if ((param == null) || (param.equals(""))) {
			return _default;
		}
		return param;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/*
	 * Untuk membuat format integer menjadi string sesuai nominal ex: 1000000 ->
	 * 1,000,000
	 */
	public static String formatCurrency(int amount) {
		// NumberFormat decimalFormat = NumberFormat.getInstance(new
		// Locale("fi", "FI", ""));\
		String pattern = "###,###,###,###,###";
		NumberFormat decimalFormat = NumberFormat.getInstance(new Locale(
				"en_US"));
		DecimalFormat df = (DecimalFormat) decimalFormat;
		df.applyPattern(pattern);
		return df.format(amount);
	}

	public static double getDistanceBetweenTwoLocation(double u_lat,
			double u_lon, double b_lat, double b_lon) {
		double dist;
		double radian = 6371;

		double dLat = (b_lat - u_lat) * Math.PI / 180;
		double dLon = (b_lon - u_lon) * Math.PI / 180;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(u_lat * (Math.PI / 180))
				* Math.cos(b_lat * (Math.PI / 180)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		dist = radian * c;

		return dist;
	}

	public static double RoundDecimal(double value, int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);

		bd = bd.setScale(decimalPlace, 6);

		return bd.doubleValue();
	}

}
