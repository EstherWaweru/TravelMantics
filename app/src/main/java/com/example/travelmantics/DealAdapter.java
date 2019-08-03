package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
//A travel deal adapter class for the list activity class
public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ImageView imagedeal;
//construcor for the adapter class to initialize data and objects  to be used
    public  DealAdapter(){

        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
        deals=FirebaseUtil.mDeals;
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal travelDeal=dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal: ", travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size()-1);
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
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }
    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    //nested viewholder of the adapter that holds information about one instance of an item in the list
    public class DealViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
           tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
           tvDescription=(TextView)itemView.findViewById(R.id.tvDescription);
           tvPrice=(TextView) itemView.findViewById(R.id.tvPrice);
           imagedeal=(ImageView)itemView.findViewById(R.id.imageDeal) ;
           itemView.setOnClickListener(this);
        }
        //binds the recyclerview with the travel deal information
        public void bind(TravelDeal deal) {
            tvTitle.setText(deal.getTitle());
            tvDescription.setText(deal.getDescription());
            tvPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());

        }
     //sends an intent to open the main activity
        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            TravelDeal selectedDeal=deals.get(position);
            Intent intent=new Intent(view.getContext(),MainActivity.class);
            intent.putExtra("Deal",selectedDeal);
            view.getContext().startActivity(intent);

        }

    }
    //displays an image with the Picasso library
    private void showImage(String url) {
        if (url != null && url.isEmpty()==false) {
            Picasso.get()
                    .load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .into(imagedeal);
        }
    }
}
