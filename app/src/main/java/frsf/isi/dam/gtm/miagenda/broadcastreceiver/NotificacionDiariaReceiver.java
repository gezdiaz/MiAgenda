package frsf.isi.dam.gtm.miagenda.broadcastreceiver;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;

public class NotificacionDiariaReceiver extends BroadcastReceiver {

    public static final String ALARMNOTIFICATION = "frsf.isi.dam.app04.ALARMNOTIFICATION";
    public static final String BOOTNOTIFICATION = Intent.ACTION_BOOT_COMPLETED;
    private final String CHANNEL_ID = "1";
    private final String TAG = "NotificacionReceiver";

    public  NotificacionDiariaReceiver(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);

            int importance  = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent actividadIntent = new Intent(context, PrincipalActivity.class);
        PendingIntent actividadPendingIntent = PendingIntent.getActivity(context,1,actividadIntent,0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar_24px)
                .setContentTitle(context.getString(R.string.notificacion_diaria_title))
                .setContentText(context.getString(R.string.notificacion_diaria_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(actividadPendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        Notification notification = notificationBuilder.build();


        switch (intent.getAction()){
            case ALARMNOTIFICATION:{
                notificationManagerCompat.notify(99, notification);
                break;
            }
            case BOOTNOTIFICATION:{

              PrincipalActivity.iniciarNotificacionesDiarias(context);

                break;
            }

            default:{
                Log.d(TAG, "Entro al default de NotificacionDiariaReceiver");
                break;
            }
        }
    }
}
