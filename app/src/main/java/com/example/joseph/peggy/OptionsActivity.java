/***************************************
 * 
 * Android Bluetooth Dual Joystick
 * yus - projectproto.blogspot.com
 * October 2012
 *  
 ***************************************/

package com.example.joseph.peggy;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OptionsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	}
	
}
