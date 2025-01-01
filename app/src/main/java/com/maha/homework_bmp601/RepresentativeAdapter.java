package com.maha.homework_bmp601;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class RepresentativeAdapter extends android.widget.BaseAdapter {

    private Context context;
    private ArrayList<Representative> representatives;
    private DatabaseHelper databaseHelper;

    public RepresentativeAdapter(Context context, ArrayList<Representative> representatives, DatabaseHelper databaseHelper) {
        this.context = context;
        this.representatives = representatives;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public int getCount() {
        return representatives.size();
    }

    @Nullable
    @Override
    public Representative getItem(int position) {
        return representatives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return representatives.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.representative_item, parent, false);
        }

        // ربط عناصر واجهة المستخدم
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtPhone = convertView.findViewById(R.id.txtPhone);
        TextView txtRegion = convertView.findViewById(R.id.txtRegion);
        ImageView imgProfile = convertView.findViewById(R.id.imgProfile);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        // تعبئة البيانات
        Representative representative = getItem(position);
        txtName.setText(representative.getName());
        txtPhone.setText(representative.getPhone());
        txtRegion.setText(representative.getRegion());
        // استخدم مكتبة عرض الصور مثل Glide أو Picasso
        imgProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image)); // صورة افتراضية

        // زر التعديل
        btnEdit.setOnClickListener(v -> showEditDialog(representative));

        // زر الحذف
        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("حذف المندوب");
            builder.setMessage("هل أنت متأكد أنك تريد حذف هذا المندوب؟");
            builder.setPositiveButton("نعم", (dialog, which) -> {
                boolean isDeleted = databaseHelper.deleteRepresentative(representative.getId());
                if (isDeleted) {
                    representatives.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "تم حذف المندوب بنجاح", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "فشل في حذف المندوب", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("إلغاء", null);
            builder.show();
        });

        return convertView;
    }
}
