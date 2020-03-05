package com.creatio.imm;

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
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class PhoneValidator extends AppCompatActivity {
    private Context context;
    private SharedPreferences prefs;
    private CircularProgressButton btnValidator;
    private PinEntryEditText txt_pin_entry;
    private EditText edtValidator;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String TAG = "PhoneValidator";
    private FirebaseAuth mAuth;
    private TextView txtMsj;
    private Integer countError = 0;
    private Button btnRenviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validator);
        context = PhoneValidator.this;
        mAuth = FirebaseAuth.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        btnRenviar = findViewById(R.id.btnRenviar);
        btnValidator = findViewById(R.id.btnValidator);
        edtValidator = findViewById(R.id.edtValidator);
        txt_pin_entry = findViewById(R.id.txt_pin_entry);
        txtMsj = findViewById(R.id.txtMsj);
        txtMsj.setText("Para continuar necesitamos validar tu número de teléfono");
        btnRenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnValidator.callOnClick();
            }
        });
        btnValidator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = edtValidator.getText().toString();
                edtValidator.setError(null);
                if (phone.trim().equalsIgnoreCase("")) {
                    edtValidator.setError("Campo necesario");
                    return;
                }
                if (phone.length() < 10) {
                    edtValidator.setError("El teléfono debe de ser de 10 digitos. Incluyendo la LADA");
                    return;
                }
                btnValidator.startAnimation();
                mAuth.setLanguageCode("es");
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+52" + edtValidator.getText().toString(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        PhoneValidator.this,               // Activity (for callback binding)
                        mCallbacks);
            }
        });
        getKeyHash("SHA");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(final PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e(TAG, "onVerificationCompleted:" + credential);
                edtValidator.setVisibility(View.GONE);
                txt_pin_entry.setVisibility(View.VISIBLE);
                btnValidator.setText("Validar");
                btnValidator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnValidator.startAnimation();
                        signInWithPhoneAuthCredential(credential);
                    }
                });

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(final String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                btnValidator.revertAnimation();
                Log.e(TAG, "onCodeSent:" + verificationId);
                edtValidator.setVisibility(View.GONE);
                txtMsj.setText("Te enviamos un mensaje de texto con el código de validación. Ingresalo el los campos de abajo. ¡Gracias!, esto puede tardar algunos minutos.");
                txt_pin_entry.setVisibility(View.VISIBLE);
                btnValidator.setText("Validar");
                btnValidator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnValidator.startAnimation();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, txt_pin_entry.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    }
                });

            }
        };

    }

    private void getKeyHash(String hashStretagy) {
        PackageInfo info;
        try {

            info = getPackageManager().getPackageInfo(
                    "com.creatio.icupon", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hash_key = new String(Base64.encode(md.digest(), 0));
                Log.e("hash",hash_key);
            }

        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            btnValidator.revertAnimation();
                            FirebaseUser user = task.getResult().getUser();
                            String url = getResources().getString(R.string.apiurl) + "UpdateCodeUser";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.e("response",response);
                                        JSONObject json =  new JSONObject(response);
                                        String success = json.getString("success");
                                        if (success.equalsIgnoreCase("true")) {
                                            countError = 0;
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("phone",edtValidator.getText().toString());
                                            editor.putBoolean("login", true);
                                            editor.apply();
                                            Toast.makeText(context, "Bienvenido a eCupon. Disfruta de los beneficios que tenemos para ti.", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(context,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Alerter.create(PhoneValidator.this)
                                                    .setTitle("Error")
                                                    .setText("Los datos enviados no son los correctos.")
                                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                                    .show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Alerter.create(PhoneValidator.this)
                                                .setTitle("Error")
                                                .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                                .show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Anything you want
                                }
                            }){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("Content-Type","application/x-www-form-urlencoded");
                                    return params;
                                }
                                @Override
                                protected Map<String,String> getParams(){
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("apikey", getResources().getString(R.string.apikey));
                                    params.put("ID", prefs.getString("ID", "0"));
                                    params.put("codigo", txt_pin_entry.getText().toString());
                                    params.put("phone", edtValidator.getText().toString());
                                    return params;
                                }

                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
//                            AndroidNetworking.post(getResources().getString(R.string.apiurl) + "UpdateCodeUser")
//                                    .addBodyParameter("apikey", getResources().getString(R.string.apikey))
//                                    .addBodyParameter("ID",prefs.getString("ID", "0"))
//                                    .addBodyParameter("code", txt_pin_entry.getText().toString())
//                                    .addBodyParameter("phone",edtValidator.getText().toString())
//                                    .setPriority(Priority.IMMEDIATE)
//                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        String success = response.getString("success");
//                                        if (success.equalsIgnoreCase("true")) {
//                                            countError = 0;
//                                            SharedPreferences.Editor editor = prefs.edit();
//                                            editor.putString("phone",edtValidator.getText().toString());
//                                            editor.putBoolean("login", true);
//                                            editor.apply();
//                                            Toast.makeText(context, "Bienvenido a iCupon. Disfruta de los beneficios que tenemos para ti.", Toast.LENGTH_SHORT).show();
//
//                                            Intent intent = new Intent(context,MainActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        } else {
//                                            Alerter.create(PhoneValidator.this)
//                                                    .setTitle("Error")
//                                                    .setText("Los datos enviados no son los correctos.")
//                                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
//                                                    .show();
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                        Alerter.create(PhoneValidator.this)
//                                                .setTitle("Error")
//                                                .setText("Ya estamos trabajando en ello. Disculpa las molestias")
//                                                .setBackgroundColorInt(getResources().getColor(R.color.red))
//                                                .show();
//                                    }
//                                }
//
//                                @Override
//                                public void onError(ANError anError) {
//                                    Alerter.create(PhoneValidator.this)
//                                            .setTitle("Error")
//                                            .setText("Ya estamos trabajando en ello. Disculpa las molestias")
//                                            .setBackgroundColorInt(getResources().getColor(R.color.red))
//                                            .show();
//                                }
//                            });
                            // ...
                        } else {
                            btnValidator.revertAnimation();
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            countError = countError + 1;
                            if (countError > 3){
                                Alerter.create(PhoneValidator.this)
                                        .setTitle("¿Problemas?")
                                        .setText("Parece que estas teniendo problemas, intenta con volver a enviar el código.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.yellow))
                                        .show();
                                btnRenviar.setVisibility(View.VISIBLE);
                            }else{
                                Alerter.create(PhoneValidator.this)
                                        .setTitle("Oh oh")
                                        .setText("El código proporcionado no es válido, intenta nuevamente.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }



                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
