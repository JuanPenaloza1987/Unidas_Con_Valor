package com.creatio.imm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditPerfilTarjeta2 extends AppCompatActivity {
    private TextView txtRegresar1;
    private Context context;
    private Button btnContinuar;
    private Button btnEditImagen;
    private ImageView imgPerfilTar;
    private EditText editTextName;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private TextView txtNumTarjeta;
    private TextView txtNombrePerfil;
    private TextView txtApellidoPaPerfil;
    private TextView txtApellidoMaPerfil;
    private TextView txtHola;
    private String ids;
    private String numTarjeta;
    private Integer aceptoTerminos = -1;
    private Switch stchAceptoTerminos;
    private String photo;
    private String init;
    private TextView txtAceptoTerminos;
    private SharedPreferences prefs;
    Bundle datos;
    private String tipoFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil_tarjeta2);
        //extraemos los datos enviados de la pantalla anterior.
        datos=getIntent().getExtras();
        context = EditPerfilTarjeta2.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ids = datos.getString("ids");
        numTarjeta = datos.getString("numTarjeta");
        init = datos.getString("init");
        txtRegresar1 = findViewById(R.id.txtRegresar1);
        btnContinuar = findViewById(R.id.btnContinuar2);
        btnEditImagen = findViewById(R.id.btnEditImagen);
        imgPerfilTar  = findViewById(R.id.imgPerfilTar);

        txtNumTarjeta = findViewById(R.id.txtNumTarjeta);
        txtNombrePerfil = findViewById(R.id.txtNombrePerfil);
        txtApellidoPaPerfil = findViewById(R.id.txtApellidoPaPerfil);
        txtApellidoMaPerfil = findViewById(R.id.txtApellidoMaPerfil);
        txtHola = findViewById(R.id.textView21);

        stchAceptoTerminos = findViewById(R.id.stchAceptoTerminos);
        stchAceptoTerminos.setVisibility(View.GONE);
        stchAceptoTerminos.setChecked(false);
        txtAceptoTerminos = findViewById(R.id.textView41);
        txtAceptoTerminos.setVisibility(View.GONE);
        txtAceptoTerminos.setPaintFlags(txtAceptoTerminos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtRegresar1.setOnClickListener(onclickRegresar1);
        btnContinuar.setOnClickListener(onclickContinuar);
        btnEditImagen.setOnClickListener(onclickEditFoto);
        stchAceptoTerminos.setChecked(true);
        stchAceptoTerminos.setOnCheckedChangeListener(aceptarTerminos);
        traerInfo();
    }

    private CompoundButton.OnCheckedChangeListener aceptarTerminos = new CompoundButton.OnCheckedChangeListener() {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked == true){
                aceptoTerminos = 1;
            }else{
                aceptoTerminos = 0;
            }
        }
    };

    private View.OnClickListener onclickContinuar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validarCampos()){
                uploadImage();
                final Intent i = new Intent(context,TarjetaMenuIniActivity.class);
                i.putExtra("ids",ids);
                i.putExtra("numTarjeta",numTarjeta);
                startActivity(i);
            }else{
                Alerter.create(EditPerfilTarjeta2.this)
                        .setText("Favor de ingresar una foto. \n "+
                                "Ingrese nombre, apellidos. \n" +
                                "Acepte términos y política de privacidad.")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
            }
        }
    };

    private View.OnClickListener onclickRegresar1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                final Intent i = new Intent(context,TarjetaMenuIniActivity.class);
                i.putExtra("ids",ids);
                i.putExtra("numTarjeta",numTarjeta);
                startActivity(i);
        }
    };

    private View.OnClickListener onclickEditFoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            showFileChooser();

  /*          final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_alert_foto);
            // set the custom dialog components - text, image and button
            ImageButton btnCamaraOpt = (ImageButton) dialog.findViewById(R.id.btnCamaraOpt);
            ImageButton btnGaleryOpt = (ImageButton) dialog.findViewById(R.id.btnGaleryOpt);

            // if button is clicked, close the custom dialog
            btnCamaraOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    tipoFoto ="camara";
                    Log.e("camaraAction", "entro22");
                    dispatchTakePictureIntent();
                }
            });
            btnGaleryOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    tipoFoto ="galery";
                    showFileChooser();
                }
            });
            dialog.show();
            */
        }
    };

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(takePictureIntent, 0);
        //}
        Log.e("camaraAction", "entroA");
    }

    public String getStringImagen(Bitmap bmp){
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
        return "";
    }

    private void uploadImage(){
        //Mostrar el diálogo de progreso
        final ProgressDialog loading = ProgressDialog.show(this,"Subiendo...","Espere por favor...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apiurl) +"UpdateInfoAppByCardId" , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Descartar el diálogo de progreso
                loading.dismiss();
                //Mostrando el mensaje de la respuesta
                //Toast.makeText(EditPerfilTarjeta.this, response , Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("responseEditProfile", response);
                    String success = json.getString("success");
                    if (success.equalsIgnoreCase("true")) {
                        Alerter.create(EditPerfilTarjeta2.this)
                                .setTitle("Hecho ")
                                .setText("Los datos enviados se han actualizado correctamente.")
                                .setBackgroundColorInt(getResources().getColor(R.color.green))
                                .show();
                    } else {
                        Alerter.create(EditPerfilTarjeta2.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados..")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    //Descartar el diálogo de progreso
                    loading.dismiss();
                    Alerter.create(EditPerfilTarjeta2.this)
                            .setTitle("Oh oh")
                            .setText("No existe información con los datos proporcionados...")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Descartar el diálogo de progreso
                loading.dismiss();
                // Anything you want
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
                //Convertir bits a cadena
                String imagen = getStringImagen(bitmap);
                //Obtener el nombre de la imagen
                String nombre = txtNumTarjeta.getText().toString().trim();

                String nombreCliente = txtNombrePerfil.getText().toString().trim();
                String apellidoPaterno = txtApellidoPaPerfil.getText().toString().trim();
                String apellidoMaterno = txtApellidoMaPerfil.getText().toString().trim();
                //Creación de parámetros
                Map<String, String> params = new HashMap<String, String>();
                //Agregando de parámetros
                params.put("apikey", getResources().getString(R.string.apikey));
                params.put("numTarjeta", numTarjeta);
                params.put("id", ids);
                params.put("nombreCliente", nombreCliente);
                params.put("apellidoPaterno", apellidoPaterno);
                params.put("apellidoMaterno", apellidoMaterno);
                params.put("foto", imagen);
                Log.e("data", String.valueOf(params));

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("login", true);
                editor.putBoolean("datauser", true);
                editor.putBoolean("membresia", true);
                editor.putString("ID", ids);
                editor.putString("numTarjeta", numTarjeta);
                editor.putString("name", nombreCliente);
                editor.putString("last_name", apellidoPaterno);
                editor.apply();

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
        final Intent iCancelar = new Intent(EditPerfilTarjeta2.this, TarjetaMenuIniActivity.class);
        iCancelar.putExtra("ids",ids);
        iCancelar.putExtra("numTarjeta",numTarjeta);
        startActivity(iCancelar);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri filePath = data.getData();
                //Cómo obtener el mapa de bits de la Galería
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Configuración del mapa de bits en ImageViev
                imgPerfilTar.setImageBitmap(bitmap);
                /*if (tipoFoto.equals("camara")){
                    Log.e("camaraAction", "entro");
                    //Cómo obtener el mapa de bits de la Galería
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    bitmap = imageBitmap;
                    Log.e("camaraAction2", String.valueOf(imageBitmap));
                    //Configuración del mapa de bits en ImageViev
                    imgPerfilTar.setImageBitmap(bitmap);
                }else if (tipoFoto.equals("galery")){
                    Log.e("camaraAction", "entro-22");
                    Uri filePath = data.getData();
                    //Cómo obtener el mapa de bits de la Galería
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //Configuración del mapa de bits en ImageViev
                    imgPerfilTar.setImageBitmap(bitmap);
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //funcion para mostrar la imagen en la pantalla
    private void mostrarFoto(String foto){
        Log.e("photoEjecutada" , foto );
        String base64String = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAIAAAB7GkOtAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTM4IDc5LjE1OTgyNCwgMjAxNi8wOS8xNC0wMTowOTowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6Q0IwQjQ2QTQyNjcyMTFFQUIxNjBDMDczMEY5NzQ0RUYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6Q0IwQjQ2QTUyNjcyMTFFQUIxNjBDMDczMEY5NzQ0RUYiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo4MkNDM0U2NTI2NzIxMUVBQjE2MEMwNzMwRjk3NDRFRiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo4MkNDM0U2NjI2NzIxMUVBQjE2MEMwNzMwRjk3NDRFRiIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PiQS300AAFbASURBVHja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";
        String base64Image = base64String.split(",")[1];
        if (foto == null ){
            foto = base64Image;
        }

        byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgPerfilTar.setImageBitmap(decodedByte);
    }

    // validar que los campos tengan informacion
    private boolean validarCampos(){
        boolean validar = true;
        float nombreCliente = txtNombrePerfil.getTextSize();
        float apellidoPaterno = txtApellidoPaPerfil.getTextSize();
        float apellidoMaterno = txtApellidoMaPerfil.getTextSize();

        if (nombreCliente == 0){
            validar = false;
        }else if (apellidoPaterno == 0){
            validar = false;
        }else if (apellidoMaterno == 0){
            validar = false;
        }else if (aceptoTerminos != 1){
            validar = false;
        }
        return validar;
    }

    private void traerInfo() {
        numTarjeta = "987654321";
        if (numTarjeta != null && numTarjeta.length() > 0 ){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apitarurl), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        Log.e("response", response);
                        String success = json.getString("success");
                        if (success.equalsIgnoreCase("true")) {
                            String datos = json.getString("card");
                            Log.e("user", datos);
                            JSONObject card = new JSONObject(datos);
                            String code = card.optString("code");
                            String name;
                            String last_name ;
                            String middle_name ;
                            name = card.getString("name");
                            last_name = card.getString("last_name");
                            middle_name = card.getString("middle_name");
                            photo = card.getString("photo");
                            Log.e("photo",photo);
                            if (photo == null || photo == "null" || photo== "") {
                                Log.e("photo1",photo);
                                String base64String = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAIAAAB7GkOtAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTM4IDc5LjE1OTgyNCwgMjAxNi8wOS8xNC0wMTowOTowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6Q0IwQjQ2QTQyNjcyMTFFQUIxNjBDMDczMEY5NzQ0RUYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6Q0IwQjQ2QTUyNjcyMTFFQUIxNjBDMDczMEY5NzQ0RUYiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo4MkNDM0U2NTI2NzIxMUVBQjE2MEMwNzMwRjk3NDRFRiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo4MkNDM0U2NjI2NzIxMUVBQjE2MEMwNzMwRjk3NDRFRiIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PiQS300AAFbASURBVHja7J0FXBVb1PYlpRQLBLFAkRSkFEEQTCwssLDFLuzExG6u3YWBYgEKGJSUDRgIiggiFiklCHz7wvf6+noVgdlzzpxznv/vykU8rJlZs2c9e+3Zey+xsrKyWgAAAEQPcbgAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAIAAAAAAgAAAAACAAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAUUcSLgBCxpcvX1JTU9PS0t69e/f+fdp78ictrbCwIDs7p6Agv6Cg8OvXr3l5eSUlJeTDUlJSsrKyFb8oISFRr169unXrKv4PDRrUb9y4sZpa03LUVFVVJSXxyADhQaysrAxeAAIKCeKvX79++fJlfHw8+VLxTU5ODlv5srg40YPWrVvp6Oi0aaOlq6ujra1NfoIbASAAALAOaa6vXyc++peHhCdPovPz8/l7SiRpaNu2bfv27c3MTDt06KCsrIzbBCAAAFAL+k+fPg0ODgkODg4PD8/KyuLy2TZv3pxoQYcO5l262JL8ALcPQAAAqDbv3r3z9/e/c+dOaOjd9PR0QbwENTW1Ll26dO3alYhBw4YNcU8BBACAyoiJifH5F9/o6GjheczExIyMjAYM6D9w4CANDXXcZQABAOB/efTokaen59Wr15KTk4X7Sg0MDPr3tx80aFCbNm1w3wEEAIgub9++JXH/zJmz8fHxonbtenp6o0aNGjFiOEaHAAQAiBCFhYVeXl4nT568ezdMxF0hJSXVp0/vsWPHdu3aVVwcCzMBBAAIL0lJSYcPHzlx4kRGRga88TNqamqjR4+ePHmSkpISvAEgAEB4IM3s9u3b+/bt9/f3R5OrhNq1aw8bNmzGjOm6urrwBoAAAMGmpKTk0qXL27Zti42NhTeqTrdu3WbPnmVraysmJgZvAAgAEDCKiopOnTq1c+fOxMQ38EbNMDAwcHVd3rt3b7gCQACAwIT+Y8eOb9q06ePHj/AGc8zMzJYtW9q9e3e4AkAAAHcpLS29cOGCm5sbev3Uad++/Zo1q62srOAKAAEAnMPPz2/lylVPnz6FK9ijW7duGzasxytiAAEAXCEuLm7+/AWBgYFwBQ+QkJAYM2bMqlUrsYIMQAAAP/n69ev69Rv27t37/ft3eIOX1KtXb8WKFc7OE4gewBsAAgB4Cmk5Z8+eXb7cFW96+YihoeGePbuNjIzgCgABADwiKSlpypSpoaGhcAXfERcXnzp1yooVKxUU5OENAAEALFJaWrp3777Vq1fzvRQX+JkWLVrs37/P2toargAQAMAK8fHx06dPDw+PgCu4yaRJE9etWycnJwdXAAgAoAZpJ/v3H1i2bNm3b98E8fwVFRUbNmwgL69Qr54i+SopKfnzv2Zn/1tmsqCgMCcnJ6MckugI6J3S1Gx99OhRY2NjNFoAAQAUSE9Pnzx5yo0bN7h/qlJSUm3+RVNbW6dFi+ZqampNmzYlX+Xlqzc+TjQgPT3j48ePSUlv3r5Nfkv+JL9NSHj14cMH7juByNuKFa5z5szB/tIAAgAYERwcPH78BM4GPgUFeSMjY1NTUxMTk7Zt27Zs2eKX3j11LXz27FlMTCz5+ujRo+fPn3M2V+jSpcvRo0ewuTSAAICaUFJSsmbN2m3btnGtkTRs2LBzZ2sbGxsLCwstLS0+9nO/fv0aFRUVGUn++5eCggJOOUpFReXkyZOWlhZozAACAKpBRkbGqFGjg4KCOHI+EhISlpaWvXr1srHpTHr6HNwkubCw8O7duzdv3gwIuMmdCpckH1q/fv306dPQpAEEAFSJmJiYoUOHcaE+u5ycXLdu3fr160tCf/369QXFgW/fvr169eqlS5fv37/PhfMZMmTIvn17ZWRk0LYBBABUhqen57Rp0/k7miEuLm5ra+vkNKJv377VfX/LKVJSUi5evHjhwsXo6Gj+nomJiYmn53kVFRW0cAABAL+BNIYVK1Zu376dj+egpaU1evSoYcOGCVmoIknV8ePHz549l5OTw69zaNKkCREjQ0NDNHUAAQD/h6KiokmTJpG+Kl+OLiEh0bt376lTp1hbWwtxEcT8/PwrV64cPHiIX0NDCgryp0+fRm0ZAAEA/0tWVtaQIUPCwsJ5f+i6desS4XF2ntCsWTPRcXhEROSuXbt8fHx4/wASrf3nH/cxY8ag2QMIAPj3jeWAAQN5P3FFWVl5xozpJPrXqVNHND3/6tVrd3f3kydPFhcX8/jQbm5r58yZg8YPAYAAiDTPnj0j0f/9+/e8PKiqqur8+fPHjBktKyuLW5CSkrJly1bey8CsWbPWr18nxANuAAIAKuPp06e9e/dJT0/n2RHr1q27cOGCKVOmIPT/QmLim40bN547d66kpIRnB3V2dt65cwc0AAIARI779+/362f/9etX3hxORkaGxH0S/RUVFeH8ShKyRYsW87K4ppOT0759e1FWDAIARIjQ0FBHxyE8i/729vabNm1s3rw5PF8VvL29ly5dSnIC3hxuyJAhhw8fggZAAIBIEBYW3r9/f94s9WrVSmPbtm2Yd1hdioqK3N3d16/fwJv9tydMmLBr106MBUEAgJDz4MGDvn378aDvLy0tvXjxIhcXl9q1a8PtNePVq9czZszgTenNSZMm7tixAz6HAACh5dmzZ7169ebBW18jI6ODBw/o6urC5wwhT+jJkycXL17CgyXECxYsWLVqJXwOAQBCyOvXid26dfv06ROrRyH9fVfX5TNnzmR1X35R4/37987OE4ODg9k+0IYNG2bNmgmHQwCAUJGWlmZjY/vu3TtWj6KtrX3q1El0/NmgtLTU3d199eo1RUVFrB7o+PHjjo4OcDgEAAgJubl5PXr0YHtDyvHjx2/evAkT/FmF3MSxY8exumxbSkrq2rWr1tbW8DYEAAg8JSUljo5D/P392TuEoqLinj17Bg4cAG/zRs6nTp1y6dJlVm9oUFBgmzZt4G3hBjWjhZ958+axGv11dHRCQ0MR/XmGgoL8yZMnN2xYz97M/ezs7MGDB2dkZMDbEAAgwLi7ux86dJg9+/b29kFBQa1aacDVPM3cxcRmzZrl4+PNXs33xMQ3I0Y4ff/+Hd6GAACB5M6dO8uWLWfPvqvr8jNnPEiHFK7mC9bW1qwO1JDEbtGiRfCzMPck8A5AWHn79q2lZafMzEw2jEtLSx85cmTQoIHwM9/Jzs4eOnQYe4vFDhzYP3LkSPgZAgAEhoKCgq5du7E07UdRUfHChQuWlhbwM0coKiqaNm362bNn2TAuIyMTEhKsp6cHPwsfGAISTmbNms1S9G/WrFlg4B1Ef05BErJDhw66uLiwYbywsHDEiBG5ubnwMwQACABHjhw5c+YMG5Zbt251+/YtLS0tOJlzubyY2Lp1bsuWLWXDeMWWRHCyEDYbDAEJGS9fvrS07MTGTp+6uro3blxv1KgRnMxldu7cydKb/4MHDzg5OcHDEADAUb59+9a5s01sbCx1y4aGhteuXUX0FwgOHTrMxnCQvLx8REQEpvwKExgCEipWrFjBRvRv166dn98NRH9BYeJE53Xr3KibzcvLmzBhAi8rVgK2kVi1ahW8IBzcunVr7tx51M1qa2v7+vrUr18fHhYgzM3Na5VP5Kdr9v379zIyMhYWmAIgJGAISEjIzs42MTFNS0uja1ZDQ93Pz09NTQ0eFkSWLVu+c+dOujalpaXDwu5iw1fhAENAQsKSJUupR39VVdUrV64i+gsubm5rhw0bRtdmUVHRlClTMRAEAQBcITAw8MSJE3RtysvLe3ldxBs/wU7wxcT27dtrZdWJrtmHDx/u2bMX7hWGFoIhIEEnLy/PzKz927dvafYLxMUvXrzQs2dPuFcIyMrKsrXtQreEgJyc3OPHj5o2bQr3IgMA/GT16tV0oz9h69atiP5CQ7169S5fvkR3Eld+fv6SJUvgW2QAgJ/ExsZaWFiWlpZStDlt2tQtW7bAt0JGSEhInz596TaV69d9O3fuDN9CAAAfIPeO9NPDwsIp2rS0tPD19ZWSkoJ7hY9du3YtXbqMokEtLa2oqEi0FsEFQ0ACzIULF+lGfxUVlVOnTuF5FlZmzZpFt3Dby5cv9+3bD8ciAwC8Ji8vr107o/fv39MySOK+n5+fuXkH+FaIyc3N69TJMiHhFS2DCgoKsbExysrK8C0yAMA7Nm/eQjH6E9asWYPoL/QoKMgfP36cYpKXm5uLN0bIAABPIaG/bVuDwsJCWga7dOly7dpVMTEx+FYU2LFjx/LlrhRzR5IENGvWDI5FBgB4wYYNGylG/wYNGhw8eADRX3SYPXu2lZUVLWvFxcVr17rBq8gAAC94/TrRyMiI4lr88+fP9e3bF44VKVJTU01MTL9+/UqnIykufv/+PW1tbTgWGQBgl7Vr11CM/iNHjkT0F0HU1NQobhldWlq6evUaeBUZAGCX6OhoS8tOtO6asrLyo0cPsdWzaEJakZ2d3d27YbQMRkVF6uvrw7HIAABbbNy4iaJmb9++HdFfdHt/YmK7d++pXbs2LYPbtm2HVyEAgC1evnzp7e1Ny1q/fv3oLgsCAoemZutFixbSsubl5ZWcnAyvQgAAK+zYsZNW919OTm779m1wKZg9e3bLli2pmCopKdm1axdcCgEA9Hn37t25c+doWVu4cEGTJk3gVSAjI7N+/Xpa1k6ePJWeng6vQgAAZXbv3l1cXEzFFOnxzZw5Ey4FFfTvb09rWUB+fv7+/QfgUkEBs4AEg5ycnNatNfPy8qhYO3fubL9+/eBV8IOnT5+am3ekEg2UlZXj419iS0FkAIAap06dphX9O3WyRPQHv6Cvr0+revCnT598fa/DpcgAAB3IPTI2NqFV0u/OndsdOmDTN/ArFFeY29jY+Pr6wKXIAAAFQkNDaUV/Ozs7RH/wW1q10hgxYgQVU0FBQURO4FIIAKDAgQPU3qqtWOEKf4I/sWzZUlpj90ePHoU/IQCAKWlpaT4+vlRMDR48yNDQEC4Ff6JZs2ZOTk5UTJ0+fbqoqAguhQAARpw7d+779+9UTM2ZMwf+BJUze/YsKna+fPly69Yt+BMCABgKwHkqdmxtbY2MjOBPUDlt2rSxs7OjYurChQvwJwQA1Jyn5VAxNW/eXPgTVAUXFxcqdnx9rxcUFMCfEADA5+5/u3btSAYAf4KqYGXViTQY5nby8vJovb4CEACRo7S09Px5OgIwdeoU+BNUnUmTJlKx4+XlBWdyGSwE4y5374b17NmTuZ169eq9fv1KRkYGLgVVJDc3r1UrDfKVoR1paenk5Ld16tSBS5EBgOrh40Nn6/9Ro0Yi+oNqoaAg7+g4hLmdoqKiW7duw58QAFBtfH3pjJ9OmOAMZ4LqMn78OCp2/P394UwIAKgeL168SEx8w9yOlZWVpmZr+BNUF2NjYx0dHSoCgHFmCACoHj4+dPbSGjp0CJwJaoaDw2DmRj59+vTkyRM4EwIAqgGVDXWlpaUHDRoEZ4KaCoAjFTt+fn5wJgQAVJXMzMwHDx4wt2NnZ6eoqAh/gprRunUrKgsCbt7EnhAQAFBlQkJCqQybDhmC8R/AiEGDBjI3QnoztMoZAQiA8BMcHMTciIyMjJ1dTzgTMMHe3p65kZKSknv37sGZEABQ1QyAuRFbW1tZWVk4EzBBU1OzRYsWzO2Eh0fAmRAA8Hc+ffr04sUL5nb69OkNZwLmdO/enYYAhMOTEADwd4KDQ6jY6dWrF5wJmNOjBwUBuH//Pq2yFgACIMzcuxfF3IixsbGKigqcCZhjY2MjLS3N0EheXl50dDScyTUk4QKu8ejRIyoPrbD6JyEh4fnzFxkZ6eLi4o0aNWrbtm3z5s3RbNhDXl7e2NgoMjKKccN+bGJiAn9CAMAfIWlyTEwsczvW1lZC5pkPHz7s33/gzJkzqampv/xT69atnJycJk+ejEUPLNGxowVzAaBV2ghQBENA3CIuLi4/P5+hEQkJCfLECo1PysrKdu/eY2houGXLlv9Gf8KrV69Xr16jr9/27NmzaELsCIA5cyOxsbHwJAQAVJ4mUxj/MTY2VlCQFw6HFBUVjRo1atGiRX/dmz4jI8PZeeLcuXNLS0vRkOhibk5BAEgGgFsDAQCV8fAhBQHo1KmT0PT9x4wZe/nylar/yoEDBxcuXIiGRJeGDRtqamoyNJKXl/fmTRKcCQEAfyQujsIKAJIBCIc3du3ade3ater+1r59+z09L6At0aVdO0PmRjAKBAEAlQvAS+ZGjIzaCYErUlJS1q51q9nvLliwgHk5Q/Az+vr6zI28fBkHT0IAwO/Jysr68uULQyP16tVTV1cXAm/8888/hYWFNftd4sajR4+gRVFET0+PuREMAUEAANvdfyMhcEVpaen5855MLJw5gxlBnMsAkpIgABAA8AcSEuKZG2nbtq0QuOL58+cMk6HY2Nj09HQ0Klo0a9asbt26DI28ffsWnoQAgD8JwCvmRrS02giFAFB4GU4lowI/aNWqFUML7969Ky4uhichAOA3JCdT6B+1bq0pBK6g0nnPyEAGQJMWLZhuuVFaWpqcnAJPQgDAb3j//j0ygAq+fs2hIQAZaFRUBaAlcyPv3kEAIADg9wKQxtCCoqKikpKSELgiOxsCwDlatqQgAJ8+fYYnIQCAlQyAyiPKBVJSkpkbSUrCK0e6GQCFXVehyhAA8Bu+fPny7ds3hkZUVVWFwxuvXycyNxIRgTKENFFWVqbSzuFJCAD4lQ8fPjA30qRJEyFwRXFx8fPnz5nbIUY+fvyIpkULKqOLX75gCAgCANhJjZs0EYYM4OnTp0VFRcztlJWVnT59Gk2LFg0aNOBIOwcQAGEjOzubuRHhGAIKDQ2lZero0WMoRUsLOTm52rVrM84AMDcXAgDYEYCGDRsKgStu3rxFy1RSUtLBg4fQuriTBBQUFMCNEADwKzk5X5kbEYKaiDk5OWFhYRQNrl+//vNnjDvTgXkDY17wDkAAhFIAKGQAderUEXQ/eHl5MZ8N9TOZmZnOzhPLysrQxpgjJSXF0AJuBAQA/AYqS5/q1Kkr6H5go67vrVu3Nm7ciDbGHAkJCeYZHtwIAQC/UlJC4V1l3bqCnQHExcWFhYWzYdnNbd2xY8fQzBj3MBTgBAgA4CjMJ2nwl3/+2c2e8VmzZnt4eKCR8JevX7/CCRAA8CvfvhWJuAdev05kNUCXlpZOmjR527ZtaGx8zXRL4AQIAPgVTI9bu3YNDzaLX7FiJZGBvDxUDAYAAgC4QURE5IULF3lzLJJn2NjYvnyJcjEAAgCECHl5eUE87cLCwqlTp/LyiM+fP+/Y0WLLli1YJwwgAEBIkJSUFMTTXrlyZUJCAo8P+u3bt1WrVltbdw4Px6ahAAIAAD/w9fXdvXsPv44eHR3dvXv30aNHo145gAAAwFNevHgxfvwEvp+Gl9clQ8N2s2bNTklBwUIAAQCAfVJTUwcNGpybm8uFkykuLj5y5EjbtgbTp8+Ii4vD3QEQAADY4uPHj3Z2dsnJyZw6KyIDx48fNzExHTBgoL+/f2lpKe4UgAAAQJPExDfdunUjXzl7hjdv3iTZia6u3saNG5nXagYAAgDAv0RFRXE8+v8gJSVl7Vq3Nm20+vTpe+rUKWxkBiAAANScQ4cO9+xpJ1ilesvKyoKCgqZMmdqypbqDg6OHh0dmZiZuJRACJOECwBu+fPkydeq069evC+4lfPv27UY5kpKSFhYWdnY97ezstLS0cHMBMgAAfk9paemxY8eMjIwFOvr/zPfv30NCQpYuXWZsbKKjoztt2vRz586lpqbiXgNkAAD8L0FBQStWrHz48KGwXmBycvKJcsj3GhrqVlbW1tZWBDU1Ndx9AAEAokhZWdnt27e3bt0aGnpXdK46MfEN+a9CDFRVVU1NTY2NjYxJmmBiUr9+fbQKAAEAQk5GRsaFCxcPHDjAg+02W7XSaNRIKSoqioN+SEtL8y6n4q8tW7Y0NTVp166dTjnNmzcXExNDawEQACAMZGVlBQQEXLp02d/fv6iIF8Vtateuffr0aU1NTWdn5ytXrnLcP0nlXLzoVfFXeXl5LS0togS6ujra2tpt2mg1a9aUecl1ACAAgEfk5+ffv38/PDw8KCgoIiKSx8WeNm/eZGBgQL45derU2rVumzdvFiDX5eXlPSrnx08kJCSaNm1KEgX1f2mpodGqRYvmampqysrK4uKYrAEgAICvfP36NSUlhXRj4+Linj59Rnjx4gW/KvyNHDmSdPwrvifxceXKFaamJs7OEwV3rRbx5NtygoODf/45EQaiAUQJmjRpoqLSmHzT8P/TqGHDBhUI6DbgAAIA+E9paenTp0+jo6Pfv39fXPw9Jye7VvmuOLm5eZn/kpGRkfnp06esrCyOnLClpcU//7j/8sM+ffpERIRPmDAhMjJKmO4OEYa0cir5TJ06derXry8rKyv/L3KKivXk5OSkpKTk5GSlpaX/5zN1iVI2atRIW1vLzMyMfAAtH0AARJr09PR//tl96tSpDx8+CMo5t2qlce7cuR9x7WdatmwZEBCwZcuWDRs2ilSpr6/lVP3zMjIy9vb2c+a4VIyhAZEFY4uii4eHR9u2BiRcClD0b9KkyeXLVxo0aPCnD0hISCxevDg8PMzExAS3+E8UFhZ6enpaWFjOmzevoKAADoEAABGirKxs4cKFkyZNzs7OFqDTbtiwobf3NZIB/PWTenp6gYF3iLYpKiridlfSDPbvP9CjR88vX77AGxAAICq4uq7Ys2evYJ0zif4+Pt7a2tpV/DxJBaZNmxod/WTs2LGYcV8Jjx49GjhwEPIACAAQCfz8/Hbs2CFY59ykSRM/vxs1GLBWUlLas2d3VFSknZ0dbn0lGrB48RL4AQIAhJyioqI5c+YK1jmrq6sT0dLV1a2xBT09PS+vi/7+/lZWndAGfsvhw4ejo6PhBwgAEGYuXrzItSqMldOxo3lg4J2qjPv/lU6dLImQ3Lp1q1u3bmgJ/2Xr1q1wAgQACDPnz3sK0NkOGzbM19dXSUmJrqJcvXolKiqSGMfWCz/j4+Obm5sHP0AAgHBSUlISFhYmEKdKQvOmTZsOHz5Uu3ZtNuzr6+sfOXI4Lu7FsmVLVVVV0TZqlQ8PEl2EHyAAQDhJTU0ViMkeLVq0uHXr5owZ09mevaOiorJ06VIiA2fOnLGzs8OWO69evcZjAgEAwgl39nKoBCcnp4iIcFNTU54dUVJSsn9/ey+vi/HxL93c1rZr105kWwgWBEAAAOAPTZs2vXz50sGDB/i1ektVVXXOnDlhYXejo6NdXZfr6OjgpgAIAADsIiUlNWvWrAcP7vfo0YML59O6davFixeT87l3L2rp0qXGxsZYSgaEEmwGB/hM9+7dN2/e1KZNGw6em145y5Yt/fjxY0BAwI0bN27fvpObm4u7BiAAADDCyqrTsmXLrKysuH+qjRs3HlVO+VSZqJCQ0JCQ4Kioe8XFxbiPAAIAQDWwtbWdM8ela9euAnfm0tLSVuWQtCA/P79CDO7eDX306HFhYSHuLIAAAPB7ateu7eDgMGvWTH19fSG4HDk5OdtyyPffv3+PjY29f//B/XISEhJwuwEEAIB/MTExGT16lKOjo7DuzywpKWlUzqRJE8lfs7Ozo6OjY2JiY8if2Ni4uLiioiI0AwABAKKCmJiYqampvX2/fv3sNTVbi9S1E52zLqfiryQ/ePny5bNnz0lm8PJlXHw8+X8ChowABAAIGxoa6lZW1p07W9va2iorK8MhFflBxYSiHz8pLS1NTk5+/fr1m39JSkr69yv5I7hF7QEEAIgcderU0dDQaN26lb6+fsUYSKNGjeCWvyIuLt6ynF9+npubm5qampKSklrO+/dpHz58+Pz5c1paGvmKcSQAAQD8xMbGZuzYsY0aNVRSUlJVVW3YsCF8QhEFBQWtcn77r9nZ2enpGVlZmVnkT/mX//km69WrV6GhoXAggAAAFtHT03V0dIAf+IJiObVqqf/3n0j0t7PrBReB6mWicAEAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAACsUFpaCicACABgl7y8fDiBg1DZR7p2bWl4EgIAhBMq1bhQBl2IkZGRhRMgAABAAASM/HwKmZmYmBg8CQEAwomcnBxzIxkZGfAkB8nKyqaRI9aFJyEAQDipU6cOBEBYSU//QqOLIA9PQgCAcCIjIyMvz/QJT09Phyc5yJcvFASgUSPUd4MAAOGFecHetLS0srIyeJJrfPz4kQvNA0AAAHdhXsK3qKjow4cP8CTXePMmibmRBg0awJMQAIAMgPVYA+jy9u1bhhbExMSY9w8ABABwl5YtWzA3kpT0Bp7kFNnZ2VlZWQyNqKmpSUlJwZkQACDEAtCSuZHY2KfwJKeIiYnlSOcAQAAAd2nRgoIAPH0KAeAWVO4IlbYBIACAyxkAhV5ebGwsPCl8AoAMAAIAhBxNTU3my/0/f/6clJQEZ3KHe/fuMTeipaUNT0IAgDAjLy/fqpUGczuRkZFwJkfIysp68eIFczsGBm3hTAgAEHL09fWZGwkPj4AnOUJUVBTzpXmysrIaGhpwJgQACDlt21Lo6AUG3oEnOUJgYCBzI7q6uhISEnAmBAAIOe3atWNuJDHxzevXiXAmF/D3D2BuxNDQEJ6EAADhp0OHDpTijj+cyXfevn0bHx/P3I6FRUc4EwIAhJ/69etra1OY7+Hj4wNn8p3r129QsWNubg5nQgCASGBpacncSGho6OfPn+FM/uLp6cnciIqKirq6OpwJAQAiAZV8v7S09PLlK3AmH0lJSaGyAqBTJ0s4EwIARIWuXbtSqf567tw5OJOPXLhwgVZ7gDMhAEBUUFJSojIXKCoqKi4uDv7kC2VlZceOHaNiqnv37vAnBACIED179qRi5+jRo3AmXwgODk5MpLAvt6GhoaqqKvwJAQAihJ0dHQHw8DiTm5sHf/KegwcPcqorACAAQGAwMTGh0u/Lyso6deoU/MljSN//2jVvKqbs7fvBnxAAIGI3Xlzc0dGBiil3d/fv37/DpbzE3X0X8/1/CK1aaRgZGcGfEAAgcgwcOJCKneTk5EuXLsGfPOPDhw+nTp2mYsrBwRH+hAAAUaR9+/a0lv+sXbsWSQDP2L59e2FhISUBGAx/QgCAiDJ8+HAqdhIT33h4eMCfPCAlJeXQocNUTLVr105XVxcuhQAAEWXMmNHi4nTawLp16/Pz8+FStnFzW1dUVETF1IQJ4+FPCAAQXZo2bUprFmBqauqOHTvhUlZ5+PAhrUxLQUF+yJAhcCkEAIg048ePo2Vq+/btycnJcClLlJWVzZ+/gMrkH4Kj4xAFBQV4FQIARBqSAbRo0YKKqcLCQhKh4FKWOHHiBJWt3yqYPHkSXAogAKKOhITEzJkzaVnz9fW9eNELXqVOWlrakiVLaVmztbWlUhkUQACAwDNq1Kj69evTsjZ37tyMjAx4lS4uLnNycnLo3aM5cCmAAIB/UVCQnzhxIi1r6enp06ZNh1cpcuLECYr110jfv0uXLvAqgACA/8+MGdPl5eVpWfP29j58+DC8SoWEhFcLFiykaHDBArynARAA8BMNGzacMWMGRYOLFy95/vw5HMuQwsLCsWPH5uVR22/VwMBg0KCBcCyAAID/w+zZs+rWrUvLWkFBwdChQ7OysuBYZjfF5cmTJxQNrljhSqUYHIAAAKFCUVFx3ry5FA0mJr6ZMMG5tLQUvq0ZBw8eOn36NEWDZmZmvXr1gmMBBAD8hmnTpqmpqVE06Ofnt2zZMji2Bty+fXvhwoV0bW7YsAGOBRAA8Hvk5OTc3Nzo2nR3/2fv3n3wbbWIjY11chpZXFxM0eaQIY4dO5rDtwACAP6Io6ODhUVHujYXLVp09eo1+LaKJCcnDxo0+OvXr3Slfd26dfAtgACAyhATE9u6dSutLUIrKC0tHTNmjK+vL9z7V1JTU/v06fP+/Xu6ZhcsmN+kSRO4F0AAwF8wNDScMYPySq7i4uLRo8cEBwfDvZXw5csXEv0TE9/QNaujozN79my4F0AAQJVwdXWlVSzsB4WFhYMGDUYeUEnf39bWNiHhFeUnXFx83769tWvXhocBBABUCTk5ub1791A3SzTAyWmkp6cnPPwLr18n2tp2od73J0yfPs3MzAweBhAAUA2sra3Hj6dfMaq4uHjCBGd3d3d4+AdRUVFdunQhGQB1y61bt1qxYgU8DCAAoNps2rSxTZs21M2WlpYuWbJ09myXkpISONnL61Lv3n2+fPlC3bKUlNSxY8dIMgcnAwgAqDYkdpw4cVxaWpoN44cPH+7Xz56NwCcoEP1bu9Zt9OjRhYWFbNhfuXKFsbExmjGAAIAaYmBgsGbNGpaMBwcHW1hY3r9/XwQdm56e3r//gI0bN7Jk38bGBjN/AAQAMGXGjOkDBvRnyXhqamr37j127NghUlsGhYSEdOxoERgYyJJ9NTW1Y8eO0l3MASAAQBQRExM7cOCAlpYWS/aLi4uXL3ft3bv3u3fvhN6Z3759W7p0We/efdh45VuBtLS0h8dpZWVlNF0AAQAUUFBQOH/+PMXNov9LaOhdExPTQ4cOC3EqEBkZZWFhuWvXrrKyMvaOsnPnDsz7BBAAQBNNzdbHjx9jdVQhNzfXxcWle/fuwldJJjs7e/Zsl27dusXFxbF6oClTJo8ZMwbNFUAAAGV69uxJepc86Cabm3ckSiAcE4S+f/9O0pq2bQ0OHz7Masef0KdPn82bN6OhAggAYIUJEybMnz+f7aOUlJRUBM1t27bl5uYJqK9IuPf29u7QwZyIWXp6OtuHMzExISmahIQEWimAAAC2WLVq5fDhw3lwoJycnBUrVuro6Li7u+fn5wtc6LewsBw2bDjbYz4VaGioe3ldxJovAAEA7FI+KWh/v379eHO4jIyMJUuWtmmjtWrV6g8fPnDcOd++fTtx4oSZWXsS+mNiYnhz0GbNmvn5+SkpKaFxAggAYB0JCYmTJ0/07t2bZ0fMzMzcsmWLtrbOuHHjQ0JC2B5MrwGvXyeSfEVLS3vatOkvXrzg2XGbNGni6+tLt5AngAAAUBnS0tJEA6ytrXl50OLiYk9Pz169ehsYGGzYsCE+Pp7vfiDKRLr8dna9yClt27bt8+fPvDw66fVfv369VSsNNEgAAQA8RVZW9sqVy7zMA36QmPjGzW2dkZGxmVn79evXP3r0iMerB96+fXvkyJH+/Qe0bKlOuvyhoaG8d0LTpk1v376tqdkaTRHUGEm4ANSY2rVrnz17ZuLESfza4v95OevWrW/QoEG3bt0sLCw6djTX0dFhYzLMu3fvIiOjIiLC79wJ5HvyQXr9N27cwMgPgAAAvjYgScnDhw/Vq6d48OAhPp5GRkaGZzm1ytctGxm10ytHS0u7ZcsWqqqq1V3ClpmZSbr58fEJz8qJiYlhb/OG6mJgYODtfa1Ro0ZofgACAPgM6W7v2LGjRYsWy5e7cuH1bG5ubmjoXfLfj59ISUk1a9ZUSUmZJAokbtatW0dSUkpeXr7iX4uKigoK8gsKCjMzM758Sf/8+TPp7BMj3PS2nZ3diRMnFBTk0fAABABwBRcXF3V1jQkTJhQUFHDt3IqLixMT37BRcJHHTJkyefPmzVjtBWiBl8CAGv372/v7+6mqqsIV9HtqkpLbt2/ftm0boj+AAIDfw/fet4mJSXh4mJWVFe4FRVRUVPz8/CZPniQMEQdVCiAA4L8oKlLYbDk7O5vvF6KsrOzr6zNnzhzcUypYWXUimtqxozkXTiY/n2kPg9VNxQEEQFCRl1dgboQLa6Nqlb8WdnNbe/HiBcxUYdhZXrx4sY+PT+PGjblwPqWlpcwbWJ06dXBnIQDgvw8GBQG4e/cud66oV69eDx7ct7Ozw82tAerq6jdv3nR1XS4pyZWZGrGxsV+/fmVoREFBATcXAgB+pVmzZsyNeHpe4NQ+OUpKSiQP2LVrJ6YtVovRo0dHRkaYm3fg1FmR1sXcSPPmzXB/IQDgV9q0acPcSEJCgo+PD6euS0xMzNnZ+cGDByQhwF3+Kxoa6r6+Pvv27eVaTzkrK+vo0aPM7WhqtsFdhgCAX2ndWpOKnWXLluXlca6ICslvSCpw4sQJFCv/E5KSkvPmzbt3756NjQ0HT2/FipU5OTk0BACbF0EAwH9QUJCnsq3j69eJs2bN5uCGyQQHh8HR0U9mz55N0gLc8Z8hQf/evag1a1bLyspy8PS8vC4dOXKEiilDQ0Pcbg4l6NyMFKLJ9Okzjh8/TsXU5MmTtm3bxtk4a2xs8vLlS9zxH7x/n6qoqMjNc/P19XVyGllcXMzcFMn/3rxJxO1GBgB+3w2kZerAgYMDBw7ibAktaWlp3G7uQ4L+hg0bhg0bTiX6l7fwzvAqBAD8HltbG4rrJG/evGlo2M7NbR33KykCrlFQUHDu3Dkzs/ak/VCstdC1a1f4llNgCIhbDBgwkARuyiIvLm5iYmJqatK8eXNxcU7sJLNz5860tDTc7h+sXr1KRoYTo/+fP3+OjY0NCwujvh+qjIxMUtIbLASDAIA/4ul5Ydy4cfADED4cHR1oveIC1HqHcAGn6NevLzZLAULJ6NGj4QQIAKgMWVnZGTNmwA9AyDAwMLC1tYUfIADgL0ydOuVHsSoAhINFixZh8QcEAPydBg0aTJ06FX4AQoOurq69fT/4gYPgJTAXycvLMzIy5k4Vcm5y9uyZyMioyMjIx48fFxUV8fjompqaHf/F/Px5z6CgINyOSggICLC0tIAfIACgqly5ctXJyQl+qFQm//88xcLCwpiYmKflPHv2LDb2KfXCONLS0tra2vr/okf+GBkZkUSt4p+GDh3GtQ34OMWIESMOHToIP0AAQPVAZKmiAPyXzMzMN2/evH2bnJz89t27d1++fElPzyCQbyq0ITc39+f1TQoKCuLi4rKysiSsN/qXhg0aNFRVVWnxLy1btmyhqqr6pzV6uE2VoKSk9ODBfdQF4iyScAFn2bNn94MHD7COtwbUL8fY2Ljyj5WUlKDGOqscOLAf0Z/L4CUwdyFPjoeHh5SUFFzBEoj+rLJ48eKePXvCDxAAUEPMzTuQPAB+AALHwIEDli9fBj9AAAAjnJyc1qxZDT8AAcLa2vrQoUOY+A8BABSYN2+eq+ty+AEISvT38rrIzco24BfwElgwWLx4sby8wpIlSzBrizukpaUlJb2luFuyENCrV6+TJ0/IycnBFcgAAE1mzpxx5owHdongDpcvX+nWrZufnx9cUcGMGdPPnz+H6A8BAKxgb28fEhKso6MDV3CByMgI8hUZQK3yhRTHjh3btGkTJlZBAACLaGtr370bOmvWTDxpfCcsLBxOqFVeypRo4ZAhjnCFwIGVwILKs2fPZs+eHRERKbIeqGQlMA9ISkrS09MX8UaooqKyceNGR0cHPI/IAABP0dPTu3nz5pkzZwwMDOAN3hMRESHKl9+wYcM1a1ZHR0cj+kMAAJ/SNzGx/v3tIyLCr1y53L17d4oF5UEVBEBEcy9tbe0tW7a8fBk3b948BQVMSRDwGIIhIKHhw4cPnp6ely9fefjwYUlJidBfL3+HgExMTOPi4kSndWloqPfq1cvJycnQ0BDPGgQAcJfc3NzQ0NCwsPCXL18mJMS/eZP0/ft3IbtGGRmZ9PQv/Dp6ZmZm06bNhLsVNWnSpHXr1m3atDEzM7WxsWnatCmeLAgAEDyKi4tzcnKIKmRnZ+fm5pWUUBYDYpmkHYS8vDyeXZSqquqrVwn8cumNGzccHPgz6UVMTMzKymrMmNFqamrUjdetW1deXkFBQZ58g+n8ogBWAgs/UlJSDcthIdXIO3Bg/86duzIyMnh8UerqLfno0vBwvr0BJj22kJCQsLCwYcOGLVq0qFUrDbRwUGPw2hDUhIKCgt279+jq6q5YsZL30b9W+QREPl5+RASfVwCUlJR4eHiYmJjMmjU7JSUFDRJAAAAvKCoqOnDgoJ6ePul+pqen8+s0ZGT4ttdYYWHho0ePuXAviouLjxw50ratwYIFC1A4CEAAAIt8//796NGjJNzMnTv348eP/D2Z58+f8+vQjx8/+fbtG3fuC5GBvXv3EUlesmQJHyUZQACA0Ib+06dPGxkZzZw56927d1w4pdjY2NzcPL4cOjw8jIP3iOQl7u7/6OjourquqKh7DAAEADCitLT0woWL7dt3mDx5SmLiG+6cWElJyb17UXw5NJeXgOXl5W3fvl1bW2fduvWQAQABADWkrKzM29u7QwfzsWPHvnz5koNnyJdATNzC/U0gcnJy1q9fr6/fdtu2bfzKkwAEAAgqAQEBFhaWw4YN5+NQ+18JD+fDVByihVlZWQJxEzMyMlasWKmrq7t7956CggK0agABAH8hMDDQxsZ24MBBMTExHD/V+/fv836Fc1hYmGDd0PT09EWLFunp6e/ff6CoqAgtHEAAwG8IDQ21s7Pr27cfCawCccJ5eXm8Vyk+LgFjwsePH+fNm6erq3fkyBHh2xcEQABAzbl3717//gPs7HqFht4VrDPn/WuAyEgB3gQ0LS1t1qzZRkZGp0+fhgwACICoEx0d7eDgaGvb5datW4J4/jxekVteBT5J0G96YuKbyZOnmJm1v3DhIupZQgCAKBIXFzds2HALC8sbN24I7lXweEBGQMd/fkt8fPzYsWPbt+/g7e2NHSEhAEBUSEh4NWbMGFNTM/LkC/q1fPz4kZerE2glHNypo/LixYuKfoC/vz8eDQgAEGZev06cNGmyiYnJxYteQtPp4+UoEK1XDoGBgWvWrFZUVOSID2NiYgYNGmxjY3vnzh08JhAAIGykpKTMmjWbhH4PDw+OFAsTExPjVFD+K7m5dCYdNW7cWFdXd968eS9ePF+6dGndunU50kju37/fr5+9nZ1daGgoHhkIABAGPnz4sGDBgrZtDY4cOVJcXMyRs+rbt+/1675UTPFsYn5UVCSVV6aWlhYV35AMYNmypc+ePZ07d668PFcGhUJD79rZ9bK373/v3j08PhAAIKikp6cvWbJET09/79593An9PXv2DA8PO3/+nLW1tbq6OnOD8fHxvClIQCvV6NjR4ue/NmjQYO3aNSQbmDFjuoyMDEdu0+3bt21tuwwe7BAdHY1HCQIABIns7GxX1xU6Orru7v8UFhZy5Ky6dOkSGHjn0iWvH1XFzc3NKYVmXkzOobXzRMeOv7nqhg0bbtq0iWQDU6ZMlpKS4sgt8/Pzs7CwHDp0WFxcHB4rCAAQgNC/bt16bW2d7du387JCb+VYWXXy87vh7X2tffv2P//cwqIjp/rmlfD9+/cHDx4wtyMvL29gYPCnf1VRUdm2bVtsbMyECRMkJblSrtXHx8fU1GzMmDEJCQl4xCAAgIvk5uaR2KGv33b9+vU5OTkcOSszMzMS90lH0srK6r//amlpyam+eSVER0dTEVQigRISEpV/plmzZu7uux49euTk5PTXD/OGsrKyixe9TExMJ02a/Pp1Ih43oUEMa0AEnYKCgiNHjm7atIkvtXn/BOnnrlq1smfPnpWHlWbNmmdmZjI8FuksX7rkJS0tzd7lXL58+cCBg8ztLFu2lFD1zyckvHJzW+vldYk7zynxNskGFiyYT4QKTx8EAPCNoqKio0ePbd68me8FGn+mvFK8a9++fasy0dPRccj169dF55b5+vrY2NhU97fi4uJWrVrNqYV7UlJSzs4T5s+fr6KigidRcMEQkEDy/fv3imrg8+bN407019LSOn78eFRUZL9+/ao4zf+3b0SFFQkJCVNTsxr8ora29rlzZ8PDw3r37s2RaykuLt63b7+env7ixYtRiBgZAOBd6D937tymTZs4VaBRQ0N90aJFw4YNq+6ry8jIqK5du4rIvTM2Ng4NDWFo5N69e+vWrefU5n3y8vKTJ0+eN29uvXr18IQiAwCsUFGe18ysPafK8zZt2nT37n8eP348cuTIGkxcMTY2ql27tojcQSrpTvv27a9evVL+Xr0TR66rohCxjo4uChFDAAB9Ksrztm/fYezYsfHx8Rw5q8aNG+/YsSM2NmbcuHE1nrMoLS1tYmIsMgJgQcsUif7du3fn1NVVFCLW09NHIWIIAKCGv79/RXneFy9ecOSUlJSUKlYtTZo0kfncG4phkePQWvdA2LVr14oVKzl4jZmZmeTEdHR0UIhYIMA7AO5y586dNWvWcq1Ao4yMzJUrl387r79m+Pn5DR7sIPR3U0NDPTY2loqpQ4cOu7i4cP+SSY64cOHC8ePHsTpDFyADEDYqyvP262fPwfK8hYWFjo5DKJ6Yubk5rZ1BuYy5OZ3uv6BE/1r/U4i4YiNCVKCEAIC/c+/ePXv7/hwvz/v161eK4lSvXj0dHR2hv7NUxn+OHj0qKNH/B+/evUMhYggA+AvR0dGDBzvY2na5ffs298+WrgZ07NhR6O8v830vzp8/P3u2i4BePgoRQwDA74mPjx8+fISFhaWfn58AnTbRgF69egcHB9MIjkL+HrhBgwaampoMo7+z80RBD50/ChH7+vriwYcAiDofPnyYMWOmqanZtWvXBPH8CwoKSNbCXANo7QvNWUiKw+Q9B+k1C0H0/8GLFy+GDBnavXv3qKgoBAEIgChSWFi4cePGtm0Njh07xpEajXzUgBYtWqiqqgq1ANRc4UhneeLEicI3bBIeHtGlS9dRo0alpKQgIEAARAhvb29jY5O1a93y8/OF4HIqNCAwMJCJEeEeBbKwqOHVkejv5DSSOwXdqHPp0mUjI2PSGeJO5SIIAGAL0tkZNGjwsGHD3759K0zXRTRg4MBBTAZ2hXg5mIyMjJGRUQ1+8caNG6xGf3FxcY40HtIZMjMzCwoKQoiAAAgnJSUle/bsNTEx9ff3F8oLJHGKRKsaa4AQbwtqYmJcg5VQwcHBo0aNZi/66+rqPnr0cO7cubKyslzwUmLimz59+k6cOIlTZS0gAIACr1697t69x8KFC7lTppFrGqCvr6+goCCUbqlBckOi/+DBDuxtpUCi/40b1zU1NdeuXfP0aez06dM4Uo/+zJkzpJOEOUIQACGhrKzs4MFDFhYWHJnwYGXVyc1tLXtlxys0oAaTmiQkJDp06CCUbaC6S8BCQ0NZjf4aGuo+Pt6NGjWq+KuKisrmzZsrChFzoR79p0+fhgwZOnnylK9fvyKAQAAEmPT0dPIkz5kzhwsdf3PzDn5+N/z8/Mj5nD9/jr3Ev0IDzp8/z3agFAjExMSqNcn1/v37jo5DWI3+pA00btz4l583adLE3X0XkYGRI0dyoRDx6dOnO3Qw5+BWKBAAUMV+3F3Sgrkw4m9mZnblyuXbt2//2MGtZ8+eXl4X2dOA0tJSZ+eJ1dWAGk+V4TI6OjqKiopVj/79+tmz1/OtiP5qamp/+kCzZs0OHNj/+PHj4cOH832Dprdv33br1n3nzp3YsBICIEiQ9rply5bevXunpaXx90wMDAzOnTsbGHjnv3vHd+7cmWsaYGpqyoW+J12qrmoPHjxgNfqTPr6Pj08l0f8HrVppHD586MGD+w4Og/krA9+/f1+2bDlJo7OyshBYIAACQG5u7vDhI1atWs3flTtaWlqnTp0KDw+rpDwvbzTAw8Ojip+Xk5Or2XRJLlPF9Q3Pnj0bNGgwq9Gf9P1btGhR9V/R1tY+ceJEWNjdvn378teHJI0myStxEcILdVAPgCavXr12cHBISEjg4zmQNN/VdQXpu1Vxlvfdu2HknFl94Uay+IkTnavyycWLF//zz27mRxwzZgzDNwpr17q9e/eO+Zm8ePG8efPmlX+GNJiuXbuxV1q9IvqTfn2NLTx+/Jj0afhbiJj0Dw4ePDhw4ADEGQgAFwkNvTt8+PDMzEx+nQDp3y1duqQGldnZHnquugZcvXptxIgRzA83evToffv21vjXiSuaNFFjnsOpqanFx7/8a8pobt7xzRu2ijw3bNjw1q2bbdq0YW7q3r17K1euCgkJ4eNTtnr1qvnz5yPa0AJDQHTw8PDo168fv6J/06ZNd+3a+eRJDSuzm5mZeXtfq1OnDntn6OLicuzYsb9+jNZEoIiICCa/HhkZSWUEryqr21xdXVmN/jduXKcS/WuV16Mn1vz8bvBx3w6iQBMnTioqKkLMgQBwha1bt06aNJkvG7Y0btx406ZNMTHRzs7OTArv8UADZs2a/de1/kpKSq1bt2J+rISEhC9fvtT418PDI6hcsoXFX2oAJCUlHTlylNXor6enR9eslZVVQEDAlSuXSZvhy+N25swZBwdH4V5TCQEQDMrKyhYuXEh6Jbw/dIMGDdatc3v27OmMGdNr167N3CB5nknnjkQNlk6Y9KmnTJn61/3vaG0KFB4ezq8EouoJDYn+LO0FS7T82rWr1KP/D7p37x4UFOjped7AwID3jf/27du9evVmovEAAsAU8uhOmOC8Z89eHh+3bt26q1evevHihYuLC905PO3atSN9RvY0ICUl5eDBg8yHTaoWxCNr9oskk3vw4AGVEKyrq1v5Z3x8fFiK/iSfI3eT7abYp0+f8PAw0iXX0tLi8VPw8OHDbt26paamIhBBAPgT/Wuw1ol56F+6dGlc3Iv58+crKMizcQjSZ2RVAw4dOlz5vANay8Fq3It/8uQJlYW4HTp0qHxZQ3Z2dnx8PEvRn2fjM2JiYv372z94cP/48eNUhu+qTkLCKzs7O2gABIDXFBUVjRjh5OnpybMjysvLz5079+nT2GXLllZ9ZSkHNSApKYnkLpV8QFNT88c2NUwgcbxm5RZ4Nv5DZZrpL5CM0MvLi/ej8+Li4o6ODqRXfuDAfg0NdZ4dNzHxDdGA5ORkBCUIAO/6/pMnT2Epef8vUlJS06ZNff782dq1a9jrmP9XA27dutmkSROWkvfKP0ClRnxxcfFfD/QHAYikcpl/TWXY2Pd4w4YNfJyiIykpOXLkSOL2nTt3/ne7IfY0oE+fPsgDIAC8oKysbOrUaTzr+zs4DH78+NGWLVuodIqrRZs2bfz8/NjQgL/2fPk7GZTJ2+OfQ6GJiUnln2Ejk1u7di3fV8xKS0tPnOgcGxvj6rqcZK680YC+ffvhnTAEgHUWLlxY9b0NmKCvrx8QEHDixAl1dXV+XWyrVhpsLLwsLf3L2kNaNeLDwqodyhnOH/2BkZGRnJxc5Z9p2rQpdd+mp6eTUPj6dSLfnxQS+hcvXvzkyWNHRwceHC4+Pp5ceE5ODmIUBIAttm/fvnfvPh48OZs3bw4PD+N7mVxX1xVszHGSl5f7a/SkMrspKiqquuu5aL0AqMpcpgYNGrAxXP7p0yc7OzsuaECt8l0ojh8/7ud3Q1OzNdvHio2NHTZsONaIQQBY4cyZMyQgsn2UHj16PHz4YPr0aXzfF5NcLBE8Niz/dcqglJSUqakp8wN9/fq1uuMhtJaAVfE1Bkv7rL1//547GlCrfO1YZGQkyZ7ZbtXBwcHOzs7Y3gYCQJm7d8OmTZvOdsd/9+5/Ll++1KxZM75fL3vRX1JSsirBkcp74FrVf6MbGUlrClCVsrfx48eztN8y0QBOvRqVkZFZuXJFUFCgpqYmqwfy8rq0atVqhCwIADWSkpKGDx/O6k4P7du3v3cvaty4cXy/WNJ7Yi/6V6Q49erVq0IApSMA4eFhVf/w58+fExJeMT+opmbrKr60J9GQyuZ3vyUlJYVr0+SNjY2JxE6aNJHVo2zdupXHC3QgAEJLbm7uwIGD2Jix9wMXF5eAAP+WLVtyIfq7uMxhL/oT5s2bV5WPdejQoYrbWf9NAKrRo6f3AqAaL282bdrI0nTbWv8zTZ5TGkBSgR07dpw9e4bV5SxTp067d+8ewhcEgGlAnDRpEhvLNSuoW7fuxYsX1q1z40Ix7orof/jwYfYOQRIpc/MOVfTMX/dRqAok9pGOMM8FoBqzmOrXr0/aAHvb8JVPk+/LtSmS9vb2JDlr27YtS/a/ffvm5DTy06dPCGIQgJrj7u5+9eo1loxraWmFhIT06tWLI1LHdvQnAX3nzh1V/zzvVwPQewNcvelbhoaG3t7X2Jsyn5CQwMGt00jKe+fO7SFDhrBk//3796NHj2Fprz0IgPBz927Y8uWuLBnv2rVr+Qux1ly4Uh5Efw0N9atXrygoKFRHACypHLqKC7vy8/OfPHnC/HBKSko1uK1mZmYXLniyV57z+fPnHNQAOTm5Y8eOrlzJ1uS60NBQvBCGANSEzMzMcePGsVTXd8KECZcvX6pbty4XrrRiT1O2+/4BAQHVHemmtS1oFfv1Dx48+P79O43ufw0TF7ZLNBMN6NfPnoNLpRYuXHjixAkm1SwqYceOHXfu3EFAgwBUjylTppIUkg3Ly5YtdXffxfdp/j+iP9t7mpLof+PGdVVV1er+YtOmTanMiCWBrypRj94LgJqPXFVoAJXqDr8lJiaGmxrg4DD42rWrbLwIIdktaeGfP39GTIMAVJWjR4+ytNfb9u3bly5dypHLrIj+rO5rVBH9a7yREZUkgISAyMi/rwag9wKA0TkTDTh16iR7kwJIosNNDbCysvL392Nju8OPHz+S/hzCGgSgSiQlJS1evIS+o8XF9+7dM3nyJET/qkPrNcBfNwUi3oiKimJ+IFlZWSMjI4ZG+vTp4+FxmlUNGDRoUM32ymYVQ0NDlvYh9/PzO336NIIbBODvvUXSWaBebpRE/8OHD40ZM4Yjl1lUVOTkNJLV6G9qako6dAw3MaU1Eeiv63ufPXv29etX5gcyMzOTlJRkbqdCA9gbJ4yIiBw82OHbt29cewD19PQCAwPZ2Ep6/vwFVZ8QDAEQUQ4fPhIaGkrd7I4d24cOHcqp6O/t7c1q9Pf2vtagQQOGdnR0dKi8Kn/w4GHle4TRqgFAaweLCg04dOggldVwvyUkJGTq1GkcfAZbtdIgjYd6HkAEfsaMmQhxEIA/kpaWtnz5cupmN27c6OzszJ0UZ+LEidevX2c7+lMJ3CT8UdkaurCwsPIpntXaMYIHKUsFpNNAEkf2NOD8+fOXLl3m4JNI8oArVy5Tfyd869atCxcuItBBAP6UJM7Pzc2la3P27NkzZ87gzjXu27f/4kUvgYj+dENq5asBqGQAJFJ36NCBrj8rNIClDeNq/bvrnyuVya/UMTY2PnPGg/qLkIULF2ZlZSHWQQB+JSAg4MqVq3RtOjo6rFvnxp1r/PDhw6pVq9izb21t7evrQ3d9Q3VX1dYgxCcnJ1PZLUdfX5+NiYxEA3bs2MHSLUtKSmI1HWRCly5d9u+nXH7j06dPK1euqgUgAD9TXFxMugZ0bZqZme3fv5+9vlsN2L17D/X32z9Hfy+vi9Va61u1lMKESjcwIiLiT9vEV2WSaNW0ypwl306c6Lxz506WjLO32Qlzhg0btmjRIro2jx079vTpUwQ9CMD/sm/fPir7AP9ATU3t/PlzMjIy3LlGEv7Onj3LavT/axHEGkB8yHxiZa3yWokJCQm//acaVI5kNVnhsQbQWgHHEq6uy/v3t6dosKSkZP78+Qh6EID/DQ3r12+gaFBaWvrMGQ82prIx4eXLlx8+fBCs6F9BFYur/JU/vQagUgWewHYJT6IBa9bQ39kmOTmZ1VoXDCE59MGDh/5aRa5ahIbe5XLeAwHgKVu2bKEyB/wng5upVDSky7Nnz9kw27t378uXL7EX/WvRG1r57WuA7OzsFy9eMDfevHlz9rb1/5HDZWRksmGWg4vCfkZBQf7s2TPkK0WbK1eu5ObbbwgAT0lJSTlw4CBFg4MHD+LOpM9fEh02zPbo0YPtkS565SF/09OPjIykUkKWynTVSvj27duYMWNYGgWisniNVUgG4O7uTtFgQkIC1gZDAGqtXetW+RKh6nYDd+/ezc0rlZZmZWsBFxeXZcuWs7RtagUNGzZs06YNczuvXyf+t0IIrS2AWB3/yczM7Nu3r5fXJTaM16tXj71SBBQZOnTosGHDKBp0c1tXUFAAARBdSEQ4d+4cLWtiYmKHDx/myCbP/4W9dxKkWzp8+Aj25hfVovkaIKIqaQEfz/C/JCUl2dp2oSVU/4XKO3besGvXzhYtWtCylpaW5uHhAQEQXbZu3UqxYNDMmTPZfg3IhHbt2rFn3MfHp1u37uzVnqVXHez/hHuS/D18+Ii5WUVFRW1tbTYu/NGjRzY2tn+av0SFnj17CsoDq6CgQHdlwJYtWykOAEAABImUlBSK0yI1NVuzV9iICqqqqnp6euzZj4mJsbbuTAIWG8bNzWkJwP95D/z48ePCwkIap2fOxoYN169f79GjJ6t72cvIyAwfPkyAHltra+tJkybSsvbu3btTp05BAESR3bt3U5z95u7uzqlZ/79lwoQJrNr/8OEDCVjUF1TXKt8gTFlZmbmdJ0+e/DxURWsCKN0tgCo4ePDQ0KHD2B6knjJlCsMdW3nPmjVrVFRUKD65rL7BggBwkezs7OPHj9Oy5uTkRDom3L/qMWNGUxxC/S0kYBFvbN26lbplKsNrJSUlDx48+FNCUGPoLgErKytbtmz5nDlz2A5MRFaXLFkicA9vnTp1KDawV69ec3YzDAgAWxw9ejQ3N49Wc+TUhj+V5/v79u1lb3fJH6xcucrZeSLd0VVao0A/ev0kzlJZBCstLW1iYkzrMonT2Jvu+TN169b19PSkO7meZwwcOKBr1660rO3atQsCIEKQjhXJr2lZW7p0iZKSkqBce+fOnbds2cKDA509e7ZPnz4UFx9QXw6WkJCQkZHB3KCRkRGt0b+srCz2pnv+jLKysr+/H0svrnnDpk0baRXMCQ+PiImJgQCICv7+/snJyVRMaWioT5kyRbAuf8qUyXv27OZBVXryXFlbd3758iUVa4aGhlSmq0dFRVZM/QoLo1UDgM74z9u3b21tu9DamKgS2rRpExQUaGBgINBPsY6OjrMztXdahw4dhgCIChRvtqvrCmlpaYHzwNixY69evcKDJQtJSUk2NrZ37txhbkpSUtLMzIy5ndzcvNjY2Fo0XwBQSE0eP35MHBUfH8/2HbG0tAgMvMP2qyDesGjRIlpL2M6fP093PxgIAEdJTU0NCAigYor0oRwdHQTUD7a2tsHBQerq6mwfKCcnZ8CAgVREl96eEJG16C0BY35Wfn5+PXr0/O8qZeqQ5urj41OvXj3heJYbN248Ywadakt5eXmslsiGAHCFs2fPUtn7pVb56D+ntvuvwVBASEgwDxavlZSUuLi4LFiwgOGyO4rLwT5+/JiY+Ia5KS0tLYaljw8fPuzoOIQH27HNnTv32LFjgpiwVsLs2bNoJbIeHmcgAMLP6dN0Fn+T7n/fvn0F3RskeJEu4ciRI3lwrL179zk4ODJJtNu370BlClN4eAStnRWYvAAgHRFX1xWzZ7uwPd2TOG3Xrp1r164R6P7Kb1FUVJw8eTIVU1FRUXSLgkAAOMf9+/dpraon/VnheJxIl/DAgf1r1qzmweUEBAR06dK1xm/gFRTkqby6TEtLo7UHVI2TkqKionHjxm3fvp1tn8vJyV244MnNHWqpMHPmDFlZWSqmKO4MBgHgIpcu0Zlg17x5c3v7fsLkmXnz5p0540HrQaqE58+fW1t3Jr2tmv06rcmgtNb+1Gx1QsV0zwsXLrLtbWVl5YAAfzs7OyF+qBs2bDhypBMVU15eXhAAoYVk3JcuXaZiavr06dzfQr262Nvb37p1U1VVle0Dff78uVev3p6eF2okAHTeWFAZdWncuLGGRrXfovN4uqcAbfbJ4HmcQSV/TUhIePbsGQRAOHnw4MG7d++o5NSjR48SShe1a9cuNDTE0NCQ7QN9+/Zt3Lhxbm7rqvtCnlP7rdbgZDDdkw00NVt369aNUhJwqZYoIUICcP36DSp2HB0dObvpP3NIBkDyAJIN8OBYGzZsIDJQrf04VVRUWrZsyRFfVXf8JyAgoGdPO0z3ZANai8J8fX0hAMLJjRs3ONXUOAtJcc6c8Zg7dy4PjnXhwkU7u17Viom0VgMwp1pvgI8ePTp4sAOrZXMqEMrpnn+lV69eVEYvnz59SmWcAALALVJTUyvWfzJES0vL2NhY6N0lJia2du2agwcPSElJsX2s+/fvW1t3rvrYK633wAyRl5dv27ZtVT5ZVla2cuWqmTNnYbone0hISNAqGOnn5wcBEDZu3bpFxQ6t+QYCgZOTk4+PD8OFTlUhJSWlS5cuVXzwLC0tueCc9u3bV2UiQFFR0fjxE9jYH/u/eZtwT/fk2bN58+Yt0XGaqAhAUFAwFTuOjo4i9VB16mQZHBxMpSZ75eTm5jk6DtmzZ29VkrD69evz3TNVGYnKzs62t7fnwR4DojDd869oa2vr6uoytxMSEkKxUiwEgCMCEMTciJmZWbNmzUTtudLQUA8KCrS1tWX7QKWlpQsXLpw1a/b3798r+ZiYmJi5Of9Hgf76AiA5OdnWtkto6F22z0R0pnv+lcGDBzE3kpOTEx0dDQEQHuLj46lMvRg4cIBoPleKiopXrlzmzfDCkSNHBgwYSPrOTIIv20hISJiZta/kA0+ePLGxsaW1FXYliNR0z78yaNAgKnZI1gsBEB5olX7t1auXyD5akpKSu3bt3Lx5Mw8KigUGBnbubFPJZm10SzDWAAMDg0pqaQUEBPTo0fPjx4886PB6e3uL1HTPvyZDrVppMLcTGRklIh4TCQG4d+8+cyPq6uo8GArnONOnT7t48YKCggLbB0pISOjcufOflssaGxvVrl2bj36oZCYSz6Z7uri4nDhxgr9+4CBUXoTUeKsSCAAXiYykUPqjR4/ueLoIPXv2vHPndvPmzdk+UEZGRp8+fTw8frN7K4l6FMvw1kgAfpOClJWVrVq1mjfTPXfs2LFunZsITvesSvtkbuTz589v3rwRBXcJvwDk5uZRWXlvY2ODp6sCPT294OCg9u3bs32g4uLiSZMmr1ix8r87RtCqEV8z/rsJRFFRkbPzRB7UW5aVlT1//tykSRPRDv+gzR2pLF55+PARBEAYePbsKfMKMKSrZWVlhafrB8rKyjduXB8yhBeTYrdt2zZihNMvJVP4uCmQurp648aNf/5JTk5O//4DeLCZsJKSUkCAf+/evdEC/4ScnJypqQlzO1TWjUIA+E9MDIUbqa2tzYW555xCRkbm6NGjy5Yt5cGxrl271r17j7S0tB8/6dChA78GQH5ZAZCSkmJr2yUkJITt42pqagYFBYrCQnTG+VknGnEjBgIgDDx9+pS5ES5MPOcgJAQvXbr0+PHjRAzYPtaTJ0+srTuTrxV/JXpMVJlPAvC/jSE6OtrGxjYuLo7tg1pYdAwMvMOdjfC4TPv2ZhyJGxAA/vPyJYWHk0pSKaw4Ojr4+d1QVlZm+0Dv378neYC3t/f/xET+jAL92Ivi5s2bPXr0+PDhA9tHHDx4kI+PD3LQKgtAeyqNLTc3FwIg8Lx69Zq5ESyzrBwzM7Pg4CB9fX22D5Sfnz98+IiKSop8WQ7WoEGDitnAJO8ZPNghNxfTPTmHkpJSkyZNmNuhVT4WAsA3iIb/PHBcMyQkJPg12iBANG/e/PbtWzzYjqailvqUKVNNTEx5f5kVg4GrV6+ZPn0G2zvGYLpnjaHSFxGFGvFCLgBUJvNqamqi/1UVFBQUPD3Pz5w5gwfHOnXqFDnQL7NxeECHDu2dnSdu3ryZ7QNhuiffBSAx8bXQO0pSuC8vJSWFuREsAK5WtrRx40YimXPnzqt8TzfmhIberWQ/Bpa4fPnKjxfRrA5iXLrkhQk/DJ5ZTeZGUlPfIwMQbKjcwtatW+OJqhYTJky4evWKoqIi2wfiwRD8z4iJifEg+mO6J41nloIAUOk+QgAEPgOgsr2UqGFjY0OimJC5jvmKwr+C6Z5UoNLwIAACz5cvX5gbadq0KZ6oGqXhbYKCgjp1soQrqgime9JCWVmZeVVkKtEDAsBPqJQBEMEiMLRo0KCBt7f3qFGj4Iq/gumedFFTU2NoISMjAwIg2FC5hbyfaiJMkI7Y/v37RLNSeVUfQkz3ZAEVFaaPbWlpaXp6OgRAoAWA6f2TkJBAwQ3mzJ079+zZM3JycnDFL2C6J2vZZ0PmRjIzsyAAAkxBQSHjZtQAzxIV+vXrd+vWTSpLNIUG7O7JHo0aNWJuJD8/T7i9JOQCkJOTw9BCnToKeJZoYWhoGBISjH01KsB0T1ahskbk61ch3w5I6DOAAoYW5OUhADRRVVUlfd7+/e1F3A+Y7sk2detSWIZSWFgg3F4ScgEoLi5mLAAYtqaMnJych4fH/PnzRdYDmO7JA2RkKMyn+qUMEQRA5KBSXg78gpiY2OrVqw4dOiiC7sV0T14JgCycAAEA3GXEiBHXr18XndfsmO4JIAAA/C8WFh1DQoJFYbs9TPcEEAAAfkVdXT04OKhLly5CfI2Y7gkgAAD8nrp1616+fGniRGehvDpM9wQQAAAqQ1JScufOnVu3bhUXF6o2iemeAAIAQJWYOnWKl9dFBQUhWXuB6Z4AAgBANejRo0dQUGDz5s0F/UIw3RNAAACoNjo6OiEhwR06dBDUhwrTPQEEAIAao6SkdOPG9SFDhgjcmcvKyp47dxbTPQEEAICaU7t27aNHj7i6Lhegc27UqJGf340+ffrg9gEIAACMEBMTW7x48YkTJ2RkZLh/tpqarYOCAk1NTXHjAAQAADo4OAz29/dTVlbm8kl27Gh+584ddXV13C8AAQCAJqRbHRISrK+vz83TGzhwgI+PD2oHAQgAAKzQrFmz27dv9+rVi2snNmvWrFOnTgnEIBUAEAAgqCgoyJ8/f44EXK48POLi27Zt27BhPaZ7AggAAKwjISFBAu4//7jzvZCArKzs2bNnpkyZjJsCIAAA8I7x48dfvXpFUVGRXydQMd2zb9++uBcAAgAAr+ncuXNwcFDr1q14f2hM9wQQAAD4TPk2y0FWVp14eVBM9wQQAAA4Qf369a9duzZmzBjeHA7TPQEEAAAOIS0tvXfvHje3tWwXEsB0TyB8SMIFAkFubt6VK5epmOrY0aJVKw0h88+cOXM0NTXHjRufn59Pv5ckLr5lyxahn/Dz+nViREQ4FVMDBgxUUJDHYwsBAHRIT/8yefIUKqYOHNgvfAJA6Nu3761bNx0dh6SmplI0Kysre/z4MVGY8EOiP602ZmVlBQEQCDAEBIQHQ0PDkJBgIyMjWgYlJCQw3RNAAAAQDFRUVAIC/AcOHEDFWuPGjTHdE0AAABAY5OTkTp06RWXHCElJjJECCAAAAoWYmBg2aQAAAgAAAAACAAAAAAIAAAAAAgAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAAAQAAAAABAAAAAAEAAAAIAAAAAAgAAAAACAAAAAABBi/p8AAwDSeK0BuZ1UUwAAAABJRU5ErkJggg==";
                                String base64Image = base64String.split(",")[1];
                                photo = base64Image;
                                Log.e("photo2",photo);
                            }
                            Log.e("photo3",photo);
                            if (numTarjeta.equals(code)){
                                txtNumTarjeta.setText(numTarjeta);
                                txtNombrePerfil.setText(name);
                                txtHola.setText("¡Hola "+name.toUpperCase()+ " !");
                                txtApellidoPaPerfil.setText(last_name);
                                txtApellidoMaPerfil.setText(middle_name);
                                mostrarFoto(photo);
                                if ( init.equals("1") ) {
                                    txtNumTarjeta.setEnabled(false);
                                    txtNombrePerfil.setEnabled(false);
                                    txtApellidoPaPerfil.setEnabled(false);
                                    txtApellidoMaPerfil.setEnabled(false);
                                }
                            }else {
                                Alerter.create(EditPerfilTarjeta2.this)
                                        .setTitle("Oh oh")
                                        .setText("No existe información con los datos proporcionados..")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }
                        } else {
                            Alerter.create(EditPerfilTarjeta2.this)
                                    .setTitle("Oh oh")
                                    .setText("No existe información con los datos proporcionados..")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }
                    } catch (JSONException e) {
                        Alerter.create(EditPerfilTarjeta2.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados...")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Anything you want
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
                    params.put("id", numTarjeta);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }else {
            Alerter.create(EditPerfilTarjeta2.this)
                    .setText("Favor de escanear un codigo de tarjeta\n")
                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                    .show();
        }
    }
}
