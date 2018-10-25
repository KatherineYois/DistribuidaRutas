package com.example.usuario.d_rutabusaqp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

//Implementar la GoogleApiClient
public class InicioTipoActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    //Para ver en console
    private static final String TAG = "InicioTipoActivity";

    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //GOOGLE
    private SignInButton btnSignInGoogle;
    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN_GOOGLE_CODE = 1;

    //FACEBOOK
    private CallbackManager callbackManager;
    private LoginButton btnSignInFacebook;

    //Creamos el layout para nuestro activity
    protected void onCreate(Bundle savedInstanceState) {//
        super.onCreate(savedInstanceState);//
        setContentView(R.layout.iniciotipo_main);//
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        Log.w(TAG,"CallBack"+callbackManager.toString());
        btnSignInGoogle   = (SignInButton) findViewById(R.id.btnSignInGoogle);
        btnSignInFacebook = (LoginButton) findViewById(R.id.btnSignInFacebook);

        iniciarlizar();

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                //La forma para abrir el layout de eleccion de cuenta de Google
                startActivityForResult(i,SIGN_IN_GOOGLE_CODE);
            }
        });

        //Permisos acceder a la cuenta de facebook
        btnSignInFacebook.setReadPermissions("email","public_profile");
        btnSignInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG,"Facebook Login Success Token: "+ loginResult.getAccessToken().getToken());
                SignInFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG,"Facebook Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG,"Facebook Error");
                error.printStackTrace();
            }
        });
    }

    //Metodo para inicia nuestro registro
    private void iniciarlizar(){
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Log.w(TAG,"onAuthStateChanged  - signed_in" +firebaseUser.getUid());
                    Log.w(TAG,"onAuthStateChanged  - signed_in" +firebaseUser.getEmail());
                }else{
                    Log.w(TAG,"onAuthStateChanged - signed_out");
                }
            }
        };

        //Configurar el registro con cuenta Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    //Es le metodo que se usa para saber que se hace despues de reconocer la cuenta de Google
    private void SingInGoogleFirebase(GoogleSignInResult googleSignInResult){
        if(googleSignInResult.isSuccess()){
            //Si se logro hacer de manera efectiva
            AuthCredential authCredential =
                    GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(),null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Si la tarea con las credenciales necesarias se hizo de manera efectiva
                        Toast.makeText(InicioTipoActivity.this, "Google Authentication Success", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(InicioTipoActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(InicioTipoActivity.this, "Google Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            //Si hubo algun inconveniente al iniciar con una cuenta
            Toast.makeText(InicioTipoActivity.this, "Google Sign In Unsuccess", Toast.LENGTH_SHORT).show();
        }
    }

    //Es el metodo que se usa para saver q ue se hace despues de reconocer la cuenta Facebook
    private void SignInFacebookFirebase(AccessToken accessToken){
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(InicioTipoActivity.this, "Facebook Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(InicioTipoActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(InicioTipoActivity.this, "Facebook Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    protected void onStop() {
        super.onStop();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    //La respuesta de todo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Si no es de Google
        if(requestCode == SIGN_IN_GOOGLE_CODE){
            //El intent "data" es la informacion de la cuenta,
            // lo que devuelve es un resultado que se utilizara en el metodo SingInGoogleFirebase
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            SingInGoogleFirebase(googleSignInResult);
        }else{
            //Es de facebook
            callbackManager.onActivityResult(resultCode,resultCode,data);
        }
    }

    //Por el implemento que se realizo GoogleApiClient
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
