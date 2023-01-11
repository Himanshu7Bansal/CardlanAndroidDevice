package com.example.cardlanandroiddevice.data

class KeyErrorException : Exception {

    constructor() : super("Key create error!")
    constructor(message: String?) : super(message)
}