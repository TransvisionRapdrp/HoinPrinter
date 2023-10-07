package com.example.hoinprinter

import android.R.attr.mode
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hoinprinter.databinding.ActivityMainBinding
import com.example.hoinprinterlib.HoinPrinter
import com.example.hoinprinterlib.module.PrinterCallback
import com.example.hoinprinterlib.module.PrinterEvent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), PrinterCallback {

    val BT_STATE_DISCONNECTED = 0; //Bluetooth disconnected
    val BT_STATE_LISTEN = 1; //Bluetooth is listening
    val BT_STATE_CONNECTING = 2; //Bluetooth connecting
    val BT_STATE_CONNECTED = 3; //Bluetooth connected
    val TAG = "MainActivity"
    lateinit var  macAddress : String
    private lateinit var activityMainBinding : ActivityMainBinding
    var bluetoothDevice : BluetoothDevice? = null
    val EVENT_FIND_BT_DEVICE = 1;

    lateinit var  mHoinPrinter : HoinPrinter
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());



        mHoinPrinter = HoinPrinter.getInstance(applicationContext, 1, this)

        setContentView(activityMainBinding.root)

        val bAdapter = BluetoothAdapter.getDefaultAdapter()

        val pairedDevices = bAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {

                // get the device name
                val deviceName = device.name

                // get the mac address
                macAddress = device.address
                if (deviceName == "BlueTooth Printer"){
                    bluetoothDevice = device
                    Log.d(TAG,device.name + " " + device.address)
                    break
                }

                // append in the two separate views
            }
        }
        val filePath = "image.png" // Replace with your desired file path

        val cacheDir = applicationContext.cacheDir
        val file = File(cacheDir, filePath)

        val text = "Hello, Image!"
        val generatedBitmap = generateImageFromText(text)

        saveImageToCache(generatedBitmap, applicationContext,file)

        activityMainBinding.btnSend.setOnClickListener {
            mHoinPrinter.printText("TVD Printer",true,true,true,true)
        }

        activityMainBinding.btnConnect.setOnClickListener {
            mHoinPrinter.connect(macAddress)
        }

        activityMainBinding.btnQRCode.setOnClickListener {
            mHoinPrinter.printQRCode("Vivek")
        }
        Log.d(TAG,file.path)

        mHoinPrinter.switchType(true)
        activityMainBinding.btnImage.setOnClickListener {

            mHoinPrinter.printImage(file.path,true)
        }

    }

    private fun saveImageToCache(bitmap: Bitmap, context: Context, file: File): File? {
        return try {


            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            outputStream.close()

            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun generateImageFromText(text: String): Bitmap {
        val width = 200 // Set the width of the image
        val height = 100 // Set the height of the image
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a background color (optional)
        canvas.drawColor(Color.WHITE)

        // Create a Paint object for the text
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 20f

        // Calculate text position
        val x = 20f
        val y = 40f

        // Draw the text on the canvas
        canvas.drawText(text, x, y, paint)

        return bitmap
    }


    override fun onState(p0: Int) {
        Log.d(TAG,"onState $p0")
        when(p0){
            BT_STATE_DISCONNECTED -> Toast.makeText(applicationContext,"Disconnected",Toast.LENGTH_LONG).show()
            BT_STATE_LISTEN -> Toast.makeText(applicationContext,"BT_STATE_LISTEN",Toast.LENGTH_LONG).show()
            BT_STATE_CONNECTING -> Toast.makeText(applicationContext,"BT_STATE_CONNECTING",Toast.LENGTH_LONG).show()
            BT_STATE_CONNECTED -> Toast.makeText(applicationContext,"BT_STATE_CONNECTED",Toast.LENGTH_LONG).show()
        }
    }

    override fun onError(p0: Int) {

    }

    override fun onEvent(p0: PrinterEvent?) {
        Toast.makeText(applicationContext,"Event $p0",Toast.LENGTH_LONG).show()
    }
}