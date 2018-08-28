package com.liveperson.japan.sample.Utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import john.dev.liveperson.com.messaging.MainApplication

class LocalStorage private constructor(context: Context) {

    private val mDefaultSharedPreferences: SharedPreferences

    var name: String
        get() = mDefaultSharedPreferences.getString(NAME, "")
        set(firstName) = mDefaultSharedPreferences.edit().putString(NAME, firstName).apply()

    var jwt: String
        get() = mDefaultSharedPreferences.getString(JWT, "")
        set(jwt) = mDefaultSharedPreferences.edit().putString(JWT, jwt).apply()

    var expire: String
        get() = mDefaultSharedPreferences.getString(EXPIRE, "")
        set(expire) = mDefaultSharedPreferences.edit().putString(EXPIRE, expire).apply()

    fun clearAll(){

    }

    init {
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    companion object {

        private var context = MainApplication.getContext()
        private val NAME = "name"
        private val JWT = "jwt"
        private val EXPIRE = "expire"
        private val ACCOUNT = "47740947";
        private val APPID = "john.dev.liveperson.com.messaging"

        fun getAccountID(): String {
            return ACCOUNT
        }

        fun getAppId(): String {
            return APPID
        }

        fun getJWT(): String {
            return LocalStorage(context).jwt
        }

        fun setJWT(jwt:String) {
            LocalStorage(context).jwt = jwt
        }

        fun getName(): String {
            return LocalStorage(context).name
        }

        fun setName(name:String) {
            LocalStorage(context).name = name
        }

        fun getExpire(): String {
            return LocalStorage(context).expire
        }

        fun setExpire(name:String) {
            LocalStorage(context).expire = name
        }

        fun cleanAll(){
            setJWT("")
            setName("")
            setExpire("")
        }
    }
}
