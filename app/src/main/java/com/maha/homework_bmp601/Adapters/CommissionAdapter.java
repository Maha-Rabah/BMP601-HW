package com.maha.homework_bmp601.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.maha.homework_bmp601.DataModel.Commission;

import java.util.ArrayList;

public class CommissionAdapter extends BaseAdapter {

    Context context;
    ArrayList<Commission> commissionList;
    private OnCommissionSelectListener onCommissionSelectListener;
    interface OnCommissionSelectListener {
        void onCommissionSelected(Commission commission);
    }
    public CommissionAdapter(Context context, ArrayList<Commission> commissionList, OnCommissionSelectListener onCommissionSelectListener) {
        this.context = context;
        this.commissionList = commissionList;
        this.onCommissionSelectListener = onCommissionSelectListener;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
