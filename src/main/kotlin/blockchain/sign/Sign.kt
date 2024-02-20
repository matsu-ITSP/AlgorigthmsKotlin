package blockchain.sign

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

// TODO rawと検証戻り値の型
interface Sign {
    fun sign(raw: String, privateKey: RSAPrivateKey): Signature
    fun verify(raw: String, signature: Signature, publicKey: RSAPublicKey): Boolean
}