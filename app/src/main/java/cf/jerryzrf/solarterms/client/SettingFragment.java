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
    private SharedPreferences sharedPreferences = null;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.perf_settings, rootKey);

        sharedPreferences = getContext().getSharedPreferences("default ", Context.MODE_PRIVATE);
        Preference loginDjiAccount = findPreference("login_dji_account");
        if (loginDjiAccount != null) {
            loginDjiAccount.setOnPreferenceClickListener(this);
        }
        ((SeekBarPreference) findPreference("photo_num")).setMax(20);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        System.out.println(222222);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (preference.getKey()) {
            case "cnDateFormat":
                editor.putBoolean("cnDateFormat", ((SwitchPreference) preference).isChecked());
                break;
            case "photo_num":
                editor.putInt("photo_num", ((SeekBarPreference) preference).getValue());
                break;
            default:
        }
        editor.apply();
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
