package com.example.contact;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemTouchHelperListener{

    private RecyclerView rcvHike;
    private Button btnAdd;
    private Button btnCancel;
    private ContactAdapter contactAdapter;

    private SearchView searchView;

    private RelativeLayout rootView;

    ArrayList<Contact> contactArrayList;
    ArrayList<Contact> contactDeleteArrayList;

    private Toolbar toolbar;

    private static final int REQUEST_CODE = 1;

    SQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        btnCancel = findViewById(R.id.btnCancel);
        setSupportActionBar(toolbar);

        rcvHike = findViewById(R.id.rcv_hike);
        contactAdapter = new ContactAdapter(this);
        myDB = new SQLiteHelper(MainActivity.this);
        contactArrayList = new ArrayList<>();
        contactDeleteArrayList = new ArrayList<>();
        rootView = findViewById(R.id.rootView);
        addMethods();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        rcvHike.setLayoutManager(linearLayoutManager);

        contactAdapter.setData(contactArrayList);
        rcvHike.setAdapter(contactAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvHike.addItemDecoration(itemDecoration);

        //xử lý vuốt phải xóa
        ItemTouchHelper.SimpleCallback simpleCallback = new RecylerViewItemToucherHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvHike);
    }

    private void addMethods() {
        storeDateInArray();
    }

    /**
     * đọc dữ liệu và gắn lên recyler view
     */
    void storeDateInArray(){
        Cursor cursor = myDB.getData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
        else{
            while (cursor.moveToNext()){
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setImage(cursor.getString(4));
                contactArrayList.add(contact);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.findItem).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    //ẩn filter khi ấn nút ẩn bàn phím
    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void openModal(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.modal);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Send feed back", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            contactArrayList = new ArrayList<>();
            storeDateInArray();
            contactAdapter.setData(contactArrayList);
            contactAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        btnAdd = findViewById(R.id.btnAdd);
        if(item.getItemId() == R.id.addItem){
            startActivityForResult(new Intent(MainActivity.this, DetailActivity.class), REQUEST_CODE);
        }
        else if(item.getItemId() == R.id.findItem){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof ContactAdapter.ContactViewHodler){
            String name = contactArrayList.get(viewHolder.getAdapterPosition()).getName();

            final Contact hikeDelete = contactArrayList.get(viewHolder.getAdapterPosition());
            final int indexDelete = viewHolder.getAdapterPosition();
            SQLiteHelper mydb = new SQLiteHelper(MainActivity.this);
            for (int i = 0; i < contactDeleteArrayList.size(); i++) {
                mydb.deleteContact(String.valueOf(contactDeleteArrayList.get(i).getId()));
            }
            contactDeleteArrayList.clear();
            contactDeleteArrayList.add(hikeDelete);

            //remove item
            contactAdapter.removeItem(indexDelete);

            //hiển thị notify undo item
            Snackbar snackbar = Snackbar.make(rootView, name + " removed!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactAdapter.undoItem(hikeDelete, indexDelete);
                    if(indexDelete == 0 || indexDelete == contactArrayList.size() - 1){
                        rcvHike.scrollToPosition(indexDelete);
                    }
                }
            }).setCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                        mydb.deleteContact(String.valueOf(hikeDelete.getId()));
                        contactDeleteArrayList.clear();
                    }
                }
            });

            //
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
