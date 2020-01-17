package com.streamxhub.flink.test

object LogoTestApp {

  def main(args: Array[String]): Unit = {
    val appName = "Flink Job Starting..."
    val logo =
      s"""
         |
         |                          ▒▓██▓██▒
         |                     ▓████▒▒█▓▒▓███▓▒
         |                  ▓███▓░░        ▒▒▒▓██▒  ▒
         |                ░██▒   ▒▒▓▓█▓▓▒░      ▒████
         |                ██▒         ░▒▓███▒    ▒█▒█▒
         |                  ░▓█            ███   ▓░▒██
         |                    ▓█       ▒▒▒▒▒▓██▓░▒░▓▓█
         |                  █░ █   ▒▒░       ███▓▓█ ▒█▒▒▒
         |                  ████░   ▒▓█▓      ██▒▒▒ ▓███▒
         |               ░▒█▓▓██       ▓█▒    ▓█▒▓██▓ ░█░
         |         ▓░▒▓████▒ ██         ▒█    █▓░▒█▒░▒█▒
         |        ███▓░██▓  ▓█           █   █▓ ▒▓█▓▓█▒
         |      ░██▓  ░█░            █  █▒ ▒█████▓▒ ██▓░▒
         |     ███░ ░ █░          ▓ ░█ █████▒░░    ░█░▓  ▓░
         |    ██▓█ ▒▒▓▒          ▓███████▓░       ▒█▒ ▒▓ ▓██▓
         | ▒██▓ ▓█ █▓█       ░▒█████▓▓▒░         ██▒▒  █ ▒  ▓█▒
         | ▓█▓  ▓█ ██▓ ░▓▓▓▓▓▓▓▒              ▒██▓           ░█▒
         | ▓█    █ ▓███▓▒░              ░▓▓▓███▓          ░▒░ ▓█
         | ██▓    ██▒    ░▒▓▓███▓▓▓▓▓██████▓▒            ▓███  █
         |▓███▒ ███   ░▓▓▒░░   ░▓████▓░                  ░▒▓▒  █▓
         |█▓▒▒▓▓██  ░▒▒░░░▒▒▒▒▓██▓░                            █▓
         |██ ▓░▒█   ▓▓▓▓▒░░  ▒█▓       ▒▓▓██▓    ▓▒          ▒▒▓
         |▓█▓ ▓▒█  █▓░  ░▒▓▓██▒            ░▓█▒   ▒▒▒░▒▒▓█████▒
         | ██░ ▓█▒█▒  ▒▓▓▒  ▓█                █░      ░░░░   ░█▒
         | ▓█   ▒█▓   ░     █░                ▒█              █▓
         |  █▓   ██         █░                 ▓▓        ▒█▓▓▓▒█░
         |   █▓ ░▓██░       ▓▒                  ▓█▓▒░░░▒▓█░    ▒█
         |    ██   ▓█▓░      ▒                    ░▒█▒██▒      ▓▓
         |     ▓█▒   ▒█▓▒░                         ▒▒ █▒█▓▒▒░░▒██
         |      ░██▒    ▒▓▓▒                     ▓██▓▒█▒ ░▓▓▓▓▒█▓
         |        ░▓██▒                          ▓░  ▒█▓█  ░░▒▒▒
         |            ▒▓▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░▓▓  ▓░▒█░
         |
         |$appName Starting...
         |
         |""".stripMargin.replaceAll("_", "")

    println(s"\033[95;1m${logo}\033[1m\n")


  }

}
