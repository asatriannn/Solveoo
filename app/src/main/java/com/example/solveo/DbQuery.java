package com.example.solveo;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static FirebaseFirestore g_firestore;

    public static List<CategoryModel> g_catList = new ArrayList<>();

    public static int g_selected_cat_index = 0;

    public static List<TestModel> g_testList = new ArrayList<>();



    public static void createUserData(String email, String name, MyCompleteListener completeListener){
        Map<String, Object> userData = new ArrayMap<>();

        userData.put("Email_ID", email);
        userData.put("Name", name);
        userData.put("Mean_Score", 0);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();

        batch.set(userDoc, userData);

        DocumentReference countDoc = g_firestore.collection("USERS").document("TotalUsers");

        batch.update(countDoc, "Count", FieldValue.increment(1));

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                completeListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                completeListener.onFailure();
            }
        });

     }


    public static void loadCategories(MyCompleteListener completeListener) {
        g_catList.clear();

        g_firestore.collection("QUIZ").document("Categories").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        long catCount = documentSnapshot.getLong("Count");

                        if (catCount == 0) {
                            completeListener.onSuccess();
                            return;
                        }

                        final int[] loadedCount = {0}; // ✅ Track how many have loaded

                        for (int i = 1; i <= catCount; i++) {
                            String catID = documentSnapshot.getString("Cat" + i + "_ID");

                            if (catID == null || catID.isEmpty()) {
                                continue;
                            }

                            g_firestore.collection("QUIZ").document(catID).get()
                                    .addOnSuccessListener(catDoc -> {
                                        if (catDoc.exists()) {
                                            String catName = catDoc.getString("Name");
                                            Long numTestsLong = catDoc.getLong("Number_of_Tests");
                                            int numOfTests = (numTestsLong != null) ? numTestsLong.intValue() : 0;


                                            g_catList.add(new CategoryModel(catID, catName, numOfTests));
                                        }

                                        loadedCount[0]++;
                                        if (loadedCount[0] == catCount) {
                                            completeListener.onSuccess(); // ✅ Call success only after all are loaded
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        loadedCount[0]++;
                                        if (loadedCount[0] == catCount) {
                                            completeListener.onSuccess(); // Still call success even if some failed
                                        }
                                    });
                        }
                    } else {
                        completeListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void loadTestData(MyCompleteListener completeListener){
        g_testList.clear();

        // 🔥 FIXED: "TESTS_LIST" (was wrongly "TEST_LIST")
        g_firestore.collection("QUIZ")
                .document(g_catList.get(g_selected_cat_index).getDocID())
                .collection("TESTS_LIST") // ✅ This was incorrect in your code
                .document("TESTS_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int numOfTest = g_catList.get(g_selected_cat_index).getNumberOfTests();

                        for (int i = 1; i <= numOfTest; i++) {
                            String testId = documentSnapshot.getString("Test" + i + "_ID");
                            String title = documentSnapshot.getString("Test" + i + "_title");
                            Long timeLong = documentSnapshot.getLong("Test" + i + "_Time");

                            Log.d("TEST_LOAD", "Test" + i + ": ID=" + testId + ", Title=" + title + ", Time=" + timeLong);

                            if (testId != null && title != null && timeLong != null) {
                                g_testList.add(new TestModel(testId, title, 0, timeLong.intValue()));
                            } else {
                                Log.w("TEST_LOAD", "❌ Missing data for Test" + i);
                            }
                        }

                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TEST_LOAD", "Failed to load test data: " + e.getMessage());
                        completeListener.onFailure();
                    }
                });
    }





}
