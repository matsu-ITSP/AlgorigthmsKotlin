package blockchain.sign.instance

import blockchain.sign.Sign
import blockchain.sign.Signature
import org.junit.Assert.*
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.test.Test

class SignInInstanceTest{
    val sign: Sign = SignInInstance()
    val publicKey: RSAPublicKey
    val privateKey: RSAPrivateKey
    init{
        val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val keyPair = generator.generateKeyPair()
        publicKey = keyPair.public as RSAPublicKey
        privateKey = keyPair.private as RSAPrivateKey
    }
    // 署名後に検証してtrueとなる
    @Test
    fun testValid(){
        val message = "message"
        val signature = sign.sign(message, privateKey)
        assertEquals(true, sign.verify(message, signature, publicKey))
    }
    // あるメッセージと別のメッセージの署名をつけると、false となる
    @Test
    fun anotherMessageSign(){
        val message = "message"
        val faultMessage = "faultMessage"
        val faultSignature = sign.sign(faultMessage, privateKey)
        assertEquals(false, sign.verify(message, faultSignature, publicKey))
    }
    // 公開鍵を持つ人と別の人の署名をつけると false となる
    @Test
    fun anotherUserSign(){
        val message = "message"
        val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val keyPair = generator.generateKeyPair()
        val faultPrivateKey = keyPair.private as RSAPrivateKey
        val faultSignature = sign.sign(message, faultPrivateKey)
        assertEquals(false, sign.verify(message, faultSignature, publicKey))
    }
    // 署名後に改ざんして検証するとfalseとなる
    @Test
    fun editedMessage(){
        val message = "message"
        val faultMessage = "messagexxx"
        val faultSignature = Signature(sign.sign(message, privateKey).signature.dropLast(3).toByteArray() + "xxx".toByteArray())
        assertEquals(false, sign.verify(faultMessage, faultSignature, publicKey))
    }
}