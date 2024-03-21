package gg.norisk.subwaysurfers.utils

import java.math.BigInteger
import java.security.MessageDigest

object HashUtils {
    private val md5 = MessageDigest.getInstance("MD5")

    fun md5(input: String): String {
        return md5(input.toByteArray())
    }

    fun md5(input: ByteArray): String {
        return BigInteger(1, md5.digest(input)).toString(16).padStart(32, '0')
    }
}
