package at.htlgkr.tournamaker.Preferences;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import at.htlgkr.tournamaker.R;


public class MySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
