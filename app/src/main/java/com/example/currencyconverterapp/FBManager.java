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

    private DatabaseReference db;

    @IgnoreExtraProperties
    public class Rate {

        public String currency;
        public Double rate;

        public Rate() {
            // Default constructor required for calls to DataSnapshot.getValue(Rate.class)
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // ...
                    Log.i("firebase", "data changed");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            db.addValueEventListener(postListener);
        }

        public Rate(String currency, Double rate) {
            this.currency = currency;
            this.rate = rate;
        }
    }

    public void getInstance() {
        db = FirebaseDatabase.getInstance().getReference();
    }


    public void writeNewRate(String rateId, String devise, Double rateValue) {
        Rate rate = new Rate(devise, rateValue);
        db.child("rates").child(rateId).setValue(rate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i("FBManager", "data written");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.e("FBManager", "error when writing data");

                    }
                });;
    }


    public void setRate(String rateId, String devise, Double rateValue) {
        db.child("rates").child(rateId).child(devise).setValue(rateValue);
    }
}
