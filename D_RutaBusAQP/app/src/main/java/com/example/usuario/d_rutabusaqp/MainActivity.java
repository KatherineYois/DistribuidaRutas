package com.example.usuario.d_rutabusaqp;

import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,RutasBusesFragment.OnFragmentInteractionListener
        ,MapaFragment.OnFragmentInteractionListener
        //Para usar cuenta de Google
        , GoogleApiClient.OnConnectionFailedListener{
    //TERMINAL
    private static final String TAG = "MainActivity";

    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //GOOGLE
    private GoogleApiClient googleApiClient;

    //LAYOUT
    private TextView tvUsuario;
    private TextView tvEmail;
    private ImageView imvFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Fragment prederminado
        Fragment fragmentDeterminado = new MapaFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragmentDeterminado).commit();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Para tomar los id de la cabecera para los datos del usuario
        View navHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        tvUsuario = (TextView) navHeader.findViewById(R.id.tvUsuario);
        tvEmail = (TextView) navHeader.findViewById(R.id.tvEmail);
        imvFoto = (ImageView) navHeader.findViewById(R.id.imvFoto);

        initialize();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment miFragment = null;
        boolean fragmentSeleccionado = false;

        if (id == R.id.nav_Rutas) {
            miFragment = new RutasBusesFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_Mapa) {
            miFragment = new MapaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_CerrarSesion){
            SignOut();
        }

        if(fragmentSeleccionado == true){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,miFragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //DATOS USUARIO
    private void initialize(){
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser !=  null){
                    tvUsuario.setText(firebaseUser.getDisplayName());
                    tvEmail.setText(firebaseUser.getEmail());
                    Picasso.with(MainActivity.this).load(firebaseUser.getPhotoUrl()).into(imvFoto);
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };

        //Inicialización de Google Account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    //CERRAR SESION
    private void SignOut(){
        Toast.makeText(this, "qweqweqwe", Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        //Cerrar sesion con google
        if(Auth.GoogleSignInApi != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        Intent i = new Intent(MainActivity.this,InicioActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, "Error in Google SignOut", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //Cerrar sesion con facebook
        if(LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }

    //TOMAR EN CUENTA EL CODIGO DE START AND STOP

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
