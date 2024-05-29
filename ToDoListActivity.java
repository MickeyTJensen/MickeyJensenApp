package com.example.myapp.todo;


import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.myapp.R;
import com.example.myapp.todo.ToDoItem;
import com.example.myapp.todo.ToDoItemAdapter;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private EditText editTextToDoItem;
    private Button buttonAddItem;
    private ListView listView;
    private List<ToDoItem> toDoItems;
    private ToDoItemAdapter adapter;
    private Button buttonResetItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        editTextToDoItem = findViewById(R.id.editTextToDoItem);
        buttonAddItem = findViewById(R.id.buttonAddItem);
        listView = findViewById(R.id.listViewToDo);
        buttonResetItems = findViewById(R.id.buttonResetItems);

        toDoItems = new ArrayList<>();
        adapter = new ToDoItemAdapter(this, toDoItems);
        listView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = editTextToDoItem.getText().toString();
                if (!itemName.isEmpty()) {
                    toDoItems.add(new ToDoItem(itemName));
                    adapter.notifyDataSetChanged(); // Refresh the list
                    editTextToDoItem.setText(""); // Clear the input field
                }
            }
        });

        buttonResetItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDoItems.clear(); // Clear the list
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
        String json = gson.toJson(toDoItems);
        editor.putString("todo_list", json);
        editor.apply();
    }

    private void loadList() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("todo_list", null);
        Type type = new TypeToken<ArrayList<ToDoItem>>() {}.getType();
        toDoItems = gson.fromJson(json, type);

        if (toDoItems == null) {
            toDoItems = new ArrayList<>();
        }

        adapter = new ToDoItemAdapter(this, toDoItems);
        listView.setAdapter(adapter);
    }
}