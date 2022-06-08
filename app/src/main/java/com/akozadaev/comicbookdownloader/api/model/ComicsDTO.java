package com.akozadaev.comicbookdownloader.api.model;

import java.util.ArrayList;
import java.util.Date;

// generate https://json2csharp.com/code-converters/json-to-pojo
public class ComicsDTO {
    public String getName() {
    }

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    public class Comics{
        public int available;
        public String collectionURI;
        public ArrayList<Item> items;
        public int returned;
    }

    public class Data{
        public int offset;
        public int limit;
        public int total;
        public int count;
        public ArrayList<Result> results;
    }

    public class Events{
        public int available;
        public String collectionURI;
        public ArrayList<Item> items;
        public int returned;
    }

    public class Item{
        public String resourceURI;
        public String name;
        public String type;

    public class Result{
        public int id;
        public String name;
        public String description;
        public Date modified;
        public Thumbnail thumbnail;
        public String resourceURI;
        public Comics comics;
        public Series series;
        public Stories stories;
        public Events events;
        public ArrayList<Url> urls;
    }

    public class Root{
        public int code;
        public String status;
        public String copyright;
        public String attributionText;
        public String attributionHTML;
        public String etag;
        public Data data;
    }

    public class Series{
        public int available;
        public String collectionURI;
        public ArrayList<Item> items;
        public int returned;
    }

    public class Stories{
        public int available;
        public String collectionURI;
        public ArrayList<Item> items;
        public int returned;
    }

    public class Thumbnail{
        public String path;
        public String extension;
    }

    public class Url{
        public String type;
        public String url;
    }


}
