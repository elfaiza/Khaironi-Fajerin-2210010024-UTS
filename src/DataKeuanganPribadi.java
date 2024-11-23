import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Vector;

public class DataKeuanganPribadi extends JFrame {
    private Connection conn;
    private JTable table;
    private JButton btnEdit, btnDelete, btnBack;
    private JLabel lblTotalPemasukan, lblTotalPengeluaran, lblSaldo;

    public DataKeuanganPribadi() {
        // Setup koneksi database
        conn = Koneksi.connect();  // Pastikan Koneksi.connect() sesuai dengan class koneksi database Anda

        // Setup UI components
        setTitle("Data Keuangan Pribadi");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel);

        // Tabel untuk menampilkan data keuangan
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk menampilkan total dan saldo
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 1));

        lblTotalPemasukan = new JLabel("Total Pemasukan: Rp. 0");
        lblTotalPemasukan.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalPemasukan.setForeground(new Color(0, 128, 0));

        lblTotalPengeluaran = new JLabel("Total Pengeluaran: Rp. 0");
        lblTotalPengeluaran.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalPengeluaran.setForeground(Color.RED);

        lblSaldo = new JLabel("Saldo: Rp. 0");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 22));
        lblSaldo.setForeground(Color.BLACK);

        infoPanel.add(lblTotalPemasukan);
        infoPanel.add(lblTotalPengeluaran);
        infoPanel.add(lblSaldo);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Tombol Edit, Hapus, dan Kembali
        JPanel buttonPanel = new JPanel();
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnBack = new JButton("Kembali");

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Tombol Edit
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String tanggal = table.getValueAt(selectedRow, 0).toString();
                    String pemasukan = table.getValueAt(selectedRow, 1).toString();
                    String pengeluaran = table.getValueAt(selectedRow, 2).toString();
                    String keterangan = table.getValueAt(selectedRow, 3).toString();  // Menambahkan keterangan
                    editData(tanggal, pemasukan, pengeluaran, keterangan, selectedRow);
                } else {
                    JOptionPane.showMessageDialog(DataKeuanganPribadi.this, "Pilih data untuk diedit.");
                }
            }
        });

        // Tombol Hapus
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String tanggal = table.getValueAt(selectedRow, 0).toString();
                    deleteData(tanggal, selectedRow);
                } else {
                    JOptionPane.showMessageDialog(DataKeuanganPribadi.this, "Pilih data untuk dihapus.");
                }
            }
        });

        // Tombol Kembali
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Menutup DataKeuanganPribadi dan membuka AplikasiKeuanganPribadi
                new Aplikasikeuanganpribadi().setVisible(true); // Ganti dengan nama class aplikasi utama Anda
                dispose(); // Menutup jendela DataKeuanganPribadi
            }
        });

        // Load data ke tabel saat frame dibuka
        loadData();
    }

    // Fungsi untuk memuat data dari database ke JTable
    private void loadData() {
        try {
            // Update query untuk mengurutkan data berdasarkan tanggal terbaru
            String sql = "SELECT * FROM keuangan ORDER BY tanggal DESC";  // Mengurutkan berdasarkan tanggal (desc)
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Membaca metadata kolom
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Menyiapkan model tabel
            Vector<String> columns = new Vector<>();
            columns.add("Tanggal");
            columns.add("Pemasukan");
            columns.add("Pengeluaran");
            columns.add("Keterangan");  // Menambahkan kolom Keterangan

            // Mengambil data
            Vector<Vector<Object>> data = new Vector<>();
            double totalPemasukan = 0, totalPengeluaran = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    if (i == 2 || i == 3) { // Misalkan kolom 2 dan 3 adalah pemasukan dan pengeluaran
                        double amount = rs.getDouble(i);
                        DecimalFormat df = new DecimalFormat("#,###");
                        row.add(df.format(amount));  // Format angka

                        // Menambahkan total pemasukan dan pengeluaran
                        if (i == 2) {
                            totalPemasukan += amount;
                        } else {
                            totalPengeluaran += amount;
                        }
                    } else if (i == 4) { // Kolom Keterangan
                        row.add(rs.getString(i)); // Ambil data keterangan dari kolom keempat
                    } else {
                        row.add(rs.getObject(i));
                    }
                }
                data.add(row);
            }

            // Menetapkan model tabel
            DefaultTableModel model = new DefaultTableModel(data, columns);
            table.setModel(model);

            // Menampilkan total pemasukan dan pengeluaran
            DecimalFormat df = new DecimalFormat("#,###");
            lblTotalPemasukan.setText("Total Pemasukan: Rp. " + df.format(totalPemasukan));
            lblTotalPengeluaran.setText("Total Pengeluaran: Rp. " + df.format(totalPengeluaran));

            // Menampilkan saldo
            double saldo = totalPemasukan - totalPengeluaran;
            lblSaldo.setText("Saldo: Rp. " + df.format(saldo));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fungsi untuk mengedit data
    private void editData(String tanggal, String pemasukan, String pengeluaran, String keterangan, int selectedRow) {
        // Menampilkan dialog untuk mengedit data
        JPanel panel = new JPanel(new GridLayout(4, 2));  // Mengubah menjadi 4 baris untuk menampung keterangan

        panel.add(new JLabel("Pemasukan:"));
        JTextField txtPemasukan = new JTextField(pemasukan);
        panel.add(txtPemasukan);

        panel.add(new JLabel("Pengeluaran:"));
        JTextField txtPengeluaran = new JTextField(pengeluaran);
        panel.add(txtPengeluaran);

        panel.add(new JLabel("Keterangan:"));
        JTextField txtKeterangan = new JTextField(keterangan);  // Menambahkan kolom untuk keterangan
        panel.add(txtKeterangan);

        int option = JOptionPane.showConfirmDialog(this, panel, "Edit Data", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                double newPemasukan = Double.parseDouble(txtPemasukan.getText());
                double newPengeluaran = Double.parseDouble(txtPengeluaran.getText());
                String newKeterangan = txtKeterangan.getText();

                // Update database
                String sql = "UPDATE keuangan SET pemasukan = ?, pengeluaran = ?, keterangan = ? WHERE tanggal = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setDouble(1, newPemasukan);
                ps.setDouble(2, newPengeluaran);
                ps.setString(3, newKeterangan);  // Update keterangan
                ps.setString(4, tanggal);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                loadData();  // Reload data setelah pengeditan
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fungsi untuk menghapus data
    private void deleteData(String tanggal, int selectedRow) {
        try {
            String sql = "DELETE FROM keuangan WHERE tanggal = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tanggal);
            int result = ps.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadData();  // Reload data setelah penghapusan
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new DataKeuanganPribadi().setVisible(true);
    }
}
