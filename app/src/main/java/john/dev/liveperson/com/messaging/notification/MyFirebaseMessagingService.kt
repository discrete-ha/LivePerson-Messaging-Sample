package john.dev.liveperson.com.messaging.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.liveperson.japan.sample.Utils.LocalStorage
import com.liveperson.messaging.sdk.api.LivePerson
import john.dev.liveperson.com.messaging.MainActivity
import john.dev.liveperson.com.messaging.R
import com.liveperson.infra.model.PushMessage




class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received [$remoteMessage]")
        val message = LivePerson.handlePushMessage(this, remoteMessage.getData(), LocalStorage.getAccountID(), false)
        NotificationUI.showPushNotification(this, message)
    }

    companion object {
        val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}