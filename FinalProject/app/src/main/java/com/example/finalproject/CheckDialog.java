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

public class CheckDialog extends AppCompatDialogFragment {
    private EditText editTextPassword;
    private CheckDialogListener checklistener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder checkBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_alert, null);

        checkBuilder.setView(view).setTitle("ENTER PASSWORD").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = editTextPassword.getText().toString();
                checklistener.checkPassword(password);
            }
        });
        editTextPassword = view.findViewById(R.id.edit_password);
        return checkBuilder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            checklistener = (CheckDialogListener) context;
        } catch (Exception exception) {
            throw new ClassCastException(context.toString()+"must implement CheckDialogListener");
        }
    }

    public interface CheckDialogListener{
        void checkPassword(String password);
    }
}
