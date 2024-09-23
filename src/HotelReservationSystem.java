import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "19112003";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("con.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner input = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an Option : ");
                int choice = input.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(input, statement);
                        break;
                    case 2:
                        viewReservations(statement);
                        break;
                    case 3:
                        getRoomNumber(connection, input);
                        break;
                    case 4:
                        updateReservation(input, statement);
                        break;
                    case 5:
                        deleteReservations(input, statement);
                        break;
                    case 0:
                        exit();
                        input.close();
                        return;
                    default:
                        System.out.println("Invalid choice!! Try again.");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Scanner input, Statement statement){
        System.out.print("Enter guest name : ");
        String guestName = input.next();
        input.nextLine();
        System.out.print("Enter room number : ");
        int roomNumber = input.nextInt();
        System.out.print("Enter contact number : ");
        String contactNumber = input.next();

        String sql = "INSERT INTO reservation (guest_name, room_number, contact_number)" +
                "VALUES('" + guestName + "', + " + roomNumber + ", '" + contactNumber + "')";

        try {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0){
                System.out.println("Reservation Successful!!");
            }else {
                System.out.println("Reservation Failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void viewReservations(Statement statement) throws SQLException{
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";

        try(ResultSet result = statement.executeQuery(sql)){
            System.out.println("Current Reservations : ");
            System.out.println("+---------------------+--------------------+--------------------+--------------------+--------------------+");
            System.out.println("| Reservation ID      | Guest              | Room Number        | Contact Number     | Reservation Date   |");
            System.out.println("+---------------------+--------------------+--------------------+--------------------+--------------------+");

            while (result.next()){
                int reservationId = result.getInt("reservation_id");
                String guestName = result.getString("guest_name");
                int roomNumber = result.getInt("room_number");
                String contactNumber = result.getString("contact_number");
                String reservationDate = result.getString("reservation_date").toString();

                //Format and Display the reservation data in a table-like format
                System.out.printf("| %-19d | %-18s | %-18d | %-18s | %-16s|\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+---------------------+--------------------+--------------------+--------------------+--------------------+");
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Scanner input, Statement statement){
        System.out.print("Enter reservation ID to update : ");
        int reservationID = input.nextInt();
        input.nextLine();

        if (!reservationExists(reservationID, statement)){
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = input.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = input.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = input.next();

        String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                "room_number = " + newRoomNumber + ", contact_number = '" + newContactNumber + "' " +
                "WHERE reservation_id = " + reservationID;

        try {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0){
                System.out.println("Reservation update successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteReservations(Scanner input, Statement statement){
        System.out.print("Enter reservation ID to delete : ");
        int reservationID = input.nextInt();

        if (!reservationExists(reservationID, statement)){
            System.out.println("Reservation is not found for the given ID");
            return;
        }

        String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationID;

        try {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0){
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Reservation deletion failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean reservationExists(int reservationID, Statement statement){
        String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationID;

        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!");
    }
}
