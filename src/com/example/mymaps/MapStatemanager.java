package com.example.mymaps;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;

public class MapStatemanager {
	private static final String LATTITUDE ="lattitude";
	private static final String LONGITUDE ="longitude";
	private static final String ZOOM ="zoom";
	private static final String TILT ="tilt";
	private static final String BEARING ="bearing";
	private static final String MAP_TYPE ="type";
	private static final String MAP_STATE="mapstate";
	
	SharedPreferences mapStates;
	
	public MapStatemanager(Context context){
		mapStates=context.getSharedPreferences(MAP_STATE,Context.MODE_PRIVATE);
	}
	
	public void saveState(GoogleMap map){
	CameraPosition position=map.getCameraPosition();	
	SharedPreferences.Editor editor = mapStates.edit();
	editor.putFloat(LATTITUDE,(float) position.target.latitude);
	editor.putFloat(LONGITUDE,(float) position.target.longitude);
	editor.putFloat(ZOOM, position.zoom);
	editor.putFloat(BEARING,position.bearing );
	editor.putFloat(TILT, position.tilt);
	editor.putInt(MAP_TYPE, map.getMapType());
	editor.commit();
	
	}
	
	
	
	public CameraPosition retrieveState(GoogleMap map){
		
		
		double lat= mapStates.getFloat(LATTITUDE, 0);
		if(lat==0){
			return null;
		}
		double lon= mapStates.getFloat(LONGITUDE, 0);
		float bear= mapStates.getFloat(BEARING, 0);
		float tilt= mapStates.getFloat(TILT, 0);
		float zoom= mapStates.getFloat(ZOOM, 0);
		
		int type = mapStates.getInt(MAP_TYPE, 0);
		map.setMapType(type);
		
		
		LatLng ll = new LatLng(lat, lon);
		CameraPosition position = new CameraPosition(ll, zoom, tilt, bear);
		return position;
		
		
		
		
	}

}
