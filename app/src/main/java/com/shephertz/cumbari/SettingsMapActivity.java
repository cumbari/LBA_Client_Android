package com.shephertz.cumbari;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shephertz.cumbari.sync.SyncApplicationData;
import com.shephertz.cumbari.utils.SharedPrefKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SettingsMapActivity extends BaseActivity
{
	private GoogleMap googleMap;
	private static Geocoder geocoder=null;

	//volley
	private static final String TAG = "Error";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyAimgMX1v7-hkRL6lhjrNCjclvTuyPGEhI";
	private RequestQueue queue;
	public static LatLng current_location = new LatLng(0, 0);
	private Marker current_location_mark;
	private String title,snippet;
	private float lat,longitude;
	private LatLng STARTING_MARKER_POSITION;
	private TextView tapmap,searchmap,activity_header_mapsettings;

	private SearchView searchView;
	private RelativeLayout searchLayout;
	private ProgressDialog progressDialog;
	private boolean isPositionChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_settings);
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		isPositionChanged = false;
		if(status!=ConnectionResult.SUCCESS){
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}else {
			setUpMapIfNeeded();
			setUpMapType();
			geocoder=new Geocoder(this);
			queue = Volley.newRequestQueue(SettingsMapActivity.this);
			setUpSearchView();
			googleMap.setOnMarkerDragListener(new OnMarkerDragListener()
			{
				@Override
				public void onMarkerDragStart(Marker marker) {

				}
				@Override
				public void onMarkerDragEnd(Marker marker) {
					current_location_mark.setSnippet(marker.getPosition().latitude + "," + marker.getPosition().longitude);
					marker.showInfoWindow();
					current_location = marker.getPosition();
					isPositionChanged = true;
					sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LATITUDE, (float) marker.getPosition().latitude);
					sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LONGITUDE, (float) marker.getPosition().longitude);
				}

				@Override
				public void onMarkerDrag(Marker marker) {
				}
			});
		}
	}

	private void setUpMapType() {
		activity_header_mapsettings = (TextView) findViewById(R.id.activity_header_mapsettings);
		typeFaceClass.setTypefaceMed(activity_header_mapsettings);

		tapmap= (TextView) findViewById(R.id.tapmap);
		typeFaceClass.setTypefaceNormal(tapmap);

		searchmap= (TextView) findViewById(R.id.searchmap);
		typeFaceClass.setTypefaceNormal(searchmap);

		searchmap.setSelected(true);

		searchmap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchmap.setSelected(true);
				tapmap.setSelected(false);
				searchLayout.setVisibility(View.VISIBLE);
				current_location_mark.setDraggable(false);
			}
		});

		tapmap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchmap.setSelected(false);
				tapmap.setSelected(true);
				searchLayout.setVisibility(View.GONE);
				current_location_mark.setDraggable(true);

			}
		});
	}

	private void setUpMapIfNeeded() {
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		googleMap = fm.getMap();
		if (googleMap != null) {
			googleMap.getUiSettings().setMapToolbarEnabled(false);
			setUpMap();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}


	private void setUpSearchView() {
		ListView listView = (ListView)findViewById(R.id.sugglistView);
		listView.setClickable(false);

		searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
		SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView)findViewById(R.id.searchView);
		if (null != searchView)
		{
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
			searchView.setFocusable(true);
			int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
			View searchPlate = searchView.findViewById(searchPlateId);
			searchPlate.setBackgroundResource(R.color.white);
			searchView.setBackgroundColor(Color.DKGRAY);
			int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
			if (searchText != null) {
				searchText.setTextColor(Color.BLACK);
				typeFaceClass.setTypefaceNormal(searchText);
			}
		}

		SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
			public boolean onQueryTextChange(String newText) {
				setvolley(newText);
				return true;
			}

			public boolean onQueryTextSubmit(String query) {
				try {
					setvolley(query);
					doMySearch(query);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		searchView.setOnQueryTextListener(queryTextListener);

		SearchView.OnSuggestionListener querySuggestionListener = new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int position) {
				return true;
			}

			@Override
			public boolean onSuggestionClick(int position) {
				return true;
			}
		};
		searchView.setOnSuggestionListener(querySuggestionListener);
	}

	private void doMySearch(String query) throws IOException {
		List<Address> list = geocoder.getFromLocationName(query, 10);
		if(list!=null && list.size() > 0) {
			Address add = list.get(0);
			LatLng location = new LatLng(add.getLatitude(), add.getLongitude());

			current_location = location;
			setcurrentlocationmark(true);
			String locality = add.getFeatureName();
			Toast.makeText(SettingsMapActivity.this, locality, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(SettingsMapActivity.this, "Could not fetch location coordinates...", Toast.LENGTH_LONG).show();
		}
	}

	private void setcurrentlocationmark(boolean isSearch)
	{
		if (googleMap == null)
		{
			setUpMapIfNeeded();
		}
		isPositionChanged = true;
		sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LATITUDE, (float) current_location.latitude);
		sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LONGITUDE, (float) current_location.longitude);

		if (current_location_mark != null)
			current_location_mark.remove();

		current_location_mark = googleMap.addMarker(new MarkerOptions()
				.position(current_location)
				.title("You are here!!")
				.snippet(current_location.latitude + "," + current_location.longitude)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location, 10));
	}

	private void setvolley(String query) {
		StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
		sb.append("?key=" + API_KEY);
		try {
			sb.append("&input=" + URLEncoder.encode(query, "utf8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = sb.toString();

		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				JSONObject jsonObj = null;
				try {
					jsonObj = new JSONObject(response);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				JSONArray predsJsonArray = null;
				try {
					predsJsonArray = jsonObj.getJSONArray("predictions");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// Extract the Place descriptions from the results
				ArrayList<String> resultList = new ArrayList<String>(predsJsonArray.length());
				for (int i = 0; i < predsJsonArray.length(); i++) {
					try {
						if (!predsJsonArray.getJSONObject(i).getString("types").contentEquals("country"))
							resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				ArrayAdapter arrayAdapter;
				final ListView listView = (ListView)findViewById(R.id.sugglistView);
				listView.setClickable(true);
				arrayAdapter = new ArrayAdapter(SettingsMapActivity.this, R.layout.setlanguage, R.id.tvName, resultList);
				listView.setVisibility(View.VISIBLE);

				listView.setAdapter(arrayAdapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						String value = (String) parent.getItemAtPosition(position);
						listView.setAdapter(null);
						Toast.makeText(SettingsMapActivity.this, value, Toast.LENGTH_LONG).show();
						listView.setClickable(false);
						try {
							doMySearch(value);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		queue.add(stringRequest);
	}

	private void setUpMap() {

		// the camera will be positioned according to the new coordinates
		float latitude = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
		float longitude = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);
		STARTING_MARKER_POSITION = new LatLng(latitude,longitude);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(STARTING_MARKER_POSITION, 10));
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng)
			{
			}
		});

		current_location_mark = googleMap.addMarker(new MarkerOptions()
					.position(STARTING_MARKER_POSITION)
					.title("You are here!!")
					.snippet(latitude + "," + longitude)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
	}

	@Override
	public void onBackPressed() {
		if(isPositionChanged) {
			fetchDataAgain();
		}else{
			finish();
		}
	}

	private void fetchDataAgain(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialog.show(SettingsMapActivity.this, getResources().getString(R.string.progress_title), getResources().getString(R.string.progress_message), false);
						progressDialog.setCancelable(false);
					}
				});
				new SyncApplicationData(sharedPreferenceUtil).syncData();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						finish();
					}
				});
			}
		}).start();
	}
}