package com.warriortech.resb.util

expect class Platform() {
    val name: String
    val version: String
}

expect fun getPlatformName(): String
