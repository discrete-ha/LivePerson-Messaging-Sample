package john.dev.liveperson.com.messaging

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.iid.FirebaseInstanceId
import com.liveperson.infra.*
import com.liveperson.infra.callbacks.InitLivePersonCallBack
import com.liveperson.japan.sample.Utils.LocalStorage
import com.liveperson.messaging.sdk.api.LivePerson
import com.liveperson.messaging.sdk.api.callbacks.LogoutLivePersonCallback
import john.dev.liveperson.com.messaging.notification.FirebaseRegistrationIntentService
import john.dev.liveperson.com.messaging.notification.LPPusher
import john.dev.liveperson.com.messaging.notification.MyFirebaseInstanceIdService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLivePerson()
        initButtons()
    }

    private fun initButtons(){
        logout_button.setOnClickListener { logout()}
        window_button.setOnClickListener { showConversationWindow()}
        fragment_button.setOnClickListener { showConversationFragment()}
    }

    private fun showConversationWindow(){
        Log.d(TAG, "showConversationWindow" )
        val authParams = LPAuthenticationParams(LPAuthenticationParams.LPAuthenticationType.AUTH)
        authParams.hostAppJWT = LocalStorage.getJWT()
        val params = ConversationViewParams(false)
        LivePerson.showConversation(this@MainActivity, authParams, params)
    }

    private fun showConversationFragment(){
        Log.d(TAG, "showConversationFragment" )
        var intent = Intent(this@MainActivity, ConversationActivity::class.java)
        intent.putExtra(Infra.KEY_READ_ONLY, false)
        startActivity(intent)
    }

    private fun initLivePerson(){

        enableAllButtons(false)
        setInformationMessage(R.string.initializing)

        LivePerson.initialize(applicationContext, InitLivePersonProperties(LocalStorage.getAccountID(), LocalStorage.getAppId(), null, object : InitLivePersonCallBack {
            override fun onInitSucceed() {
                setInformationMessage("")
                setAppInfo()
                enableAllButtons(true)
                LPPusher.register()
            }

            override fun onInitFailed(e: Exception) {
                setInformationMessage(R.string.error_initializing)
                Log.e(TAG, "onInitFailed $e" )
            }
        }))
    }

    private fun setInformationMessage(msg: String){
        runOnUiThread {  information_tv.setText(msg) }
    }

    private fun setInformationMessage(resId: Int){
        runOnUiThread {  information_tv.setText(resId) }
    }

    private fun setAppInfo(){
        runOnUiThread {
            tv_sdk.setText(LivePerson.getSDKVersion())
            tv_account.setText(LocalStorage.getAccountID())
            tv_name.setText(LocalStorage.getName())
            app_info.visibility = View.VISIBLE
        }
    }

    private fun enableAllButtons(enable: Boolean){
        enableButton(enable, logout_button)
        enableButton(enable, window_button)
        enableButton(enable, fragment_button)
    }

    private fun enableButton(enable: Boolean, btn: Button) {
        runOnUiThread { btn.setEnabled(enable) }
    }

    private fun logout(){

        enableAllButtons(false)
        setInformationMessage(R.string.loggingout)

        LivePerson.logOut(this, LocalStorage.getAccountID(), LocalStorage.getAppId(), object : LogoutLivePersonCallback {
            override fun onLogoutSucceed() {
                LocalStorage.setJWT("")
                setInformationMessage("")
                goLogin()
            }

            override fun onLogoutFailed() {
                enableAllButtons(true)
            }
        })
    }

    private fun goLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

}

