package com.calleja.jesus.moneymanager

import android.app.Activity
import android.widget.Toast

fun Activity.toast(mensaje: CharSequence, duracion: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, mensaje, duracion).show()
fun Activity.toast(resourceId: Int, duracion: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resourceId, duracion).show()
