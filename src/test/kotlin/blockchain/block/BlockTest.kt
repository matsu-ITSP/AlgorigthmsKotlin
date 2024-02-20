package blockchain.block

import blockchain.sign.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockTest {

    @Test
    fun test(){
        // ひとつめ、親なし
        val block1 = Block(
            parent = null,
            contents = "1",
            signature =
             Signature("1".toByteArray()),
        )
        assertEquals(null, block1.parent)
        assertEquals("1", block1.contents)
        assertEquals(Signature("1".toByteArray()), block1.signature)
        assertEquals(0, block1.recreateNum)
        assertEquals(1, block1.treeLength)
        assertEquals(
            BlockHash(block1),
            block1.hash
        )
        // ふたつめ、親あり
        val block2 = Block(
            parent = block1,
            contents = "2",
            signature = Signature("2".toByteArray()),
        )
        assertEquals(block1, block2.parent)
        assertEquals("2", block2.contents)
        assertEquals(Signature("2".toByteArray()), block2.signature)
        assertEquals(0, block2.recreateNum)
        assertEquals(2, block2.treeLength)
        assertEquals(
            BlockHash(block2),
            block2.hash
        )
    }
}