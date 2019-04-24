package com.naldana.ejemplo11

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        var correo = sharedPref.getString(getString(R.string.save_email_key), "")
        tv_data.text = correo

        bt_save.setOnClickListener {

            with(sharedPref.edit()) {
                putString(getString(R.string.save_email_key), et_option.text.toString())
                commit()

            }


            tv_data.text = et_option.text.toString() // Solamente para mostrar el valor de inmediato
        }

        bt_write_external.setOnClickListener{
            correo = sharedPref.getString(getString(R.string.save_email_key), "")
            val nombreArchivo= "ok.txt"
            try{
                var memoriasd= Environment.getExternalStorageDirectory()
                var rutaSD= File(memoriasd.path, nombreArchivo);
                var crear = OutputStreamWriter(FileOutputStream(rutaSD));
                crear.write(correo);
                crear.flush();
                crear.close();
            }catch(e: FileNotFoundException){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }



        bt_write_internal.setOnClickListener {
            correo = sharedPref.getString(getString(R.string.save_email_key), "")
            val nombreArchivo = "email.txt"
            val fileContent = "email: $correo"


            openFileOutput(nombreArchivo,Context.MODE_PRIVATE).use {
                it.write(fileContent.toByteArray())
            }

        }


        bt_read_internal.setOnClickListener{

            val nombreArchivo = "email.txt"
            openFileInput(nombreArchivo).use {
                val text = it.bufferedReader().readText()
                tv_data.text = text
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            montarRuta()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                montarRuta()
            }
        }
    }

    private fun montarRuta() {
        val direccion = "${Environment.getExternalStorageDirectory()}/$packageName"
        File(direccion).mkdirs()
        val file = "%1\$tY%1\$tm%1\$td%1\$tH%1\$tM%1\$tS.log".format(Date())
        File("$direccion/$file").printWriter().use {
            it.println("text")
        }
    }
}
