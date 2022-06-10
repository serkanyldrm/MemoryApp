package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MemoryListener,Dialog.DialogListener,CheckDialog.CheckDialogListener{

    public static final int REQUEST_CODE_ADD_MEMORY = 1;
    public static final int REQUEST_CODE_UPDATE_MEMORY = 2;
    public static final int REQUEST_CODE_SHOW_MEMORIES = 3;

    private RecyclerView memoryRecyclerView;
    private List<Memory> memoryList;
    private MemoriesAdapter memoriesAdapter;
    private AlertDialog dialogPasswordMemory;
    private int memoryClickedPosition = -1;
    private EditText inputMemoryPassword;
    private TextView textOK,textCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageAddMemory = findViewById(R.id.imageAddMemory);
        imageAddMemory.setOnClickListener(v -> startActivityForResult(new Intent(getApplicationContext(), CreateMemoryActivity.class), REQUEST_CODE_ADD_MEMORY));

        ImageButton imageButtonSetPassword = findViewById(R.id.imageSettings);
        imageButtonSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        memoryRecyclerView = findViewById(R.id.memoryRecyclerView);
        memoryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        memoryList = new ArrayList<>();
        memoriesAdapter = new MemoriesAdapter(memoryList, this);
        memoryRecyclerView.setAdapter(memoriesAdapter);

        getMemories(REQUEST_CODE_SHOW_MEMORIES, false);
        BufferedReader br = null;
        InputStream ıS = null;
        //Scanner sc = new Scanner(is);

        File dir = new File(String.valueOf(getFilesDir()));
        File file = new File(dir,"password.txt");
        if(file.exists()) {
            openCheckDialog();
        }
    }

    @Override
    public void onMemoryClicked(Memory memory, int postition) {
        String realPword = memory.getPassword();
        if (realPword.isEmpty()) {
            memoryClickedPosition = postition;
            Intent intent = new Intent(getApplicationContext(), CreateMemoryActivity.class);
            intent.putExtra("isViewOrUpdate", true);
            intent.putExtra("memory", memory);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_MEMORY);
        } else if (dialogPasswordMemory == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_password_memory, (ViewGroup) findViewById(R.id.layoutPasswordMemoryContainer));
            builder.setView(view);
            dialogPasswordMemory = builder.create();
            if (dialogPasswordMemory.getWindow() != null) {
                dialogPasswordMemory.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            textOK = view.findViewById(R.id.textOkPasswordMemory);
            textOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputMemoryPassword = view.findViewById(R.id.textPasswordMemory);
                    String inputPword = inputMemoryPassword.getText().toString();
                    if (realPword.equals(inputPword)) {
                        memoryClickedPosition = postition;
                        Intent intent = new Intent(getApplicationContext(), CreateMemoryActivity.class);
                        intent.putExtra("isViewOrUpdate", true);
                        intent.putExtra("memory", memory);
                        startActivityForResult(intent, REQUEST_CODE_UPDATE_MEMORY);
                    }else{
                        Toast.makeText(MainActivity.this,"Password is Wrong", Toast.LENGTH_LONG).show();
                    }
                }
            });
            textCancel = view.findViewById(R.id.textPasswordCancel);
            textCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogPasswordMemory.dismiss();
                }
            });
        }
        dialogPasswordMemory.show();
        }

        /*
        memoryClickedPosition = postition;
        Intent intent = new Intent(getApplicationContext(), CreateMemoryActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("memory", memory);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_MEMORY);
         */

    private void getMemories(final int requestCode, final boolean isMemoryDeleted) {
        @SuppressLint("StaticFieldLeak")
        class GetMemoriesTask extends AsyncTask<Void, Void, List<Memory>> {

            @Override
            protected List<Memory> doInBackground(Void... voids) {
                return MemoriesDatabase.getDatabase(getApplicationContext()).memoryDao().getAllMemories();
            }

            @Override
            protected void onPostExecute(List<Memory> memories) {
                super.onPostExecute(memories);
                if (requestCode == REQUEST_CODE_SHOW_MEMORIES) {
                    memoryList.addAll(memories);
                    memoriesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_MEMORY) {
                    memoryList.add(0, memories.get(0));
                    memoriesAdapter.notifyItemInserted(0);
                    memoryRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_MEMORY) {
                    memoryList.remove(memoryClickedPosition);
                    if (isMemoryDeleted) {
                        memoriesAdapter.notifyItemRemoved(memoryClickedPosition);
                    } else {
                        memoryList.add(memoryClickedPosition, memories.get(memoryClickedPosition));
                        memoriesAdapter.notifyItemChanged(memoryClickedPosition);
                    }
                }
            }
        }
        new GetMemoriesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_MEMORY && resultCode == RESULT_OK) {
            getMemories(REQUEST_CODE_ADD_MEMORY, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_MEMORY && resultCode == RESULT_OK) {
            if (data != null) {
                getMemories(REQUEST_CODE_UPDATE_MEMORY, data.getBooleanExtra("isMemoryDeleted", false));
            }
        }
    }

    public void openDialog() {
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "Set Password");
    }

    public void openCheckDialog() {
        CheckDialog checkDialog = new CheckDialog();
        checkDialog.setCancelable(false);
        checkDialog.show(getSupportFragmentManager(), "Enter Password");
    }
    /*
    public void openCheckMemoryDialog(String realPword){
        CheckMemoryDialog checkMemoryDialog = new CheckMemoryDialog();
        checkMemoryDialog.setRealPword(realPword);
        checkMemoryDialog.setCancelable(false);
        checkMemoryDialog.show(getSupportFragmentManager(),"Enter Password");
    }
    */
    @Override
    public void applyPassword(String password) {
        Writer output;

        if (password.length() >= 6) {
            File file = new File(getApplicationContext().getFilesDir() + "/password.txt");
            try {
                output = new BufferedWriter(new FileWriter(file, false));
                output.write(password);
                output.close();
                Toast toast = Toast.makeText(MainActivity.this, "New password set.", Toast.LENGTH_LONG);
                toast.show();
                openCheckDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(MainActivity.this, "Password must be at least 6 characters long.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void checkPassword(String password) {
        BufferedReader br = null;
        String pword = "";
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            is = new FileInputStream(getApplicationContext().getFilesDir() + "/password.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(is);
        pword = sc.nextLine();
        //System.out.println(pword);
        //System.out.println(password);
                if (!password.equals(pword)) {
                    Toast toast = Toast.makeText(MainActivity.this, "Password is wrong", Toast.LENGTH_LONG);
                    toast.show();
                    openCheckDialog();
                }else{
                    //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    //startActivity(intent);
                }


    }

    /*
    @Override
    public void checkMemoryPassword(String inputPword,String realPword) {
        if (!inputPword.equals(realPword)) {
            Toast toast = Toast.makeText(MainActivity.this, "Password is wrong", Toast.LENGTH_LONG);
            toast.show();
            openCheckMemoryDialog(realPword);
        }
    }
     */
}