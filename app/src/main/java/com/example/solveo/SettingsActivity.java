package com.example.solveo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout changePassword, editName, editPic, deleteAcc, reportP;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        init();
    }

    private void init() {
        changePassword = findViewById(R.id.changePassword);
        editName = findViewById(R.id.editName);
        editPic = findViewById(R.id.editProfilePicture);
        deleteAcc = findViewById(R.id.deleteAccount);
        reportP = findViewById(R.id.reportProblem);

        // Hide change password for Google accounts
        boolean isGoogleUser = FirebaseAuth.getInstance().getCurrentUser()
                .getProviderData().get(1).getProviderId().equals("google.com");

        if (isGoogleUser) {
            changePassword.setVisibility(View.GONE);
        } else {
            changePassword.setOnClickListener(v -> {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Reset email sent to " + email, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

        editName.setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setHint("Enter your new name");
            input.setText(DbQuery.myProfile.getName());
            input.setPadding(40, 30, 40, 30);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Edit Your Name")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            updateName(newName);
                        } else {
                            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        editPic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        deleteAcc.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your Solveo account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteUserAccount();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        reportP.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"solveo.math.app@gmail.com"}); // <-- replace with your email
            intent.putExtra(Intent.EXTRA_SUBJECT, "Solveo App – Report a Problem");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi Solveo team,\n\nI'm experiencing an issue with the app:\n\n[Write your issue here]\n\nThanks!");

            try {
                startActivity(Intent.createChooser(intent, "Send report using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(SettingsActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateName(String newName) {
        String uid = FirebaseAuth.getInstance().getUid();
        DbQuery.g_firestore.collection("USERS").document(uid)
                .update("Name", newName)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                    DbQuery.myProfile.setName(newName);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        String uid = FirebaseAuth.getInstance().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference().child("profile_pictures/" + uid + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            DbQuery.g_firestore.collection("USERS")
                                    .document(uid)
                                    .update("profile", downloadUri.toString())
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                    });
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUserAccount() {
        String uid = FirebaseAuth.getInstance().getUid();

        // 🔥 Delete profile picture from storage
        StorageReference profilePicRef = FirebaseStorage.getInstance()
                .getReference().child("profile_pictures/" + uid + ".jpg");

        profilePicRef.delete().addOnSuccessListener(unused -> {
            // Optional: show toast or log here
        }).addOnFailureListener(e -> {
            // Optional: image didn't exist — not a problem
        });

        // 🧨 Delete subcollection first (MY_SCORES)
        DbQuery.g_firestore.collection("USERS")
                .document(uid)
                .collection("USER_DATA")
                .document("MY_SCORES")
                .delete()
                .addOnSuccessListener(unused -> {

                    // ✅ Decrease total user count
                    DbQuery.g_firestore.collection("USERS")
                            .document("TotalUsers")
                            .update("Count", FieldValue.increment(-1));

                    // 📦 Delete the main user document
                    DbQuery.g_firestore.collection("USERS")
                            .document(uid)
                            .delete()
                            .addOnSuccessListener(unused2 -> {

                                // 🧼 Delete FirebaseAuth account
                                FirebaseAuth.getInstance().getCurrentUser().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to delete Firebase account", Toast.LENGTH_SHORT).show();
                                        });

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to delete user document", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete subcollection", Toast.LENGTH_SHORT).show();
                });
    }



}
