package john.dev.liveperson.com.messaging.authentication

import android.content.Context
import com.liveperson.japan.sample.Utils.LocalStorage
import john.dev.liveperson.com.messaging.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TokenTool{

    companion object {

        fun getJWT(name : String, expire: String, context: Context){
            val host = context.getString(R.string.authentication_host)
            val url = "$host/$name/$expire"

            val obj = URL(url)

            with(obj.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                BufferedReader(InputStreamReader(inputStream)).use {
                    try {
                        val response = StringBuffer()
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        var jsonString = response.toString()
                        val jsonObj = JSONObject(jsonString)
                        var jwt = jsonObj.getString("token")
                        println("got jwt - $jwt" )
                        LocalStorage.setJWT(jwt)
                    }catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun isJWTValid(): Boolean {
            var jwt = LocalStorage.getJWT()
            var segments = jwt.split('.')
            if (segments.count() != 3)
                return false

            if(jwt == "" || jwt == null)
                return false

            return true
        }
    }
}
