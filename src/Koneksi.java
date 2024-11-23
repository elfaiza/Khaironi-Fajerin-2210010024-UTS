/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Disporapar HST
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {

    // Path ke database SQLite
    private static final String DB_URL = "jdbc:sqlite:keuangan.db";

    // Metode untuk mendapatkan koneksi ke database
    public static Connection connect() {
        Connection conn = null;
        try {
            // Mencoba menghubungkan ke database SQLite
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Koneksi ke database SQLite berhasil!");
        } catch (SQLException e) {
            System.out.println("Error saat menghubungkan ke database: " + e.getMessage());
        }
        return conn;
    }

    static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

