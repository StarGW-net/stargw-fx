package net.stargw.fox;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FOXRateAlertAdapter extends ArrayAdapter<FOXRateAlertRecord> implements Filterable
{

    private Context context;

    private ArrayList<FOXRateAlertRecord> appsSorted; // for filtering
    private ArrayList<FOXRateAlertRecord> appsOriginal; // for filtering

    public FOXRateAlertAdapter(Context context, ArrayList<FOXRateAlertRecord> items) {
        // super(context, 0, items);   // this causes the filter to modify the actual ArrayList passed in!!!
        super(context,R.layout.row_fullview3);
        this.context = context;

        this.appsOriginal = new ArrayList<FOXRateAlertRecord>();
        this.appsSorted = new ArrayList<FOXRateAlertRecord>();

        this.appsSorted.addAll(items);
        this.appsOriginal.addAll(items);

        // Global.Log("HERE",3);
    }


    public void reset(ArrayList<FOXRateAlertRecord> items)
    {
        // Global.Log("HERE - change data",3);
        notifyDataSetInvalidated();
        this.appsSorted.clear();
        this.appsSorted.addAll(items);
        this.appsOriginal.clear();
        this.appsOriginal.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return appsSorted.size();
    }

    @Override
    public FOXRateAlertRecord getItem(int position) {
        return appsSorted.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // if (GPXRecord != null) && position >= GPXRecord.six

        FOXRateAlertRecord rateAlert = getItem(position);

        try {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ratealerts, parent, false);
            }
            // Lookup view for data population
            TextView text1 = (TextView) convertView.findViewById(R.id.countryCode1);
            text1.setText(rateAlert.code1);

            ImageView im1 = (ImageView) convertView.findViewById(R.id.countryFlag1);
            im1.setImageResource(Global.getFlag(rateAlert.code1));

            TextView text2 = (TextView) convertView.findViewById(R.id.countryCode2);
            text2.setText(rateAlert.code2);

            ImageView im2 = (ImageView) convertView.findViewById(R.id.countryFlag2);
            im2.setImageResource(Global.getFlag(rateAlert.code2));

            TextView text3 = (TextView) convertView.findViewById(R.id.rateAmount);
            text3.setText(String.format(java.util.Locale.US,"%.3f", rateAlert.value)  );

            // Return the completed view to render on screen
            return convertView;

        } catch (IndexOutOfBoundsException e) {
            Global.Log("Array out of bounds in adaptor",1);
            return null;
        }
    }

    /*
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
*/

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                appsSorted = (ArrayList<FOXRateAlertRecord>)results.values;
                notifyDataSetChanged();
                clear();

                if (appsSorted != null) {
                    for(int i = 0, l = appsSorted.size(); i < l; i++) {
                        add(appsSorted.get(i));
                    }
                }
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
                    ArrayList<FOXRateAlertRecord> filteredItems = new ArrayList<FOXRateAlertRecord>();

                    for(int i = 0, l = appsOriginal.size(); i < l; i++)
                    {
                        FOXRateAlertRecord app = appsOriginal.get(i);
                        if( (app.code1.toLowerCase().contains(constraint)) ||
                                (app.code2.toLowerCase().contains(constraint))  ) {
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
