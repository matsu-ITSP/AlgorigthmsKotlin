package blockchain.block

import blockchain.Properties
import blockchain.block.output.BlockOrEmpty
import blockchain.sign.Signature
import org.junit.Assert.*
import kotlin.test.Test

class TreeTest {

    @Test
    fun testMaxLength() {
        // init
        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "11",
            signature = Signature("11".toByteArray())
        )
        val tree = Tree(mutableListOf(block1, block11))
        assertEquals(tree.getMaxLength(), 2)
        // 更新時
        val block111 = Block(
            parent = block11,
            contents = "111",
            signature = Signature("111".toByteArray())
        )
        // 非更新時
        tree.add(block111)
        assertEquals(tree.getMaxLength(), 3)

        // 更新時
        val block112 = Block(
            parent = block11,
            contents = "112",
            signature = Signature("112".toByteArray())
        )
        tree.add(block112)
        assertEquals(tree.getMaxLength(), 3)
    }

    @Test
    fun testFindByHash() {
        // init
        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "11",
            signature = Signature("11".toByteArray())
        )
        val tree = Tree(mutableListOf(block1, block11))

        // TODO 最後のものであることはどう検証する？ ハッシュなので絶対違う値にならない？
        assertEquals(block1, tree.findByHash(BlockHash(block1)))

        val failBlock = Block(
            parent = block1,
            contents = "99",
            signature = Signature("99".toByteArray())
        )
        // ないとき
        assertEquals(null, tree.findByHash(BlockHash(failBlock)))
    }

    // 枝分かれしないとき
    @Test
    fun testToTwoDimensionNoBranch() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block111 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val tree = Tree(mutableListOf(block1, block11, block111))
        val result = tree.toTwoDimension()
        val expected = listOf(
            listOf(BlockOrEmpty.WrappedBlock(block1)),
            listOf(BlockOrEmpty.WrappedBlock(block11)),
            listOf(BlockOrEmpty.WrappedBlock(block111)),
        )
        assertEquals(expected, result)
    }

    // 木がないとき
    @Test
    fun testToTwoDimensionNoNode() {
        val tree = Tree(mutableListOf())
        val result = tree.toTwoDimension()
        assertEquals(listOf(listOf<BlockOrEmpty>()), result)
    }

    // 長さ1のとき
    @Test
    fun testToTwoDimensionOneNode() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val tree = Tree(mutableListOf(block1))
        val result = tree.toTwoDimension()
        val expected = listOf(
            listOf(BlockOrEmpty.WrappedBlock(block1))
        )
        assertEquals(expected, result)
    }

    // 枝分かれ一回で最長の木が複数のとき
    @Test
    fun testToTwoDimensionSomeBranch() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block12 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block111 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val block121 = Block(
            parent = block12,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val tree = Tree(mutableListOf(block1, block11, block111, block12, block121))
        val result = tree.toTwoDimension()
        val expected = listOf(
            listOf(BlockOrEmpty.WrappedBlock(block1)),
            listOf(BlockOrEmpty.WrappedBlock(block11), BlockOrEmpty.WrappedBlock(block12)),
            listOf(BlockOrEmpty.WrappedBlock(block111), BlockOrEmpty.WrappedBlock(block121)),
        )
        assertEquals(expected, result)
    }

    // 分岐により親が右に動くとき
    @Test
    fun testToTwoDimensionParentMoveRight() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block12 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block111 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block112 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block113 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block121 = Block(
            parent = block12,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val tree = Tree(mutableListOf(block1, block11, block111, block112, block113, block12, block121))
        val result = tree.toTwoDimension()
        val expected = listOf(
            listOf(BlockOrEmpty.WrappedBlock(block1)),
            listOf(
                BlockOrEmpty.WrappedBlock(block11),
                BlockOrEmpty.Empty,
                BlockOrEmpty.Empty,
                BlockOrEmpty.WrappedBlock(block12)
            ),
            listOf(
                BlockOrEmpty.WrappedBlock(block111),
                BlockOrEmpty.WrappedBlock(block112),
                BlockOrEmpty.WrappedBlock(block113),
                BlockOrEmpty.WrappedBlock(block121)
            ),
        )
        assertEquals(expected, result)
    }

    // 分岐により子が右に動くとき
    @Test
    fun testToTwoDimensionChildrenMoveRight() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block12 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block13 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block111 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block131 = Block(
            parent = block13,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val tree = Tree(mutableListOf(block1, block11, block12, block13, block111, block131))
        val result = tree.toTwoDimension()
        val expected = listOf(
            listOf(BlockOrEmpty.WrappedBlock(block1)),
            listOf(
                BlockOrEmpty.WrappedBlock(block11),
                BlockOrEmpty.WrappedBlock(block12),
                BlockOrEmpty.WrappedBlock(block13)
            ),
            listOf(BlockOrEmpty.WrappedBlock(block111), BlockOrEmpty.Empty, BlockOrEmpty.WrappedBlock(block131)),
        )
        assertEquals(expected, result)
    }

    // 上の子、右は関係なし
    @Test
    fun testCreateBranch2Parent2Child() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block21 = Block(
            parent = block2,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block31 = Block(
            parent = block3,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            block11, block21, block31
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    // 上の子、右も子
    @Test
    fun testCreateBranch2Parent23Child() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block21 = Block(
            parent = block2,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block22 = Block(
            parent = block2,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            block11, block21, block22
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┣" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┓" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    // 左の子、右は関係なし
    @Test
    fun testCreateBranch1Parent12Child() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block12 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block31 = Block(
            parent = block3,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            block11, block12, block31
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┣" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┓" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    // 左の子、右も左の子
    @Test
    fun testCreateBranch1Parent123Child() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block12 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block13 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            block11, block12, block13
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┣" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┳" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┓" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    // 子なし、右も関係なし
    @Test
    fun testCreateBranch1Parent1Child() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            BlockOrEmpty.WrappedBlock(block11), BlockOrEmpty.Empty, BlockOrEmpty.Empty
        )
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            " " + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            " " + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    // 子なし、右は親子関係
    @Test
    fun testCreateBranch13Parent13Child() {

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block31 = Block(
            parent = block3,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            BlockOrEmpty.WrappedBlock(block11), BlockOrEmpty.Empty, BlockOrEmpty.WrappedBlock(block31)
        )
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            " " + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    // 子なし、右は左と親子関係
    @Test
    fun testCreateBranch1Parent13Child() {
        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block2 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block3 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block13 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )

        val parents = listOf(
            block1, block2, block3
        ).map { BlockOrEmpty.WrappedBlock(it) }
        val children = listOf(
            BlockOrEmpty.WrappedBlock(block11), BlockOrEmpty.Empty, BlockOrEmpty.WrappedBlock(block13)
        )
        val tree = Tree(mutableListOf(block1))
        val expected = listOf(
            "┣" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "━" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1),
            "┓" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
        )
        assertEquals(expected, tree.createBranch(parents, children))
    }

    @Test
    // block1から親(block12)が右に動くパターン、block111から子が右に動くパターン
    fun testToString(){

        val block1 = Block(
            parent = null,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block12 = Block(
            parent = block1,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block111 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block112 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block113 = Block(
            parent = block11,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block121 = Block(
            parent = block12,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block1111 = Block(
            parent = block111,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block1112 = Block(
            parent = block111,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block1113 = Block(
            parent = block111,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11111 = Block(
            parent = block1111,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val block11131 = Block(
            parent = block1113,
            contents = "1",
            signature = Signature("1".toByteArray())
        )
        val tree = Tree(mutableListOf(
            block1,
            block11, block12,
            block111, block112, block113, block121,
            block1111, block1112, block1113,
            block11111, block11131
        ))
        val result = tree.toString()
        println(result)
        val expected =
            "$block1" + "\n" +
            "┣" + "━".repeat(29) + "┓" + " ".repeat(9) +"\n" +
            "$block11" + " ".repeat(20) + "$block12" +"\n" +
            "┣" + "━".repeat(9) + "┳" + "━".repeat(9) + "┓" + " ".repeat(9) + "┃" + " ".repeat(9) +"\n" +
            "$block111$block112$block113$block121" +"\n" +
            "┣" + "━".repeat(9) + "┳" + "━".repeat(9) + "┓" + " ".repeat(9) + "\n" +
            "$block1111$block1112$block1113" +"\n" +
            "┃" + " ".repeat(19) + "┃" + " ".repeat(9) + "\n" +
            "$block11111" + " ".repeat(10) + "$block11131"
        assertEquals(
            expected.lines().joinToString("\n") { it.dropLastWhile { it == ' ' } },
            result.lines().joinToString("\n") { it.dropLastWhile { it == ' ' } }
        )
    }
}