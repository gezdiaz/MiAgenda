package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes.MisPacientesAdapter;

public class HistoriaClinicaActivity extends AppCompatActivity {

    public static final String TAG  = "HistoriaClinicaActivity";

    private List<Turno> mockTurnos;
    private Paciente p;
    private View contextView;
    private RecyclerView historiaClinicaRecyclerView;
    private HistoriaClinicaAdapter historiaClinicaAdapter;
    private RecyclerView.LayoutManager historiaClinicaLayoutManager;
    private MaterialButton generarPdfBtn, verPdfBtn;
    private ProgressBar creacionPdfProgressBar;

    private Toolbar toolbar;
    private MaterialTextView pacienteLbl;
    private Snackbar creacionPdfSnackbarExito, creacionPdfSnackbarError;

    private final int anchoPagina = 595, largoPagina = 842, PDF_ERROR_CODIGO = -1, PDF_EXITO_CODIGO = 1;
    private int numeroPagina, margenX, margenY;
    private String paciente;
    private PdfDocument documentoPdf;
    private PdfDocument.PageInfo paginaDocumento;
    private PdfDocument.Page contenidoPagina;
    private Canvas canvas;
    private Paint paint;
    private final int maxCantCaracteresXRenglon = 105;
    private String directorioDestinoPdf;
    private DatosFirestore datosFirestore;
    private FirestoreRecyclerOptions<frsf.isi.dam.gtm.miagenda.entidades.Turno> firestoreRecyclerOptions;

    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_clinica);

        p = (Paciente)getIntent().getSerializableExtra("paciente");

        myHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                int codigoHiloSecundario = inputMessage.what;

                switch (codigoHiloSecundario) {
                    case (PDF_EXITO_CODIGO): {
                        //Termino de crear exitosamente el pdf

                        creacionPdfProgressBar.setVisibility(View.GONE);
                        generarPdfBtn.setVisibility(View.GONE);
                        verPdfBtn.setVisibility(View.VISIBLE);
                        creacionPdfSnackbarExito.show();

                        break;
                    }
                    case (PDF_ERROR_CODIGO): {
                        //Error al crear PDF
                        creacionPdfProgressBar.setVisibility(View.GONE);
                        generarPdfBtn.setEnabled(true);
                        creacionPdfSnackbarError.show();
                        break;
                    }
                }
            }
        };

        toolbar = findViewById(R.id.historia_clinica_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextSecondary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        generarPdfBtn = findViewById(R.id.generar_pdf_btn);
        verPdfBtn = findViewById(R.id.ver_pdf_btn);

        creacionPdfProgressBar = findViewById(R.id.crear_pdf_progress_bar);

        contextView = findViewById(R.id.historia_clinica_linear_layout);
        creacionPdfSnackbarExito = Snackbar.make(contextView, R.string.exito_guardar_pdf, BaseTransientBottomBar.LENGTH_LONG);
        creacionPdfSnackbarExito.setTextColor(Color.WHITE);
        creacionPdfSnackbarExito.setActionTextColor(Color.WHITE);
        creacionPdfSnackbarExito.setAction(R.string.ver_pdf_snackbar, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verPdf();
            }
        });


        creacionPdfSnackbarExito.setBackgroundTint(getResources().getColor(R.color.colorPrimary));

        creacionPdfSnackbarError = Snackbar.make(contextView, R.string.error_guardar_pdf, BaseTransientBottomBar.LENGTH_LONG);
        creacionPdfSnackbarError.setBackgroundTint(Color.RED);
        creacionPdfSnackbarError.setTextColor(Color.WHITE);

        ActivityCompat.requestPermissions(HistoriaClinicaActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        historiaClinicaRecyclerView = findViewById(R.id.historia_clinica_recyclerview);
        historiaClinicaLayoutManager = new LinearLayoutManager(this);
        historiaClinicaRecyclerView.setLayoutManager(historiaClinicaLayoutManager);

        datosFirestore = DatosFirestore.getInstance();
        Query query = datosFirestore.getAllTurnosDePacienteQuery(p.getDni());
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<frsf.isi.dam.gtm.miagenda.entidades.Turno>().setQuery(query, frsf.isi.dam.gtm.miagenda.entidades.Turno.class).build();

        historiaClinicaAdapter = new HistoriaClinicaAdapter(firestoreRecyclerOptions);
        historiaClinicaAdapter.notifyDataSetChanged();
        historiaClinicaRecyclerView.setAdapter(historiaClinicaAdapter);

        pacienteLbl = findViewById(R.id.paciente_hist_clin_lbl);
        pacienteLbl.append(": " + p.getNombre()+" "+p.getApellido());

        generarPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creacionPdfProgressBar.setVisibility(View.VISIBLE);
                generarPdfBtn.setEnabled(false);
                final Message msg = new Message();
                //Hilo secundario para crear el PDF
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            generarPdf(msg);
                            myHandler.sendMessage(msg);
                        } catch (Exception e) {
                            msg.what = PDF_ERROR_CODIGO;
                            myHandler.sendMessage(msg);
                        }
                    }
                };
                Thread hiloSecundario = new Thread(r);
                hiloSecundario.start();
            }
        });


        verPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verPdf();
            }
        });
        Log.d(TAG,String.valueOf(firestoreRecyclerOptions.getSnapshots().size()));
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

    private void generarPdf(Message msg) {

        documentoPdf = new PdfDocument();
        paint = new Paint();
        numeroPagina = 1;
        margenX = 25;
        margenY = 45;

        crearNuevaPagina();

         for (frsf.isi.dam.gtm.miagenda.entidades.Turno t : historiaClinicaAdapter.getSnapshots()) {

            //Aca le sumo cinco al margenY para que no quede un fecha colgada en una hoja y la respectiva descripcon en una hoja nueva.
            if (((margenY + 5 + paint.descent() - paint.ascent()) >= largoPagina)) {
                numeroPagina++;
                documentoPdf.finishPage(contenidoPagina);
                crearNuevaPagina();
            }

            canvas.drawText(getFechaToString(t), margenX, margenY, paint);

            margenY += 5 + paint.descent() - paint.ascent();

            for (String linea : t.getDescripcion().split("\n")) {
                if (linea.length() > maxCantCaracteresXRenglon) {
                    ArrayList<String> arrayStrings = new ArrayList<>();

                    cortarString(linea, arrayStrings);

                    for (String lineaAux : arrayStrings) {

                        if ((margenY >= largoPagina)) {
                            numeroPagina++;
                            documentoPdf.finishPage(contenidoPagina);
                            crearNuevaPagina();
                        }
                        canvas.drawText(lineaAux, margenX, margenY, paint);
                        margenY += paint.descent() - paint.ascent();
                    }
                } else {
                    if ((margenY >= largoPagina)) {
                        numeroPagina++;
                        documentoPdf.finishPage(contenidoPagina);
                        crearNuevaPagina();
                    }
                    canvas.drawText(linea, margenX, margenY, paint);
                    margenY += paint.descent() - paint.ascent();
                }

                if ((margenY >= largoPagina)) {
                    numeroPagina++;
                    documentoPdf.finishPage(contenidoPagina);
                    crearNuevaPagina();
                } else {
                    canvas.drawLine(margenX, margenY, 580, margenY, paint);
                }

                margenY += 10 + paint.descent() - paint.ascent();

            }

        }

        documentoPdf.finishPage(contenidoPagina);

        directorioDestinoPdf = Environment.getExternalStorageDirectory() + getString(R.string.directorio_destino_pdf);

        //Directorio de los PDFs
        File f = new File(directorioDestinoPdf);

        if (!f.exists()) {
            f.mkdir();
        }

        File archivoPDF = new File(directorioDestinoPdf + p.getNombre()+"-"+p.getApellido()+"-" +p.getDni()+ ".pdf");

        //Se guarda el pdf en el archivo(File) "archivoPDF"
        try {
            documentoPdf.writeTo(new FileOutputStream(archivoPDF));
            msg.what = PDF_EXITO_CODIGO;
        } catch (IOException e) {
            msg.what = PDF_ERROR_CODIGO;
        }
    }

    private String getFechaToString(frsf.isi.dam.gtm.miagenda.entidades.Turno t) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(t.getFecha());
     return cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
    }

    private void crearNuevaPagina() {
        margenX = 25;
        margenY = 45;
        //documentoPdf.finishPage(contenidoPagina);
        //Informacion del diseño de la pagina: ancho, largo y numero de pagina
        paginaDocumento = new PdfDocument.PageInfo.Builder(anchoPagina, largoPagina, numeroPagina).create();

        //Datos que se van a mostrar en una pagina
        contenidoPagina = documentoPdf.startPage(paginaDocumento);

        //Clase que sirve para "dibujar" el contenido en el PDF
        canvas = contenidoPagina.getCanvas();

        //Seteo de contenido en el Documento
        paciente = p.getNombre() + " " + p.getApellido();

        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        paint.setUnderlineText(true);
        canvas.drawText("Paciente: " + paciente.toUpperCase(), margenX , margenY, paint);
        margenY += 30;


        paint.setTextSize(10);
        paint.setFakeBoldText(false);
        paint.setUnderlineText(false);

    }

    private void cortarString(String linea, ArrayList<String> lineasARetornar) {

        Log.d(TAG,linea);
        //Este metodo es para aquellos casos en que un string deba mostrarse en mas de un renglon.

        if (linea.length() > maxCantCaracteresXRenglon) {
            //lineasARetornar.add(linea.substring(0, 50));
            String lineaCortada = (linea.substring(0, maxCantCaracteresXRenglon));
            if(lineaCortada.lastIndexOf(' ') == 0){
                lineaCortada = lineaCortada.substring(1);
            }
            if (lineaCortada.lastIndexOf(' ') != -1) {

                lineasARetornar.add(lineaCortada.substring(0, lineaCortada.lastIndexOf(' ')));
                linea = linea.substring(lineaCortada.lastIndexOf(' ')+1);
                cortarString(linea, lineasARetornar);

            } else {
                lineasARetornar.add(lineaCortada);
                cortarString(linea.substring(maxCantCaracteresXRenglon), lineasARetornar);
            }

        } else {
            lineasARetornar.add(linea);
        }
    }


    private void verPdf() {

        File file = new File(directorioDestinoPdf + p.getNombre() + "-"+ p.getApellido() + "-" + p.getDni() + ".pdf");
        Uri contentUri = Uri.fromFile(file);
        Intent verPdfIntent = new Intent(Intent.ACTION_VIEW);
        if (file.exists()) {
            //Dependiendo del numero de version hay que usar FILEPROVIDER.
            if (Build.VERSION.SDK_INT >= 24) {
                Uri apkURI = FileProvider.getUriForFile(HistoriaClinicaActivity.this, HistoriaClinicaActivity.this.getApplicationContext().getPackageName() + ".provider", file);
                verPdfIntent.setDataAndType(apkURI, "application/pdf");
                verPdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {

                verPdfIntent.setDataAndType(contentUri, "application/pdf");
                verPdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            }
            try {
                startActivity(verPdfIntent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
                Toast.makeText(getApplicationContext(), getString(R.string.instalar_aplicacion_pdf), Toast.LENGTH_LONG).show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.historia_clinica_linear_layout), getString(R.string.archivo_inexistente_snackbar), BaseTransientBottomBar.LENGTH_LONG);
            snackbar.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
            snackbar.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        historiaClinicaAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        historiaClinicaAdapter.stopListening();
    }
}

