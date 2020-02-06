package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;

public class NuevoPacienteActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private Calendar fechaNacimiento;
    private TextInputEditText nombrePacienteEdit, apellidoPacienteEdit, fechaNacimientoEdit,dniEdit, telefonoEdit, provinciaEdit, ciudadEdit, calleEdit, numeroDireccionEdit,obraSocialEdit;
    private MaterialButton tomarFotoBtn, registrarPacBtn;
    private  boolean[] validaciones = {false,false,false,false,false,false,false,false,false,false};
    private boolean datosValidos = true;
    private int contadorWhile = 0;

    private MaterialDatePicker.Builder<Long> builder;
    private MaterialDatePicker<Long> datePicker;

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
        tomarFotoBtn = findViewById(R.id.tomar_foto_btn);
        registrarPacBtn = findViewById(R.id.registrar_paciente_btn);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opcion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion_option_item:{
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

    private void buildPickerDate(){

        //Tambien se puede hacer con un DatePickerDialog pero este es el que recomienda MaterialDesign

        builder =  MaterialDatePicker.Builder.datePicker();
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_TEXT);
        builder.setTitleText(R.string.fecha_nacimiento_calendario);
        //builder.setSelection(Calendar.getInstance().getTimeInMillis());
        datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long aLong) {
                fechaNacimiento = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                fechaNacimiento.setTimeInMillis(aLong);
                String fechaSeleccionada = (fechaNacimiento.get(Calendar.DATE)) + "/"+(fechaNacimiento.get(Calendar.MONTH)+1) + "/"+fechaNacimiento.get(Calendar.YEAR);
                fechaNacimientoEdit.setText(fechaSeleccionada);
            }
        });
    }
}
