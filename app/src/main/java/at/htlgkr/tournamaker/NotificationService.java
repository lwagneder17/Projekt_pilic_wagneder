package at.htlgkr.tournamaker;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {

    }
}
