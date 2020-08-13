package com.example.labexamandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private static final String TAG ="MainActivity".getClass().getName() ;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mSignInButton;
    private GoogleSignInClient mSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mFirebaseAuth;
    private TextView email,password;
    private Button signIn;
    private TextView phoneTextView;
    private LoginButton facebookButton;
    private CallbackManager mCallbackManager;
    GoogleSignInAccount account ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setConnection();
        Googlesingconfigration();
        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PhoneActivity.class));
            }
        });
       signIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            String em= email.getText().toString();
            final String pass= password.getText().toString();
            if (em!="" && pass!="")
            {
             mFirebaseAuth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                   Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
                }
                 }
             });
            }
           }
       });
       mSignInButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent singInIntent=mGoogleSignInClient.getSignInIntent();
               startActivityForResult(singInIntent,RC_SIGN_IN );

           }
       });
       facebookButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mCallbackManager = CallbackManager.Factory.create();
               LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
               LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                   @Override
                   public void onSuccess(LoginResult loginResult) {
                       Log.d(TAG, "facebook:onSuccess:" + loginResult);
                       handleFacebookAccessToken(loginResult.getAccessToken());
                   }

                   @Override
                   public void onCancel() {
                       Log.d(TAG, "facebook:onCancel");

                   }

                   @Override
                   public void onError(FacebookException error) {
                       Log.d(TAG, "facebook:onError", error);

                   }
               });
           }
       });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);

            }
        }
        else if (requestCode > 0)
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));


                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());


                        }


                    }
                });
    }


    private void Googlesingconfigration() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(MainActivity.this,gso);
    }

    private void setConnection() {
        signIn = findViewById(R.id.signIn);
        mSignInButton = findViewById(R.id.GoogleSignIn);
        email=findViewById(R.id.emailhere);
        phoneTextView=findViewById(R.id.authhere);
        password= findViewById(R.id.passwordhere);
        mFirebaseAuth= FirebaseAuth.getInstance();
        facebookButton=findViewById(R.id.fblogin);
        account = GoogleSignIn.getLastSignedInAccount(this);


    }

    public void normalLogin(View view) {
        mFirebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful())
              {
                  startActivity(new Intent(MainActivity.this,HomeActivity.class));
                  finish();
              }
            }
        });
    }
}