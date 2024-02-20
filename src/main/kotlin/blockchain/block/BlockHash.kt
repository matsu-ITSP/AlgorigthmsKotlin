package blockchain.block

import java.security.MessageDigest

data class BlockHash(val block: Block) {
    val hash: String = hash(block.information()) // 型は不明

    companion object {
        private fun hash(raw: String): String =
            MessageDigest.getInstance("SHA-256")
                .digest(raw.toByteArray())
                .joinToString(separator = "") {
                    "%02x".format(it)
                }
    }
}
