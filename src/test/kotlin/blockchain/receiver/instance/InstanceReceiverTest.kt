package blockchain.receiver.instance

import blockchain.block.Block
import blockchain.block.Tree
import blockchain.server.Server
import blockchain.server.instance.InstanceAsServer
import org.junit.Assert.*
import kotlin.test.Test

class InstanceReceiverTest{

    // findServers
    // 名前が空欄のとき
    @Test
    fun testFindServersNoName(){
        val receiver = InstanceReceiver()
        assertEquals(
            mutableListOf<String>(),
            receiver.findServer("")
        )
    }

    // foundServers に名前があるとき
    @Test
    fun testNameInFoundServers(){
        val receiver = InstanceReceiver()
        receiver.createServer("server1", emptyList())
        receiver.createServer("server2", listOf("server1"))
        assertEquals(
            mutableListOf("server1"),
            receiver.findServer("server2").map { it.name }
        )
    }

    // foundServers のサーバーと接続している中に名前があるとき
    @Test
    fun testFindServerInConnection(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer

        val server2 = InstanceAsServer("server2", setOf(server1.id))
        assertEquals(
            mutableListOf("server1"),
            receiver.findServer("server2").map { it.name }
        )
    }

    // 名前がないとき
    @Test
    fun testFindServerNotFound(){
        val receiver = InstanceReceiver()
        receiver.createServer("server1", emptyList())
        assertEquals(
            mutableListOf<String>(),
            receiver.findServer("xxx")
        )

    }

    // createServer
    // foundServer 中に接続先があるとき
    @Test
    fun testCreateServerWithConnect(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer

        val server2: InstanceAsServer = receiver.createServer("server2", listOf("server1"))as InstanceAsServer
        assertEquals(
            "server2",
            server2.name
        )
        assertEquals(1, server2.connectionServers.size)
        assertEquals(
            server1.id,
            server2.connectionServers.first()
        )
    }

    // foundServer 中に接続先がないとき
    @Test
    fun testCreateServerNoConnect(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        assertEquals("server1", server1.name)
        assertEquals(0, server1.connectionServers.size)
    }

    // showTree の異常系、指定されたサーバーがfoundServer中にない
    @Test
    fun testShowTreeNoServer(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        val server2: InstanceAsServer = receiver.createServer("server2", listOf("server1"))as InstanceAsServer
        val result: Tree? = receiver.showTree("server3")
        assertNull(result)
    }

    // showTree 正常系
    @Test
    fun testShowTree(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        val server2: InstanceAsServer = receiver.createServer("server2", listOf("server1"))as InstanceAsServer
        receiver.createBlock("block", "server2")
        val result: Tree = receiver.showTree("server2")!!
        assertEquals(1, result.getMaxLength())
        assertEquals("block", result.getMaxLengthBlock()!!.contents)
    }

    // connect 正常系、接続先二つ
    @Test
    fun testConnect(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        val server2: InstanceAsServer = receiver.createServer("server2", emptyList())as InstanceAsServer
        val server3: InstanceAsServer = receiver.createServer("server3", emptyList())as InstanceAsServer
        receiver.connect("server1", listOf("server2", "server3"))
        assertEquals(mutableSetOf(server2.id, server3.id), server1.connectionServers)
        assertEquals(mutableSetOf(server1.id), server2.connectionServers)
        assertEquals(mutableSetOf(server1.id), server3.connectionServers)
    }
    // connect 正常系、一つが不明な接続先でもう一つが知っている接続先
    // connect 異常系、不明なサーバーから接続

    // createBlock の異常系、指定されたサーバーがfoundServer中にない
    @Test
    fun testCreateBlockNoServer(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        val server2: InstanceAsServer = receiver.createServer("server2", listOf("server1"))as InstanceAsServer
        receiver.createBlock("block", "server3")
        assertEquals(0, server1.ledger.getMaxLength())
        assertEquals(0, server2.ledger.getMaxLength())
    }

    // createBlock 正常系
    @Test
    fun testCreateBlock(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        receiver.createBlock("block", "server1")
        assertEquals(1, server1.ledger.getMaxLength())
    }

    // ブロック作成しても接続されている他のサーバーには送信されない
    // step を行うと接続しているサーバーに送信される
    @Test
    fun testCopyBlockToOtherServer(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        val server2: InstanceAsServer = receiver.createServer("server2", listOf("server1"))as InstanceAsServer
        val server3: InstanceAsServer = receiver.createServer("server3", listOf("server2"))as InstanceAsServer
        receiver.createBlock("block", "server1")
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals(0, server2.ledger.getMaxLength())
        assertEquals(0, server3.ledger.getMaxLength())
        receiver.step()
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals(1, server2.ledger.getMaxLength())
        assertEquals(0, server3.ledger.getMaxLength())
        receiver.step()
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals(1, server2.ledger.getMaxLength())
        assertEquals(1, server3.ledger.getMaxLength())
    }

    // ブロック作成しても接続されている他のサーバーには送信されない
    // skip を行うとすべてのサーバーにブロックが共有される
    @Test
    fun testSkip(){
        val receiver = InstanceReceiver()
        val server1: InstanceAsServer = receiver.createServer("server1", emptyList()) as InstanceAsServer
        val server2: InstanceAsServer = receiver.createServer("server2", listOf("server1"))as InstanceAsServer
        val server3: InstanceAsServer = receiver.createServer("server3", listOf("server2"))as InstanceAsServer
        receiver.createBlock("block", "server1")
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals(0, server2.ledger.getMaxLength())
        assertEquals(0, server3.ledger.getMaxLength())
        receiver.skip()
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals(1, server2.ledger.getMaxLength())
        assertEquals(1, server3.ledger.getMaxLength())
    }

}