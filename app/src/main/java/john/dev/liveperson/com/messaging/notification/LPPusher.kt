package john.dev.liveperson.com.messaging.notification

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.liveperson.infra.ICallback
import com.liveperson.infra.LPAuthenticationParams
import com.liveperson.japan.sample.Utils.LocalStorage
import com.liveperson.messaging.sdk.api.LivePerson
import john.dev.liveperson.com.messaging.MainActivity

class LPPusher{

    companion object {
        val TAG = LPPusher::class.java.simpleName

        fun register(){
            val token = FirebaseInstanceId.getInstance().token
            Log.d(TAG, "FCM token: $token")
//            val authParams = LPAuthenticationParams(LPAuthenticationParams.LPAuthenticationType.AUTH)
//            authParams.hostAppJWT = LocalStorage.getJWT()
//            LivePerson.registerLPPusher(LocalStorage.getAccountID(), LocalStorage.getAppId(), token, authParams, object : ICallback<Void, Exception> {
//                override fun onSuccess(value: Void) {
//                    Log.d(MainActivity.TAG, "registerLPPusher onSuccess: ")
//                }
//
//                override fun onError(exception: Exception) {
//                    Log.d(MainActivity.TAG, "registerLPPusher onError: $exception")
//                }
//            })

            LivePerson.registerLPPusher(LocalStorage.getAccountID(), LocalStorage.getAppId(), token)
        }
    }
}
