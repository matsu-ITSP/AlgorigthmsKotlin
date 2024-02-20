package blockchain.server.actualserver

import blockchain.block.Block
import blockchain.block.Tree
import blockchain.server.Server
import blockchain.server.ServerId
import java.security.interfaces.RSAPublicKey

class ActualServer(
    override val name: String,
    override val id: ServerId,
    override val ledger: Tree,
    override val publicKey: RSAPublicKey
) : Server {
    override fun connect(ids: List<ServerId>) {
        TODO("Not yet implemented")
    }

    override fun receiveConnection(id: ServerId): Tree {
        TODO("Not yet implemented")
    }

    override fun createBlock(contents: String) {
        TODO("Not yet implemented")
    }

    override fun receiveBlock(block: Block): Boolean {
        TODO("Not yet implemented")
    }

    override fun sendLedger(oldTree: Tree) {
        TODO("Not yet implemented")
    }

    override fun receiveLedger(ledger: Tree) {
        TODO("Not yet implemented")
    }
}