/**
  * Copyright 2019 bejson.com 
  */
package com.yibao.music.model.album;
import java.util.List;

/**
 * Auto-generated: 2019-12-01 15:15:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Result {

    private int artistCount;
    private List<Artists> artists;
    public void setArtistCount(int artistCount) {
         this.artistCount = artistCount;
     }
     public int getArtistCount() {
         return artistCount;
     }

    public void setArtists(List<Artists> artists) {
         this.artists = artists;
     }
     public List<Artists> getArtists() {
         return artists;
     }

}