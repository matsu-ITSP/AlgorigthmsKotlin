package blockchain.receiver

import blockchain.block.Tree
import blockchain.server.Server

interface Receiver {
    /**
     * その名前のサーバーがどのサーバーと接続しているか聞きに行く
     * 返ってきた答えのサーバー一覧はローカルに保存し、createServer するときの接続に利用する
     *
     * @param name
     * @return name と接続しているサーバー名の一覧
     */
    fun findServer(name: String): List<Server>

    /**
     * その名前のサーバーを作成し、connectTo サーバーがある場合は接続する
     *
     * @param name 作成するサーバー名
     * @param connectTo サーバーと接続するサーバー名の一覧
     * @return TODO
     */
    fun createServer(name: String, connectTo: List<String>): Server


    /**
     * serverName サーバーから connectTo サーバーに接続を追加する
     *
     * @param serverName 作成するサーバー名
     * @param connectTo サーバーと接続するサーバー名の一覧
     * @return TODO
     */
    fun connect(serverName: String, connectTo: List<String>): Boolean

    /**
     * serverName で指示されたサーバーに、contents という内容のブロックを作らせる
     *
     * @param contents ブロックの内容
     * @param serverName
     * @return TODO
     */
    fun createBlock(serverName: String, contents: String): Boolean

    /**
     * serverName で指定されたサーバーの持つ台帳を取得する
     *
     * @param serverName
     * @return 指定されたサーバーの台帳、存在しなければ null を返す
     */
    fun showTree(serverName: String): Tree?
}