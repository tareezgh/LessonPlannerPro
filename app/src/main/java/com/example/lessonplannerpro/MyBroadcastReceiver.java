package com.example.lessonplannerpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /************************************************ our custom receiver **********************************************************/

        if (intent.getAction().equals("com.example.lessonplannerpro.CUSTOM_INTENT")) {
            ItemsRecycleAdapter.listener.UpdateLessonList();
        }

        /************************************************ receiver work when the device is ON **********************************************************/
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, MyForegroundService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
