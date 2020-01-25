package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText correoEdit;
    private TextInputEditText contraseniaEdit;
    private MaterialButton iniciarSesionBtn;
    private MaterialButton iniciarConGoogleBtn;
    private MaterialButton crearCuentaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_avtivity);
        correoEdit = findViewById(R.id.login_usuario_edit);
        contraseniaEdit = findViewById(R.id.login_password_edit);
        iniciarSesionBtn = findViewById(R.id.iniciar_sesion_btn);
        iniciarConGoogleBtn = findViewById(R.id.iniciar_sesion_google_btn);
        crearCuentaBtn = findViewById(R.id.crear_cuenta_btn);
        iniciarSesionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correoEdit.clearFocus();
                contraseniaEdit.clearFocus();
                if(correoEdit.getText().toString().isEmpty() || contraseniaEdit.getText().toString().isEmpty()){

                    Toast t = Toast.makeText(getApplicationContext(),getString(R.string.datos_login_no_validos), Toast.LENGTH_LONG);
                    t.show();
                }
                else{
                    Intent i1 = new Intent(getApplicationContext(), PrincipalActivity.class);
                    //TODO iniciar sesión con mail y contraseña
                    startActivity(i1);
                }
            }
        });
        iniciarConGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO iniciar sesión con google
            }
        });

    }


}
