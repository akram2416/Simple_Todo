package com.example.simpletodo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

        public static final String KEY_ITEM_TEXT = "item text";
        public static final String KEY_ITEM_POSITION ="item position";
        public static final int EDIT_TEXTT_CODE=20;


    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

      ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener(){
           @Override
           public void onItemLongClicked(int position) {
                //delete
               items.remove(position);
               itemsAdapter.notifyItemRemoved(position);
               Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
               saveItems();
           }
       };

      ItemsAdapter.OnClickListener onClickListener= new ItemsAdapter.OnClickListener() {
          @Override
          public void onItemClicked(int position) {
              Log.d("Main Activity", "Single click at Position" + position);
            // create new activity
              Intent i= new Intent (MainActivity.this, EditActivity.class);


              // pass the relevant data


              i.putExtra(KEY_ITEM_TEXT, items.get(position));
              i.putExtra(KEY_ITEM_POSITION, position);




              // display the activity

            startActivityForResult(i, EDIT_TEXTT_CODE);


          }
      };
      itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
      rvItems.setAdapter(itemsAdapter);
      rvItems.setLayoutManager(new LinearLayoutManager(this));

      btnAdd.setOnClickListener(v -> {

          String todoItem =  etItem.getText().toString();
          items.add(todoItem);
          itemsAdapter.notifyItemInserted(items.size()-1);
          etItem.setText("");
          Toast.makeText(getApplicationContext(), "Item was Added", Toast.LENGTH_SHORT).show();

        saveItems();

      });

    }

   @Override
   protected void onActivityResult (int requestcode, int resultCode,@Nullable Intent data){
        if (requestcode==RESULT_OK && requestcode== EDIT_TEXTT_CODE){
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position =data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(),"Item is Updated", Toast.LENGTH_SHORT).show();


        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");

        }
   }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }
    private void loadItems(){
        try {
        items = new ArrayList<>(FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        } catch (IOException e){
            Log.e("MainActivity","Error Reading Items",e);
            items=new ArrayList<>();

        }

    }
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error Reading Items",e);
        }
    }
}