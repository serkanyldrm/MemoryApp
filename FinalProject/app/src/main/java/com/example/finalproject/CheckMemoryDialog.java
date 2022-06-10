package com.example.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CheckMemoryDialog extends AppCompatDialogFragment {
    private EditText editTextPassword;
    private CheckMemoryDialogListener checkMemoryListener;
    String realPword ="";
    String inputPword ="";

    public String getRealPword() {
        return realPword;
    }

    public void setRealPword(String realPword) {
        this.realPword = realPword;
    }

    public String getInputPword() {
        return inputPword;
    }

    public void setInputPword(String inputPword) {
        this.inputPword = inputPword;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder checkMemoryBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_alert,null);

        checkMemoryBuilder.setView(view).setTitle("ENTER MEMORY PASSWORD").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputPword = editTextPassword.getText().toString();
                checkMemoryListener.checkMemoryPassword(inputPword,realPword);
            }
        });
        editTextPassword = view.findViewById(R.id.edit_password);
        return checkMemoryBuilder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            checkMemoryListener = (CheckMemoryDialogListener) context;
        } catch (Exception exception) {
            throw new ClassCastException(context.toString()+"must implement CheckMemoryDialogListener");
        }
    }
    public interface CheckMemoryDialogListener{
        void checkMemoryPassword(String realPword,String inputPword);
    }
}
