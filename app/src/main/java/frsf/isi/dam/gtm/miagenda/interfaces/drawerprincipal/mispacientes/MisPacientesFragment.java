package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import frsf.isi.dam.gtm.miagenda.R;

public class MisPacientesFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mis_pacientes, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        textView.setText("This is Mis Pacientes fragment");
        return root;
    }
}