package com.vdcodeassociate.firebase_retest_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("Notebook").document("My first Note");
    private ListenerRegistration noteListner;

    private EditText title;
    private EditText description;
    private TextView textViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.edit_text_title);
        description = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.textview_data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteListner = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT);
                    Log.d("ERROR : ", error.toString());
                    return;
                }

                if(documentSnapshot.exists()){
                    String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);
                    textViewData.setText("Title : " + title + "\n" + "Description : " + description);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteListner.remove();
    }

    public void saveNote(View view){

        String title = this.title.getText().toString();
        String description = this.description.getText().toString();

        Map<String,Object> note = new HashMap<>();
        note.put(KEY_TITLE,title);
        note.put(KEY_DESCRIPTION,description);

//        db.document("Notebook/My first Note");
        docRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d("ERROR : ",e.toString());
            }
        });

    }

    public void loadNote(View view){
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String title = documentSnapshot.getString(KEY_TITLE);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);

//                            Map<String, Object> data = documentSnapshot.getData();
                            textViewData.setText("Title : " + title + "\n" + "Description : " + description);
                        }else {
                            Toast.makeText(MainActivity.this,"Document doesn't exist.",Toast.LENGTH_SHORT);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d("ERROR : ",e.toString());
            }
        });
    }

}