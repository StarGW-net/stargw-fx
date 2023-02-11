package net.stargw.fox;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FOXCountryListAdapter extends ArrayAdapter<String>
{

    private Context context;

    private ArrayList<String> appsSorted; // for filtering
    private ArrayList<String> appsOriginal; // for filtering

    public FOXCountryListAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);

        this.context = context;

        this.appsOriginal = new ArrayList<String>();

        for(int i = 0, l = getCount(); i < l; i++) {
            String code = getItem(i);
            this.appsOriginal.add(code);
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // if (GPXRecord != null) && position >= GPXRecord.six

        String code = getItem(position);

        try {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_quickview, parent, false);
            }
            // Lookup view for data population
            TextView text1 = (TextView) convertView.findViewById(R.id.rowText1);
            text1.setText(code);

            ImageView im = (ImageView) convertView.findViewById(R.id.flag);
            im.setImageResource(Global.getFlag((code)));

            TextView text4 = (TextView) convertView.findViewById(R.id.rowText2);
            if (Global.CodeToCurrency.containsKey(code))
            {
                text4.setText(context.getString(Global.CodeToCurrency.get(code)));
            } else {
                text4.setText("");
            }

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

                appsSorted = (ArrayList<String>)results.values;
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
                    ArrayList<String> filteredItems = new ArrayList<String>();

                    for(int i = 0, l = appsOriginal.size(); i < l; i++)
                    {
                        String app = appsOriginal.get(i);
                        String name = "AAA";
                        if (Global.CodeToCurrency.containsKey(app.toUpperCase())) {
                            int z = Global.CodeToCurrency.get(app.toUpperCase());
                            name = context.getResources().getString(z).toLowerCase();
                        }
                        if( (app.toLowerCase().contains(constraint) ) ||
                                (name.toLowerCase().contains(constraint)) ) {
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
