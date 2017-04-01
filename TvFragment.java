package raj.project.imdbsearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TvFragment extends Fragment {

	
	Button details;
	MyAdapter adapter;
	EditText title, year;
	ArrayList<String> items2 = new ArrayList<String>();
	ArrayList<String> testing = new ArrayList<String>();
	ArrayList<ListItem> items = new ArrayList<>();

	ProgressDialog ringProgressDialog;
	JSONObject json;
	Handler hand = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_tv, container, false);
		Button search = (Button) view.findViewById(R.id.tv_search);
		title = (EditText) view.findViewById(R.id.et_tv_name);
		year = (EditText) view.findViewById(R.id.et_tv_year);

		ListView listview = (ListView) view.findViewById(R.id.tv_listivew);

	    items = MySharedPref.getTVSearchList(getActivity().getApplicationContext());
	     
		adapter = new MyAdapter(this, items);

		listview.setAdapter(adapter);

		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				items.clear();
				fetchDetails(title.getText().toString().trim(), year.getText()
						.toString().trim());
				// adapter.refreshItems(testing);
			}
		});
		return view;
	}

	@Override
	public void onStop() {		
		
		MySharedPref.storeTVSearchList(items, getActivity().getApplicationContext());				  
		super.onStop();
	}

	public class MyAdapter extends BaseAdapter {
		Context context;
		ArrayList<ListItem> items;
		LayoutInflater inflater;

		MyAdapter(TvFragment frag, ArrayList<ListItem> aitems) {
			this.context = frag.getActivity();
			this.items = aitems;
			inflater = (LayoutInflater) context
					.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		}

		public void refreshItems(ArrayList messages) {
			this.items = messages;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final int position1 = position;
			View row = convertView;
			//MyViewHolder viewHolder;

			//if (row == null) 
				row = inflater.inflate(R.layout.list_item, null);
			
			TextView title = (TextView) row.findViewById(R.id.et_title);
			TextView year = (TextView) row.findViewById(R.id.et_year);

			title.setText(items.get(position1).getTitle());
			year.setText(items.get(position1).getYear());

			ImageView poster = (ImageView) row.findViewById(R.id.iv_poster);
			poster.setImageBitmap(null);
			new DownloadImageTask(
					(ImageView) row.findViewById(R.id.iv_poster))
					.execute(items.get(position1).getImageURL());

			details = (Button) row.findViewById(R.id.bt_detail);

			details.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					DetailFragment detail = new DetailFragment();
					
					//Passing bundle with item ID clicked
					Bundle b = new Bundle();
					b.putString("ID", items.get(position1).getID() );
					detail.setArguments(b);
					
					ft.addToBackStack("tv");
					ft.hide(TvFragment.this);
					ft.add(android.R.id.tabcontent, detail);
					ft.commit();

				}
			});

			/*viewHolder.setText(row, items.get(position1).getTitle(), items.get(position1)
							.getYear(), items.get(position1).getImageURL());*/
			return row;

		}

/*		private class MyViewHolder {
			TextView title;
			TextView director;
			TextView year;
			ImageView poster;

			public void setText(View row, String atitle, String ayear, String url) {
				title = (TextView) row.findViewById(R.id.et_title);
				year = (TextView) row.findViewById(R.id.et_year);

				title.setText(atitle);
				year.setText(ayear);

				poster = (ImageView) row.findViewById(R.id.iv_poster);
				poster.setImageBitmap(null);
				new DownloadImageTask(
						(ImageView) row.findViewById(R.id.iv_poster))
						.execute(url);

			}
		}*/
	}

	public void fetchDetails(final String name, final String year) {
		ringProgressDialog = ProgressDialog.show(getActivity(),
				"Please wait ...", "Fetching Posts ...", true);
		ringProgressDialog.setCancelable(true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					json = new JSONParser().getJSONFromUrl("&type=series&s="+name+"&y="+year);
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*ringProgressDialog.dismiss();
				ringProgressDialog = null;*/
				if (json != null) {
					try {
					JSONArray result=json.getJSONArray("Search");
					for(int i=0;i<Math.min(10, result.length());i++) {
						ListItem obj = new ListItem();
						//obj.setDirector(json.getString("Director"));
						obj.setID(result.getJSONObject(i).getString("imdbID"));
						obj.setTitle(result.getJSONObject(i).getString("Title"));
						obj.setYear(result.getJSONObject(i).getString("Year").replaceAll("[^0-9]", "-"));
						obj.setImageURL(result.getJSONObject(i).getString("Poster"));
						System.out.println(result.getJSONObject(i).getString("Year").replaceAll("[^0-9]", "-"));
						items.add(obj);
					}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					hand.post(new Runnable() {
						@Override
						public void run() {
							adapter.refreshItems(items);
							ringProgressDialog.dismiss();
							ringProgressDialog = null;
						}
					});
				} else {
					hand.post(new Runnable() {
						@Override
						public void run() {
							ringProgressDialog.dismiss();
							ringProgressDialog = null;
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
