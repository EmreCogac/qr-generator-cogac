package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var Qr : ImageView

    private lateinit var  QrText : EditText

    private lateinit var QrButton : Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var downloadButton : Button

    private lateinit var iletisim : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Qr = findViewById(R.id.qr)
        QrText= findViewById(R.id.QrText)
        QrButton= findViewById(R.id.QrOlustur)
        downloadButton = findViewById(R.id.Qrindir)
        iletisim = findViewById(R.id.iletisim)


        iletisim.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/emre-aktas-9176a31a6/?originalSubdomain=tr"))
            startActivity(browserIntent)
        }






        downloadButton.isVisible = false
        QrButton.setOnClickListener{
            val qrCodeText = QrText.text.toString().trim()
            if(qrCodeText.isNotEmpty())
            {
                val qrCodeSize = 200
                val qrCodeBitmap = generateQRCode(qrCodeText, qrCodeSize, qrCodeSize)

                Qr.setImageBitmap(qrCodeBitmap)
                downloadButton.isVisible = true

                downloadButton.setOnClickListener {
                    var icerik = "Hadi sen de QR Üretici ile tamamen bedavaya işletmen veya özel paylaşımlar için qr üret!"
                        shareQRCode(qrCodeBitmap,icerik)
                    }

            }
            else{
                Qr.setImageResource(R.mipmap.qrdeneme_round)
                Toast.makeText(this, "Lütfen kutucuğu doldurunuz içi boş olamaz", Toast.LENGTH_SHORT).show()
                downloadButton.isVisible = false
            }
        }



       }
    private fun generateQRCode(text: String, width: Int, height: Int): Bitmap? {
        try {
            val bitMatrix: BitMatrix = QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height,
                null
            )
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.parseColor("#D4ADFC"))
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
    private fun shareQRCode(bitmap: Bitmap?, text: String) {
        val path = File(cacheDir, "qr_code.png")
        try {
            FileOutputStream(path).use { outputStream ->
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }

            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", path)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text) // Metni paylaşmak için Intent'e ekleyin
            }
            startActivity(Intent.createChooser(shareIntent, "Paylaş"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }




}









