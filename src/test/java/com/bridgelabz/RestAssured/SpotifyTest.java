package com.bridgelabz.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class SpotifyTest {



    public String token = "";
    public String user_ID = "";
    public int totalNoOfPlayList;
    public String JSON = "application/json";
    public String trackIdArray[];
    public String playListArray[];


    //Before method
    @BeforeMethod
    public void get() {
        //Token get expired in every 30 min so put the recent token
        token = "Bearer ";
    }

    //here we will get the user id
    @Test(priority = 1)
    public void userID_GET_Request() {
        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", token)
                .when()
                .get("https://api.spotify.com/v1/me");
        //This step will fetch the user id from the response
        user_ID = response.path("id");
        String userName = response.path("display_name");
        //Validation of status code
        response.then().assertThat().statusCode(200);
        System.out.println(user_ID);
        System.out.println(userName);
    }

     //here we will get the total information of user profile
    @Test(priority = 2)
    public void userInfo_GET_Request() {
        Response response = given()
                .accept(JSON)
                .contentType(JSON)
                .header("Authorization", token)
                .when()
                .get("https://api.spotify.com/v1/users/" + user_ID + "/");
        //Validation of Status code
        response.then().assertThat().statusCode(200);
        response.prettyPrint();
    }

    //here we will get number of total playlist present
    @Test(priority = 3)
    public void TotalPlaylist_GET_Request() {
        Response response = given()
                .accept(JSON)
                .contentType(JSON)
                .header("Authorization", token)
                .when()
                .get("https://api.spotify.com/v1/users/" + user_ID + "/playlists");
        response.then().assertThat().statusCode(200);
        totalNoOfPlayList = response.path("total"); //get total playlist
        System.out.println("Total PlayList:" + totalNoOfPlayList);
        //response.prettyPrint(); //Optional
    }

    //here we will get details like UserId of each playlist created
    @Test(priority = 4)
    public void userPlayListInfo_GET_Request() {
        Response response = given()
                .accept(JSON)
                .contentType(JSON)
                .header("Authorization", token)
                .when()
                .get("https://api.spotify.com/v1/users/" + user_ID + "/playlists");
        //response.prettyPrint(); //optional
        playListArray = new String[totalNoOfPlayList];
        for (int index = 0; index < playListArray.length; index++) {
            playListArray[index] = response.path("items[" + index + "].id"); //get playlist id
        }
       for (String id : playListArray) { //print play list
            System.out.println("PlayList Id" + id);
        }
    }

    //here we will create the new playlist
    @Test(priority = 5)
    public void createPlayList_POST_Request() {
        Response response = given()
                .accept(JSON)
                .contentType(JSON)
                .header("Authorization", token)
                .body("{\"name\": \"DJ Remixx\",\"description\": \"New playlist description\",\"public\": true}")
                .when()
                .post(" https://api.spotify.com/v1/users/" + user_ID + "/playlists");
        String name = response.path("owner.display_name");
        System.out.println("Name Of Playlist: " + name);
        response.then().assertThat().statusCode(201); //Validation Of PlayList
        //response.prettyPrint();
    }

    //get List of items in playList
    @Test(priority = 6)
    public void playListItems_GET_Request() {
        Response response = given()
                .accept(JSON)
                .contentType(JSON)
                .header("Authorization", token)
                .when().accept(JSON)
                .get("https://api.spotify.com/v1/playlists/" + playListArray[0] + "/tracks");
        int totalTracks = response.path("total");
        trackIdArray = new String[totalTracks];
        for(int index = 0; index < trackIdArray.length; index++) {
            trackIdArray[index] = response.path("items[" + index + "].track.uri");
        }
    }

    //here we are changing the name of playlist
    @Test(priority = 7)
    public void changeName_PUT_Request() {
        Response response = given()
                .accept(JSON)
                .contentType(JSON)
                .header("Authorization", token)
                .body("{\"name\": \"PunjabiMix_Changed\",\"description\": \"New playlist description\",\"public\": true}")
                .when()
                .put("https://api.spotify.com/v1/playlists/" + playListArray[0] + "");
        //Validation OF Status Code
        response.then().assertThat().statusCode(200);
    }
}
