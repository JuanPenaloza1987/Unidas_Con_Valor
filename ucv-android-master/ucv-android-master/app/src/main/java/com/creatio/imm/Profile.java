package com.creatio.imm;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class Profile extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private LinearLayout lyInfo;
    private ImageButton btnBack;
    private TextView txtName, txtDirection, txtDate, txtEmail, txtDigits;
    private CircleImageView img_profile;
    private Button btnEdit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = Profile.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        btnBack = findViewById(R.id.btnBack);
        lyInfo = findViewById(R.id.lyInfo);
        txtName = findViewById(R.id.txtName);
        btnEdit = findViewById(R.id.btnEdit);
        txtDigits = findViewById(R.id.txtDigits);
        txtDirection = findViewById(R.id.txtDirection);
        txtDate = findViewById(R.id.txtDate);
        img_profile = findViewById(R.id.img_profile);
        txtEmail = findViewById(R.id.txtEmail);
        progressDialog = new ProgressDialog(context);
        Glide.with(this).load(R.drawable.img_fondo)
                .apply(bitmapTransform(new BlurTransformation(25, 3)))
                .into((ImageView) findViewById(R.id.img_fondo));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DataUser.class);
                startActivity(intent);
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_alert_lottie);
                // set the custom dialog components - text, image and button
                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                txtTitle.setText("¿Quieres editar tu foto de perfil?");
                txtMsj.setText("Podrás seleccionar una fotografía de tu galería.");


                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                // if button is clicked, close the custom dialog
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Dexter.withActivity(Profile.this)
                                .withPermissions(
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                ).withListener(new MultiplePermissionsListener() {

                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                                ImagePicker.create(Profile.this) // Activity or Fragment
                                        .single() // multi mode (default mode)
                                        .showCamera(true) // show camera or not (true by default)
                                        .imageDirectory("Camera")
                                        .start();

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                        }).check();

                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetInformationUser();
        GetCards();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            progressDialog.setMessage("Cargando");
            progressDialog.show();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
            UploadImage(getImageUri(context, bitmap));
        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    public void UploadImage(Uri filePath) {
        File file =  new File(getRealPathFromURI(filePath));
        AndroidNetworking.upload(getResources().getString(R.string.apiurl) + "SaveImages")
                .addMultipartFile("file", file)
                .addMultipartParameter("apikey", getResources().getString(R.string.apikey))
                .addMultipartParameter("type", "users")
                .addMultipartParameter("ID", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());

                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {
                        Alerter.create(Profile.this)
                                .setTitle("Hecho")
                                .setText("La imagen se ha actualizado correctamente.")
                                .setBackgroundColorInt(getResources().getColor(R.color.green))
                                .show();
                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");

                        }
                        GetInformationUser();

                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Alerter.create(Profile.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados.")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                    progressDialog.dismiss();
                    Alerter.create(Profile.this)
                            .setTitle("Oh oh")
                            .setText("No existe información con los datos proporcionados.")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onError(ANError anError) {
                Log.e("Error", anError.toString());
                Alerter.create(Profile.this)
                        .setTitle("Oh oh")
                        .setText("No existe información con los datos proporcionados.")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void GetInformationUser() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "GetInformationUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("type", prefs.getString("type", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("info usetr", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String last_name = obj.optString("last_name");
                            String email = obj.optString("description");
                            String code = obj.optString("create_date");
                            String status = obj.optString("status");
                            String date_birthday = obj.optString("date_birthday");
                            String image = obj.optString("profile_img");
                            String direction = obj.optString("direction");
                            String nivel = obj.getString("nivel");

                            txtName.setText(name + " " + last_name);
                            txtEmail.setText(prefs.getString("email", ""));
                            txtDate.setText("Fecha de nacimiento: " + date_birthday);
                            txtDirection.setText("Dirección: " + direction);


                            RequestOptions options = new RequestOptions()
                                    .placeholder(R.drawable.no_image)
                                    .error(R.drawable.no_image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .override(1000, 1000)
                                    .priority(com.bumptech.glide.Priority.HIGH);
                            Glide.with(context)
                                    .load(image)
                                    .apply(options)
                                    .into(img_profile);


                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
    public void GetCards() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "GetCards")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("info usetr", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);

                            String last4 = obj.optString("last4");
                            String defaul = obj.optString("default");
                            String src_id = obj.optString("id");
                            txtDigits.setText("****-****-****-"+last4);


                        }

                    } else {
                        txtDigits.setText("Ninguna tarjeta agregada");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
    private String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android OS below sdk version 19
            ret = getImageRealPath(getContentResolver(), uri, null);
        }

        return ret;
    }

    private String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
    *  content uri like content://media/external/images/media/1302716
    *  */
    private boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
    *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
    * */
    private boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }
}
