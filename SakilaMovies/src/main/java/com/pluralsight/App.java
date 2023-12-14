package com.pluralsight;

import com.pluralsight.data.DataManager;
import org.apache.commons.dbcp2.BasicDataSource;

public class App {

    private static final String URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String USER = "root";
    private static final String PASS = "1550329";

    public static void main(String[] args) {
        //  Database Information
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASS);

        DataManager dataManager = new DataManager(dataSource);
        dataManager.MainMenu();
    }
}
