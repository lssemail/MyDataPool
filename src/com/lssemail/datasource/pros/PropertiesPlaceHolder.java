package com.lssemail.datasource.pros;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesPlaceHolder extends Properties {

    private static final long serialVersionUID = 1L;
    private static final String DB_PROS = "data.properties";

    private static PropertiesPlaceHolder holder = new PropertiesPlaceHolder();

    private PropertiesPlaceHolder(){
        try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(DB_PROS)) {
            this.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesPlaceHolder getInstance(){

        return holder;
    }

}

