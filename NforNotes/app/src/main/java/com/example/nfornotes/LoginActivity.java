package com.example.nfornotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailtext,passwordtext;
    Button loginbtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailtext=findViewById(R.id.email_edit_text);
        passwordtext=findViewById(R.id.password_edit_text);

        loginbtn=findViewById(R.id.login_btn);
        progressBar=findViewById(R.id.progress_bar);
        createAccountBtnTextView=findViewById(R.id.create_account_text_view_btn);

        loginbtn.setOnClickListener(v->loginUser());
        createAccountBtnTextView.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    void loginUser(){
        String email=emailtext.getText().toString();
        String password=passwordtext.getText().toString();
        boolean isvalidated=validateData(email,password);
        if (!isvalidated){
            return;
        }

        loginAccountInFirebase(email,password);
    }
void loginAccountInFirebase(String email,String password){
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    changeInProgress(true);
    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            changeInProgress(false);
            if (task.isSuccessful()){
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
finish();

                } else {
                    Utility.showToast(LoginActivity.this,"Email Not Verified, Plz Verify your email");

                }


            }else{
                Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
            }
        }
    });
}

    void changeInProgress(boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            loginbtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginbtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailtext.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordtext.setError("password Length is invalid");
            return false;
        }

        return true;
    }
}