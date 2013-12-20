package weather.common.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;

/**
 * Cache file to Card
 * 
 * @author ChungPV1
 * 
 */
public class FileCache {

	/**
	 * 
	 */
	private File cacheDir;

	/**
	 * 
	 * @param context
	 */
	public FileCache(Context context) {

		// Find the directory to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"WeatherCache");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()){
			cacheDir.mkdirs();
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public File getFile(String url) {
		String filename = url;
		try {
			filename = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		File f = new File(cacheDir, filename);

		return f;

	}

	/**
     * 
     */
	public void clear() {
		File[] files = cacheDir.listFiles();

		if (files == null)
			return;

		for (File f : files)
			f.delete();
	}

}