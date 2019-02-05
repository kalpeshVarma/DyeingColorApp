package com.kalpv.todoapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kalpv.todoapp.Adapter.ListItemAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    List<ToDo> toDoList = new ArrayList<>();
    FirebaseFirestore db;

    RecyclerView listView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    List<String> weight;

    public MaterialSpinner materialSpinner;
    public MaterialEditText title_et, description_et; //public so as to get access from ListAdapter

    ListItemAdapter adapter;
    public boolean isUpdate = false; //flag to check if new or update
    public String idUpdate = ""; //Id of item to update


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init firebase store
        db = FirebaseFirestore.getInstance();

        fab = findViewById(R.id.fab);
        title_et = findViewById(R.id.title);
        description_et = findViewById(R.id.description);
        listView = findViewById(R.id.listToDo);
        relativeLayout = findViewById(R.id.ll);
        materialSpinner = findViewById(R.id.spinner);

        listView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        weight = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            weight.add(String.valueOf(i) + " Kg.");
        }

        progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        materialSpinner.setItems(weight);

        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                loadData();
            }
        });

        loadData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!title_et.getText().toString().isEmpty() && !description_et.getText().toString().isEmpty()) {
                    if (!isUpdate) {
                        setData(title_et.getText().toString(), description_et.getText().toString(), String.valueOf(materialSpinner.getSelectedIndex() + 1));
                    } else {
                        updateData(title_et.getText().toString(), description_et.getText().toString(), String.valueOf(materialSpinner.getSelectedIndex() + 1));
                        isUpdate = !isUpdate; //reset flag
                    }
                }
                if(title_et.getText().toString().isEmpty())
                    title_et.setError("Color code cannot be empty");
                if(description_et.getText().toString().isEmpty())
                    description_et.setError("Ingredients cannot be empty");
            }
        });

    }

    private void updateData(String title, String description, String parentId) {
        db.collection("ColorList" + String.valueOf(parentId)).document(idUpdate).update("title", title, "description", description, "parentId", parentId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                title_et.setText("");
                description_et.setText("");
                materialSpinner.setEnabled(true);
                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        //Realtime refresh
        db.collection("ColorList").document(idUpdate).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                loadData();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getTitle().toString()) {

            case "Delete":
                deleteItem(item.getOrder());
                break;

            case "Update":
                //TODO: update textfields
                title_et.setText(toDoList.get(item.getOrder()).getTitle());
                description_et.setText(toDoList.get(item.getOrder()).getDescription());
                materialSpinner.setSelectedIndex(Integer.parseInt(toDoList.get(item.getOrder()).getParentId()) - 1);

                isUpdate = true;
                idUpdate = toDoList.get(item.getOrder()).getId();
                materialSpinner.setEnabled(false);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteItem(int index) {
        db.collection("ColorList" + String.valueOf(toDoList.get(index).getParentId()))
                .document(toDoList.get(index).getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData(String title, String description, String parentId) {
//        relativeLayout.setBackgroundColor(Color.parseColor("#4d000000"));
        progressBar.setVisibility(View.VISIBLE);
        //Random id
        String id = UUID.randomUUID().toString();
        Map<String, Object> todo = new HashMap<>();
        todo.put("id", id);
        todo.put("parentId", parentId);
        todo.put("title", title);
        todo.put("description", description);

        db.collection("ColorList" + parentId).document(id).set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Refresh data
                loadData();
                title_et.setText("");
                description_et.setText("");

            }
        });

    }

    private void loadData() {

        progressBar.setVisibility(View.VISIBLE);

        if (toDoList.size() > 0) {
            toDoList.clear(); //Removes old value
        }

        db.collection("ColorList" + String.valueOf(materialSpinner.getSelectedIndex() + 1))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            ToDo toDo = new ToDo(doc.getString("id"), doc.getString("parentId"),
                                    doc.getString("title"), doc.getString("description"));
                            toDoList.add(toDo);
                        }
                        adapter = new ListItemAdapter(MainActivity.this, toDoList);
                        listView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
