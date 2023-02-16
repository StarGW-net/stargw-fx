package net.stargw.fx;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by swatts on 17/11/15.
 */
public class FOXCurrencyAdapter extends ArrayAdapter<String> {

    private Context context;

    private TreeMap<String, Float> treeMap;
    private String[] mapKeys;

    ActivityMainListener listener;

    /*
    private TreeMap<String, Float> appsSorted; // for filtering
    private TreeMap<String, Float> appsOriginal; // for filtering
    */

    private ArrayList<FOXCurrencyRecord> appsSorted; // for filtering
    private ArrayList<FOXCurrencyRecord> appsOriginal; // for filtering

    // ActivityMainListener listener;

    public FOXCurrencyAdapter(Context context, TreeMap<String, Float> apps) {
        super(context, R.layout.activity_main);

        this.context = context;
        // this.treeMap = apps;

        listener = (ActivityMain) context;

        // mapKeys = treeMap.descendingKeySet().toArray(new String[getCount()]);

        // this.appsOriginal = new TreeMap<String, Float> (new FOXRecordSort());
        this.appsOriginal = new ArrayList<FOXCurrencyRecord>();

        treeMap = new TreeMap<String, Float>();

        treeMap.putAll(apps); // copy of the TreeMap
        mapKeys = treeMap.keySet().toArray(new String[getCount()]);


        for(int i = 0, l = getCount(); i < l; i++) {
            String code = getItem(i);
            FOXCurrencyRecord z = new FOXCurrencyRecord();
            z.code = code;
            z.value = treeMap.get(code);
            this.appsOriginal.add(z); // .put(code,0f); // don't care about the value for the filter
        }

    }

    public int getCount() {
        return treeMap.size();
    }

    public String getItem(int position) {
        // return String.valueOf(treeMap.get(mapKeys[treeMap.size()-position]));
        return mapKeys[position];
    }

    public String getValue(int position) {
        // return String.valueOf(treeMap.get(mapKeys[treeMap.size()-position]));
        return String.valueOf(treeMap.get(mapKeys[position]));
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // if (GPXRecord != null) && position >= GPXRecord.six

        try {

            FOXCurrencyRecord fox = new FOXCurrencyRecord();

            fox.value = treeMap.get(mapKeys[position]);
            fox.code = mapKeys[position];

            final String myCode = fox.code;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_fullview3, parent, false);
            }
            // Lookup view for data population
            TextView text1 = (TextView) convertView.findViewById(R.id.rowText1);
            text1.setText(fox.code);

            TextView text3 = (TextView) convertView.findViewById(R.id.rowTextValue);
            if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(fox.code)) {
                float f1 = Global.FOXCurrencyAll.get(fox.code) / Global.FOXCurrencyAll.get(Global.getBaseCurrency())  * Global.getBaseCurrencyAmount();
                text3.setText(String.format(java.util.Locale.US,"%.3f", f1)  );
            } else {
                text3.setText("0");
            }



            TextView text4 = (TextView) convertView.findViewById(R.id.rowText2);
            if (Global.CodeToCurrency.containsKey(fox.code))
            {
                text4.setText(context.getString(Global.CodeToCurrency.get(fox.code)));
            } else {
                text4.setText("");
            }

            SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
            boolean ui_inverse = (SP.getBoolean("ui_inverse",true));

            TextView text5 = (TextView) convertView.findViewById(R.id.rowTextValue2);
            if (ui_inverse == true) {
                if (Global.getBaseCurrencyAmount() != 1f) {
                    if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(fox.code)) {
                        float f1 = Global.FOXCurrencyAll.get(fox.code) / Global.FOXCurrencyAll.get(Global.getBaseCurrency());  // * Global.getBaseCurrencyAmount();
                        text5.setText("1" + Global.getBaseCurrency() + " = " + String.format(java.util.Locale.US, "%.3f", f1));
                    } else {
                        text5.setText("0");
                    }
                } else {
                    if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(fox.code)) {
                        float f1 = Global.FOXCurrencyAll.get(Global.getBaseCurrency()) / Global.FOXCurrencyAll.get(fox.code);  // * Global.getBaseCurrencyAmount();
                        text5.setText("1" + fox.code + " = " + String.format(java.util.Locale.US, "%.3f", f1));
                    } else {
                        text5.setText("unknown");
                    }
                }
            } else {
                text5.setText("");
            }

            ImageView im = (ImageView) convertView.findViewById(R.id.flag);

            im.setImageResource(Global.getFlag(fox.code));


            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // listener.changeSelectedItem(pos); // STEVE ADD HERE
                    listener.displayOptions(myCode);
                }
            });

            LinearLayout l1 = (LinearLayout) convertView.findViewById(R.id.rowMiddle);
            l1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // listener.changeSelectedItem(pos); // STEVE ADD HERE
                    listener.displayOptions(myCode);
                }
            });

            LinearLayout l2 = (LinearLayout) convertView.findViewById(R.id.rowLayoutValue);
            l2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // listener.changeSelectedItem(pos); // STEVE ADD HERE
                    listener.enterAmount(myCode);
                }
            });

            // Return the completed view to render on screen
            return convertView;

        } catch (IndexOutOfBoundsException e) {
            Global.Log("Array out of bounds in adaptor",1);
            return null;
        }
    }



    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                appsSorted = (ArrayList<FOXCurrencyRecord>)results.values;
                notifyDataSetChanged();
                // clear();
                treeMap.clear();

                if (appsSorted != null) {

                    // mapKeys = new String[appsSorted.size()+1];

                    for(int i = 0, l = appsSorted.size(); i < l; i++) {
                        FOXCurrencyRecord k = appsSorted.get(i);
                        treeMap.put(k.code, k.value);
                    }
                }
                mapKeys = treeMap.keySet().toArray(new String[getCount()]);
                notifyDataSetInvalidated();

                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                Global.Log("Filter stuff",3);

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<FOXCurrencyRecord> filteredItems = new ArrayList<FOXCurrencyRecord>();

                    for(int i = 0, l = appsOriginal.size(); i < l; i++)
                    {
                        FOXCurrencyRecord app = appsOriginal.get(i);
                        if( (app.code.toString().toLowerCase().contains(constraint) ) ) {
                            // ) || (app.packageName.toString().toLowerCase().contains(constraint)) ) {
                            filteredItems.add(app);
                        }

                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
                    synchronized(this)
                    {
                        result.values = appsOriginal;
                        result.count = appsOriginal.size();
                    }
                }
                return result;
            }

        };

        return filter;
    }


}
