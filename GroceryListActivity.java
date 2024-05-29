package com.example.myapp.grocery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.myapp.R;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private EditText editTextGroceryItem;
    private Button buttonAddItem;
    private ListView listView;
    private List<GroceryItem> groceryItems;
    private GroceryItemAdapter adapter;
    private Button buttonResetItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        editTextGroceryItem = findViewById(R.id.editTextGroceryItem);
        buttonAddItem = findViewById(R.id.buttonAddItem);
        listView = findViewById(R.id.listViewGrocery);
        buttonResetItems = findViewById(R.id.buttonResetItems);

        groceryItems = new ArrayList<>();
        adapter = new GroceryItemAdapter(this, groceryItems);
        listView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = editTextGroceryItem.getText().toString();
                if (!itemName.isEmpty()) {
                    groceryItems.add(new GroceryItem(itemName));
                    adapter.notifyDataSetChanged(); // Refresh the list
                    editTextGroceryItem.setText(""); // Clear the input field
                }
            }
        });

        buttonResetItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groceryItems.clear(); // Clear the list
                adapter.notifyDataSetChanged(); // Notify adapter of the change
                saveList(); // Save the now empty list
            }
        });

        }
    @Override
    protected void onPause() {
        super.onPause();
        saveList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void saveList() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(groceryItems);
        editor.putString("grocery_list", json);
        editor.apply();
    }

    private void loadList() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("grocery_list", null);
        Type type = new TypeToken<ArrayList<GroceryItem>>() {}.getType();
        groceryItems = gson.fromJson(json, type);

        if (groceryItems == null) {
            groceryItems = new ArrayList<>();
        }

        adapter = new GroceryItemAdapter(this, groceryItems);
        listView.setAdapter(adapter);
    }
}