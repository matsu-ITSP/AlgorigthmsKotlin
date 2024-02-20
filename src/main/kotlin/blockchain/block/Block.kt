package blockchain.block

import blockchain.Properties
import blockchain.sign.Signature
import java.sql.Timestamp
import java.time.Instant

data class Block(
    val parent: Block?,
    val timestamp: Instant = Instant.now(),
    val contents: String,
    val signature: Signature,
    val recreateNum: Int = 0
) {
    val treeLength: Int = (parent?.treeLength ?: 0) + 1
    val hash: BlockHash = BlockHash(this)

    override fun toString(): String {
        return hash.hash.hashString()
    }

    private fun String.hashString(): String =
        this
            .take(Properties.BLOCK_OUTPUT_LENGTH - 1)
            .padEnd(Properties.BLOCK_OUTPUT_LENGTH - 1, ' ')
            .plus(".")

    fun information(): String =
        "parent: ${parent?.toString() ?: ""}, timestamp: $timestamp, contents: $contents, signature: $signature, recreateNum: $recreateNum, treeLength: $treeLength"
}
