package com.example.nfornotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NotesDetailsActivity extends AppCompatActivity {
    EditText titledittext,contentedittext;
    ImageButton savenotebtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode;
    TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_details);
        titledittext=findViewById(R.id.note_title_text);
        contentedittext=findViewById(R.id.note_content_text);
        savenotebtn=findViewById(R.id.save_note_btn);
        deleteNoteTextViewBtn=findViewById(R.id.delete_note_text_view_btn);

        pageTitleTextView=findViewById(R.id.page_title); 
        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
        docId=getIntent().getStringExtra("docId");

        titledittext.setText(title);
        contentedittext.setText(content);
        if (docId!=null && !docId.isEmpty() ){
            isEditMode=true;
        }

        if (isEditMode){
            pageTitleTextView.setText("Edit Your Note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }
        savenotebtn.setOnClickListener(v->saveNote());
        deleteNoteTextViewBtn.setOnClickListener(v->deleteNoteFromFirebase());
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference=Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NotesDetailsActivity.this,"Notes deleted Successfully");
                    finish();
                }else{
                    Utility.showToast(NotesDetailsActivity.this,"Failed While Deleting the Note");
                }
            }
        });
    }

    void saveNote(){
        String title=titledittext.getText().toString();
        String content=contentedittext.getText().toString();
        if (title==null||title.isEmpty()){
            titledittext.setError("Title is required");
            return;
        }

        Notes note=new Notes();
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    public void saveNoteToFirebase(Notes note){
        DocumentReference documentReference;
        if (isEditMode){
            documentReference=Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            documentReference=Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NotesDetailsActivity.this,"Notes added Successfully");
                    finish();
                }else{
                    Utility.showToast(NotesDetailsActivity.this,"Failed While Adding the Note");
                }
            }
        });
    }
}