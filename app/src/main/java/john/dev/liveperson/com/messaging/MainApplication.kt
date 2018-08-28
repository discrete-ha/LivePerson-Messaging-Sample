package john.dev.liveperson.com.messaging

import android.app.Application
import com.liveperson.api.LivePersonIntents
import android.support.v4.content.LocalBroadcastManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.liveperson.infra.LPAuthenticationParams
import com.liveperson.japan.sample.Utils.LocalStorage
import com.liveperson.messaging.sdk.api.LivePerson
import john.dev.liveperson.com.messaging.authentication.TokenTool
import john.dev.liveperson.com.messaging.util.DialogManager


class MainApplication : Application() {

    private var mLivePersonReceiver: BroadcastReceiver? = null
    private var mReconnectTask: ReconnectTask? = null

    override fun onCreate() {
        super.onCreate()

        instance = this
        registerToLivePersonEvents()
    }

    fun registerToLivePersonEvents() {
        createLivePersonReceiver()
        mLivePersonReceiver?.let {
            LocalBroadcastManager.getInstance(applicationContext)
                    .registerReceiver(it, LivePersonIntents.getIntentFilterForAllEvents())
        }
    }

    private fun createLivePersonReceiver() {
        if (mLivePersonReceiver != null) {
            return
        }
        mLivePersonReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                Log.d(TAG, "Got LP intent event with action " + intent.action!!)
                when (intent.action) {
                    LivePersonIntents.ILivePersonIntentAction.LP_ON_TOKEN_EXPIRED_INTENT_ACTION -> {
                        onTokenExpired()
                    }

                }
            }
        }
    }

    inner class ReconnectTask internal constructor(private val mUserName: String, private val mExpire: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            Log.d(TAG, "ReconnectTask")
            TokenTool.getJWT(mUserName, mExpire, getContext())
            return true
        }

        override fun onPostExecute(success: Boolean?) {
            Log.d(TAG, "ReconnectTask onPostExecute")
            mReconnectTask = null
            if (success!! && TokenTool.isJWTValid()) {
                Log.d(LoginActivity.TAG,getString(R.string.msg_login_success))
                val authParams = LPAuthenticationParams(LPAuthenticationParams.LPAuthenticationType.AUTH)
                authParams.hostAppJWT = LocalStorage.getJWT()
                LivePerson.reconnect(authParams)
            }
        }

        override fun onCancelled() {
            mReconnectTask = null
        }
    }

    private fun onTokenExpired() {
        Log.d(TAG, "onTokenExpired reconnect")
        mReconnectTask = ReconnectTask(LocalStorage.getName(), LocalStorage.getExpire())
        mReconnectTask!!.execute(null as Void?)
    }

    companion object {
        lateinit var instance: MainApplication
        val TAG = MainApplication::class.java.simpleName

        fun getContext(): MainApplication {
            return instance
        }
    }

}
