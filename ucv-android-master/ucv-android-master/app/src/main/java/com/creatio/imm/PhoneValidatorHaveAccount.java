package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class PhoneValidatorHaveAccount extends AppCompatActivity {
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
        context = PhoneValidatorHaveAccount.this;
        mAuth = FirebaseAuth.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        btnRenviar = findViewById(R.id.btnRenviar);
        btnValidator = findViewById(R.id.btnValidator);
        edtValidator = findViewById(R.id.edtValidator);
        txt_pin_entry = findViewById(R.id.txt_pin_entry);
        txtMsj = findViewById(R.id.txtMsj);
        btnRenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnValidator.callOnClick();
            }
        });
        btnValidator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetworking.post(getResources().getString(R.string.apiurl) + "PhonePassLost")
                        .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                        .addBodyParameter("phone",edtValidator.getText().toString())
                        .setPriority(Priority.IMMEDIATE)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success = response.getString("success");
                            String message = response.getString("message");
                            if (success.equalsIgnoreCase("true")) {
                                String phone = "+52" + edtValidator.getText().toString();

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
                                        phone,        // Phone number to verify
                                        60,                 // Timeout duration
                                        TimeUnit.SECONDS,   // Unit of timeout
                                        PhoneValidatorHaveAccount.this,               // Activity (for callback binding)
                                        mCallbacks);
                            } else {
                                Alerter.create(PhoneValidatorHaveAccount.this)
                                        .setTitle("Error")
                                        .setText("No existe el teléfono en nuestra base de datos.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Alerter.create(PhoneValidatorHaveAccount.this)
                                    .setTitle("Error")
                                    .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Alerter.create(PhoneValidatorHaveAccount.this)
                                .setTitle("Error")
                                .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                });

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
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
                txtMsj.setText("Te enviamos un mensaje de texto con el código de validación. Ingresalo el los campos de abajo. ¡Gracias!, esto puede tardar algunos minutos");
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


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ex",e.toString());
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            btnValidator.revertAnimation();
                            FirebaseUser user = task.getResult().getUser();
                            AndroidNetworking.post(getResources().getString(R.string.apiurl) + "PassLost")
                                    .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                                    .addBodyParameter("codigo", txt_pin_entry.getText().toString())
                                    .addBodyParameter("phone",edtValidator.getText().toString())
                                    .setPriority(Priority.IMMEDIATE)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.e("responseee", response.toString());
                                        String success = response.getString("success");
                                        String message = response.getString("message");
                                        if (success.equalsIgnoreCase("true")) {
                                            countError = 0;
                                            JSONArray arr = response.getJSONArray("data");
                                            for (int i = 0; i < arr.length(); i++) {
                                                JSONObject obj = arr.getJSONObject(i);
                                                String ID = obj.optString("ID");
                                                String name = obj.optString("name");
                                                String last_name = obj.optString("last_name");
                                                String email = obj.optString("email");
                                                String code = obj.optString("password");
                                                String type = obj.optString("type_rel");
                                                SharedPreferences.Editor editor = prefs.edit();

                                                editor.putString("ID", ID);
                                                editor.putString("name", name);
                                                editor.putString("last_name", last_name);
                                                editor.putString("email", email);
                                                editor.putString("code", code);
                                                editor.putString("type", type);
                                                editor.apply();


                                            }
                                            Toast.makeText(context, "Bienvenido a eCupon. Disfruta de los beneficios que tenemos para ti.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Alerter.create(PhoneValidatorHaveAccount.this)
                                                    .setTitle("Error")
                                                    .setText("Los datos enviados no son los correctos.")
                                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                                    .show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Alerter.create(PhoneValidatorHaveAccount.this)
                                                .setTitle("Error")
                                                .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                                .show();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Alerter.create(PhoneValidatorHaveAccount.this)
                                            .setTitle("Error")
                                            .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                                            .show();
                                }
                            });
                            // ...
                        } else {
                            btnValidator.revertAnimation();
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            countError = countError + 1;
                            if (countError > 3){
                                Alerter.create(PhoneValidatorHaveAccount.this)
                                        .setTitle("¿Problemas?")
                                        .setText("Parece que estas teniendo problemas, intenta con volver a enviar el código.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.yellow))
                                        .show();
                                btnRenviar.setVisibility(View.VISIBLE);
                            }else{
                                Alerter.create(PhoneValidatorHaveAccount.this)
                                        .setTitle("Oh oh")
                                        .setText("El código proporcionado no es válido, intenta nuevamente.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }



                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Log.e("error","error");
                            }
                        }
                    }
                });
    }

}
