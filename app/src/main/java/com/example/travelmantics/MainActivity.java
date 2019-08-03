   package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
//Class for the admin to write data and upload images to the realtime database and cloud storage respectively
   public class MainActivity extends AppCompatActivity {
       private static final int PICTURE_RESULT = 42;
       private FirebaseDatabase mFirebaseDatabase;
       private DatabaseReference mDatabaseReference;
       EditText txtTitle;
       EditText txtDescription;
       EditText txtPrice;
       TravelDeal deal;
       Button btnImage;
       ImageView mImageView;


       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
        txtTitle=(EditText) findViewById(R.id.text_title);
        txtDescription=(EditText)findViewById(R.id.text_description);
        txtPrice=(EditText)findViewById(R.id.text_price);
        mImageView=(ImageView)findViewById(R.id.image);
        Intent intent=getIntent();
        TravelDeal deal= (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal==null){
            deal=new TravelDeal();
        }
        this.deal=deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        btnImage=(Button) findViewById(R.id.btn_image);
        //selects an image from the admin local directory
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Insert Picture"), PICTURE_RESULT);
            }
        });
    }
       //Gets the image,uploads it and downloads the url for the image
       @Override
       protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
           super.onActivityResult(requestCode, resultCode, data);
           if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
               Uri imageUri=data.getData();
               final StorageReference reference=FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());

               UploadTask uploadTask=reference.putFile(imageUri);
               Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                   @Override
                   public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful()){
                           throw task.getException();
                       }


                       return reference.getDownloadUrl();
                   }
               }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task) {
                       if(task.isSuccessful()){
                           Uri downloadURi=task.getResult();
                           deal.setImageUrl(downloadURi.toString());
                            showImage(downloadURi.toString());
                       }
                   }
               });

           }
       }

       @Override
       public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this,"Deal saved",Toast.LENGTH_SHORT).show();
                clean();
                backToList();
                return true;
            case R.id.delete_action:
                deleteDeal();
                Toast.makeText(this,"DEal deleted",Toast.LENGTH_SHORT).show();
                backToList();
                return  true;
                default:
                    return super.onOptionsItemSelected(item);
        }

       }
      //Resets the edit texts to empty strings
       private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();

       }
  //saves the data to the firebase realtime database
       private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
       if(deal.getId()==null){
           mDatabaseReference.push().setValue(deal);}
       else{
           mDatabaseReference.child(deal.getId()).setValue(deal);
       }


       }
       //Deletes the whole information about the travel deal including the associated image
       private void deleteDeal(){
           if(deal==null){
               Toast.makeText(this,"Please save deal before exiting",Toast.LENGTH_SHORT).show();
               return;
           }
           mDatabaseReference.child(deal.getId()).removeValue();
           if(deal.getImageUrl() != null && deal.getImageUrl().isEmpty() == false) {
               StorageReference picRef = FirebaseUtil.mFirebaseStorage.getReference(deal.getImageUrl());
               picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.d("Delete Image", "Image Successfully Deleted");
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d("Delete Image", e.getMessage());
                   }
               });
           }
       }
       //opens the activity that displays all the travel deals
       private  void backToList(){
           Intent intent=new Intent(this,ListActivity.class);
           startActivity(intent);
       }

       @Override
       public boolean onCreateOptionsMenu(Menu menu) {
           MenuInflater inflater=getMenuInflater();
           inflater.inflate(R.menu.save_menu,menu);
           if (FirebaseUtil.isAdmin) {
               menu.findItem(R.id.delete_action).setVisible(true);
               menu.findItem(R.id.save_menu).setVisible(true);
               enableEditTexts(true);
               findViewById(R.id.btn_image).setEnabled(true);
           }
           else {
               menu.findItem(R.id.delete_action).setVisible(false);
               menu.findItem(R.id.save_menu).setVisible(false);
               enableEditTexts(false);
               findViewById(R.id.btn_image).setEnabled(false);
           }
           return  true;

       }
       private void enableEditTexts(boolean isEnabled) {
           txtTitle.setEnabled(isEnabled);
           txtDescription.setEnabled(isEnabled);
           txtPrice.setEnabled(isEnabled);
       }
       //displays an image
       private void showImage(String url){
           if (url != null && url.isEmpty() == false) {
               int width = Resources.getSystem().getDisplayMetrics().widthPixels;
               Picasso.get()
                       .load(url)
                       .resize(width, width*2/3)
                       .centerCrop()
                       .into(mImageView);
           }
       }
   }
