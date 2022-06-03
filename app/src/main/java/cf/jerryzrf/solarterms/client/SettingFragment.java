package cf.jerryzrf.solarterms.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

/**
 * @author JerryZRF
 */
public class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.perf_settings, rootKey);
        ((SeekBarPreference) findPreference("photo_num")).setMax(20);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("cf.jerryzrf.solarterms_preferences", Context.MODE_PRIVATE).edit();
        switch (preference.getKey()) {
            case "cnDateFormat":
                editor.putBoolean("cnDateFormat", ((SwitchPreference) preference).isChecked()).apply();
                break;
            case "photo_num":
                editor.putInt("photo_num", ((SeekBarPreference) preference).getValue()).apply();
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "about":
                Intent intent = new Intent();
                intent.setClass(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }
}
