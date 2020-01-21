package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        correoEdit = findViewById(R.id.loginUsuarioEdit);
        contraseniaEdit = findViewById(R.id.loginPasswordEdit);
        iniciarSesionBtn = findViewById(R.id.iniciarSesionBtn);
        iniciarConGoogleBtn = findViewById(R.id.iniciarSesionGoogleBtn);
        crearCuentaBtn = findViewById(R.id.crearCuentaBtn);
        iniciarSesionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correoEdit.clearFocus();
                contraseniaEdit.clearFocus();
                if(correoEdit.getText().toString().isEmpty() || contraseniaEdit.getText().toString().isEmpty()){

                    //Tuve que hacer un Layout a parte para poder cambiar el color del fondo del toast ya que no se distinguia por el fondo blanco.
                    //Ademas del layout a parte se creo tambien un drawable para poder agregar bordes redondeados a dicho Layout

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_error_login,
                            (ViewGroup) findViewById(R.id.linearLayout_toast));

                    TextView text = layout.findViewById(R.id.errorTextLbl);
                    text.setText(getString(R.string.datosLoginNoValidos));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 50);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });
    }
}
