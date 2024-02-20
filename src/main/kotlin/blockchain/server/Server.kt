package blockchain.server

import blockchain.block.Block
import blockchain.block.Tree
import blockchain.server.instance.ServerIdAsInstance
import blockchain.sign.Sign
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

interface Server {

    val name: String
    val id: ServerId
    val ledger: Tree
    val publicKey: RSAPublicKey

    // ServerId を継承したもののリストにしたいが反変となるので継承できない
    // val connectionServers: MutableList<ServerId>

    /**
     * 他のサーバーと接続し、そのサーバーが持つ最新の台帳を受け取り、自分の台帳に追記する
     * 複数接続可
     * @param ids 接続先のサーバー
     * @return TODO
     */
    fun connect(ids: List<ServerId>)

    /**
     * 他のサーバーからの接続開始を受信する
     * @param id サーバーの id
     * @return 自身の台帳
     */
    fun receiveConnection(id: ServerId): Tree

    /**
     * 新しいブロックを作成して台帳に追記する // 接続しているサーバーにそのブロックを送信する、というのは別機能
     * @param contents ブロックに書き込む内容
     */
    fun createBlock(contents: String)

    /**
     * ブロックを受信し、自分が所持しているブロックか確認
     *
     * 持っている -> 一度送信したブロックなので何もしない
     * 持っていない -> 台帳に追記する // 台帳に追記し、送信元以外の接続されているサーバーにそのブロックを送信する、というのは別機能
     *
     * @param block 受信したブロック
     * @return 未所持のブロックなら true
     */
    fun receiveBlock(block: Block): Boolean

    /**
     * 接続しているサーバーに自分の現在の台帳を送信する
     * 主に createBlock 後、receiveBlock 後に利用
     *
     * 時間を step, skip で進めるため、ブロックの受信・作成とは分ける必要がある
     * 並列に動作するため、自身の最初の台帳を引数として受け取る
     *
     * @param oldTree: 最初の自身の台帳
     */
    fun sendLedger(oldTree: Tree)

    fun receiveLedger(ledger: Tree)
}