package net.stargw.fx;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FOXCurrencyAdapter extends RecyclerView.Adapter<FOXCurrencyAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final ActivityMainListener listener;
    
    private final List<String> mapKeys;
    private final List<String> mapKeysFull; // For filtering
    private final Map<String, Float> treeMap;

    private ItemTouchHelper itemTouchHelper;
    private boolean showDragHandles = false;
    private boolean manualDragStarted = false;

    public FOXCurrencyAdapter(Context context, Map<String, Float> apps) {
        this.context = context;
        this.listener = (ActivityMainListener) context;
        this.treeMap = apps;
        this.mapKeys = new ArrayList<>(apps.keySet());
        this.mapKeysFull = new ArrayList<>(mapKeys);
    }

    public void updateData(Map<String, Float> apps) {
        this.treeMap.clear();
        this.treeMap.putAll(apps);
        this.mapKeys.clear();
        this.mapKeys.addAll(apps.keySet());
        this.mapKeysFull.clear();
        this.mapKeysFull.addAll(mapKeys);
        notifyDataSetChanged();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public void setShowDragHandles(boolean show) {
        if (this.showDragHandles != show) {
            this.showDragHandles = show;
            notifyDataSetChanged();
        }
    }

    public boolean isShowDragHandles() {
        return showDragHandles;
    }

    public void setManualDragStarted(boolean started) {
        this.manualDragStarted = started;
    }

    public boolean isManualDragStarted() {
        return manualDragStarted;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_fullview3, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String myCode = mapKeys.get(position);

        holder.text1.setText(myCode);

        if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(myCode)) {
            float f1 = Global.FOXCurrencyAll.get(myCode) / Global.FOXCurrencyAll.get(Global.getBaseCurrency()) * Global.getBaseCurrencyAmount();
            holder.textValue.setText(String.format(java.util.Locale.US, "%.3f", f1));
        } else {
            holder.textValue.setText("0");
        }

        if (Global.CodeToCurrency.containsKey(myCode)) {
            holder.text2.setText(context.getString(Global.CodeToCurrency.get(myCode)));
        } else {
            holder.text2.setText("");
        }

        SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        boolean ui_inverse = (SP.getBoolean("ui_inverse", true));

        if (ui_inverse) {
            if (Global.getBaseCurrencyAmount() != 1f) {
                if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(myCode)) {
                    float f1 = Global.FOXCurrencyAll.get(myCode) / Global.FOXCurrencyAll.get(Global.getBaseCurrency());
                    holder.textValue2.setText("1" + Global.getBaseCurrency() + " = " + String.format(java.util.Locale.US, "%.3f", f1));
                } else {
                    holder.textValue2.setText("0");
                }
            } else {
                if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(myCode)) {
                    float f1 = Global.FOXCurrencyAll.get(Global.getBaseCurrency()) / Global.FOXCurrencyAll.get(myCode);
                    holder.textValue2.setText("1" + myCode + " = " + String.format(java.util.Locale.US, "%.3f", f1));
                } else {
                    holder.textValue2.setText("unknown");
                }
            }
        } else {
            holder.textValue2.setText("");
        }

        holder.flag.setImageResource(Global.getFlag(myCode));

        holder.flag.setOnClickListener(v -> listener.displayOptions(myCode));
        holder.rowMiddle.setOnClickListener(v -> listener.displayOptions(myCode));
        holder.rowLayoutValue.setOnClickListener(v -> listener.enterAmount(myCode));

        holder.dragHandle.setVisibility(showDragHandles ? View.VISIBLE : View.GONE);

        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (itemTouchHelper != null) {
                    manualDragStarted = true;
                    itemTouchHelper.startDrag(holder);
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mapKeys.size();
    }

    public String getItem(int position) {
        return mapKeys.get(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mapKeys, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mapKeys, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        
        // Update the actual data source
        Map<String, Float> newMap = new LinkedHashMap<>();
        for (String k : mapKeys) {
            newMap.put(k, treeMap.get(k));
        }
        Global.FOXCurrencyMy = newMap;
        Global.writeFOXFile(Global.FOXCurrencyMy, Global.FILE_FOX_MY);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<String> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mapKeysFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String item : mapKeysFull) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mapKeys.clear();
                mapKeys.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2, textValue, textValue2;
        ImageView flag, dragHandle;
        LinearLayout rowMiddle, rowLayoutValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.rowText1);
            text2 = itemView.findViewById(R.id.rowText2);
            textValue = itemView.findViewById(R.id.rowTextValue);
            textValue2 = itemView.findViewById(R.id.rowTextValue2);
            flag = itemView.findViewById(R.id.flag);
            dragHandle = itemView.findViewById(R.id.drag_handle);
            rowMiddle = itemView.findViewById(R.id.rowMiddle);
            rowLayoutValue = itemView.findViewById(R.id.rowLayoutValue);
        }
    }
}
