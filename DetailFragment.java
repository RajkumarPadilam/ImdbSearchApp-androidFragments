package raj.project.imdbsearch;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {

	Handler hand = new Handler();
	ProgressDialog ringProgressDialog;
	JSONObject json;
	TextView Director, Title, Plot;
	ImageView Poster;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		String ID = getArguments().getString("ID");
		
		Director = (TextView) view.findViewById(R.id.detail_director);
		Title = (TextView) view.findViewById(R.id.detail_title);
		Plot = (TextView) view.findViewById(R.id.detail_plot);
		Poster = (ImageView) view.findViewById(R.id.detail_poster);
		fetchDetails(ID, Poster);
		
		return view;
	}

	public void fetchDetails(final String ID, final ImageView image) {
		ringProgressDialog = ProgressDialog.show(getActivity(),
				"Please wait ...", "Fetching Posts ...", true);
		ringProgressDialog.setCancelable(true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					json = new JSONParser().getJSONFromUrl("&i="
							+ ID+"&r=json");
				} catch (Exception e) {
					e.printStackTrace();
				}

				ringProgressDialog.dismiss();
				ringProgressDialog = null;
				if (json != null) {
					hand.post(new Runnable() {
						@Override
						public void run() {
							try {
								Director.setText(json.getString("Director"));
								Title.setText(json.getString("Title"));
								Plot.setText(json.getString("Plot"));
								new DownloadImageTask(image)
										.execute(json.getString("Poster"));
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					hand.post(new Runnable() {
						@Override
						public void run() {
							// Toast.makeText(getApplicationContext(),
							// "Error while Processing",
							// Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}

}
