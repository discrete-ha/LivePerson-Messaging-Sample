package john.dev.liveperson.com.messaging

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment
import com.liveperson.messaging.sdk.api.LivePerson
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import com.liveperson.infra.ConversationViewParams
import com.liveperson.infra.LPAuthenticationParams
import com.liveperson.japan.sample.Utils.LocalStorage
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {

    private var mConversationFragment: ConversationFragment? = null
    private val LIVEPERSON_FRAGMENT = "liveperson_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        runOnUiThread { initFragment() }
        close_conversation_btn.setOnClickListener { closeConversationView() }

    }

    override fun onResume() {
        super.onResume()
        mConversationFragment?.let {
            attachFragment()
        }
    }

    private fun closeConversationView(){
        finish()
    }

    private fun initFragment() {
        var livePersonFragment = supportFragmentManager.findFragmentByTag(LIVEPERSON_FRAGMENT)
        Log.d(TAG, "initFragment. mConversationFragment = $mConversationFragment")
        if (livePersonFragment == null) {
            val authParams = LPAuthenticationParams(LPAuthenticationParams.LPAuthenticationType.AUTH)
            authParams.hostAppJWT = LocalStorage.getJWT()
            val params = ConversationViewParams(false)
            mConversationFragment = LivePerson.getConversationFragment(authParams, params) as ConversationFragment

            if (isValidState()) {
                val notificationIntent = Intent(this, ConversationActivity::class.java)
                notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
                LivePerson.setImageServicePendingIntent(pendingIntent)
                val ft = supportFragmentManager.beginTransaction()
                ft.add(R.id.fragment_frame, mConversationFragment, LIVEPERSON_FRAGMENT).commitAllowingStateLoss()
            }
        } else {
            attachFragment()
        }
    }

    private fun attachFragment() {
        if (mConversationFragment!!.isDetached()) {
            Log.d(TAG, "attachFragment")
            if (isValidState()) {
                val ft = supportFragmentManager.beginTransaction()
                ft.attach(mConversationFragment).commitAllowingStateLoss()
            }
        }
    }

    private fun isValidState(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !isFinishing && !isDestroyed
        } else {
            !isFinishing
        }
    }

    companion object {
        val TAG = ConversationActivity::class.java.simpleName
    }
}
