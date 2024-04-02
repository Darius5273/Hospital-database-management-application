package com.example.pr2.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Room extends DBConnection{
    private int roomId;
    private String type;
    private int chargesPerDay;

    public Room(int roomId, String type, int chargesPerDay) {
        this();
        this.roomId = roomId;
        this.type = type;
        this.chargesPerDay = chargesPerDay;
    }

    public Room() {
        super();
        this.roomId = -1;
        this.type = null;
        this.chargesPerDay = -1;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChargesPerDay() {
        return chargesPerDay;
    }

    public void setChargesPerDay(int chargesPerDay) {
        this.chargesPerDay = chargesPerDay;
    }


    public static ObservableList<Room> getRoomsByType(String roomType) {
        ObservableList<Room> rooms = FXCollections.observableArrayList();
        StringBuilder queryBuilder = new StringBuilder("SELECT room_id, type, charges_day FROM rooms ");

        // Check if a roomType is specified
        if (roomType != null && !roomType.isEmpty()) {
            queryBuilder.append("WHERE LOWER(type) LIKE LOWER(?) ");
        }

        queryBuilder.append("ORDER BY room_id");

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            if (roomType != null && !roomType.isEmpty()) {
                preparedStatement.setString(1, "%" + roomType + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int roomId = resultSet.getInt("room_id");
                    String type = resultSet.getString("type");
                    int chargesPerDay = resultSet.getInt("charges_day");

                    Room room = new Room(roomId, type, chargesPerDay);
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return rooms;
    }


    public static ObservableList<Room> getRoomsByTypeAndTime(String roomType, String admissionDate, String dischargeDate) {
        ObservableList<Room> roomsByTypeAndTime = FXCollections.observableArrayList();
        if(dischargeDate!=null) {
        String query = "SELECT room_id, type, charges_day FROM rooms WHERE LOWER(type) LIKE LOWER(?) " +
                "AND room_id NOT IN " +
                "(SELECT room_id FROM admissions " +
                "WHERE ((? BETWEEN admission_date AND COALESCE(discharge_date, 'infinity')) " +
                "OR (? BETWEEN admission_date AND COALESCE(discharge_date, 'infinity'))) " +
                "OR (admission_date BETWEEN ? AND ?) " +
                "OR (discharge_date is not null AND discharge_date BETWEEN ? AND ?)) " +
                "ORDER BY room_id";

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setString(1, "%" + roomType + "%");
        preparedStatement.setDate(2, java.sql.Date.valueOf(admissionDate));
        preparedStatement.setDate(3, java.sql.Date.valueOf(dischargeDate));
        preparedStatement.setDate(4, java.sql.Date.valueOf(admissionDate));
        preparedStatement.setDate(5, java.sql.Date.valueOf(dischargeDate));
        preparedStatement.setDate(6, java.sql.Date.valueOf(admissionDate));
        preparedStatement.setDate(7, java.sql.Date.valueOf(dischargeDate));


        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                String type = resultSet.getString("type");
                int chargesPerDay = resultSet.getInt("charges_day");

                Room room = new Room(roomId, type, chargesPerDay);
                roomsByTypeAndTime.add(room);
            }
        }
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
}
else {
     String query = "SELECT room_id, type, charges_day FROM rooms WHERE LOWER(type) LIKE LOWER(?) " +
                    "AND room_id NOT IN " +
                    "(SELECT room_id FROM admissions " +
                    "WHERE ((? BETWEEN admission_date AND COALESCE(discharge_date, 'infinity')) " +
                    "OR (admission_date BETWEEN ? AND 'infinity'))) " +
                    "ORDER BY room_id";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setString(1, "%" + roomType + "%");
        preparedStatement.setDate(2, java.sql.Date.valueOf(admissionDate));
        preparedStatement.setDate(3, java.sql.Date.valueOf(admissionDate));


        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                String type = resultSet.getString("type");
                int chargesPerDay = resultSet.getInt("charges_day");

                Room room = new Room(roomId, type, chargesPerDay);
                roomsByTypeAndTime.add(room);
            }
        }
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
}
        return roomsByTypeAndTime;
    }

    public static ObservableList<Room> getRoomsByTime(String admissionDate, String dischargeDate) {
        ObservableList<Room> roomsByTime = FXCollections.observableArrayList();
        if(dischargeDate!=null) {
            String query = "SELECT room_id, type, charges_day FROM rooms WHERE " +
                    "room_id NOT IN " +
                    "(SELECT room_id FROM admissions " +
                    "WHERE ((? BETWEEN admission_date AND COALESCE(discharge_date, 'infinity')) " +
                    "OR (? BETWEEN admission_date AND COALESCE(discharge_date, 'infinity'))) " +
                    "OR (admission_date BETWEEN ? AND ?) " +
                    "OR (discharge_date is not null AND discharge_date BETWEEN ? AND ?)) " +
                    "ORDER BY room_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, java.sql.Date.valueOf(admissionDate));
                preparedStatement.setDate(2, java.sql.Date.valueOf(dischargeDate));
                preparedStatement.setDate(3, java.sql.Date.valueOf(admissionDate));
                preparedStatement.setDate(4, java.sql.Date.valueOf(dischargeDate));
                preparedStatement.setDate(5, java.sql.Date.valueOf(admissionDate));
                preparedStatement.setDate(6, java.sql.Date.valueOf(dischargeDate));


                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            while (resultSet.next()) {
                                int roomId = resultSet.getInt("room_id");
                                String type = resultSet.getString("type");
                                int chargesPerDay = resultSet.getInt("charges_day");

                                Room room = new Room(roomId, type, chargesPerDay);
                                roomsByTime.add(room);
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
                else {
            String query = "SELECT room_id, type, charges_day FROM rooms WHERE " +
                    "room_id NOT IN " +
                    "(SELECT room_id FROM admissions " +
                    "WHERE ((? BETWEEN admission_date AND COALESCE(discharge_date, 'infinity')) " +
                    "OR (admission_date BETWEEN ? AND 'infinity'))) " +
                    "ORDER BY room_id";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, java.sql.Date.valueOf(admissionDate));
                preparedStatement.setDate(2, java.sql.Date.valueOf(admissionDate));


                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            while (resultSet.next()) {
                                int roomId = resultSet.getInt("room_id");
                                String type = resultSet.getString("type");
                                int chargesPerDay = resultSet.getInt("charges_day");

                                Room room = new Room(roomId, type, chargesPerDay);
                                roomsByTime.add(room);
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
        return roomsByTime;
    }


    public static Room getRoomsById(int roomId) {
        String query = "SELECT room_id, type, charges_day FROM rooms WHERE room_id=? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, roomId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int Id = resultSet.getInt("room_id");
                    String type = resultSet.getString("type");
                    int chargesPerDay = resultSet.getInt("charges_day");

                    Room room = new Room(Id, type, chargesPerDay);
                    return room;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

}
