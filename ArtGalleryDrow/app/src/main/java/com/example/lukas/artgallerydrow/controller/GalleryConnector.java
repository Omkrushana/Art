package com.example.lukas.artgallerydrow.controller;

/**
 * Created by plame_000 on 29-Sep-17.
 */

public class GalleryConnector {

    GalleryConnector(){}

    public static abstract class User{
        public static final String ID = "id";
        public static final String NAME  = "name";
        public static final String PASS = "password";
        public static final String EMAIL = "email";
        public static final String MONEY = "money";
        public static final String ADDRESS = "address";
        public static final String TYPE = "type";
        public static final String TABLE_NAME = "Users";
    }

    public static abstract class Type{
        public static final String ID = "id";
        public static final String Type = "type";
        public static final String TABLE_NAME = "Types";
    }

    public static abstract class SubType{
        public static final String ID = "id";
        public static final String SUBTYPE = "subtype";
        public static final String TYPE_ID = "type_id";
        public static final String TABLE_NAME = "Subtypes";
    }

    public static abstract class Items{
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String PRICE = "price";
        public static final String TYPE_ITEM = "typeItem";
        public static final String SUBTYPE_ITEM = "subtypeItem";
        public static final String DESCRIPTION = "description";
        public static final String SELLER_ID = "seller_id";
        public static final String BUYER_ID = "buyer_id";
        public static final String TABLE_NAME = "Items";
    }

    public static abstract class Pictures{
        public static final String ID = "id";
        public static final String ORIGINAL_PIC = "originalPic";
        public static final String SOLD_PIC = "soldPic";
        public static final String ITEM_ID = "item_id";
        public static final String TABLE_NAME = "Pictures";
    }

}
