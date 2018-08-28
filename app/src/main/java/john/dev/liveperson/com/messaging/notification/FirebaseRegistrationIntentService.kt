package john.dev.liveperson.com.messaging.notification


import android.app.IntentService
import android.content.Intent
import android.util.Log

class FirebaseRegistrationIntentService : IntentService(TAG) {


    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent: registering the token to pusher")
        LPPusher.register()
    }

    companion object {
        val TAG = FirebaseRegistrationIntentService::class.java.simpleName
    }
}