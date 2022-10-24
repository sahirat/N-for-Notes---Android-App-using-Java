package com.example.nfornotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailtext,passwordtext,cpasswordtext;
    Button createaccountbtn;
    ProgressBar progressBar;
    TextView loginbtntextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailtext=findViewById(R.id.email_edit_text);
        passwordtext=findViewById(R.id.password_edit_text);
        cpasswordtext=findViewById(R.id.confirm_password_edit_text);
        createaccountbtn=findViewById(R.id.create_account_btn);
        progressBar=findViewById(R.id.progress_bar);
        loginbtntextView=findViewById(R.id.login_text_view_btn);

        createaccountbtn.setOnClickListener(v->createAccount());
        loginbtntextView.setOnClickListener(v->finish());
    }

    void createAccount(){
 String email=emailtext.getText().toString();
 String password=passwordtext.getText().toString();
 String confirmpassword=cpasswordtext.getText().toString();
 boolean isvalidated=validateData(email,password,confirmpassword);
 if (!isvalidated){
     return;
 }

 createAccountInFirebase(email,password);
    }

    void createAccountInFirebase(String email,String password){
        changeInProgress(true);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    Utility.showToast(CreateAccountActivity.this, "Successfully created");

                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();

                }else{
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());

                }
            }
        });
    }

    void changeInProgress(boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            createaccountbtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createaccountbtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password,String confirmpassword){
          if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
          {
              emailtext.setError("Email is invalid");
              return false;
          }
          if(password.length()<6){
              passwordtext.setError("password Length is invalid");
              return false;
          }
          if(!password.equals(confirmpassword)){
              cpasswordtext.setError("Password is not matches");
              return false;
          }
          return true;
    }
}