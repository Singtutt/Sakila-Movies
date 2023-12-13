package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    private static final String URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String USER = "root";
    private static final String PASS = "~CONFIDENTIAL~";

    public static void main( String[] args ) {
    //  Database Information
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASS);

        try (Scanner scan = new Scanner(System.in)) {
            boolean exit = false;
            int option;

            while (!exit) {
                System.out.println("""
                        -Main Menu-
                        \t 1. Search Last Name For Actor.
                        \t 0. Exit
                        Select An Option:""");

                option = scan.nextInt();
                scan.nextLine();

                switch (option) {
                    case 1:
                        System.out.println("Enter Last Name For Actor: ");
                        String lastName = scan.nextLine();
                        actorsLastName(dataSource, lastName, scan);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("\n-----Invalid Option. Please Input A Valid Option-----\n");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void actorsLastName(BasicDataSource dataSource, String lastNames, Scanner scan) throws SQLException {

        String query = "SELECT actor_id, first_name, last_name FROM actor WHERE last_name = ? ORDER BY first_name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, lastNames);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("\t\t\t\t-----Searching Under " + lastNames + "-----\n");
                System.out.printf("| %-10s | %-20s | %-20s |%n" , "ID", "First Name", "Last Name");
                System.out.println("-------------------------------------------------------------------------");

                do {
                    int id = resultSet.getInt("actor_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    System.out.printf("| %-10d | %-20s | %-20s |%n", id, firstName, lastName);
                } while (resultSet.next());

                System.out.println("\n Choose An Actor (ID) To View Film Information | Enter '0' To Go Back To The Main Menu: \n");
                int id = scan.nextInt();

                if (id != 0) {
                    actorFilms(dataSource, id);
                } else {
                    System.out.println("\n\t\t\t\t-----Redirecting to Main Menu-----\n");
                }
            } else {
                System.out.println("\t\t\t\t-----Error Searching Under " + lastNames + "-----\n");
            }
        }
    }

    private static void actorFilms(BasicDataSource dataSource, int id) throws SQLException {
        String queryActorName = ("""
                SELECT first_name, last_name
                FROM actor
                WHERE actor_id = ?""");
        String queryFilms = ("""
                SELECT film.title FROM film
                JOIN film_actor ON film.film_id = film_actor.film_id
                WHERE film_actor.actor_id =?""");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement actorNameStatement = connection.prepareStatement(queryActorName);
             PreparedStatement filmsStatement = connection.prepareStatement(queryFilms)) {

            filmsStatement.setInt(1, id);
            ResultSet filmsSet = filmsStatement.executeQuery();

            actorNameStatement.setInt(1, id);
            ResultSet actorNameSet = actorNameStatement.executeQuery();
            String actorName = "";

            if (actorNameSet.next()) {
                String firstName = actorNameSet.getString("first_name");
                String lastName = actorNameSet.getString("last_name");
                actorName = firstName + " " + lastName;
            }

            System.out.println("\t\t\t\t------Film Inventory Under " + actorName + "-----\n");
            System.out.printf("%-30s%n", "Film Title");
            System.out.println("---------------------------------------");

            while (filmsSet.next()) {
                String title = filmsSet.getString("title");
                System.out.printf("| %-30s |%n", title);
            }
            System.out.println("\n\t\t\t\t------Redirecting to Main Menu-----\n");
        }
    }
}


