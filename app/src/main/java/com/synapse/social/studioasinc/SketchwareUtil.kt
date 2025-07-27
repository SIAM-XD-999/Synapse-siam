package com.synapse.social.studioasinc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object SketchwareUtil {

    fun sortListMap(listMap: ArrayList<HashMap<String, Any>>, key: String, isNumber: Boolean, ascending: Boolean) {
        Collections.sort(listMap, Comparator { _compareMap1, _compareMap2 ->
            try {
                if (isNumber) {
                    val _count1 = _compareMap1[key].toString().toInt()
                    val _count2 = _compareMap2[key].toString().toInt()
                    if (ascending) Integer.compare(_count1, _count2) else Integer.compare(_count2, _count1)
                } else {
                    if (ascending) _compareMap1[key].toString().compareTo(_compareMap2[key].toString())
                    else _compareMap2[key].toString().compareTo(_compareMap1[key].toString())
                }
            } catch (e: Exception) {
                0
            }
        })
    }

    fun isConnected(context: Context): Boolean {
        val _connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = _connectivityManager.activeNetwork
        if (activeNetwork != null) {
            val networkCapabilities: NetworkCapabilities? = _connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }

    fun copyFromInputStream(inputStream: InputStream): String? {
        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var i: Int
        try {
            while (inputStream.read(buf).also { i = it } != -1) {
                outputStream.write(buf, 0, i)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            return null
        }
        return outputStream.toString()
    }

    fun hideKeyboard(context: Context, view: View?) {
        if (view != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(context: Context, view: View?) {
        if (view != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun showMessage(context: Context, s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    fun getLocationX(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[0]
    }

    fun getLocationY(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[1]
    }

    fun getRandom(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }

    fun getCheckedItemPositionsToArray(list: ListView): ArrayList<Double> {
        val result = ArrayList<Double>()
        for (i in 0 until list.count) {
            if (list.isItemChecked(i)) {
                result.add(i.toDouble())
            }
        }
        return result
    }

    fun getDip(context: Context, input: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, input.toFloat(), context.resources.displayMetrics)
    }

    fun getDisplayWidthPixels(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getDisplayHeightPixels(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getAllKeysFromMap(map: Map<String, Any>?, output: ArrayList<String>?) {
        if (output == null) return
        output.clear()
        if (map == null || map.isEmpty()) return
        output.addAll(map.keys)
    }

    fun convertDpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }
}