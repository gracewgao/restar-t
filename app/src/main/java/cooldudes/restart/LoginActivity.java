package cooldudes.restart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;

import cooldudes.restart.model.AppUser;

public class LoginActivity extends AppCompatActivity {

    public static AppUser appUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static FirebaseUser user;

    private static int RC_SIGN_IN = 1;
    private static String ANONYMOUS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Checks if user is signed in
        if (user != null){
            onSignedInInitialize(user);
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.FirebaseLogin)
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }

    }

    // Checks if user is properly signed in
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                user = mAuth.getCurrentUser();
                onSignedInInitialize(user);
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Please sign in again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Signs in the user
    private void onSignedInInitialize(final FirebaseUser user){

        FirebaseUserMetadata metadata = user.getMetadata();
        if (Math.abs(metadata.getCreationTimestamp() - metadata.getLastSignInTimestamp()) < 100) {
            // creates new user
            appUser = new AppUser(user.getUid());
            // redirects to onboarding screens
            Intent i = new Intent(this, OnboardingActivity.class);
            this.startActivity(i);
            this.finish();
            Toast.makeText(getApplicationContext(), "Signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

        } else {
            // existing user
            Intent i = new Intent(this, MainActivity.class);
            this.startActivity(i);
            this.finish();
            Toast.makeText(getApplicationContext(), "Signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }

    }

    public static void signOut(final Activity c){
        AuthUI.getInstance()
                .signOut(c)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(c, LoginActivity.class);
                        c.startActivity(i);
                        c.finish();
                    }
                });
        user = null;
    }
}