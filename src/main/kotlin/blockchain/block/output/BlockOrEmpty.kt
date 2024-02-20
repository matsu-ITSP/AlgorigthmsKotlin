package blockchain.block.output

import blockchain.block.Block

sealed class BlockOrEmpty{
    data class WrappedBlock(
        val block: Block
    ): BlockOrEmpty() {
        override fun toString(): String = block.toString()
    }
    object Empty: BlockOrEmpty()

    override fun toString(): String {
        return when(this){
            is WrappedBlock -> block.toString()
            is Empty -> "          "
        }
    }

    fun getBlockOrNull(): Block? = when(this){
        is WrappedBlock -> block
        is Empty -> null
    }

    companion object{
        fun createEmptyBlocks(num: Int): List<BlockOrEmpty> = (0 until num).map {
            Empty
        }
    }
}

