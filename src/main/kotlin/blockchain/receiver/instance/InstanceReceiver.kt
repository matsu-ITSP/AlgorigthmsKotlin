package blockchain.receiver.instance

import blockchain.block.Block
import blockchain.block.Tree
import blockchain.receiver.Receiver
import blockchain.server.Server
import blockchain.server.instance.InstanceAsServer
import logger
import java.util.logging.Logger

class InstanceReceiver : Receiver {

    private val foundServers: MutableSet<InstanceAsServer> = mutableSetOf()

    override fun findServer(name: String): List<Server> {
        if (name == "") {
            return foundServers.toList()
        }
        foundServers
            .find { it.name == name }
            ?.let {
                foundServers.addAll(it.connectionServers.map { it.server })
                return it.connectionServers.map { it.server }
            }
        foundServers
            .flatMap { it.connectionServers }
            .map { it.server }
            .find { it.name == name }
            ?.let {
                foundServers.addAll(it.connectionServers.map { it.server })
                return it.connectionServers.map { it.server }
            }
        logger.debug("There is no server which name is $name.")
        return mutableListOf()
    }

    override fun createServer(name: String, connectTo: List<String>): Server {
        // TODO 名前重複チェック
        val server = InstanceAsServer(
            name,
            foundServers.filter {
                connectTo.contains(it.name)
            }.map { it.id }.toSet()
        )
        foundServers.add(server)
        return server
    }

    override fun connect(serverName: String, connectTo: List<String>): Boolean {
        val server = foundServers.find { it.name == serverName } ?: return false
        val filteredConnectTo = foundServers.filter {
            connectTo.contains(it.name)
        }
        if (filteredConnectTo.isEmpty()) {
            return false
        }
        filteredConnectTo.map { it.id }.let {
            server.connect(it)
        }
        return true
    }

    override fun createBlock(serverName: String, contents: String): Boolean {
        val server = foundServers.find { it.name == serverName } ?: return false
        server.createBlock(contents)
        return true
    }

    override fun showTree(serverName: String): Tree? {
        val server = foundServers.find { it.name == serverName } ?: return null
        return server.ledger
    }

    fun step() {
        // 現在の台帳のみを送信するため、現在の台帳をコピーしてから送信する
        val oldTrees = foundServers.associateWith { it.ledger.copy() }
        oldTrees.forEach {
            it.key.sendLedger(it.value)
        }
    }

    fun skip() {
        // 最終的に同じ台帳になるため、他から受け取って更新後の台帳を送信しても問題ない
        // TODO 問題あって、連結されていないサーバーがあると同じ台帳にならない
        while (!foundServers.all { it.ledger == foundServers.last().ledger }) {
            foundServers.forEach { it.sendLedger(it.ledger) }
        }
    }
}