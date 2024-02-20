package blockchain.server.instance

import blockchain.block.Block
import blockchain.block.Tree
import blockchain.server.Server
import blockchain.server.ServerId
import blockchain.sign.Sign
import blockchain.sign.instance.SignInInstance
import logger
import java.lang.Exception
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class InstanceAsServer(override val name: String, ids: Set<ServerIdAsInstance>) : Server {

    override val id = ServerIdAsInstance(this)
    val connectionServers: MutableSet<ServerIdAsInstance>
    override val ledger = Tree()
    override val publicKey: RSAPublicKey

    private val privateKey: RSAPrivateKey

    // TODO DIしたい
    private val signer: Sign

    init {
        connectionServers = ids.toMutableSet()
        connectionServers.forEach {
            it.server.receiveConnection(id).let { tree ->
                ledger.addAllNotContains(tree)
            }
        }
        val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val keyPair = generator.generateKeyPair()
        publicKey = keyPair.public as RSAPublicKey
        privateKey = keyPair.private as RSAPrivateKey
        signer = SignInInstance()
    }

    override fun connect(ids: List<ServerId>) {
        ids.filterIsInstance<ServerIdAsInstance>().forEach {
            connectionServers.add(it)
            it.server.receiveConnection(this.id).let { tree ->
                ledger.addAllNotContains(tree)
            }
        }
    }

    override fun receiveConnection(id: ServerId): Tree {
        if (id !is ServerIdAsInstance) {
            throw Exception()
        }
        connectionServers.add(id)
        return ledger
    }

    override fun createBlock(contents: String) {
        val block = Block(
            parent = choiceParentBlockByMaxLength(),
            contents = contents,
            signature = signer.sign(contents, privateKey)
        )
        ledger.add(block)
//        connectionServers.forEach {
//            if (!it.server.receiveBlock(block)) {
//                logger.info("$id cant send block($block) to ${it.server.id}")
//            }
//        }
    }

    override fun receiveBlock(block: Block): Boolean {
        if (ledger.findByHash(block.hash) == null) {
            ledger.add(block)
//            connectionServers.forEach {
//                if (!it.server.receiveBlock(block)) {
//                    logger.info("$id cant send block($block) to ${it.server.id}")
//                }
//            }
        }
        return true
    }


    override fun sendLedger(oldTree: Tree) {
        connectionServers.forEach {
            it.server.receiveLedger(oldTree)
        }
    }

    override fun receiveLedger(ledger: Tree) {
        this.ledger.addAllNotContains(ledger)
    }

    /**
     * 新しいブロックの親として、最長の長さを持つブロックを選択して返す
     */
    private fun choiceParentBlockByMaxLength(): Block? = ledger.getMaxLengthBlock()

    /**
     * 新しいブロックの親として、信頼度を考慮しつつ長いブロックを返す
     *
     * TODO サーバーの信頼度リスト、長さと信頼度の選択アルゴリズム
     * TODO MAX(長さ+Σ(そのブロックを作成したサーバーの信頼度*そのブロックまでの距離に応じた定数)) 距離: 末端ブロックなら0、親なら1、親の親なら2、...
     */
}