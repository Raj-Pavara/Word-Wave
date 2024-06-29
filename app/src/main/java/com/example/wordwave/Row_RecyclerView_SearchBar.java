package com.example.wordwave;

//this is class for store data of every row of recyclerview of mainactivity of searchbar
public class Row_RecyclerView_SearchBar {
    String imageUrl,username,fullname,userId;
    Row_RecyclerView_SearchBar(String imageUrl,String username,String fullname,String userId){
     this.imageUrl = imageUrl;
     this.username = username;
     this.fullname = fullname;
     this.userId = userId;
    }
}
