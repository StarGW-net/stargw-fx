package net.stargw.fx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class MySettingsActivity extends AppCompatActivity {

    public static class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);


            Preference pref1 = findPreference("use_ecb");
            Preference pref2 = findPreference("use_open");

            pref1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = (boolean) newValue;
                    CheckBoxPreference pref2 = (CheckBoxPreference) findPreference("use_open");
                    if (val) {
                        pref2.setChecked(false);
                    } else {
                        pref2.setChecked(true);
                    }

                    CheckBoxPreference c = (CheckBoxPreference) preference;
                    c.setChecked(val);

                    SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                    SP.edit().putBoolean("use_ecb", val).putBoolean("use_open", !val).apply();

                    Global.Log("Checkbox changed updating ECB!",3);
                    Global.updateCurrency();
                    Global.setUpdateCurrencySchedule();

                    return val;
                }
            });



            pref2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = (boolean) newValue;
                    CheckBoxPreference pref1 = (CheckBoxPreference) findPreference("use_ecb");
                    if (val) {
                        pref1.setChecked(false);
                    } else {
                        pref1.setChecked(true);
                    }

                    CheckBoxPreference c = (CheckBoxPreference) preference;
                    c.setChecked(val);

                    SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                    SP.edit().putBoolean("use_ecb", !val).putBoolean("use_open", val).apply();

                    String api = SP.getString("openexchange","none");
                    if (val) {
                        if (!(api.equalsIgnoreCase("none")))
                        {
                            Global.Log("Checkbox changed updating OER!",3);
                            Global.updateCurrency();
                            Global.setUpdateCurrencySchedule();
                        } else {
                            Global.Log("Rate feed changed to OER but key is still none!",3);
                        }
                    } else {
                        Global.Log("Checkbox changed updating ECB!",3);
                        Global.updateCurrency();
                        Global.setUpdateCurrencySchedule();
                    }
                    return val;
                }
            });

            Preference key = findPreference("openexchange");

            key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String k = (String) newValue;
                    
                    EditTextPreference c = (EditTextPreference) preference;
                    c.setText(k);
                    
                    SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                    SP.edit().putString("openexchange", k).apply();

                    if (!(k.equalsIgnoreCase("none")))
                    {
                        Global.Log("Key changed updating OER!",3);
                        Global.updateCurrency(); // async
                        Global.setUpdateCurrencySchedule();
                    } else {
                        Global.Log("Key is still none!",3);
                    }
                    return true;
                }
            });
            /*
            Preference battery = findPreference("battery");
            battery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Global.openBatterySettings(getContext());
                    return false;
                }

            });
            */
            Preference perms = findPreference("app_permissions2");
            perms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Global.Log("ACTION_APPLICATION_DETAILS_SETTINGS", 2);

                    Intent intent = new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + Global.getContext().getPackageName())
                    );

                    startActivity(intent);
                    return false;
                }

            });

            Preference notifications = findPreference("notifications");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            {
                notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + Global.getContext().getPackageName()));
                        startActivity(intent);
                        return false;
                    }
                });
            } else {
                notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Use the documented action string:
                        Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, Global.getContext().getPackageName())
                                .putExtra(Settings.EXTRA_CHANNEL_ID, 0); //  MY_CHANNEL_ID
                        startActivity(settingsIntent);
                        return false;
                    }
                });
            }

/*

*/

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceFragmentCompat mySettingsFragment = new MySettingsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, mySettingsFragment)
                .commit();

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        setTitle("Settings");


        // CheckBoxPreference pref1 = (CheckBoxPreference)  mySettingsFragment.findPreference("use_ecb");




        // Everything else is done in the XML file

    }




}

