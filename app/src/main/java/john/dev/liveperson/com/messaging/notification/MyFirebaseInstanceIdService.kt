package john.dev.liveperson.com.messaging.notification

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.liveperson.infra.ICallback
import com.liveperson.infra.LPAuthenticationParams
import com.liveperson.japan.sample.Utils.LocalStorage
import com.liveperson.messaging.sdk.api.LivePerson
import android.content.Intent




class MyFirebaseInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh")
        LPPusher.register()
    }

    companion object {
        val TAG = MyFirebaseInstanceIdService::class.java.simpleName
    }
}