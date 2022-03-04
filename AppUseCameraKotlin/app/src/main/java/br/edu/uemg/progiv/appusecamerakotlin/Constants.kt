package br.edu.uemg.progiv.appusecamerakotlin

import android.Manifest

object Constants {

    const val TAG = "cameraX"
    const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-SSS"
    const val REQUEST_CODE_PERMISSIONS = 123
    val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)

}