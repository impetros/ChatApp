package com.petros.chatapp

class User {
    var username: String? = null
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var photo: String? = null

    constructor() {}

    constructor(username: String?, name: String?, email: String?, uid: String, photo: String) {
        this.username = username
        this.name = name
        this.email = email
        this.uid = uid
        this.photo = photo
    }
}