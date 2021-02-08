package com.nabilmh.compareecncryptsrings

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nabilmh.compareecncryptsrings.AesCbcWithIntegrity.CipherTextIvMac
import com.nabilmh.compareecncryptsrings.AesCbcWithIntegrity.generateKeyFromPassword
import com.nabilmh.compareecncryptsrings.AesCbcWithIntegrity.generateSalt
import com.nabilmh.compareecncryptsrings.AesCbcWithIntegrity.saltString
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class MainActivity : AppCompatActivity() {

    companion object {
//        private const val PLAY_TEST_TEXT = "Some Text Line !"
        private const val PLAY_TEST_TEXT = "আল্লাহ তা'আলা বলেন, “মানুষের মধ্যে এমন ব্যক্তি আছে, পার্থিব জীবন সম্পর্কে যার কথাবার্তা তােমাকে চমৎকৃত করে এবং তার অন্তরে যা আছে সে সম্মন্ধে সে আল্লাহকে সাক্ষী রাখে, প্রকৃতপক্ষে সে ভীষণ কলহপ্রিয়” (সূরাতুল বাকারাহ : ২০৪) এই আয়াতে যার কথা বলা হয়েছে বা এই আয়াতটি যে ব্যক্তির প্রসঙ্গে অবতীর্ণ হয়েছে সে কে? "
        private const val REPS = 10000

        private val ISO = charset("ISO-8859-1") // is used because we can get our bytes from string back without any problems
        private val UTF = charset("UTF-8")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val handler = Handler()
        handler.postDelayed({
            control()
            testBase64()
            testXor()
            testShifting()

            testFastestCipher("Blowfish/ECB/PKCS5Padding", "Blowfish", false)
            testFastestCipher("Blowfish/CBC/PKCS5Padding", "Blowfish", true)
            testFastestCipher("Blowfish/OFB/PKCS5Padding", "Blowfish", true)
            testFastestCipher("Blowfish/CTR/PKCS5Padding", "Blowfish", true)
            testFastestCipher("Blowfish/CTR/PKCS5Padding", "Blowfish", true)
            testFastestCipher("Blowfish/CFB/PKCS5Padding", "Blowfish", true)

            if (Build.VERSION.SDK_INT >= 28) {
                testFastestCipher("ChaCha20/None/NoPadding", "ChaCha20", true)
            }
            testAesCbc64()

            testFastestCipher("AES/ECB/PKCS5Padding", "AES", false)
            testFastestCipher("AES/CBC/PKCS5Padding", "AES", true)
            testFastestCipher("AES/CFB/PKCS5Padding", "AES", true)
            testFastestCipher("AES/OFB/PKCS5Padding", "AES", true)
            testFastestCipher("AES/CTR/PKCS5Padding", "AES", true)


        }, 200)
    }

    private fun testShifting() {
        var encMsg = ""
        var decMsg = ""

        var timeTook = System.currentTimeMillis()

        for (i in 0..REPS) {
            encMsg = PLAY_TEST_TEXT.mapIndexed { index, c ->
              c + 100
            }.joinToString(separator = "")
        }
        timeTook = System.currentTimeMillis() - timeTook

        var timeTook1 = System.currentTimeMillis()

        for (i in 0..REPS) {
            decMsg = encMsg.mapIndexed { index, c ->
                c - 100
            }.joinToString(separator = "")
        }

        timeTook1 = System.currentTimeMillis() - timeTook1

        val textView = findViewById<TextView>(R.id.textView)

        textView.text = "${textView.text}\n\nShifting\nEnc (bytes displayed as ISO-8859-1): $encMsg\n$timeTook ms\nDec: $decMsg\n$timeTook1 ms"
    }

    private fun testXor() {
        var encMsg = ""
        var decMsg = ""

        val xorKey = "P"

        var timeTook = System.currentTimeMillis()

        for (i in 0..REPS) {
            encMsg = PLAY_TEST_TEXT.xor(xorKey)
        }
        timeTook = System.currentTimeMillis() - timeTook

        var timeTook1 = System.currentTimeMillis()

        for (i in 0..REPS) {
            decMsg = encMsg.xor(xorKey)
        }

        timeTook1 = System.currentTimeMillis() - timeTook1

        val textView = findViewById<TextView>(R.id.textView)

        textView.text = "${textView.text}\n\nXOR\nEnc (bytes displayed as ISO-8859-1): $encMsg\n$timeTook ms\nDec: $decMsg\n$timeTook1 ms"
    }

    private fun testBase64() {
        var encMsg = ""
        var decMsg = ""

        var timeTook = System.currentTimeMillis()

        for (i in 0..REPS) {
            encMsg = Base64.encodeToString(PLAY_TEST_TEXT.encodeToByteArray(), Base64.DEFAULT)
        }
        timeTook = System.currentTimeMillis() - timeTook

        var timeTook1 = System.currentTimeMillis()

        for (i in 0..REPS) {
            decMsg = String(Base64.decode(encMsg, Base64.DEFAULT))
        }

        timeTook1 = System.currentTimeMillis() - timeTook1

        val textView = findViewById<TextView>(R.id.textView)

        textView.text = "${textView.text}\n\nbase64\nEnc (bytes displayed as ISO-8859-1): $encMsg\n$timeTook ms\nDec: $decMsg\n$timeTook1 ms"
    }

    private fun testAesCbc64() {
        var encMsg = ""
        var decMsg = ""

        var timeTook = System.currentTimeMillis()
        var EXAMPLE_PASSWORD = "12345678"
            var salt = saltString(generateSalt());
        // You can store the salt, it's not secret. Don't store the key. Derive from password every time
        var keys = generateKeyFromPassword(EXAMPLE_PASSWORD, salt)

        for (i in 0..REPS) {
            encMsg = AesCbcWithIntegrity.encrypt("some test", keys).toString()
        }
        timeTook = System.currentTimeMillis() - timeTook

        var timeTook1 = System.currentTimeMillis()

        for (i in 0..REPS) {
            val cipherTextIvMac = CipherTextIvMac(encMsg)
            decMsg = AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys)
        }

        timeTook1 = System.currentTimeMillis() - timeTook1

        val textView = findViewById<TextView>(R.id.textView)

        textView.text = "${textView.text}\n\nAESCBCINTEGRITY\nEnc (bytes displayed as ISO-8859-1): $encMsg\n$timeTook ms\nDec: $decMsg\n$timeTook1 ms"
    }

    private fun control() {
        var encMsg = ""
        var decMsg = ""

        var timeTook = System.currentTimeMillis()

        for (i in 0..REPS) {
            encMsg = PLAY_TEST_TEXT
            decMsg = encMsg
        }

        timeTook = System.currentTimeMillis() - timeTook
        val textView = findViewById<TextView>(R.id.textView)

        textView.text = "${textView.text}\n\nControl\nEnc (bytes displayed as ISO-8859-1): $encMsg\nDec: $decMsg\n$timeTook ms"
    }


    private fun testFastestCipher(transformation: String, keyAlgorithm: String, requiresIV: Boolean) {

        val cipherEnc = Cipher.getInstance(transformation)
        val cipherDec = Cipher.getInstance(transformation)

        val secretKeyString = when (keyAlgorithm) {
            "ChaCha20" -> "aNdRgUkXp2s5v8y/B?E(G+KbPeShVmYq"
            else -> "C&E)H@McQfTjWnZr"
        }
        val secretKey = SecretKeySpec(secretKeyString.toByteArray(), keyAlgorithm)

        if (requiresIV) {
            val ivKey = when (keyAlgorithm) {
                "Blowfish" -> "12345678"
                "ChaCha20" -> "123456789123"
                else -> "1234567891230000"
            }
            val iv = IvParameterSpec(ivKey.toByteArray())
            cipherEnc.init(Cipher.ENCRYPT_MODE, secretKey, iv)
            cipherDec.init(Cipher.DECRYPT_MODE, secretKey, iv)
        } else {
            cipherEnc.init(Cipher.ENCRYPT_MODE, secretKey)
            cipherDec.init(Cipher.DECRYPT_MODE, secretKey)
        }

        var encMsg = ""
        var decMsg = ""

        var timeTook = System.currentTimeMillis()

        for (i in 0..REPS) {
            encMsg = encryptMsg(PLAY_TEST_TEXT, cipherEnc)
        }

        var timeTook1 = System.currentTimeMillis()

        for (i in 0..REPS) {
            decMsg = decryptMsg(encMsg, cipherDec)
        }

        timeTook1 = System.currentTimeMillis() - timeTook1

        timeTook = System.currentTimeMillis() - timeTook
        val textView = findViewById<TextView>(R.id.textView)

        textView.text = "${textView.text}\n\n$transformation\nEnc (bytes displayed as ISO-8859-1): $encMsg\n$timeTook ms\nDec: $decMsg\n$timeTook1 ms"
    }

    private fun encryptMsg(message: String, cipher: Cipher): String {
        val bytes = cipher.doFinal(message.toByteArray(UTF))
        return String(bytes, ISO)
    }

    private fun decryptMsg(cipherText: String, cipher: Cipher): String {
        val encryptedString = cipherText.toByteArray(ISO)
        return String(cipher.doFinal(encryptedString), UTF)
    }

    infix fun String.xor(that: String) = mapIndexed { index, c ->
        that.toCharArray().first().toInt().xor(c.toInt())
    }.joinToString(separator = "") {
        it.toChar().toString()
    }
}