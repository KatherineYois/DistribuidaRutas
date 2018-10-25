package com.example.usuario.d_rutabusaqp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InicioActivity extends AppCompatActivity{


    /*--------------------------------------*/
    private Button btnIniciarSesion;
    private Button btnRegistrar;

    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Creamos el layout
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_main);

        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CODIGO
                Intent i = new Intent(InicioActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InicioActivity.this,InicioTipoActivity.class);
                startActivity(i);
                }
            });
        }
    }
