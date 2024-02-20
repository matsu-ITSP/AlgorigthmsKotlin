package blockchain.block

import blockchain.Properties
import blockchain.block.output.BlockOrEmpty
import kotlin.math.max

/**
 * Block の木構造を表現するクラス
 * 実際はリスト
 * 順序性はある（後に追加された要素は、必ずそれより前に追加された要素の子になる）
 */
data class Tree(private val list: MutableList<Block> = mutableListOf()) {
    private var maxLength = 0

    init {
        maxLength = list.maxOfOrNull { it.treeLength } ?: 0
    }

    fun getMaxLength(): Int = maxLength

    fun getMaxLengthBlock(): Block? = list.maxByOrNull { it.treeLength }

    fun add(element: Block): Tree {
        list.add(element)
        if (element.treeLength > maxLength) {
            maxLength = element.treeLength
        }
        return this
    }

    fun addAllNotContains(other: Tree): Tree {
        list.addAll(other.list - this.list)
        maxLength = max(this.maxLength, other.maxLength)
        return this
    }

    /**
     * リストの後方から検索
     *
     * @param hash
     * @return 見つかればそのブロック、見つからなければ null
     */
    fun findByHash(hash: BlockHash): Block? =
        list.lastOrNull() {
            it.hash == hash
        }

    /**
     * 順序は異なっていても中身が同じなら同じ木と評価する
     */
    override fun equals(other: Any?): Boolean {
        if(other !is Tree) {
            return false
        }
        return this.list.toSet() == other.list.toSet()
    }

    fun copy() = Tree(ArrayList(list))

    override fun toString(): String {
        val twoDimension = toTwoDimension()
        val branch = createBranch(twoDimension)
        return branch.mapIndexed { index, list ->
            twoDimension[index].joinToString("") { it.toString() } + "\n" + list.joinToString("")
        }.plus(twoDimension.last().joinToString("") { it.toString() })
            .joinToString("\n")
    }

    /**
     * 入った順に並んでいる木を二次元リストに変換する
     * result[x][y]の子はresult[x+1][y], result[x+1][y+1], ... に入る
     * 分岐が存在する場合、子ノードが親ノードの真下か右に来るように空ノードが挿入される
     * 言い換えると、result[x][y] と result[x+1][y1], ... result[x][yn] が親子関係のとき、y1 > ... > yn とすると、y = y1 となる
     * また、z を ym < z < ym+1 を満たす整数とし、result[x+1][z] が空ノードのとき、必ず空ノードでない result[w][z] が存在し、親を辿ると result[x+1][ym] となる
     * TODO これでもまだ無駄な空ノードを入れられるのでもっと制約つける？十分？
     */
    fun toTwoDimension(): List<List<BlockOrEmpty>> {
        if (list.size == 0) {
            return listOf(emptyList()) // TODO 空リストか空リストの入ったリスト、どっちが適切？
        }
        val source = list.groupBy { it.treeLength }.map { it.key - 1 to it.value.toMutableList() }.toMap()
        val result: MutableList<MutableList<BlockOrEmpty>> =
            (0 until maxLength).map { mutableListOf<BlockOrEmpty>() }.toMutableList()
        // source の最後の要素を result[treeLength][0] に移動
        val sourceLastIndex = source.keys.maxOrNull()!!
        result[sourceLastIndex].add(BlockOrEmpty.WrappedBlock(source[sourceLastIndex]?.removeFirst()!!))
        // i = treeLength-1 から逆順に、result[i][0] を、result[i+1][0] の親で埋める
        for (i in (sourceLastIndex - 1) downTo 0) {
            val parent = result[i + 1][0].getBlockOrNull()?.parent!!
            source[i]?.remove(parent)
            result[i].add(0, BlockOrEmpty.WrappedBlock(parent))
        }
        while (source.any { it.value.isNotEmpty() }) {
            for (i in source.maxByOrNull { it.value.isNotEmpty() }?.key!! downTo 1) {
                val parents = result[i - 1]
                // 非効率だがターゲットに移動済みのものも入れる
                val children: MutableList<BlockOrEmpty> = (result[i] +
                        (source[i]!!
                            // 親ノードが処理済みの方に移動済みのものを取得
                            .filter { parents.map { it.getBlockOrNull() }.contains(it.parent) }
                            // 親ノードと同じ順になるように子ノードをソート
                            .sortedBy {
                                createSortNumByParents(
                                    it,
                                    parents.filterIsInstance<BlockOrEmpty.WrappedBlock>().map { it.getBlockOrNull()!! }
                                )
                            }
                            // 結果用の空を持つ型に変換
                            .map { BlockOrEmpty.WrappedBlock(it) }))
                    .toMutableList()
                if (children.isEmpty()) {
                    continue
                }
                for (index in 0 until children.size) {
                    // index の示す位置の子を見て、その親の位置によって場合分け、親に合わせて位置が変わることがあるため、foreach などは使えない
                    // 子が空ノードのとき、親ノードが真下のとき、長男ノードではないとき、何もしない
                    if (children[index] is BlockOrEmpty.Empty ||
                        (index < parents.size && children[index].getBlockOrNull()!!.parent == parents[index].getBlockOrNull()) ||
                        !isEldest(children, index)
                    ) {
                        continue
                    }
                    // 子が親ノードより左のとき、真上に来るように、子ノードの左に空ノードを挿入する
                    val parentIndex =
                        parents.map { it.getBlockOrNull() }.indexOf(children[index].getBlockOrNull()!!.parent)
                    if (index < parentIndex) {
                        children.addAll(index, BlockOrEmpty.createEmptyBlocks(parentIndex - index))
                        continue
                    }
                    // 子が親ノードより右かつ長男ノードのとき、親ノードとそれが長男である限りその親すべてが真上に来るように、親ノードとその親すべての左に空ノードを挿入する
                    if (parentIndex < index && isEldest(children, index)) {
                        for (j in i - 1 downTo 1) {
                            if (isEldest(result[j], parentIndex)) {
                                result[j].addAll(parentIndex, BlockOrEmpty.createEmptyBlocks(index - parentIndex))
                            } else {
                                result[j].addAll(parentIndex, BlockOrEmpty.createEmptyBlocks(index - parentIndex))
                                // addAll で、親に要素が2つしかないのに index=3に入れようとして壊れてる
                                break
                            }
                        }
                    }
                }
                result[i] = children
                source[i]!!.removeIf { children.map { it.getBlockOrNull() }.contains(it) }
            }
        }
        return result
    }

    private fun isEldest(children: List<BlockOrEmpty>, index: Int): Boolean =
        index == 0 || children[index].getBlockOrNull()!!.parent != children[index - 1].getBlockOrNull()?.parent

    /**
     * 親のリストと子を受け取り、子の親が親リストの何番目かを返却する
     * 親が1つしかない・親リストが空のときは、無意味なソートとなるため、常に1を返却する
     */
    private fun createSortNumByParents(child: Block, parents: List<Block>): Int {
        if (parents.size <= 1) {
            return 1
        }
        val parentsToIndex = parents.mapIndexed { index, block -> block to index }.toMap()
        return parentsToIndex[child.parent]!!
    }

    /**
     * 二次元となったブロックを受け取り、nとn+1行目の間の枝を result[n] に入れる
     *
     */
    fun createBranch(twoDimensionTree: List<List<BlockOrEmpty>>): List<List<String>> {
        val result: MutableList<List<String>> = mutableListOf()
        for (i in 0..twoDimensionTree.size - 2) {
            result.add(
                createBranch(
                    parents = twoDimensionTree[i],
                    children = twoDimensionTree[i + 1]
                )
            )
        }
        return result
    }

    fun createBranch(parents: List<BlockOrEmpty>, children: List<BlockOrEmpty>): List<String> {
        return children.mapIndexed { index, child ->
            val right = children.drop(index+1).find { it is BlockOrEmpty.WrappedBlock } ?: BlockOrEmpty.Empty // 右になってない
            val up = if (parents.size <= index) {
                BlockOrEmpty.Empty
            } else {
                parents[index]
            }
            when {
                // 上下に繋げるとき
                child is BlockOrEmpty.WrappedBlock && child.block.parent == up.getBlockOrNull() -> {
                    // 右と親が同じとき
                    if (child.block.parent == right.getBlockOrNull()?.parent) {
                        "┣" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
                    } else {
                        "┃" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
                    }
                }
                // 上には繋げないが下には繋げるとき: 左と親が同じことは確定している
                child is BlockOrEmpty.WrappedBlock -> {
                    // 右と親が同じとき
                    if (child.block.parent == right.getBlockOrNull()?.parent) {
                        "┳" + "━".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
                    } else {
                        "┓" + " ".repeat(Properties.BLOCK_OUTPUT_LENGTH - 1)
                    }
                }
                // 上下に繋げないとき
                child is BlockOrEmpty.Empty -> {
                    // 右が存在しないか、右上と親子関係にあるとき
                    if (right is BlockOrEmpty.Empty || right is BlockOrEmpty.WrappedBlock &&
                        parents.map { it.getBlockOrNull() }.indexOf(right.block.parent) == children.indexOf(right)
                    ) {
                        " ".repeat(Properties.BLOCK_OUTPUT_LENGTH)
                    } else {
                        "━".repeat(Properties.BLOCK_OUTPUT_LENGTH)
                    }
                }
                else -> throw Exception()
            }
        }
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }
}
