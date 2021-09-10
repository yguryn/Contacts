package com.example.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    MainAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        checkPermission();
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);

                if(phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setNumber(number);
                    if(name.startsWith("В")) {
                        arrayList.add(model);
                    }
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
            getContactList();
        } else {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}