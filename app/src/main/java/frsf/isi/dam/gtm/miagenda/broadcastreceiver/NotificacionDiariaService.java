package frsf.isi.dam.gtm.miagenda.broadcastreceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;


public class NotificacionDiariaService extends IntentService {

    public final static  String BOOTACTION = "frsf.isi.dam.app04.ALARMNOTIFICATION";
    private static final String CHANNEL_ID = "1";
    private static final String TAG ="NotificationService" ;

    public NotificacionDiariaService (){
        super("NotificacionDiariaService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent broadcastIntent = new Intent(getApplicationContext(), NotificacionDiariaReceiver.class);
        broadcastIntent.setAction(NotificacionDiariaReceiver.ALARMNOTIFICATION);
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1,broadcastIntent,0);

        Calendar ahora = Calendar.getInstance();
        ahora.setTimeZone(TimeZone.getDefault());
        //TODO usar las variables de los turnos
        Calendar horaAlarmaNotificacion = Calendar.getInstance();
        horaAlarmaNotificacion.setTimeZone(TimeZone.getDefault());
        horaAlarmaNotificacion.set(Calendar.HOUR_OF_DAY,6);
        horaAlarmaNotificacion.set(Calendar.MINUTE,30);
        horaAlarmaNotificacion.set(Calendar.SECOND,0);


        if(horaAlarmaNotificacion.getTimeInMillis()< ahora.getTimeInMillis()){
            horaAlarmaNotificacion.set(Calendar.DATE, horaAlarmaNotificacion.get(Calendar.DATE)+1);
        }

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, ahora.getTimeInMillis()+2000,120000,broadcastPendingIntent);
    }

}
