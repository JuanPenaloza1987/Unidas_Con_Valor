package com.creatio.imm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginPremium extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CircularProgressButton btnLogin;
    private Context context;
    private SharedPreferences prefs;
    private String token_fb = "";
    private String type = "1";
    private EditText edtCode;
    private EditText edtPass;
    Button btnGoogle, btnFacebook, btnRegister,btnNormal;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    String refreshedToken = "sin token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_accountp);
        FacebookSdk.setApplicationId("671710776559354");
        FacebookSdk.sdkInitialize(getApplicationContext());
        context = LoginPremium.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        btnLogin = findViewById(R.id.btnLogin);
        edtCode = findViewById(R.id.edtCode);
        btnNormal = findViewById(R.id.btnNormal);
        edtPass = findViewById(R.id.edtPass);
        btnFacebook = findViewById(R.id.btnFace);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.startAnimation();
                Login();
            }
        });
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Login.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Register.class);
                startActivity(intent);
            }
        });


        callbackManager = CallbackManager.Factory.create();
        //------------------

//        AppEventsLogger.activateApp(this);

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(
                            "com.creatio.imm",
                            PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                    }
                } catch (PackageManager.NameNotFoundException e) {

                } catch (NoSuchAlgorithmException e) {

                }
                LoginManager.getInstance().logInWithReadPermissions(LoginPremium.this, Arrays.asList("public_profile", "email"));

            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        //Configuracion FIREBASE

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        FirebaseApp.initializeApp(this);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        mAuth = FirebaseAuth.getInstance();
        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("ds", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("ds", "onAuthStateChanged:signed_out");
                }
            }
        };
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code

                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String id = object.getString("id");
                                    LoginPay(email, id, name, "fb", id, "");
                                } catch (JSONException e) {
                                    Toast.makeText(context, "Error en el inicio de sesión por facebook", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(context, "Se canceló el inicio de sesión por facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                Toast.makeText(context, "Error en el inicio de sesión por facebook", Toast.LENGTH_SHORT).show();
                Log.e("error fb", exception.toString());
            }
        });
    }

    private void signIn() {
        FirebaseAuth.getInstance().signOut();
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(context, "Iniciando", Toast.LENGTH_SHORT).show();
                    }
                });
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("DS", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("DS", "signInWithCredential:onComplete:" + task.isSuccessful());
                        String name = acct.getDisplayName();
                        String id = acct.getId();
                        String email = acct.getEmail();
                        String image = acct.getPhotoUrl().toString();


                        if (!task.isSuccessful()) {
                            Log.w("DS", "signInWithCredential", task.getException());
                            Toast.makeText(LoginPremium.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            LoginPay(email, id, name, "gg", id, image);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void LoginPay(final String email, final String pass, final String name,final String media, final String id,final String image) {
        final ProgressDialog dialog = ProgressDialog.show(LoginPremium.this, null, "Iniciando sesión");
        // --- [Header elements] ---
        dialog.show();
        String url = getResources().getString(R.string.apiurl) + "LoginPay";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("DS", response);
                try {
                    JSONObject json = new JSONObject(response);


                    dialog.dismiss();
                    String success = json.getString("success");
                    if (success.equalsIgnoreCase("true")) {
                        JSONArray arr = json.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String last_name = obj.optString("last_name");
                            String email = obj.optString("email");
                            String phone = obj.optString("email");
                            String code = obj.optString("password");
                            String type = obj.optString("type_rel");
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("login", true);
                            editor.putBoolean("datauser", true);
                            editor.putBoolean("membresia", true);
                            editor.putString("ID", ID);
                            editor.putString("name", name);
                            editor.putString("last_name", last_name);
                            editor.putString("phone", phone);
                            editor.putString("email", email);
                            editor.putString("code", code);
                            editor.putString("type", type);
                            editor.apply();
                            dialog.dismiss();
                        }
                        finish();
                        Intent intent = new Intent(context,Colonies.class);
                        startActivity(intent);

                    }else{
                        Alerter.create(LoginPremium.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados.")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    Alerter.create(LoginPremium.this)
                            .setTitle("Oh oh")
                            .setText("No existe información con los datos proporcionados.")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                    btnLogin.revertAnimation();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
                dialog.dismiss();
                Alerter.create(LoginPremium.this)
                        .setTitle("Oh oh")
                        .setText("No existe información con los datos proporcionados.")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
                btnLogin.revertAnimation();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apikey", getResources().getString(R.string.apikey));
                params.put("pass", pass);
               // params.put("token", refreshedToken);
                params.put("media", media);
                params.put("name", name);
                params.put("email", email);
                params.put("id", id);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();

    }

    public void Login() {
        final String phone = edtCode.getText().toString();
        final String pass = edtPass.getText().toString();

        edtCode.setError(null);
        if (phone.trim().equalsIgnoreCase("")) {
            edtCode.setError("El teléfono es necesario.");
            btnLogin.revertAnimation();
            return;
        }
        edtPass.setError(null);
        if (pass.trim().equalsIgnoreCase("")) {
            edtPass.setError("El password es necesario.");
            btnLogin.revertAnimation();
            return;
        }

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "LoginHaveAccount")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("phone", phone)
                .addBodyParameter("password", pass)
                .addBodyParameter("token", token_fb)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                try {
                    btnLogin.revertAnimation();
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String last_name = obj.optString("last_name");
                            String email = obj.optString("email");
                            String phone = obj.optString("email");
                            String code = obj.optString("password");
                            String type = obj.optString("type_rel");
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("login", true);
                            editor.putBoolean("datauser", true);
                            editor.putBoolean("membresia", true);

                            editor.putString("ID", ID);
                            editor.putString("name", name);
                            editor.putString("last_name", last_name);
                            editor.putString("phone", phone);
                            editor.putString("email", email);
                            editor.putString("code", code);
                            editor.putString("type", type);
                            editor.apply();


                        }

                        setResult(300);
                        finish();

                    } else {
                        Alerter.create(LoginPremium.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados.")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    Alerter.create(LoginPremium.this)
                            .setTitle("Oh oh")
                            .setText("No existe información con los datos proporcionados.")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                    btnLogin.revertAnimation();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Alerter.create(LoginPremium.this)
                        .setTitle("Oh oh")
                        .setText("No existe información con los datos proporcionados.")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
                btnLogin.revertAnimation();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
