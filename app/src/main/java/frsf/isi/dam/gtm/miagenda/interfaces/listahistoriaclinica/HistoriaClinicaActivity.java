package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;

public class HistoriaClinicaActivity extends AppCompatActivity {

    private List<Turno> mockTurnos;
    private RecyclerView historiaClinicaRecyclerView;
    private RecyclerView.Adapter historiaClinicaAdapter;
    private  RecyclerView.LayoutManager historiaClinicaLayoutManager;
    private MaterialButton generarPdfBtn;

    private Toolbar toolbar;
    private MaterialTextView pacienteLbl;

    private final int anchoPagina = 595, largoPagina = 842;
    private int numeroPagina, margenX, margenY;
    private String paciente;
    private  PdfDocument documentoPdf;
    private PdfDocument.PageInfo paginaDocumento;
    private PdfDocument.Page contenidoPagina;
    private Canvas canvas;
    private Paint paint;
    private final int maxCantCaracteresXRenglon = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_clinica);

        toolbar = findViewById(R.id.historia_clinica_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        generarPdfBtn = findViewById(R.id.generar_pdf_btn);

        ActivityCompat.requestPermissions(HistoriaClinicaActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        //prueba con turnos falsos
        mockTurnos= new ArrayList<>();
        for(int i=0; i<20; i++){
            mockTurnos.add(new Turno(((i+1)+"/2/2020"),"Problema x"+i,"Sin nombre"));
        }
        mockTurnos.add(new Turno("21/2/2020","Problema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqindProblema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqindProblema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqindProblema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqindProblema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqindProblema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqindProblema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklas nldnasklndlaksndk lasndlkasnlkdnsklanqkndoqsndoqind","Sin nombre"));
        historiaClinicaRecyclerView = findViewById(R.id.historia_clinica_recyclerview);
        historiaClinicaLayoutManager = new LinearLayoutManager(this);
        historiaClinicaRecyclerView.setLayoutManager(historiaClinicaLayoutManager);
        historiaClinicaAdapter = new HistoriaClinicaAdapter(mockTurnos);
        historiaClinicaRecyclerView.setAdapter(historiaClinicaAdapter);

        pacienteLbl = findViewById(R.id.paciente_hist_clin_lbl);

        pacienteLbl.append(": "+mockTurnos.get(0).getPaciente());

        generarPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generarPdf();
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
                //TODO cerrar sesión
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

    private void generarPdf(){

        documentoPdf = new PdfDocument();
        paint = new Paint();
        numeroPagina = 1;
        margenX = 40;
        margenY = 50;

        crearNuevaPagina();

        for(Turno t : mockTurnos){

            if((margenY >= largoPagina)){
                numeroPagina++;
                documentoPdf.finishPage(contenidoPagina);
                crearNuevaPagina();
            }

            canvas.drawText(t.getFecha().toString(),margenX,margenY,paint);

            margenY += 5 + paint.descent() - paint.ascent();

            for(String linea: t.getDescripcion().split("\n")){
                if(linea.length()>maxCantCaracteresXRenglon){
                    ArrayList<String> arrayStrings = new ArrayList<>();

                    cortarString(linea,arrayStrings);

                    for(String lineaAux: arrayStrings) {

                        if((margenY >= largoPagina)){
                            numeroPagina++;
                            documentoPdf.finishPage(contenidoPagina);
                            crearNuevaPagina();
                        }
                        canvas.drawText(lineaAux, margenX, margenY, paint);
                        margenY +=  paint.descent() - paint.ascent();
                    }
                }else{
                    if((margenY >= largoPagina)){
                        numeroPagina++;
                        documentoPdf.finishPage(contenidoPagina);
                        crearNuevaPagina();
                    }
                    canvas.drawText(linea,margenX,margenY,paint);
                    margenY +=  paint.descent() - paint.ascent();
                }

                if((margenY >= largoPagina)){
                    numeroPagina++;
                    documentoPdf.finishPage(contenidoPagina);
                    crearNuevaPagina();
                }
                else {
                    canvas.drawLine(margenX, margenY, 580, margenY, paint);
                }

                margenY += 10 + paint.descent() - paint.ascent();

            }

        }

        documentoPdf.finishPage(contenidoPagina);

        String directorioDestino = Environment.getExternalStorageDirectory() +  getString(R.string.directorio_destino_pdf);

        //Directorio de los PDFs
        File f = new File(directorioDestino);

        if(!f.exists()){
            f.mkdir();
        }

        File archivoPDF = new File(directorioDestino + mockTurnos.get(0).getPaciente() + ".pdf");

        //Se guarda el pdf en el archivo(File) "archivoPDF"
        try {
            documentoPdf.writeTo(new FileOutputStream(archivoPDF));
        }
        catch (IOException e){
            Toast t = Toast.makeText(getApplicationContext(), R.string.error_guardar_pdf,Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void crearNuevaPagina() {
        margenX = 40;
        margenY = 50;
        //documentoPdf.finishPage(contenidoPagina);
        //Informacion del diseño de la pagina: ancho, largo y numero de pagina
        paginaDocumento = new PdfDocument.PageInfo.Builder(anchoPagina, largoPagina, numeroPagina).create();

        //Datos que se van a mostrar en una pagina
        contenidoPagina = documentoPdf.startPage(paginaDocumento);

        //Clase que sirve para "dibujar" el contenido en el PDF
         canvas = contenidoPagina.getCanvas();

        //Seteo de contenido en el Documento
        paciente = pacienteLbl.getEditableText().toString();

        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        paint.setUnderlineText(true);
        canvas.drawText(paciente,margenX+100,margenY,paint);
        margenY += 55;

        paint.setTextSize(25);
        paint.setFakeBoldText(false);
        paint.setUnderlineText(false);

    }

    private void cortarString(String linea,ArrayList<String> lineasARetornar) {

        //Este metodo es para aquellos casos en que un string deba mostrarse en mas de un renglon.

        if (linea.length() > maxCantCaracteresXRenglon) {
            //lineasARetornar.add(linea.substring(0, 50));
            String lineaCortada = (linea.substring(0,maxCantCaracteresXRenglon));
            if(lineaCortada.lastIndexOf(' ') != -1) {

                lineasARetornar.add(lineaCortada.substring(0, lineaCortada.lastIndexOf(' ')));
                linea = linea.substring(lineaCortada.lastIndexOf(' ') + 1, linea.length() - 1);
                cortarString(linea, lineasARetornar);
            }
            else{
                lineasARetornar.add(lineaCortada);
                cortarString(linea.substring(maxCantCaracteresXRenglon+1), lineasARetornar);
            }

        } else {
            lineasARetornar.add(linea);
        }
    }


}

