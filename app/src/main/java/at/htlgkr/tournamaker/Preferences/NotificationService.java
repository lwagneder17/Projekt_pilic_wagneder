package at.htlgkr.tournamaker.Preferences;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.R;

public class NotificationService extends IntentService
{
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private static DatabaseReference benutzerDataBase = FragmentsActivity.benutzerDataBase;
    private Gson gson = new Gson();

    public NotificationService()
    {
        super("NotificationService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        if(currentBenutzer.getFriends().getFriendRequests().size() > 1)
        {
            CharSequence name = "name";
            String description = "bruh";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("10", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder  = new NotificationCompat.Builder(this, "10")
                    .setSmallIcon(android.R.drawable.btn_star_big_on)
                    .setColor(Color.GREEN)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Sie haben mtehrere Freundschafsanfragen bekommen")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(10, builder.build());
        }
        else if(currentBenutzer.getFriends().getFriendRequests().size() == 1)
        {
            CharSequence name = "name";
            String description = "bruh";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder  = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(android.R.drawable.btn_star_big_on)
                    .setColor(Color.GREEN)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Sie haben eine Freundschaftsanfrage bekommen")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, builder.build());
        }




        if(currentBenutzer.getFriends().getFriendDenied().size() > 1)
        {
            CharSequence name = "name";
            String description = "bruh";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("10", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder  = new NotificationCompat.Builder(this, "10")
                    .setSmallIcon(android.R.drawable.btn_star_big_on)
                    .setColor(Color.RED)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Mehrere Freunschaftsanfragen wurden abgelehnt")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(10, builder.build());

            currentBenutzer.getFriends().getFriendDenied().clear();
            benutzerDataBase.child(currentBenutzer.getUsername()).setValue(gson.toJson(currentBenutzer));
        }
        else if(currentBenutzer.getFriends().getFriendDenied().size() == 1)
        {
            String username = currentBenutzer.getFriends().getFriendDenied().get(0);

            CharSequence name = "name";
            String description = "bruh";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(android.R.drawable.btn_star_big_on)
                    .setColor(Color.RED)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(username+" hat ihre Freundschaftsanfrage abgelehnt")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, builder.build());

            currentBenutzer.getFriends().getFriendDenied().clear();
            benutzerDataBase.child(currentBenutzer.getUsername()).setValue(gson.toJson(currentBenutzer));
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
