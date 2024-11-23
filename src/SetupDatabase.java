    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Disporapar HST
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SetupDatabase {

    public static void main(String[] args) throws SQLException {
        // Tes koneksi
        try (Connection conn = Koneksi.connect()) {
            if (conn != null) {
                System.out.println("Koneksi berhasil. Melanjutkan pembuatan tabel...");
                createTable(conn);
            } else {
                System.out.println("Koneksi gagal. Tidak dapat melanjutkan.");
            }
        }
    }

    // Metode untuk membuat tabel
    public static void createTable(Connection conn) {
        String sql = """
                CREATE TABLE IF NOT EXISTS Keuangan (
                    tanggal DATE NOT NULL,
                    pemasukan REAL DEFAULT NULL,
                    pengeluaran REAL DEFAULT NULL,
                    keterangan TEXT
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel 'Keuangan' berhasil dibuat!");
        } catch (Exception e) {
            System.out.println("Error saat membuat tabel: " + e.getMessage());
        }
    }

    static void setup() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    static Connection connect() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
