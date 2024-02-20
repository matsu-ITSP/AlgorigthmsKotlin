package blockchain.receiver.instance

import blockchain.receiver.Receiver
import blockchain.server.Server

/**
 * コマンドライン入力にしたがってサーバー(インスタンス)作成・ブロック作成を行うCUI処理機
 * createServer <name> <name>* 新規サーバーを立ち上げる。第一引数でサーバー名を指定し、第二引数以降で接続するサーバーを指定する。
 * connect <name> <name>* 接続を追加する。第一引数で接続元のサーバー名を指定し、第二引数以降で接続するサーバーを指定する。
 * findServers <name>? サーバーを検索する。
 * 引数なしのときは過去に作成したサーバー名・過去に検索で見つかったサーバー名を返却する。
 * 引数ありのときは「過去に作成したサーバー名・過去に検索で見つかったサーバー名・それぞれに接続しているサーバー一覧」
 * から引数の名前で検索し、見つかればそのサーバーに接続しているサーバー名の一覧を返却する。
 * 見つからなければ空リストを返却する。
 * createBlock <serverName> <contents> ブロックを作成する。第二引数で定めた内容のブロックを、第一引数で指定した名前のサーバーの台帳に
 * 追加する。追加してブロックは、接続先から接続先へと反映されていく。
 * showTree <name> 第一引数で指定されたサーバーの台帳を表示する。
 * exit プログラムを終了する。
 * その他 メッセージを出して命令をスキップする。
 *
 * TODO サーバーは他のサーバーが作成したブロックの正当性を検証したい
 */
class CommandlineInterface {
    companion object {
        private val receiver: InstanceReceiver = InstanceReceiver()
        fun input() {
            while (true) {
                val command = readLine()?.split(" ") ?: listOf("")
                when (command[0]) {
                    "findServer" -> findServer(command)
                    "createServer" -> createServer(command)
                    "connect" -> connect(command)
                    "showTree" -> showTree(command)
                    "createBlock" -> createBlock(command) // TODO 全ての入力は半角に固定する、表示が崩れるため
                    "" -> step()
                    "skip" -> skip()
                    "exit" -> break
                    else -> println("Command is not found.")
                }
            }
            println("bye!")
        }

        private fun findServer(command: List<String>) {
            if (command.size != 2) {
                println("findServer has 1 argument.")
                return
            }
            val servers = receiver.findServer(command[1])
            if (servers.isEmpty()) {
                println("${command[1]} has no connection.")
                return
            }
            println("${command[1]} has connection to ${servers.joinToString(", ") { it.name }}")
        }

        private fun createServer(command: List<String>) {
            if (command.size == 1) {
                println("createServer has at least 1 argument.")
                return
            }
            receiver.createServer(command[1], command.drop(2))
            println("Server ${command[1]} is created.")
        }

        private fun connect(command: List<String>) {
            if (command.size < 3) {
                println("connect has at least 2 arguments.")
                return
            }
            if (!receiver.connect(command[1], command.drop(2))) {
                println("${command[1]} not found or server to connect to was not found.")
            }
            println("${command[1]} connect to ${command.drop(2).joinToString(", ")}.")
        }

        private fun showTree(command: List<String>) {
            if (command.size != 2) {
                println("showTree has 1 arguments.")
                return
            }
            receiver.showTree(command[1])?.let {
                println("Show tree of ${command[1]}")
                println(it)
            } ?: run { println("${command[1]} is not exist.") }
        }

        private fun createBlock(command: List<String>) {
            if (command.size != 3) {
                println("createBlock has 2 arguments.")
                return
            }
            if (!receiver.createBlock(command[1], command[2])) {
                println("${command[1]} not found.")
            }
            println("${command[1]} create block ${command[2]}")
        }

        private fun step() {
            receiver.step()
            println("Step ran.")
        }

        private fun skip() {
            receiver.skip()
            println("skip ran.")
        }
    }
}