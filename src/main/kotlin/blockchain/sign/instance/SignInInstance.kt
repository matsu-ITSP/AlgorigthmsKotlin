package blockchain.sign.instance

import blockchain.sign.Sign
import blockchain.sign.Signature
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class SignInInstance: Sign {
    override fun sign(raw: String, privateKey: RSAPrivateKey): Signature {
        // TODO ByteArray のラッパーで署名の意味の Signature と java.security.Signature が重複して最悪
        val signer = java.security.Signature.getInstance("SHA256withRSA")
        signer.initSign(privateKey)
        signer.update(raw.toByteArray())
        return Signature(signer.sign())
    }

    override fun verify(raw: String, signature: Signature, publicKey: RSAPublicKey): Boolean {
        val verifier = java.security.Signature.getInstance("SHA256withRSA")
        verifier.initVerify(publicKey)
        verifier.update(raw.toByteArray())
        return verifier.verify(signature.signature)
    }
}