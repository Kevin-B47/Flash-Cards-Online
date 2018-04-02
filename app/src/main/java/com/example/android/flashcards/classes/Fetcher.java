package com.example.android.flashcards.classes;

import android.util.Log;

/**
 * Created by Kevin on 2/14/2018.
 */

public class Fetcher {

    private final static String devURL = "localhost/";
    private final static String realURL = "https://kevinblock.net/";


    private final static String authURL = "flashcards/auth.php?";
    private final static String syncURL = "flashcards/sync.php";
    private final static String storeCardURL = "flashcards/storecard.php";
    private final static String collectionURL = "flashcards/collections.php";
    private final static String downloadCollectionULR = "flashcards/download.php?";

    private static String BuildURL(String url){
        boolean isDev = false;
        if (isDev){
            return devURL+url;
        }else{
            return realURL+url;
        }
    }

    /**
     * Post request that registers or logins users, handles token creation and updating. Can also validate users
     * @param username (required)
     * @param password (login and registering)
     * @param token
     * @param newuser (registering only)
     * @param displayname (registering only)
     *
     */

    public static String AuthURL(){
        return BuildURL(authURL);
    }


    /**
     * Post request that syncs user data on login (fetches collections and cards)
     * @param username (required)
     * @param token
     */

    public static String DataSyncURL(){
        return BuildURL(syncURL);
    }

    /**
     * Post request that stores, updates and removes cards
     * @param username (required)
     * @param token (required)
     * @param collection (required)
     * @param cardid (removing only)
     * @param removecard (removing only)
     * @param fronttext (adding only)
     * @param backttext (adding only)
     */

    public static String StoreCardURL(){
        return BuildURL(storeCardURL);
    }

    /**
     * Post request that creates, renames and removes collections
     * @param userid (required)
     * @param token (required)
     * @param collection (creation / remove only)
     * @param collectionname (rename only)
     */

    public static String CollectionURL(){
        return BuildURL(collectionURL);
    }

    public static String FetchCollections(){
        return BuildURL(downloadCollectionULR);
    }

    public static String DownloadURL(int id){
        //Log.d("New URL",BuildURL(downloadCollectionULR + "id="+String.valueOf(id)));
        return BuildURL(downloadCollectionULR + "id="+String.valueOf(id));
    }
}
