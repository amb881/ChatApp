package com.andre.chatapp

class Users {
    var email: String? = ""
    var password: String? = ""
    //var profileImageUrl: String? =""
    constructor(email: String, password: String,){
        this.email = email
        this.password = password
        //this.profileImageUrl = profileImageUrl
    }
}