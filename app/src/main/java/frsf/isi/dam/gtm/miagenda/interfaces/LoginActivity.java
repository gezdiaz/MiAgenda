package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import frsf.isi.dam.gtm.miagenda.R;

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
                    mostrarToastError();
                }
            }
        });
    }

    private void mostrarToastError(){
        //Tuve que hacer un Layout a parte para poder cambiar el color del fondo del toast ya que no se distinguia por el fondo blanco.
        //Ademas del layout a parte se creo tambien un drawable para poder agregar bordes redondeados a dicho Layout

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_error_login,
                (ViewGroup) findViewById(R.id.linear_layout_toast));

        TextView text = layout.findViewById(R.id.error_text_lbl);
        text.setText(getString(R.string.datos_login_no_validos));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
