package com.example.socialmedia.data

import android.content.Context
import java.io.IOException

class CreateReadJSON {
    companion object{
        fun getJSONDataFromAsset(context: Context, fileName : String) : String?{
            val jsonString : String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            }catch (ex : IOException){
                ex.printStackTrace()
                return null
            }
            return jsonString
        }
    }
}