package com.example.owner.mapDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class UserRegisterActivity extends AppCompatActivity {

    EditText fName, lName, username, password, confirmPass;
    String str_FName, str_LName, str_userN, pass, cPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        fName = findViewById(R.id.fNameEditText);
        lName = findViewById(R.id.lNameEditText);
        username = findViewById(R.id.phoneEditText);
        password = findViewById(R.id.passwordEditText);
        confirmPass = findViewById(R.id.confirmPassEditText);

        OpenLogin();

    }

    private void OpenLogin() {
        Button backButton = findViewById(R.id.OpenLogin);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onRegister(View view) {
        String str_FName = fName.getText().toString();
        String str_LName = lName.getText().toString();
        String str_userN = username.getText().toString();
        String pass = password.getText().toString();
        String cPass = confirmPass.getText().toString();

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);

        if (pass.equals(cPass)) {
            String type = "register";
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, str_FName, str_LName, str_userN, pass, dateString);
            clearEditText();
            Toast.makeText(this, "Thank you for registering your account, please return to login page", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "passwords do not match, please try again", Toast.LENGTH_LONG).show();
        }


    }

    private void clearEditText() {
        fName.setText("");
        lName.setText("");
        username.setText("");
        password.setText("");
        confirmPass.setText("");
    }

}
