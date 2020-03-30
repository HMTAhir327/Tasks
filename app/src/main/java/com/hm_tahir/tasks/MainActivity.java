package com.hm_tahir.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore objfirebaseFirestore;
    private CollectionReference objCollectionReference;
    private DocumentReference objDocumentReference;

    private static String stuDetails="StudentDetails";

    private Dialog objDialog;
    private EditText docId,deptName,smesNum;
    private TextView showAlldata;
    String Alldata="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            objfirebaseFirestore=FirebaseFirestore.getInstance();
            objCollectionReference=objfirebaseFirestore.collection(stuDetails);
            objDialog=new Dialog(this);
            objDialog.setContentView(R.layout.dialog);
            objDialog.setCancelable(false);
            docId=findViewById(R.id.documentId);
            deptName=findViewById(R.id.dpartmentName);
            smesNum=findViewById(R.id.smesterNumber);
            showAlldata=findViewById(R.id.showalldata);

        }
        catch (Exception e){
            Toast.makeText(this,"onCreate"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public  void delDocument(View view){
        try{
            if(!docId.getText().toString().isEmpty()){
                objDocumentReference=objfirebaseFirestore.collection(stuDetails).document(docId.getText().toString());

                objDocumentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Document deleted successfully",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Fails to delete document"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(MainActivity.this,"Enter the id of the document",Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){

        }
    }
    public  void delCollection(View view){
        try{
            objCollectionReference.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot objDocumentSnapshot:queryDocumentSnapshots){
                                String id=  objDocumentSnapshot.getId();
                                objDocumentReference=objfirebaseFirestore.collection(stuDetails).document(id);
                                objDocumentReference.delete();
                            }
                            Toast.makeText(MainActivity.this,"Collection deleted successfully",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Fails to delete collection"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e){
            Toast.makeText(MainActivity.this,"delCollection"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public void getAlldata(View view){
        try{
            objCollectionReference.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot objDocumentSnapshot:queryDocumentSnapshots){
                                String id=  objDocumentSnapshot.getId();
                                String departmentName=  objDocumentSnapshot.getString("Department");
                                String smesternumber=  objDocumentSnapshot.getString("Smester");

                                Alldata="Document ID: "+id+"\nDepartment Name: "+departmentName+"\nSmester: "+smesternumber+"\n";
                                showAlldata.setText(Alldata);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Fails to retrieve collection"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch (Exception e){
            Toast.makeText(this,"getAlldata"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }    }

    public void addValues(View view){
        try{
            if(!docId.getText().toString().isEmpty() && !deptName.getText().toString().isEmpty() && !smesNum.getText().toString().isEmpty()){
                objDialog.show();
                objCollectionReference.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                int check=0;
                                for(DocumentSnapshot objDocumentSnapshot:queryDocumentSnapshots){
                                    String id=  objDocumentSnapshot.getId();
                                    String ch=docId.getText().toString();
                                    if(ch.equals(id)){
                                        check=1;
                                    }else{
                                        check=0;
                                    }
                                }
                                if(check==1){
                                    Toast.makeText(MainActivity.this,"On this document data already exists try another",Toast.LENGTH_SHORT).show();
                                }else{
                                    Map<String,Object> objMap=new HashMap<>();
                                    objMap.put("Department",deptName.getText().toString());
                                    objMap.put("Smester",smesNum.getText().toString());

                                    objfirebaseFirestore.collection(stuDetails)
                                            .document(docId.getText().toString())
                                            .set(objMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    docId.setText("");
                                                    deptName.setText("");
                                                    smesNum.setText("");

                                                    docId.requestFocus();
                                                    objDialog.dismiss();
                                                    Toast.makeText(MainActivity.this,"Data added successfully",Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            objDialog.dismiss();
                                            Toast.makeText(MainActivity.this,"Failed to add Data",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

            }else{
                Toast.makeText(MainActivity.this,"Fill all fields",Toast.LENGTH_SHORT).show();
            }


        }
        catch (Exception e){
            objDialog.dismiss();
            Toast.makeText(this,"addValues"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
