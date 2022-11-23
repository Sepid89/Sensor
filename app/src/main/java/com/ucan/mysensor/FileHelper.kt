package com.ucan.mysensor

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileHelper {
    companion object {
        fun createFile(fileDir: File?): File {
            val sdf = SimpleDateFormat("dd.M.yyyy hh_mm_ss")
            val currentDate = sdf.format(Date())
            return File("$fileDir/Beschleunigung_$currentDate.csv")
        }


        fun createBufferedWriter(file: File?): BufferedWriter {
            val fileWriter = FileWriter(file?.absoluteFile)
            return BufferedWriter(fileWriter)
        }

        fun writeFile(
            accValue: ArrayList<Float>,
            timeValue: ArrayList<Long>,
            bufferedWriter: BufferedWriter
        ) {
            bufferedWriter.use { out ->
                for ((index, value) in accValue.toArray().withIndex()) {
                    out.write("$value;${timeValue.get(index)}".replace(".", ","))
                    out.newLine()
                }
            }
            bufferedWriter.close()
        }
    }
}
