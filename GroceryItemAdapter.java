package com.example.myapp.grocery;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.grocery.GroceryItem;

import java.util.List;

public class GroceryItemAdapter extends ArrayAdapter<GroceryItem> {
    public GroceryItemAdapter(Context context, List<GroceryItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_grocery, parent, false);
        }

        GroceryItem item = getItem(position);

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        TextView textViewItem = convertView.findViewById(R.id.textViewItem);

        textViewItem.setText(item.getName());
        checkBox.setChecked(item.isChecked());

        // Set strikethrough text if the item is checked
        if (item.isChecked()) {
            textViewItem.setPaintFlags(textViewItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textViewItem.setPaintFlags(textViewItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        checkBox.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            item.setChecked(isChecked);

            // Update the text view appearance based on the checkbox state
            if (isChecked) {
                textViewItem.setPaintFlags(textViewItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textViewItem.setPaintFlags(textViewItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            notifyDataSetChanged(); // Refresh the list view
        });

        return convertView;
    }
}