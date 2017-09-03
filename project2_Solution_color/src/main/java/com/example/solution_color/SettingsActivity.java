package com.example.solution_color;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	private EditTextPreference sketchiness;
	private EditTextPreference saturation;

	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		PrefsFragment mPrefsFragment = new PrefsFragment();
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();





	  }
	
	public static class PrefsFragment extends PreferenceFragment {
		private EditTextPreference sketchiness;
		private EditTextPreference saturation;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);



			sketchiness = (EditTextPreference)findPreference("Share Subject");

			saturation = (EditTextPreference)findPreference("Share Text");




		}


	}
}
