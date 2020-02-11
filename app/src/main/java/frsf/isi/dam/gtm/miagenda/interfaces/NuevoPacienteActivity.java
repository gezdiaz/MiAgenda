package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.ArchivosCloudStorage;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica.HistoriaClinicaActivity;

public class NuevoPacienteActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;

    private Toolbar myToolBar;
    private Calendar fechaNacimiento;
    private RelativeLayout fotoPacienteRelativeLayout;
    private TextInputEditText nombrePacienteEdit, apellidoPacienteEdit, fechaNacimientoEdit, dniEdit, telefonoEdit, provinciaEdit, ciudadEdit, calleEdit, numeroDireccionEdit, obraSocialEdit, paisEdit, departamentoEdit;
    private MaterialButton tomarFotoBtn, registrarPacBtn, eliminarFotoBtn;
    private ImageView fotoPacienteImageView;
    private boolean[] validaciones = {false, false, false, false, false, false, false, false, false, false};
    private boolean datosValidos = true;
    private int contadorWhile = 0;
    private ProgressDialog progressDialog;

    private MaterialDatePicker.Builder<Long> builder;
    private MaterialDatePicker<Long> datePicker;

    private Bitmap imageBitmap;
    private String TAG = "NuevoPacienteActivity";
    private String rutaImagen;
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case DatosFirestore.SAVE_PACIENTE:
                    Log.d(TAG, "Paciente guardado correctamente");
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    finish();
                    break;
                case DatosFirestore.ERROR_SAVE_PACIENTE:
                    Log.d(TAG, "Error al guardar el paciente");
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Snackbar.make(findViewById(R.id.nuevo_paciente_linear_lay), "Se produjo un error al guardar el nuevo paciente", BaseTransientBottomBar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.colorCancelar))
                            .show();
                    break;
                case ArchivosCloudStorage.CARGA_IMAGEN:
                    Log.d(TAG, "Se completo la carga de la imagen");
                    Toast.makeText(getApplicationContext(), "Se cargó la imagen", Toast.LENGTH_LONG).show();
                    break;
                case ArchivosCloudStorage.ERROR_CARGA_IMAGEN:
                    Log.d(TAG, "Error al cargar la imagen");
                    Toast.makeText(getApplicationContext(), "No se pudo cargar la imagen", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    //TODO REVISAR LAS VALIDACIONES CUANDO SE UTILIZA TEXTO AUTOCOMPLETADO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_paciente);
        myToolBar = findViewById(R.id.nuevo_paciente_toolbar);
        myToolBar.setTitleTextColor(getResources().getColor(R.color.colorTextSecondary));
        setSupportActionBar(myToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildPickerDate();

        nombrePacienteEdit = findViewById(R.id.nombre_paciente_edit);
        apellidoPacienteEdit = findViewById(R.id.apellido_paciente_edit);
        fechaNacimientoEdit = findViewById(R.id.fecha_nacimiento_edit);
        dniEdit = findViewById(R.id.dni_paciente_edit);
        telefonoEdit = findViewById(R.id.telefono_edit);
        provinciaEdit = findViewById(R.id.provincia_edit);
        ciudadEdit = findViewById(R.id.ciudad_edit);
        calleEdit = findViewById(R.id.calle_edit);
        numeroDireccionEdit = findViewById(R.id.numero_direccion_edit);
        obraSocialEdit = findViewById(R.id.obra_social_edit);
        fotoPacienteImageView = findViewById(R.id.foto_paciente_image_view);
        tomarFotoBtn = findViewById(R.id.tomar_foto_btn);
        registrarPacBtn = findViewById(R.id.registrar_paciente_btn);
        fotoPacienteRelativeLayout = findViewById(R.id.image_view_paciente_relative_layout);
        eliminarFotoBtn = findViewById(R.id.eliminar_foto_btn);
        paisEdit = findViewById(R.id.pais_edit);
        departamentoEdit = findViewById(R.id.departamento_direccion_edit);

        fechaNacimientoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getSupportFragmentManager(), datePicker.toString());
            }
        });

        nombrePacienteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && nombrePacienteEdit.getText().toString().isEmpty()) {
                    nombrePacienteEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[0] = false;
                } else {
                    validaciones[0] = true;
                }
            }
        });

        apellidoPacienteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && apellidoPacienteEdit.getText().toString().isEmpty()) {
                    apellidoPacienteEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[1] = false;
                } else {
                    validaciones[1] = true;
                }
            }
        });

        telefonoEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && telefonoEdit.getText().toString().isEmpty()) {
                    telefonoEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[3] = false;
                } else {
                    validaciones[3] = true;
                }
            }
        });

        provinciaEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && provinciaEdit.getText().toString().isEmpty()) {
                    provinciaEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[4] = false;
                } else {
                    validaciones[4] = true;
                }
            }
        });

        ciudadEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && ciudadEdit.getText().toString().isEmpty()) {
                    ciudadEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[5] = false;
                } else {
                    validaciones[5] = true;
                }
            }
        });

        calleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && calleEdit.getText().toString().isEmpty()) {
                    calleEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[6] = false;
                } else {
                    validaciones[6] = true;
                }
            }
        });

        numeroDireccionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && numeroDireccionEdit.getText().toString().isEmpty()) {
                    numeroDireccionEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[7] = false;
                } else {
                    validaciones[7] = true;
                }
            }
        });

        obraSocialEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && obraSocialEdit.getText().toString().isEmpty()) {
                    obraSocialEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[8] = false;
                } else {
                    validaciones[8] = true;
                }
            }
        });

        dniEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && dniEdit.getText().toString().isEmpty()) {
                    dniEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[9] = false;
                } else {
                    validaciones[9] = true;
                }
            }
        });

        registrarPacBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contadorWhile = 0;
                datosValidos = true;

                nombrePacienteEdit.clearFocus();
                apellidoPacienteEdit.clearFocus();
                fechaNacimientoEdit.requestFocus();
                fechaNacimientoEdit.clearFocus();
                dniEdit.clearFocus();
                telefonoEdit.clearFocus();
                provinciaEdit.clearFocus();
                ciudadEdit.clearFocus();
                calleEdit.clearFocus();
                numeroDireccionEdit.clearFocus();
                obraSocialEdit.clearFocus();

                if (fechaNacimientoEdit.getText().toString().isEmpty()) {
                    validaciones[2] = false;
                } else {
                    validaciones[2] = true;
                }

                while (contadorWhile < validaciones.length && datosValidos) {
                    if (validaciones[contadorWhile] == false) {
                        datosValidos = false;
                    }
                    contadorWhile++;
                }
                if (datosValidos) {
                    //TODO Registrar paciente
                    Paciente p = new Paciente(
                            nombrePacienteEdit.getText().toString(),
                            apellidoPacienteEdit.getText().toString(),
                            obraSocialEdit.getText().toString(),
                            fechaNacimiento.getTime(),
                            dniEdit.getText().toString(),
                            Long.parseLong(telefonoEdit.getText().toString()),
                            paisEdit.getText().toString().isEmpty() ? "Argentina" : paisEdit.getText().toString(),
                            provinciaEdit.getText().toString(),
                            ciudadEdit.getText().toString(),
                            calleEdit.getText().toString(),
                            numeroDireccionEdit.getText().toString(),
                            departamentoEdit.getText().toString());
                    progressDialog = ProgressDialog.show(NuevoPacienteActivity.this, getString(R.string.por_favor_espere), getString(R.string.guardando_paciente));
                    progressDialog.setCancelable(false);
                    DatosFirestore.getInstance().savePaciente(p, handler);
                    if (imageBitmap != null) {
                        ArchivosCloudStorage.getInstance().saveImageEnPaciente(dniEdit.getText().toString(), imageBitmap, handler, getApplicationContext());
                    }
                    // finish();
                } else {
                    Snackbar s = Snackbar.make(findViewById(R.id.nuevo_paciente_linear_lay), R.string.datos_paciente_no_validos, BaseTransientBottomBar.LENGTH_LONG);
                    s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                    s.show();
                }

            }
        });


        tomarFotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }

            }
        });

        eliminarFotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarImagen(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opcion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion_option_item: {
                Intent i1 = new Intent(this, LoginActivity.class);
                //Le digo a LogInActivity que cierre sesión
                i1.putExtra(LoginActivity.SignOut, true);
                startActivity(i1);
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildPickerDate() {

        //Tambien se puede hacer con un DatePickerDialog pero este es el que recomienda MaterialDesign

        builder = MaterialDatePicker.Builder.datePicker();
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_TEXT);
        builder.setTitleText(R.string.fecha_nacimiento_calendario);
        //builder.setSelection(Calendar.getInstance().getTimeInMillis());
        datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long aLong) {
                fechaNacimiento = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                fechaNacimiento.setTimeInMillis(aLong);
                String fechaSeleccionada = (fechaNacimiento.get(Calendar.DATE)) + "/" + (fechaNacimiento.get(Calendar.MONTH) + 1) + "/" + fechaNacimiento.get(Calendar.YEAR);
                fechaNacimientoEdit.setText(fechaSeleccionada);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Muestro la miniatura pero después cargo la imagen completa
            actualizarImagen((Bitmap) extras.get("data"));
            //imageBitmap = (Bitmap) extras.get("data");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(NuevoPacienteActivity.this.getContentResolver(), Uri.parse("file:" + rutaImagen));
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                        bitmap = null;
//                        byte[] bytes = baos.toByteArray();
//                        baos = null;
//                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        bytes = null;
//                        Message m = Message.obtain();
//                        m.what = IMAGEN_LISTA;
//                        m.obj = bitmap;
//                        handler.sendMessage(m);
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error al obtener la imagen desde archivo", e);
//                    }
//                }
//            }).run();

        }
    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/MiAgendaTemp/");
//        if (!storageDir.exists()) {
//            storageDir.mkdir();
//        }
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
////        File image = new File(storageDir+"/"+imageFileName+".jpg");
//
//        // Save a file: path for use with ACTION_VIEW intents
//        rutaImagen = image.getAbsolutePath();
//        return image;
//    }

    private void actualizarImagen(Bitmap bitmap) {
        imageBitmap = bitmap;
        fotoPacienteImageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            fotoPacienteRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            fotoPacienteRelativeLayout.setVisibility(View.GONE);
        }
    }

}
