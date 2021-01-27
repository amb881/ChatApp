package com.andre.chatapp

class User {
    var email: String? = ""
    var password: String? = ""
    var profileImageUrl: String? =""
    constructor(email: String, password: String, profileImageUrl: String){
        this.email = email
        this.password = password
        this.profileImageUrl = profileImageUrl
    }
}