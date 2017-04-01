package raj.project.imdbsearch;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MySharedPref {

	public static void storeTVSearchList(ArrayList<ListItem> items, Context context) {
		
		Gson gson = new Gson();
		String tvSearchItems = gson.toJson(items);
		SharedPreferences appSharedPrefs = PreferenceManager
				  .getDefaultSharedPreferences(context);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString("TV", tvSearchItems);
		prefsEditor.commit();
	}

	public static void storeMovieSearchList(ArrayList<ListItem> items, Context context) {
		
		Gson gson = new Gson();
		String tvSearchItems = gson.toJson(items);
		SharedPreferences appSharedPrefs = PreferenceManager
				  .getDefaultSharedPreferences(context);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString("MOVIE", tvSearchItems);
		prefsEditor.commit();
	}

	public static ArrayList<ListItem> getTVSearchList(Context context) {
		ArrayList<ListItem> items = new ArrayList<>();

		SharedPreferences appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		if (appSharedPrefs.contains("TV")) {
			String json = appSharedPrefs.getString("TV", "");
			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<ListItem>>() {
			}.getType();
			items = gson.fromJson(json, type);

		}
		return items;
	}

	public static ArrayList<ListItem> getMovieSearchList(Context context) {
		ArrayList<ListItem> items = new ArrayList<>();

		SharedPreferences appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		if (appSharedPrefs.contains("MOVIE")) {
			String json = appSharedPrefs.getString("MOVIE", "");
		    Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<ListItem>>() {
			}.getType();
			items = gson.fromJson(json, type);

		}
		return items;
	}

}
