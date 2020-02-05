package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import frsf.isi.dam.gtm.miagenda.R;

public class NuevoPacienteActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;

    private Toolbar myToolBar;
    private Calendar fechaNacimiento;
    private RelativeLayout fotoPacienteRelativeLayout;
    private TextInputEditText nombrePacienteEdit, apellidoPacienteEdit, fechaNacimientoEdit,dniEdit, telefonoEdit, provinciaEdit, ciudadEdit, calleEdit, numeroDireccionEdit,obraSocialEdit;
    private MaterialButton tomarFotoBtn, registrarPacBtn, eliminarFotoBtn;
    private ImageView fotoPacienteImageView;
    private  boolean[] validaciones = {false,false,false,false,false,false,false,false,false,false};
    private boolean datosValidos = true;
    private int contadorWhile = 0;

    private MaterialDatePicker.Builder<Long> builder;
    private MaterialDatePicker<Long> datePicker;

    private  Bitmap imageBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_paciente);
        myToolBar = findViewById(R.id.nuevo_paciente_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);// set drawable icon
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
        numeroDireccionEdit  = findViewById(R.id.numero_direccion_edit);
        obraSocialEdit = findViewById(R.id.obra_social_edit);
        fotoPacienteImageView = findViewById(R.id.foto_paciente_image_view);
        tomarFotoBtn = findViewById(R.id.tomar_foto_btn);
        registrarPacBtn = findViewById(R.id.registrar_paciente_btn);
        fotoPacienteRelativeLayout = findViewById(R.id.image_view_paciente_relative_layout);
        eliminarFotoBtn = findViewById(R.id.eliminar_foto_btn);

        fechaNacimientoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getSupportFragmentManager(),datePicker.toString());
            }
        });

        nombrePacienteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && nombrePacienteEdit.getText().toString().isEmpty()){
                    nombrePacienteEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[0] = false;
                }
                else {
                    validaciones[0] = true;
                }
            }
        });

        apellidoPacienteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && apellidoPacienteEdit.getText().toString().isEmpty()){
                    apellidoPacienteEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[1] = false;
                }
                else {
                    validaciones[1] = true;
                }
            }
        });

        telefonoEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && telefonoEdit.getText().toString().isEmpty()){
                    telefonoEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[3] = false;
                }
                else {
                    validaciones[3] = true;
                }
            }
        });

        provinciaEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && provinciaEdit.getText().toString().isEmpty()){
                    provinciaEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[4] = false;
                }
                else {
                    validaciones[4] = true;
                }
            }
        });

        ciudadEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && ciudadEdit.getText().toString().isEmpty()){
                    ciudadEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[5] = false;
                }
                else {
                    validaciones[5] = true;
                }
            }
        });

        calleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && calleEdit.getText().toString().isEmpty()){
                    calleEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[6] = false;
                }
                else {
                    validaciones[6] = true;
                }
            }
        });

        numeroDireccionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && numeroDireccionEdit.getText().toString().isEmpty()){
                    numeroDireccionEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[7] = false;
                }
                else {
                    validaciones[7] = true;
                }
            }
        });

        obraSocialEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && obraSocialEdit.getText().toString().isEmpty()){
                    obraSocialEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[8] = false;
                }
                else {
                    validaciones[8] = true;
                }
            }
        });

        dniEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && dniEdit.getText().toString().isEmpty()){
                    dniEdit.setError(getString(R.string.campo_vacio_error));
                    validaciones[9] = false;
                }
                else {
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

            if(fechaNacimientoEdit.getText().toString().isEmpty()){
                validaciones[2] = false;
            }
            else{
                validaciones[2] = true;
            }

            while(contadorWhile<validaciones.length && datosValidos){
                if(validaciones[contadorWhile] == false){
                    datosValidos = false;
                    }
                    contadorWhile++;
                }
                if(datosValidos){
                    //TODO Registrar paciente
                }
                else{
                    Toast t = Toast.makeText(getApplicationContext(),getString(R.string.datos_login_no_validos),Toast.LENGTH_LONG);
                    t.show();
                }

            }
        });


        tomarFotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }


            }
        });

        eliminarFotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoPacienteImageView.setImageBitmap(null);
                fotoPacienteRelativeLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_paciente, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion_option_item:{
                Intent i1 = new Intent(this, LoginActivity.class);
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

    private void buildPickerDate(){

        //Tambien se puede hacer con un DatePickerDialog pero este es el que recomienda MaterialDesign

        builder =  MaterialDatePicker.Builder.datePicker();
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_TEXT);
        builder.setTitleText(R.string.fecha_nacimiento_calendario);
        //builder.setTheme(R.style.MaterialDatePickerEditado);
        //builder.setSelection(Calendar.getInstance().getTimeInMillis());
        datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long aLong) {
                fechaNacimiento = Calendar.getInstance();
                fechaNacimiento.setTimeInMillis(aLong);
                fechaNacimiento.add(Calendar.DATE,1);
                String fechaSeleccionada = (fechaNacimiento.get(Calendar.DATE)) + "/"+(fechaNacimiento.get(Calendar.MONTH)+1) + "/"+fechaNacimiento.get(Calendar.YEAR);
                fechaNacimientoEdit.setText(fechaSeleccionada);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            fotoPacienteImageView.setImageBitmap(imageBitmap);
            fotoPacienteRelativeLayout.setVisibility(View.VISIBLE);
        }
    }
}
