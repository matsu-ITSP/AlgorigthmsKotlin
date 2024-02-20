package blockchain.sign

data class Signature (val signature: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Signature

        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        return signature.contentHashCode()
    }
}