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
                    CheckBoxPreference pref1 = (CheckBoxPreference) findPreference("use_open");
                    // your code here
                    if ((boolean) newValue == false)
                    {
                        pref1.setEnabled(true);
                        pref1.setChecked(true);
                    } else {
                        // pref1.setEnabled(false);
                        pref1.setChecked(false);
                    }
                    // preference.setEnabled((boolean) newValue);
                    CheckBoxPreference c = (CheckBoxPreference) preference;
                    c.setChecked((boolean) newValue);


                    return (boolean) newValue;
                }
            });



            pref2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    CheckBoxPreference pref1 = (CheckBoxPreference) findPreference("use_ecb");
                    // your code here
                    if ((boolean) newValue == false)
                    {
                        pref1.setEnabled(true);
                        pref1.setChecked(true);
                    } else {
                        // pref1.setEnabled(false);
                        pref1.setChecked(false);
                    }
                    // preference.setEnabled((boolean) newValue);
                    CheckBoxPreference c = (CheckBoxPreference) preference;
                    c.setChecked((boolean) newValue);
                    SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                    String api = SP.getString("openexchange","none");
                    if (!(api.equalsIgnoreCase("none")))
                    {
                        Global.Log("Checkbox changed updating OER!",3);
                        Global.updateCurrency();
                        Global.setAlarm();
                    } else {
                        Global.Log("Rate feed changed to OER but key is still none!",3);
                    }
                    return (boolean) newValue;
                }
            });

            Preference key = findPreference("openexchange");

            key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String k = (String) newValue;
                    if (!(k.equalsIgnoreCase("none")))
                    {
                        EditTextPreference c = (EditTextPreference) preference;
                        c.setText(k);
                        Global.Log("Key changed updating OER!",3);
                        Global.updateCurrency(); // async
                        Global.setAlarm();
                    } else {
                        Global.Log("Key is still none!",3);
                    }
                    return true;
                }
            });

            Preference battery = findPreference("battery");
            battery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Use the documented action string:
/*
                    if (Build.MANUFACTURER == "samsung") {
                        val intent = Intent()
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                            intent.component = ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity");
                        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            intent.component = ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity");
                        }
                    }
                } else {
*/
                    // startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS));
/*
                    Intent tetherSettings = new Intent();
                    tetherSettings.setClassName("com.android.settings", "com.android.settings.SubSettings");

                    startActivity(tetherSettings);
*/

                    startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));

                    // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), );

                    // startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));

                    /*
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + "net.stargw.fx"));
                    startActivity(intent);
*/

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

