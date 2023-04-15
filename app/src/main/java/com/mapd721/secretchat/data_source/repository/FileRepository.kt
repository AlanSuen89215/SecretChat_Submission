package com.mapd721.secretchat.data_source.repository

interface FileRepository {
    /**
     * @return path of the uploaded file
     */
    fun saveSync(bytes: ByteArray): String
    fun saveSync(bytes: ByteArray, name: String): String
    fun getSync(path: String): ByteArray
}