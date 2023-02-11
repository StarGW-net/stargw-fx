package net.stargw.fox;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by swatts on 17/11/15.
 */
public class LogAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> appsSorted; // for filtering
    private ArrayList<String> appsOriginal; // for filtering

    public LogAdapter(Context context, ArrayList<String> apps) {
        super(context, 0, apps);

        this.appsOriginal = new ArrayList<String>();
        for(int i = 0, l = getCount(); i < l; i++) {
            String app = getItem(i);
            this.appsOriginal.add(app);
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String apps = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_logs_row2, parent, false);
        }

        TextView text1 = (TextView) convertView.findViewById(R.id.activity_logs_row_text);
        TextView date1 = (TextView) convertView.findViewById(R.id.activity_logs_row_date);
        LinearLayout row = (LinearLayout) convertView.findViewById(R.id.activity_logs_row_all);

        String d = "";
        String t = "";

        // Try to figure out what sort of log it it
        int logType = 0;
        if(apps.length() > 18) {
            if (apps.charAt(14) == ' ') {
                logType = 1;
            } else {
                if (Character.isDigit(apps.charAt(0))) {
                    logType = 2;
                }
            }
        }

        switch (logType) {
            case 1:
                char level = apps.charAt(15);
                d = apps.substring(0, 14);
                t = apps.substring(17);

                if ((Global.getLogLevel() > 1) && (level != '0' )) {
                    date1.setText(d + "    [level = " + level + "]");
                } else {
                    date1.setText(d);
                }
                text1.setText(t);
                break;

            case 2: // This is an ADB log
                d = apps.substring(0, 14);
                t = apps.substring(19);

                date1.setText(d);
                text1.setText(t);
                break;

            default:
                date1.setText("");
                date1.setVisibility(View.GONE);
                text1.setText(d + " >>>" + apps);
                break;

        }

        return convertView;

    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                appsSorted = (ArrayList<String>)results.values;
                notifyDataSetInvalidated();
                clear();
                for(int i = 0, l = appsSorted.size(); i < l; i++)
                    add(appsSorted.get(i));

                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<String> filteredItems = new ArrayList<String>();

                    for(int i = 0, l = appsOriginal.size(); i < l; i++)
                    {
                        String app = appsOriginal.get(i);
                        // Logs.myLog("Filter match: " + constraint + " v " + app , 3);
                        if( (app.toLowerCase().contains(constraint) ) ) {
                        // ) || (app.packageName.toString().toLowerCase().contains(constraint)) ) {
                            filteredItems.add(app);
                            // Logs.myLog("Add match: " + app, 3);
                        } else {
                            if(app.length() > 18) {
                                char level = app.charAt(15);
                                String extra = "level = " + level;
                                if (extra.toLowerCase().contains(constraint))
                                {
                                    filteredItems.add(app);
                                }
                            }
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
