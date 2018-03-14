package com.patatascrucks.mobile.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.patatascrucks.mobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Project PatatasApp
 * Created by Ricardo on 8/7/2016.
 */
public abstract class SingleSelectionListAdapter extends RecyclerView.Adapter<SingleSelectionListAdapter.ViewHolder> {
    private Context mContext;
    private List<ArrayList<String>> mDataset;
    private SparseBooleanArray selectedItems;
    private int prev = -1;

    public SingleSelectionListAdapter(Context context, List<ArrayList<String>> dataset) {
        super();
        mContext = context;
        mDataset = dataset;
        selectedItems = new SparseBooleanArray();
    }

    public void add(ArrayList<String> item) {
        mDataset.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void insert(int position, ArrayList<String> item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(ArrayList<String> item) {
        mDataset.remove(item);
        notifyItemRemoved(getItemCount());
    }

    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(createView(mContext, parent, viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String> item = mDataset.get(position);

        bindView(mDataset.get(position), holder, selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else {
            return 0;
        }
    }

    public String getValueAt(int position, int column) {
        return mDataset.get(position).get(column);
    }

    public void setValueAt(Object value, int position) {
        ArrayList<String> item = mDataset.get(position);
        item.set(0, value.toString());
        item.set(3, value.toString().length() > 0 ? String.valueOf(Double.parseDouble(value.toString()) * Double.parseDouble(item.get(2))) : "0.00");
        notifyItemChanged(position);
    }

    public double getTotalValue() {
        double total = 0;
        for (ArrayList<String> item : mDataset) {
            total += Double.parseDouble(item.get(3));
        }
        return total;
    }

    public boolean toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            if (selectedItems.size() > 0) {
                selectedItems.delete(prev);
                notifyItemChanged(prev);
            }
            prev = pos;
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
        return selectedItems.get(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    protected abstract View createView(Context context, ViewGroup parent, int viewType);

    protected abstract void bindView(ArrayList<String> item, ViewHolder holder, boolean isSelected);

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Map<Integer, View> mMapView;

        ViewHolder(View rowView) {
            super(rowView);

            mMapView = new HashMap<>();
            mMapView.put(0, rowView);
        }

        void initViewById(int id) {
            View view = getView() != null ? getView().findViewById(id) : null;

            if (view != null) mMapView.put(id, view);
        }

        private View getView() {
            return getView(0);
        }

        public View getView(int id) {
            if (mMapView.containsKey(id)) {
                return mMapView.get(id);
            } else {
                initViewById(id);
            }

            return mMapView.get(id);
        }
    }
}
