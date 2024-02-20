package blockchain.server.instance

import blockchain.block.Tree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstanceAsServerTest {

    // init で id がないとき
    @Test
    fun testInitNoServer() {
        val server1 = InstanceAsServer("server1", emptySet())
        assertEquals("server1", server1.name)
        assertEquals(server1, server1.id.server)
        assertEquals(emptySet(), server1.connectionServers)
        assertEquals(Tree(), server1.ledger)
    }

    // init で接続先にまだ台帳がないとき
    @Test
    fun testInitNoLedger() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", setOf(server1.id))
        assertEquals(setOf(server1.id), server2.connectionServers)
        assertEquals(setOf(server2.id), server1.connectionServers)
        assertEquals(Tree(), server1.ledger)
        assertEquals(Tree(), server2.ledger)
    }

    // init で接続先に台帳があるとき
    @Test
    fun testInit() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", setOf(server1.id))
        server1.createBlock("block1")
        server1.sendLedger(server1.ledger)
        server1.createBlock("block2")
        val server3 = InstanceAsServer("server3", setOf(server1.id, server2.id))
        assertEquals(setOf(server1.id, server2.id), server3.connectionServers)
        assertEquals(server1.ledger, server3.ledger)
    }

    // connection で id が空のとき
    @Test
    fun testConnectEmpty() {
        val server1 = InstanceAsServer("server1", emptySet())
        server1.connect(emptyList())
        assertEquals(emptySet(), server1.connectionServers)
    }

    // connection で id が複数あるとき（木が追加されることも検証）
    @Test
    fun testConnect() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", setOf(server1.id))
        server1.createBlock("block1")
        server1.sendLedger(server1.ledger)
        server1.createBlock("block2")
        val server3 = InstanceAsServer("server3", emptySet())
        server3.connect(listOf(server1.id, server2.id))
        assertEquals(setOf(server1.id, server2.id), server3.connectionServers)
        assertEquals(server1.ledger, server3.ledger)
    }

    // receiveConnection
    @Test
    fun testReceiveConnection() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", emptySet())
        server1.receiveConnection(server2.id)
        assertEquals(setOf(server2.id), server1.connectionServers)
        assertEquals(emptySet(), server2.connectionServers)
    }

    // createBlock 繋がっている他のサーバーにはブロックが送信されないことも検証
    @Test
    fun testCreateBlock() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", setOf(server1.id))
        server1.createBlock("block1")
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals("block1", server1.ledger.getMaxLengthBlock()!!.contents)
        assertEquals(0, server2.ledger.getMaxLength())
    }

    // receiveBlock 受信したブロックが作成済みのとき、両方のサーバーの中身が変わらないことを確認
    @Test
    fun testReceiveBlockCreated() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", emptySet())
        server1.createBlock("block1")
        server2.connect(listOf(server1.id))
        assertTrue(server1.receiveBlock(server2.ledger.getMaxLengthBlock()!!))
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals("block1", server1.ledger.getMaxLengthBlock()!!.contents)
        assertEquals(1, server2.ledger.getMaxLength())
        assertEquals("block1", server2.ledger.getMaxLengthBlock()!!.contents)
    }

    // receiveBlock 受信したブロックが未作成のとき、ブロックが作成され、繋がっている他のサーバーにはブロックが送信されないことを検証
    @Test
    fun testReceiveBlock() {
        val server1 = InstanceAsServer("server1", emptySet())
        val server2 = InstanceAsServer("server2", setOf(server1.id))
        val server3 = InstanceAsServer("server3", setOf(server1.id))
        server2.createBlock("block1")
        assertTrue(server1.receiveBlock(server2.ledger.getMaxLengthBlock()!!))
        assertEquals(1, server1.ledger.getMaxLength())
        assertEquals("block1", server1.ledger.getMaxLengthBlock()!!.contents)
        assertEquals(1, server2.ledger.getMaxLength())
        assertEquals("block1", server2.ledger.getMaxLengthBlock()!!.contents)
        assertEquals(0, server3.ledger.getMaxLength())
    }
}