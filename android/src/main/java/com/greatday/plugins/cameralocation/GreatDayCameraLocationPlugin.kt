package com.greatday.plugins.cameralocation

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import com.senjuid.camera.CameraPlugin
import com.senjuid.camera.CameraPluginListener
import com.senjuid.camera.CameraPluginOptions
import com.senjuid.location.LocationPlugin
import com.senjuid.location.LocationPlugin.LocationPluginListener
import com.senjuid.location.LocationPluginOptions
import org.json.JSONException
import org.json.JSONObject


@CapacitorPlugin(name = "GreatDayCameraLocation", requestCodes = [CameraPlugin.REQUEST])
class GreatDayCameraLocationPlugin : Plugin() {

    private var cameraPlugin: CameraPlugin? = null
    private var cameraListener: CameraPluginListener? = null
    private var locationPlugin: LocationPlugin? = null
    private var locationListener: LocationPluginListener? = null

    @PluginMethod
    fun getLocationCamera(call: PluginCall) {
        val photoName = call.getString("photoName")
        val quality = parseQuality(call.getString("quality"))
        val maxSize = parseMaxSize(call.getString("maxSize"))

        val options: CameraPluginOptions = CameraPluginOptions.Builder()
            .setName(photoName!!)
            .setDisableFacingBack(true)
            .setMaxSize(maxSize)
            .setQuality(quality)
            .build()

        this.getLocationAndTakePhoto(call, options, null, null, null, false)
    }

    @PluginMethod
    fun getLocationCameraSwap(call: PluginCall) {
        val photoName = call.getString("photoName")
        val quality = parseQuality(call.getString("quality"))
        val maxSize = parseMaxSize(call.getString("maxSize"))

        val options: CameraPluginOptions = CameraPluginOptions.Builder()
            .setName(photoName!!)
            .setDisableFacingBack(false)
            .setMaxSize(maxSize)
            .setQuality(quality)
            .build()

        this.getLocationAndTakePhoto(call, options, null, null, null, false)
    }

    @PluginMethod
    fun getLocationRadiusCamera(call: PluginCall) {
        val photoName = call.getString("photoName")
        val quality = parseQuality(call.getString("quality"))
        val maxSize = parseMaxSize(call.getString("maxSize"))
        val location = call.getString("location")
        val label1 = call.getString("label1")
        val label2 = call.getString("label2")
        val showAddress = call.getBoolean("showAddress")

        val options: CameraPluginOptions = CameraPluginOptions.Builder()
            .setName(photoName!!)
            .setDisableFacingBack(true)
            .setMaxSize(maxSize)
            .setQuality(quality)
            .build()

        this.getLocationAndTakePhoto(call, options, location, label1, label2, showAddress)
    }

    @PluginMethod
    fun getLocationRadiusCameraSwap(call: PluginCall) {
        val photoName = call.getString("photoName")
        val quality = parseQuality(call.getString("quality"))
        val maxSize = parseMaxSize(call.getString("maxSize"))
        val location = call.getString("location")
        val label1 = call.getString("label1")
        val label2 = call.getString("label2")

        val options: CameraPluginOptions = CameraPluginOptions.Builder()
            .setName(photoName!!)
            .setDisableFacingBack(false)
            .setMaxSize(maxSize)
            .setQuality(quality)
            .build()

        this.getLocationAndTakePhoto(call, options, location, label1, label2, false)
    }

    @Throws(JSONException::class)
    private fun getLocationAndTakePhoto(
        call: PluginCall,
        cameraOption: CameraPluginOptions,
        location: String?,
        message1: String?,
        message2: String?,
        showAddress: Boolean?
    ) {
        val jsonLocation = JSONObject()
        cameraListener = object : CameraPluginListener {
            override fun onSuccess(photoPath: String, native: Boolean) {
                try {
                    jsonLocation.put("path", photoPath)
                    jsonLocation.put("native", native)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val ret = JSObject()
                ret.put("result", jsonLocation.toString())
                call.resolve(ret)
            }

            override fun onCancel() {
                val ret = JSObject()
                ret.put("result", "cancelled")
                call.resolve(ret)
            }
        }
        locationListener = object : LocationPluginListener {
            override fun onLocationRetrieved(lon: Double, lat: Double, isMock: Boolean) {
                try {
                    var address = ""
                    if (showAddress == true) {
                        address = locationPlugin!!.getCompleteAddress(lat, lon)
                    }
                    jsonLocation.put("latitude", lat.toString())
                    jsonLocation.put("longitude", lon.toString())
                    jsonLocation.put("address", address)
                    jsonLocation.put("mock", isMock)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                cameraPlugin = CameraPlugin(activity)
                cameraPlugin?.setCameraPluginListener(cameraListener)
                startActivityForResult(
                    call,
                    cameraPlugin?.getIntent(cameraOption), "requestCamera"
                )
            }

            override fun onCanceled() {
                val ret = JSObject()
                ret.put("result", "error location")
                call.resolve(ret)
            }
        }
        locationPlugin = LocationPlugin(activity)
        locationPlugin?.setLocationPluginListener(locationListener)
        val options = LocationPluginOptions.Builder()
            .setData(location)
            .setMessage(message1, message2)
            .build()
        val intent: Intent = locationPlugin!!.getIntent(options)
        startActivityForResult(call, intent, "requestLocationWithLanguage")
    }

    @ActivityCallback
    private fun requestCamera(call: PluginCall?, result: ActivityResult) {
        if (call == null) {
            return
        }
        if (result.resultCode == Activity.RESULT_OK) {
            val performNativeCamera = result.data?.getBooleanExtra("native", false)
            cameraListener?.let {
                val photoPath = result.data?.getStringExtra("photo")
                if (performNativeCamera!!) {
                    it.onSuccess("", true)
                } else {
                    if (photoPath != null) {
                        it.onSuccess(photoPath, false)
                    } else {
                        it.onCancel()
                    }
                }
            }
        } else {
            cameraListener?.onCancel()
        }
    }

    @ActivityCallback
    private fun requestLocationWithLanguage(call: PluginCall?, result: ActivityResult) {
        if (call == null) {
            return
        }

        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            if (locationListener != null) {
                if (data != null) {
                    val lon: Double = data.getDoubleExtra("lon", 0.0)
                    val lat: Double = data.getDoubleExtra("lat", 0.0)
                    val isMock: Boolean = data.getBooleanExtra("isMock", false)
                    locationListener?.onLocationRetrieved(lon, lat, isMock)
                } else {
                    locationListener?.onCanceled()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (locationListener != null) {
                locationListener?.onCanceled()
            }
        }
    }

    private fun parseQuality(qualityStr: String?): Int {
        return if (qualityStr != null && qualityStr.isNotEmpty()) {
            qualityStr.toInt()
        } else 100
    }

    private fun parseMaxSize(maxSizeStr: String?): Int {
        return if (maxSizeStr != null && maxSizeStr.isNotEmpty()) {
            maxSizeStr.toInt()
        } else 1024
    }
}