package com.example.mymaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements
		OnConnectionFailedListener, ConnectionCallbacks, LocationListener,
		com.google.android.gms.location.LocationListener, OnMapLongClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private static final int ERROR_RESULT = 0;
	GoogleMap map;
	EditText loc;
	Button go;
	GoogleApiClient myClient;
	Location myLocation;
	LocationRequest myrequest;
	Marker marker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isServiceOK()) {

			setContentView(R.layout.activity_map);
			if (init()) {
				Toast.makeText(this, "Maps ready!!", Toast.LENGTH_SHORT).show();
				// map.setMyLocationEnabled(true); used for current location
				// uses a lot of battery cause of gps
				// gotoLocation(26.832136, 80.944271, 15);

				myClient = new GoogleApiClient.Builder(this)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.addApi(LocationServices.API).build();

				myClient.connect();
				myrequest = LocationRequest.create()
						.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
						.setInterval(10 * 1000) // 10 seconds, in milliseconds
						.setFastestInterval(1 * 1000); // 1 second, in
														// milliseconds
				

			}

		} else {
			setContentView(R.layout.activity_main);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mapNormal) {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		} else if (item.getItemId() == R.id.mapSatellite) {
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else if (item.getItemId() == R.id.mapHybrid) {
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		} else if (item.getItemId() == R.id.mapTerrain) {
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		} else if (item.getItemId() == R.id.mapNone) {
			map.setMapType(GoogleMap.MAP_TYPE_NONE);
		} else if (item.getItemId() == R.id.mapLoc) {

			myLocation = LocationServices.FusedLocationApi
					.getLastLocation(myClient);

			if (myLocation != null) {

				handleNewLocation(myLocation);
			} else {
				LocationServices.FusedLocationApi.requestLocationUpdates(
						myClient, myrequest, this);
			}
		}
		return true;
	}

	@Override
	protected void onStart() {
		myClient.connect();
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean isServiceOK() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog d = GooglePlayServicesUtil.getErrorDialog(isAvailable, this,
					ERROR_RESULT);
			d.show();
		} else {
			Toast.makeText(this, "Can't connect to Maps!!", Toast.LENGTH_SHORT)
					.show();
		}
		return false;
	}

	private void gotoLocation(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate upd = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		map.moveCamera(upd);
		
		
	}

	public void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public void geoLocation(View v) {
		hideKeyboard(v);
		String location = loc.getText().toString();
		Geocoder gc = new Geocoder(this);
		try {
			List<Address> l = gc.getFromLocationName(location, 1);
			String locality = l.get(0).getLocality();
			String country = l.get(0).getCountryName();
			Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
			float lat =(float) l.get(0).getLatitude();
			float lon =(float) l.get(0).getLongitude();
			gotoLocation(lat, lon, 20);
			putMarker(locality,country,lat,lon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void putMarker(String locality,String country, float lat, float lon) {
		
		if(marker!=null){
			marker.remove();
		}
		MarkerOptions opt = new MarkerOptions()
							.title(locality)
							.position(new LatLng(lat, lon))
							.snippet(country)
							.draggable(true)
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
							//.icon(BitmapDescriptorFactory.fromResource(R.drawable.name));
		marker= map.addMarker(opt);
	}

	private boolean init() {
		if (map == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			map = mapFrag.getMap();
			loc = (EditText) findViewById(R.id.etadd);
			go = (Button) findViewById(R.id.bgo);
			if(map!=null){
			map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
				
				@Override
				public View getInfoWindow(Marker arg0) {
					
					return null;
				}
				
				@Override
				public View getInfoContents(Marker marker) {
					
					View v= getLayoutInflater().inflate(R.layout.info, null);
					
					TextView tvLocality = (TextView)v.findViewById(R.id.tvlocality);
					TextView tvlat = (TextView)v.findViewById(R.id.tvlat);
					TextView tvlon = (TextView)v.findViewById(R.id.tvlon);
					TextView tvcountry = (TextView)v.findViewById(R.id.tvcountry);
					if(tvLocality!=null)
					{LatLng position = marker.getPosition();
					tvLocality.setText(marker.getTitle());
					tvlat.setText(String.valueOf(position.latitude));
					tvlon.setText(String.valueOf(position.latitude));
					tvcountry.setText(marker.getSnippet());
					}
					return v;
					
					
				}
			});
			}
			
			map.setOnMapLongClickListener(this);
			map.setOnMarkerClickListener(this);
			map.setOnMarkerDragListener(this);
		}
		return (map != null);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MapStatemanager mgr = new MapStatemanager(this);
		CameraPosition pos = mgr.retrieveState(map);
		if (pos != null) {
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(pos);

			map.moveCamera(update);

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		myClient.disconnect();
		super.onStop();
		MapStatemanager mgr = new MapStatemanager(this);
		mgr.saveState(map);

	}

	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected!", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Toast.makeText(this, "Suspended!", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Failed!", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onLocationChanged(Location loc) {

		// TODO Auto-generated method stub
		handleNewLocation(loc);
	}

	private void handleNewLocation(Location loc) {
		// TODO Auto-generated method stub
		CameraUpdate up = CameraUpdateFactory.newLatLng(new LatLng(myLocation
				.getLatitude(), myLocation.getLongitude()));
		map.animateCamera(up);
		putMarker("Your Position", "Duh!", (float)loc.getLatitude(),(float)loc.getLongitude());

	}



	@Override
	public boolean onMarkerClick(Marker arg0) {
		LatLng ll = arg0.getPosition();
		arg0.showInfoWindow();
		Toast.makeText(this, arg0.getTitle()+"("+ll.latitude+","+ll.longitude+")", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onMapLongClick(LatLng ll) {
		Geocoder gc = new Geocoder(this);
		List<Address> list=null;
		try {
			list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Address add= list.get(0);
		marker.setTitle(add.getLocality());
		marker.setSnippet(add.getCountryName());
		putMarker(add.getLocality(), add.getCountryName(),(float)ll.latitude,(float)ll.longitude);
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDrag(Marker marker) {
	
		
	}

	@Override
	
	public void onMarkerDragEnd(Marker marker) {
		LatLng ll = marker.getPosition();
		Geocoder gc = new Geocoder(this);
		List<Address> list=null;
		try {
			list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
			Address add = list.get(0);
			marker.setTitle(add.getLocality());
			marker.setSnippet(add.getCountryName());
			putMarker(add.getLocality(),add.getCountryName(),(float)ll.latitude,(float)ll.longitude);
			marker.showInfoWindow();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}



}
