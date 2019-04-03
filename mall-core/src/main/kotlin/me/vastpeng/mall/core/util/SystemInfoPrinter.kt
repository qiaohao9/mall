package me.vastpeng.mall.core.util

import java.util.LinkedHashMap


class SystemInfoPrinter {
    companion object {
        const val CREATE_PART_COPPER = "XOXOXOXOX"

        private var maxSize = 0

        fun printInfo(title: String, infos: LinkedHashMap<String, String?>) {
            setMaxSize(infos)

            printHeader(title)

            for ((key, value) in infos) {
                printLine(key, value)
            }

            printEnd()
        }

        private fun setMaxSize(infos: Map<String, String>) {
            for ((key, value) in infos) {
                if (value == null)
                    continue

                val size = key.length + value.length

                if (size > maxSize)
                    maxSize = size
            }

            maxSize += 30
        }

        private fun printHeader(title: String) {
            println(getLineCopper())
            println("")
            println("              $title")
            println("")
        }

        private fun printEnd() {
            println("  ")
            println(getLineCopper())
        }

        private fun getLineCopper(): String {
            var copper = ""
            for (i in 0 until maxSize) {
                copper += "="
            }

            return copper
        }

        private fun printLine(head: String, line: String?) {
            if (line == null)
                return

            if (head.startsWith(CREATE_PART_COPPER)) {
                println("")
                println("    [[  $line  ]]")
                println("")
            } else {
                println("    $head        ->        $line")
            }
        }
    }
}