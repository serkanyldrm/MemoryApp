package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateMemoryActivity extends AppCompatActivity {

    private EditText inputMemoryTitle, inputMemoryText, inputMemoryLocation,inputMemoryPassword;
    private TextView textDateTime;
    private LinearLayout layoutAddImage,deleteMemory,layoutMap,layoutShare;
    private ImageView imageMemory;

    private String SelectedImagePath;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogDeleteMemory;

    private Memory alreadyAvailableMemory;

    //List<Integer> emojiList = Arrays.asList(0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606, 0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612, 0x1F613, 0x1F614, 0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F61A);
    //AutoCompleteTextView autoCompleteTextView;
    //ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memory);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutMap = findViewById(R.id.layoutMap);
        layoutMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("location",inputMemoryLocation.getText().toString());
                startActivity(intent);
            }
        });

        //autoCompleteTextView = findViewById(R.id.emoji);

        //adapterItems = new ArrayAdapter<Integer>(this,R.layout.)
        //autoCompleteTextView.setAdapter();

    inputMemoryTitle = findViewById(R.id.inputMemoryTitle);
    inputMemoryText = findViewById(R.id.inputMemoryText);
    textDateTime = findViewById(R.id.textDateTime);
    imageMemory = findViewById(R.id.imageMemory);
    inputMemoryLocation = findViewById(R.id.inputMemoryLocation);
    inputMemoryPassword = findViewById(R.id.inputMemoryPassword);
    textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:a", Locale.getDefault()).format(new Date()));
    /*
    layoutShare=findViewById(R.id.layoutShare);
    layoutShare.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Paint paint = new Paint();
            String title = inputMemoryTitle.getText().toString();
            String text = inputMemoryText.getText().toString();
            String date = textDateTime.getText().toString();
            String location = inputMemoryLocation.getText().toString();
            ImageView image = imageMemory;
            int x = 10,y=25;
            page.getCanvas().drawText(title,x,y,paint);
            x=10;
            y=75;
            page.getCanvas().drawText(date,x,y,paint);
            x=10;
            y=100;
            page.getCanvas().drawText(location,x,y,paint);
            x=10;
            y=150;
            page.getCanvas().drawText(text,x,y,paint);
        }
    });
     */




    layoutAddImage = findViewById(R.id.layoutAddImage);
    layoutAddImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(CreateMemoryActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION);
            }else{
                selectImage();
            }
        }
    });

    SelectedImagePath = "";

    if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
        alreadyAvailableMemory = (Memory) getIntent().getSerializableExtra("memory");
        setViewOrUpdateMemory();
    }

    deleteMemory = findViewById(R.id.layoutDeleteMemory);

    if(alreadyAvailableMemory != null){
        deleteMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteNoteDialog();
            }
        });
    }

    ImageView imageSave = findViewById(R.id.imageSave);
    imageSave.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveMemory();
        }
    });
    }

    private void showDeleteNoteDialog(){
        if(dialogDeleteMemory == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateMemoryActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_memory,(ViewGroup) findViewById(R.id.layoutDeleteMemoryContainer));
            builder.setView(view);
            dialogDeleteMemory = builder.create();
            if(dialogDeleteMemory.getWindow() != null){
                dialogDeleteMemory.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteMemory).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteMemoryTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            MemoriesDatabase.getDatabase(getApplicationContext()).memoryDao().deleteMemory(alreadyAvailableMemory);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isMemoryDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }

                    new DeleteMemoryTask().execute();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteMemory.dismiss();
                }
            });
        }
       dialogDeleteMemory.show();
    }

    private void setViewOrUpdateMemory(){
        inputMemoryTitle.setText(alreadyAvailableMemory.getTitle());
        inputMemoryText.setText(alreadyAvailableMemory.getMemoryText());
        textDateTime.setText(alreadyAvailableMemory.getDateTime());
        inputMemoryLocation.setText(alreadyAvailableMemory.getLocation());
        if(alreadyAvailableMemory.getImagePath() != null && !alreadyAvailableMemory.getImagePath().trim().isEmpty()){
            imageMemory.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableMemory.getImagePath()));
            imageMemory.setVisibility(View.VISIBLE);
            SelectedImagePath = alreadyAvailableMemory.getImagePath();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageMemory.setImageBitmap(bitmap);
                        imageMemory.setVisibility(View.VISIBLE);

                        SelectedImagePath = getPathFromUri(selectedImageUri);
                    }catch (Exception exception){
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null,null,null,null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void saveMemory(){
        if(inputMemoryTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Memory Title can not be empty",Toast.LENGTH_SHORT).show();
            return;
        } else if(inputMemoryText.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Memory Text can not be empty",Toast.LENGTH_SHORT).show();
            return;
        }

        final Memory memory = new Memory();
        memory.setTitle(inputMemoryTitle.getText().toString());
        memory.setMemoryText(inputMemoryText.getText().toString());
        memory.setDateTime(textDateTime.getText().toString());
        memory.setImagePath(SelectedImagePath);
        memory.setLocation(inputMemoryLocation.getText().toString());
        memory.setPassword(inputMemoryPassword.getText().toString());
        if(alreadyAvailableMemory != null){
            memory.setId(alreadyAvailableMemory.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveMemoryTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids){
                MemoriesDatabase.getDatabase(getApplicationContext()).memoryDao().insertMemory(memory);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveMemoryTask().execute();
        Toast.makeText(this,"Memory Created",Toast.LENGTH_SHORT).show();
    }

}
