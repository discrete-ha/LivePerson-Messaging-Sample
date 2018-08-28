package john.dev.liveperson.com.messaging

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.liveperson.japan.sample.Utils.LocalStorage
import john.dev.liveperson.com.messaging.authentication.TokenTool
import john.dev.liveperson.com.messaging.notification.LPPusher
import john.dev.liveperson.com.messaging.util.DialogManager
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (LocalStorage.getName() != null && LocalStorage.getExpire() != null){
            user_name.setText(LocalStorage.getName())
            expire.setText(LocalStorage.getExpire())
        }

        login_button.setOnClickListener { attemptLogin() }
        context = this
    }

    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        user_name.error = null
        expire.error = null

        val userNameStr = user_name.text.toString()
        val expireStr = expire.text.toString()

        LocalStorage.setName(userNameStr);
        LocalStorage.setExpire(expireStr);

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(expireStr) && !isExpireValid(expireStr)) {
            expire.error = getString(R.string.error_invalid_expire)
            focusView = expire
            cancel = true
        }

        if (TextUtils.isEmpty(userNameStr)) {
            user_name.error = getString(R.string.error_field_required)
            focusView = user_name
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            hideKeyboard()
            showProgress(true)
            mAuthTask = UserLoginTask(userNameStr, expireStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun hideKeyboard(){
        if(currentFocus != null){
            val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }


    private fun isExpireValid(expire: String): Boolean {
        return  expire.toInt() > 0
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    inner class UserLoginTask internal constructor(private val mUserName: String, private val mExpire: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {

            if (mUserName == "" || mExpire == "" || mUserName == null || mExpire == null){
                return false
            }

            TokenTool.getJWT(mUserName, mExpire,this@LoginActivity)
            return true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            if (success!! && TokenTool.isJWTValid()) {
                Log.d(TAG,getString(R.string.msg_login_success))
                goApp()
            } else {
                showProgress(false)
                expire.requestFocus()
                DialogManager.Alert(context, getString(R.string.error_login_failed))
            }

        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    private fun goApp(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun checkUserLoggedIn(){
        var expire = LocalStorage.getExpire()
        var name = LocalStorage.getName()

        if (expire != "" && name != "" && expire != null && name != null && TokenTool.isJWTValid()){
            goApp()
        }
    }

    override fun onResume(){
        super.onResume()
        showProgress(false)
        checkUserLoggedIn()
        Log.d(TAG, "onResume")
    }

    companion object {
        val TAG = LoginActivity::class.java.simpleName
        lateinit var context: LoginActivity
    }
}
