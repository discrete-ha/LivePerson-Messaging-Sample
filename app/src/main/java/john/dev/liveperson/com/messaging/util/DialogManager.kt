package john.dev.liveperson.com.messaging.util

import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.Toast

class DialogManager{

    companion object {
        fun Alert(context: Context, msg:String){
            val dialog = AlertDialog.Builder(context).setMessage(msg)
                    .setPositiveButton("Confirm", { dialog, i ->
                        Toast.makeText(context, "Hello Friends", Toast.LENGTH_LONG).show()
                    })


            dialog.show()
        }
    }
}