package com.maha.homework_bmp601;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * Custom adapter for displaying delegate information in a ListView.
 */
public class DelegateAdapter extends android.widget.BaseAdapter {

    private Context context;
    private ArrayList<Delegate> delegateList;
    private OnDelegateSelectListener onDelegateSelectListener;

    // Interface to handle delegate selection
    public interface OnDelegateSelectListener {
        void onDelegateSelected(Delegate delegate);
    }

    public DelegateAdapter(Context context, ArrayList<Delegate> delegateList, OnDelegateSelectListener onDelegateSelectListener) {
        this.context = context;
        this.delegateList = delegateList;
        this.onDelegateSelectListener = onDelegateSelectListener;
    }

    @Override
    public int getCount() {
        return delegateList.size();
    }

    @Nullable
    @Override
    public Delegate getItem(int position) {
        return delegateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return delegateList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_delegate, parent, false);
        }

        // Bind UI components
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtNumber = convertView.findViewById(R.id.txtNumber);
        TextView txtArea = convertView.findViewById(R.id.txtArea);
        ImageView imgPhoto = convertView.findViewById(R.id.imgPhoto);
        LinearLayout layoutItem = convertView.findViewById(R.id.layoutItem);

        // Get delegate for the current position
        Delegate delegate = getItem(position);

        // Set delegate details
        txtName.setText(delegate.getName());
        txtNumber.setText(delegate.getPhone());
        txtArea.setText(delegate.getRegion());

        // Set placeholder image ()
//        imgPhoto.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.));

        // Handle click event to select a delegate
        layoutItem.setOnClickListener(v -> onDelegateSelectListener.onDelegateSelected(delegate));

        return convertView;
    }
}