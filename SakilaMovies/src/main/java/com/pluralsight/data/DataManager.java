package com.pluralsight.data;

import com.pluralsight.model.Actor;
import com.pluralsight.model.Film;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataManager {
    //  Attributes
    private final BasicDataSource dataSource;

    //  Constructor
    public DataManager(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    //  Main Menu Flow
    public void MainMenu() {
        Scanner scan = new Scanner(System.in);
        boolean process = true;

        while (process) {
            displayMenu();
            if (scan.hasNextInt()) {
                int option = scan.nextInt();
                scan.nextLine();
                process = menuOptions(option, scan);
            } else {
                System.out.println("\n\t\t[~Invalid Option: Enter A Valid Option~]\n");
                scan.nextLine();
            }
        }
    }

    public void displayMenu() {
        System.out.println("""
                \n\t\t\t---Main Menu---
                                        
                \t1. Search By Actor.
                \t0. Exit Application.
                                        
                Enter An Option:\s""");
    }

    public boolean menuOptions(int option, Scanner scan) {
        return switch (option) {
            case 0 -> {
                System.out.println("\n\t\t\t\t------Exiting Program... Have a Good Day!-----\n");
                yield false;
            }
            case 1 -> {
                searchActor(scan);
                yield true;
            }
            default -> {
                System.out.println("\n\t\t[~Invalid Option: Enter A Valid Option~]\n");
                yield true;
            }
        };
    }

    public void searchActor(Scanner scan) {
        //  Prompt User For Actor Name (First/Last)
        System.out.println("\nEnter First Name For Actor: ");
        String firstName = scan.nextLine().trim();
        System.out.println("\nEnter Last Name For Actor: ");
        String lastName = scan.nextLine().trim();
        List<Actor> actors;

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            actors = sqlActorName(firstName, lastName);
        } else if (!firstName.isEmpty()) {
            actors = sqlActorFirst(firstName);
        } else if (!lastName.isEmpty()) {
            actors = sqlActorLast(lastName);
        } else {
            System.out.println("invalid"); //   Changing Thursday
            return;
        }

        if (!actors.isEmpty()) {
            actorsDisplay(actors);
            System.out.println("\nChoose An Actor (ID) To View Film Information | Enter '0' To Go Back To The Main Menu: ");

            try {
                int actorID = scan.nextInt();
                scan.nextLine();

                if (actorID != 0) {
                    List<String> fullNameList = sqlFullName(actorID);
                    if (!fullNameList.isEmpty()) {
                        String fullName = fullNameList.get(0);
                        List<Film> films = sqlFilms(actorID);
                        filmsDisplay(films, fullName);
                    } else {
                        System.out.println("\n\t\t[~Error Forming Full Name~]");
                    }
                }
                System.out.println("\n\t\t\t\t------Redirecting to Main Menu-----\n");
            } catch (Exception e) {
                System.out.println("\n\t\t[~Invalid Option: Enter A Valid Option~]\n");
                scan.nextLine();
            }
        } else {
            System.out.println("\n\t\t[~Error Searching For Actor~]");
        }
    }

    //  SQL: Query Actor (ID, First/Last Name)
    public List<Actor> sqlActorName(String lastName, String firstName) {
        List<Actor> actors = new ArrayList<>();

        String query = ("""
                SELECT actor_id, first_name, last_name
                FROM actor
                WHERE first_name = ? AND last_name = ?
                ORDER BY first_name""");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int actorId = resultSet.getInt("actor_id");
                String nameFirst = resultSet.getString("first_name");
                String nameLast = resultSet.getString("last_name");

                Actor actor = new Actor(actorId, nameFirst, nameLast);
                actors.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }

    public List<String> sqlFullName(int actorID) {

        String queryActorName = ("""
                SELECT first_name, last_name
                FROM actor
                WHERE actor_id = ?""");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement actorStatement = connection.prepareStatement(queryActorName)) {

            actorStatement.setInt(1, actorID);
            ResultSet actorSet = actorStatement.executeQuery();

            List<String> fullNameList = new ArrayList<>();
            if (actorSet.next()) {
                String firstName = actorSet.getString("first_name");
                String lastName = actorSet.getString("last_name");
                fullNameList.add(firstName + " " + lastName);

            }
            return fullNameList;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Actor> sqlActorFirst(String firstName) {
        List<Actor> actors = new ArrayList<>();

        String query = ("""
                SELECT actor_id, first_name, last_name
                FROM actor
                WHERE first_name = ?
                ORDER BY first_name""");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, firstName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int actorId = resultSet.getInt("actor_id");
                String nameFirst = resultSet.getString("first_name");
                String nameLast = resultSet.getString("last_name");

                Actor actor = new Actor(actorId, nameFirst, nameLast);
                actors.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }

    public List<Actor> sqlActorLast(String lastName) {
        List<Actor> actors = new ArrayList<>();

        String query = ("""
                SELECT actor_id, first_name, last_name
                FROM actor
                WHERE last_name = ?
                ORDER BY first_name""");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, lastName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int actorId = resultSet.getInt("actor_id");
                String nameFirst = resultSet.getString("first_name");
                String nameLast = resultSet.getString("last_name");

                Actor actor = new Actor(actorId, nameFirst, nameLast);
                actors.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }

    public List<Film> sqlFilms(int actorID) {
        List<Film> films = new ArrayList<>();

        String queryFilms = ("""
                SELECT film.film_id, title, description, release_year, length
                FROM film
                JOIN film_actor ON film.film_id = film_actor.film_id
                WHERE film_actor.actor_id = ?""");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement filmsStatement = connection.prepareStatement(queryFilms)) {

            filmsStatement.setInt(1, actorID);
            ResultSet filmsSet = filmsStatement.executeQuery();

            while (filmsSet.next()) {
                int filmID = filmsSet.getInt("film_id");
                String title = filmsSet.getString("title");
                String description = filmsSet.getString("description");
                int year = filmsSet.getInt("release_year");
                int length = filmsSet.getInt("length");

                Film film = new Film(filmID, title, year, length, description);
                films.add(film);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    // Display and Format Actor Table.
    public void actorsDisplay(List<Actor> actors) {
        System.out.println("\t\t\t\t-----Actors Inventory----\n");
        System.out.printf("| %-10s | %-20s | %-20s |%n", "ID", "First Name", "Last Name");
        System.out.println("------------------------------------------------------------");

        for (Actor actor : actors) {
            System.out.printf("| %-10d | %-20s | %-20s |%n", actor.getActorID(), actor.getFirstName(), actor.getLastName());
        }
        System.out.println("------------------------------------------------------------");
    }

    // Display and Format Film Table.
    public void filmsDisplay(List<Film> films, String fullName) {
        System.out.println("\t\t\t\t\t\t\t-----Film Inventory Under " + fullName + "-----\n");
        System.out.printf("| %-6s | %-28s | %-13s | %-18s | %-125s |%n", "ID", "Title", "Release Year", "Length (Minutes)", "Description");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Film film : films) {
            System.out.printf("| %-6d | %-28s | %-13d | %-18d | %-125s |%n", film.getFilmID(), film.getTitle(), film.getReleaseYear(), film.getLength(), film.getDescription());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
