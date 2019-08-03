package com.example.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//A utility class for doing the common firebase controls
public class FirebaseUtil {
    public static  FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static  FirebaseUtil mFirebaseUtil;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageRef;
    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;
    public static ArrayList<TravelDeal>mDeals;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateLitener;


    private FirebaseUtil(){}
    public static boolean isAdmin;
    //Initializing method for the firebase objects
    public static void openFbReference(String ref, final ListActivity callerActivity) {
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth=FirebaseAuth.getInstance();
            caller=callerActivity;
            mAuthStateLitener=new FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   if (firebaseAuth.getCurrentUser()==null){
                       FirebaseUtil.signIn();}
                   else{
                      String userId=mFirebaseAuth.getUid();
                      checkAdmin(userId);
                   }
                   Toast.makeText(callerActivity.getBaseContext(),"Welcome Back",Toast.LENGTH_SHORT).show();

               }
           };
            connectStorage();
        }
        mDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);

    }

    //Checks if the person logged in is a user or an admin
    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin=false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(userId);
        ChildEventListener listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
               caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }
   //Signs users in with the email and google option
    private static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
               new AuthUI.IdpConfig.GoogleBuilder().build());


// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
    //Listens to whether a user is logged in or not
    public static void attachListener(){
     mFirebaseAuth.addAuthStateListener(mAuthStateLitener);
    }

    public static  void detachListener(){
      mFirebaseAuth.removeAuthStateListener(mAuthStateLitener);
    }
    //Initializes the firebase storage service instances
    public static void connectStorage(){
        mFirebaseStorage=FirebaseStorage.getInstance();
        mStorageRef=mFirebaseStorage.getReference().child("deals_pictures");
    }
}
