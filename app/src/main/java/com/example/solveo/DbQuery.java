package com.example.solveo;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.solveo.Models.ArticleModel;
import com.example.solveo.Models.CategoryModel;
import com.example.solveo.Models.CompletedTestModule;
import com.example.solveo.Models.LeaderboardUserModel;
import com.example.solveo.Models.ProfileModel;
import com.example.solveo.Models.QuestionModel;
import com.example.solveo.Models.RankModel;
import com.example.solveo.Models.TestModel;
import com.example.solveo.Models.VideoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static FirebaseFirestore g_firestore;
    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testList = new ArrayList<>();
    public static int g_selected_test_index = 0;
    public static List<QuestionModel> g_questionList = new ArrayList<>();
    public static ProfileModel myProfile = new ProfileModel("NA", null, 0);
    public static RankModel myPerformance = new RankModel(0, -1);
    public static List<CompletedTestModule> g_completedTests = new ArrayList<>();
    public static List<ArticleModel> g_articleList = new ArrayList<>();
    public static List<VideoModel> g_videoList = new ArrayList<>();


    public static final int NOT_VISITED = 0;
    public static final int NOT_ANSWERED = 1;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;

    public static void createUserData(String email, String name, MyCompleteListener completeListener) {
        Map<String, Object> userData = new ArrayMap<>();
        userData.put("Email_ID", email);
        userData.put("Name", name);
        userData.put("Mean_Score", 0);

        DocumentReference userDoc = g_firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();
        batch.set(userDoc, userData);

        DocumentReference countDoc = g_firestore.collection("USERS").document("TotalUsers");
        batch.update(countDoc, "Count", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(unused -> completeListener.onSuccess())
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void getUserData(final MyCompleteListener completeListener) {
        g_firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myProfile.setName(documentSnapshot.getString("Name"));
                        myProfile.setEmail_ID(documentSnapshot.getString("Email_ID"));
                        Long score = documentSnapshot.getLong("Mean_Score");
                        myProfile.setMean_Score(score != null ? score : 0);

                        String profileUrl = documentSnapshot.getString("profile");
                        if (profileUrl != null) {
                            myProfile.setProfileURL(profileUrl);
                        }


                        myPerformance.setScore(documentSnapshot.getLong("Mean_Score").intValue());
                        completeListener.onSuccess();
                    } else {
                        completeListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void completedTests(MyCompleteListener completeListener) {
        g_completedTests.clear(); // reset before loading new data

        g_firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_SCORES")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String testId = entry.getKey();
                                Long scoreLong = (Long) entry.getValue();
                                int score = scoreLong != null ? scoreLong.intValue() : 0;
                                g_completedTests.add(new CompletedTestModule(testId, score));
                            }
                        }
                        completeListener.onSuccess();
                    } else {
                        completeListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void loadVideos(MyCompleteListener completeListener) {
        g_videoList.clear();

        g_firestore.collection("VIDEOS")
                .document("Main")
                .get()
                .addOnSuccessListener(doc -> {
                    Long countLong = doc.getLong("Number_of_Videos");
                    if (countLong != null) {
                        int count = countLong.intValue();

                        for (int i = 1; i <= count; i++) {
                            String videoID = doc.getString("Video" + i + "_ID");

                            if (videoID != null) {
                                g_firestore.collection("VIDEOS")
                                        .document(videoID)
                                        .get()
                                        .addOnSuccessListener(videoSnap -> {
                                            String title = videoSnap.getString("Video_title");
                                            String desc = videoSnap.getString("Video_description");
                                            String link = videoSnap.getString("Video_link");

                                            if (title != null && desc != null && link != null) {
                                                g_videoList.add(new VideoModel(videoID, title, desc, link));
                                            }

                                            if (g_videoList.size() == count) {
                                                completeListener.onSuccess();
                                            }
                                        })
                                        .addOnFailureListener(e -> completeListener.onFailure());
                            }
                        }
                    } else {
                        completeListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }


    public static void loadData(final MyCompleteListener completeListener) {
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                getUserData(completeListener);
            }

            @Override
            public void onFailure() {
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

                        List<CategoryModel> tempList = new ArrayList<>(Collections.nCopies((int) catCount, null));
                        final int[] loadedCount = {0};

                        for (int i = 1; i <= catCount; i++) {
                            final int index = i;
                            String catID = documentSnapshot.getString("Cat" + i + "_ID");

                            if (catID == null || catID.isEmpty()) {
                                loadedCount[0]++;
                                if (loadedCount[0] == catCount) {
                                    g_catList.clear();
                                    g_catList.addAll(tempList);
                                    completeListener.onSuccess();
                                }
                                continue;
                            }

                            g_firestore.collection("QUIZ").document(catID).get()
                                    .addOnSuccessListener(catDoc -> {
                                        if (catDoc.exists()) {
                                            String catName = catDoc.getString("Name");
                                            Long numTestsLong = catDoc.getLong("Number_of_Tests");
                                            int numOfTests = (numTestsLong != null) ? numTestsLong.intValue() : 0;
                                            tempList.set(index - 1, new CategoryModel(catID, catName, numOfTests));
                                        }
                                        loadedCount[0]++;
                                        if (loadedCount[0] == catCount) {
                                            g_catList.clear();
                                            g_catList.addAll(tempList);
                                            completeListener.onSuccess();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        loadedCount[0]++;
                                        if (loadedCount[0] == catCount) {
                                            g_catList.clear();
                                            g_catList.addAll(tempList);
                                            completeListener.onSuccess();
                                        }
                                    });
                        }
                    } else {
                        completeListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void loadTestData(MyCompleteListener completeListener) {
        g_testList.clear();

        g_firestore.collection("QUIZ")
                .document(g_catList.get(g_selected_cat_index).getDocID())
                .collection("TESTS_LIST")
                .document("TESTS_INFO")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    int numOfTest = g_catList.get(g_selected_cat_index).getNumberOfTests();

                    for (int i = 1; i <= numOfTest; i++) {
                        String testId = documentSnapshot.getString("Test" + i + "_ID");
                        String title = documentSnapshot.getString("Test" + i + "_title");
                        Long timeLong = documentSnapshot.getLong("Test" + i + "_Time");

                        if (testId != null && title != null && timeLong != null) {
                            g_testList.add(new TestModel(testId, title, 0, timeLong.intValue()));
                        }
                    }

                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void loadArticles(MyCompleteListener completeListener) {
        g_articleList.clear();

        g_firestore.collection("ARTICLES")
                .document("Articles")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long numOfArticlesLong = documentSnapshot.getLong("Number_of_Articles");

                    if (numOfArticlesLong != null) {
                        int numOfArticles = numOfArticlesLong.intValue();

                        for (int i = 1; i <= numOfArticles; i++) {
                            String articleID = documentSnapshot.getString("Article" + i + "_ID");

                            if (articleID != null) {
                                int finalI = i; // for logging/debugging
                                g_firestore.collection("ARTICLES")
                                        .document(articleID)
                                        .get()
                                        .addOnSuccessListener(articleSnap -> {
                                            String title = articleSnap.getString("Article_title");
                                            String content = articleSnap.getString("Article_content");

                                            if (title != null && content != null) {
                                                g_articleList.add(new ArticleModel(content, title, articleID));
                                            }

                                            if (g_articleList.size() == numOfArticles) {
                                                completeListener.onSuccess();
                                            }
                                        })
                                        .addOnFailureListener(e -> completeListener.onFailure());
                            }
                        }
                    } else {
                        completeListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }


    public static void loadquestions(MyCompleteListener completeListener) {
        g_questionList.clear();
        g_firestore.collection("Questions")
                .whereEqualTo("CATEGORY", g_catList.get(g_selected_cat_index).getDocID())
                .whereEqualTo("TEST", g_testList.get(g_selected_test_index).getTestID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        g_questionList.add(new QuestionModel(
                                doc.getString("QUESTION"),
                                doc.getString("A"),
                                doc.getString("B"),
                                doc.getString("C"),
                                doc.getString("D"),
                                doc.getLong("ANSWER").intValue(),
                                -1,
                                NOT_VISITED
                        ));
                    }
                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void loadMyScores(MyCompleteListener completeListener) {
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    for (int i = 0; i < g_testList.size(); i++) {
                        Long scoreLong = documentSnapshot.getLong(g_testList.get(i).getTestID());
                        int top = (scoreLong != null) ? scoreLong.intValue() : 0;
                        g_testList.get(i).setTopScore(top);
                    }
                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }

    public static void loadLeaderboardUsers(MyCompleteListener completeListener, List<LeaderboardUserModel> leaderboardList) {
        g_firestore.collection("USERS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    leaderboardList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.contains("Name") && doc.contains("Mean_Score")) {
                            String name = doc.getString("Name");
                            String profileUrl = doc.getString("profile");
                            int score = doc.getLong("Mean_Score") != null ? doc.getLong("Mean_Score").intValue() : 0;
                            leaderboardList.add(new LeaderboardUserModel(doc.getId(), name, profileUrl, score));
                        }
                    }
                    completeListener.onSuccess();
                })
                .addOnFailureListener(e -> completeListener.onFailure());
    }



    public static void saveResults(int finalScore, MyCompleteListener completeListener) {
        String uid = FirebaseAuth.getInstance().getUid();
        DocumentReference userDoc = g_firestore.collection("USERS").document(uid);
        DocumentReference scoreDoc = userDoc.collection("USER_DATA").document("MY_SCORES");

        scoreDoc.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> scores = new ArrayMap<>();

            if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                scores.putAll(documentSnapshot.getData());
            }

            String testId = g_testList.get(g_selected_test_index).getTestID();
            if (testId == null || testId.isEmpty()) {
                completeListener.onFailure();
                return;
            }

            Long prevScoreLong = (Long) scores.get(testId);
            int prevScore = prevScoreLong != null ? prevScoreLong.intValue() : -1;

            if (finalScore > prevScore) {
                scores.put(testId, finalScore);
            }

            int sum = 0;
            int count = 0;
            for (Object value : scores.values()) {
                if (value instanceof Number) {
                    sum += ((Number) value).intValue();
                    count++;
                }
            }
            int newMean = count > 0 ? sum / count : 0;

            WriteBatch batch = g_firestore.batch();
            batch.update(userDoc, "Mean_Score", newMean);
            batch.set(scoreDoc, scores, SetOptions.merge());

            batch.commit().addOnSuccessListener(unused -> {
                g_testList.get(g_selected_test_index).setTopScore(finalScore);
                myPerformance.setScore(finalScore);
                myProfile.setMean_Score(newMean);
                completeListener.onSuccess();
            }).addOnFailureListener(e -> completeListener.onFailure());
        }).addOnFailureListener(e -> completeListener.onFailure());
    }



}