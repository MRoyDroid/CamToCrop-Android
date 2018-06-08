package com.mithuroy.camtocrop.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Mithu on June'08 2018
 */

fun generateRandomNumber(): String {
    val instance: MessageDigest?
    var hexString: StringBuilder? = null
    try {
        instance = MessageDigest.getInstance("MD5")
        val messageDigest = instance!!.digest(System.nanoTime().toString().toByteArray())
        hexString = StringBuilder()
        for (aMessageDigest in messageDigest) {
            val hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return hexString!!.toString()
}