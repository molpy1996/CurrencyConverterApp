package com.example.currencyconverterapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class FBManager {

    private DatabaseReference dbRef;

    @IgnoreExtraProperties
    public class Rate_Item {

        public String currency;
        public Double rate;

        // Default constructor that we need for calls to DataSnapshot.getValue(Rate.class)
        public Rate_Item() {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("firebase", "data updated");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            dbRef.addValueEventListener(postListener);
        }

        public Rate_Item(String currency, Double rate) {
            this.currency = currency;
            this.rate = rate;
        }
    }

    public void getInstance() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    public void insertRate(String rateId, String currency, Double rate) {
        Rate_Item item = new Rate_Item(currency, rate);
        dbRef.child("rates").child(rateId).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("FBManager", "data written into firebase");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.e("FBManager", "error when writing data to firebase");

                    }
                });;
    }

    //if we want to modify Rates in the firebase
    public void setRate(String rateId, String devise, Double rateValue) {
        dbRef.child("rates").child(rateId).child(devise).setValue(rateValue);
    }
}
