package com.andre.chatapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//class User {
//    var email: String? = ""
//    var password: String? = ""
//    var profileImageUrl: String? =""
//    constructor(email: String, password: String, profileImageUrl: String){
//        this.email = email
//        this.password = password
//        this.profileImageUrl = profileImageUrl
//    }
//}
@Parcelize
class User (val uid: String, val username: String, val email: String, val password: String, val profileImageUrl: String): Parcelable{
    constructor() : this("","","","", "")
}