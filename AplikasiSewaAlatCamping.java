// Library yang digunakan
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

// Class utama AplikasiSewaAlatCamping yang merupakan turunan dari Application
public class AplikasiSewaAlatCamping extends Application {
    private Map<String, Integer> inventaris = new HashMap<>();
    private Map<String, Integer> hargaBarang = new HashMap<>();
    private Map<String, Integer> diskonBarang = new HashMap<>();
    private Map<String, Integer> riwayatStok = new HashMap<>();
    private Map<String, Integer> barangSewaPenyewa = new HashMap<>();
    private String namaPenyewa;
    private String alamatPenyewa;

    private ListView<String> listViewBarang;
    private TextArea textAreaRiwayatStok;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Penyewaan Peralatan Camping");

        // Inisialisasi data barang, harga, dan diskon
        inisialisasiInventaris();
        inisialisasiHargaBarang();
        inisialisasiDiskonBarang();
        inisialisasiRiwayatStok(); // tambahan pemanggilan metode inisialisasiRiwayatStok

        // Membuat komponen GUI
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label labelJenisPengguna = new Label("Pilih Peran:");
        ToggleGroup groupJenisPengguna = new ToggleGroup();

        RadioButton radioAdmin = new RadioButton("Admin");
        radioAdmin.setToggleGroup(groupJenisPengguna);

        RadioButton radioPenyewa = new RadioButton("Penyewa");
        radioPenyewa.setToggleGroup(groupJenisPengguna);

        grid.add(labelJenisPengguna, 0, 0);
        grid.add(radioAdmin, 1, 0);
        grid.add(radioPenyewa, 2, 0);

        // Komponen untuk Admin
        Label labelAdmin = new Label("Admin Panel:");
        labelAdmin.setVisible(false);

        Label labelNamaBarang = new Label("Nama Barang:");
        TextField fieldNamaBarang = new TextField();

        Label labelJumlahBarang = new Label("Jumlah Barang:");
        Spinner<Integer> spinnerJumlahBarang = new Spinner<>(0, Integer.MAX_VALUE, 0);

        Label labelHargaBarang = new Label("Harga Barang:");
        TextField fieldHargaBarang = new TextField();

        Label labelDiskonBarang = new Label("Diskon Barang (%):");
        TextField fieldDiskonBarang = new TextField();

        Button tombolTambahBarang = new Button("Tambah/Ubah Barang");
        tombolTambahBarang.setOnAction(e -> {
            String namaBarang = fieldNamaBarang.getText();
            int jumlahBarang = spinnerJumlahBarang.getValue();
            int harga = Integer.parseInt(fieldHargaBarang.getText());
            int diskon = Integer.parseInt(fieldDiskonBarang.getText());

            inventaris.put(namaBarang, jumlahBarang);
            hargaBarang.put(namaBarang, harga);
            diskonBarang.put(namaBarang, diskon);

            int stokAwal = riwayatStok.getOrDefault(namaBarang, 0);
            riwayatStok.put(namaBarang, stokAwal + jumlahBarang);

            updateListView();
            System.out.println("Barang ditambahkan/ubah: " + namaBarang + " (" + jumlahBarang + ") - Harga: $" + harga + ", Diskon: " + diskon + "%");
        });

        grid.add(labelAdmin, 0, 1);
        grid.add(labelNamaBarang, 0, 2);
        grid.add(fieldNamaBarang, 1, 2);
        grid.add(labelJumlahBarang, 0, 3);
        grid.add(spinnerJumlahBarang, 1, 3);
        grid.add(labelHargaBarang, 0, 4);
        grid.add(fieldHargaBarang, 1, 4);
        grid.add(labelDiskonBarang, 0, 5);
        grid.add(fieldDiskonBarang, 1, 5);
        grid.add(tombolTambahBarang, 0, 6, 2, 1);

        // Komponen untuk Penyewa
        Label labelPenyewa = new Label("Penyewa Panel:");
        labelPenyewa.setVisible(false);

        Label labelNamaPenyewa = new Label("Nama Penyewa:");
        TextField fieldNamaPenyewa = new TextField();

        Label labelSewaBarang = new Label("Sewa Barang:");
        listViewBarang = new ListView<>(FXCollections.observableArrayList(inventaris.keySet()));
        listViewBarang.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label labelJumlahSewa = new Label("Jumlah Sewa:");
        Spinner<Integer> spinnerJumlahSewa = new Spinner<>(0, Integer.MAX_VALUE, 0);

        Label labelAlamat = new Label("Alamat Pengiriman:");
        TextField fieldAlamat = new TextField();

        Button tombolSewaBarang = new Button("Sewa Barang");
        tombolSewaBarang.setOnAction(e -> {
            ObservableList<String> selectedItems = listViewBarang.getSelectionModel().getSelectedItems();
            namaPenyewa = fieldNamaPenyewa.getText();
            alamatPenyewa = fieldAlamat.getText();

            for (String barangDipilih : selectedItems) {
                if (inventaris.containsKey(barangDipilih)) {
                    int stokAwal = inventaris.get(barangDipilih);
                    int jumlahSewa = spinnerJumlahSewa.getValue();

                    if (jumlahSewa <= stokAwal) {
                        inventaris.put(barangDipilih, stokAwal - jumlahSewa);
                        barangSewaPenyewa.put(barangDipilih, jumlahSewa);
                        System.out.println("Barang disewa: " + barangDipilih + " - Jumlah: " + jumlahSewa);
                    } else {
                        System.out.println("Stok tidak mencukupi untuk disewa: " + barangDipilih);
                    }
                } else {
                    System.out.println("Barang tidak tersedia untuk disewa: " + barangDipilih);
                }
            }
            cetakStruk();
        });

        grid.add(labelPenyewa, 0, 7);
        grid.add(labelNamaPenyewa, 0, 8);
        grid.add(fieldNamaPenyewa, 1, 8);
        grid.add(labelSewaBarang, 0, 9);
        grid.add(listViewBarang, 1, 9);
        grid.add(labelJumlahSewa, 0, 10);
        grid.add(spinnerJumlahSewa, 1, 10);
        grid.add(labelAlamat, 0, 11);
        grid.add(fieldAlamat, 1, 11);
        grid.add(tombolSewaBarang, 0, 12, 2, 1);

        // Riwayat Stok untuk Admin
        Label labelRiwayatStok = new Label("Riwayat Stok:");
        textAreaRiwayatStok = new TextArea();
        textAreaRiwayatStok.setEditable(false);

        Button tombolRiwayatStok = new Button("Riwayat Stok");
        tombolRiwayatStok.setOnAction(e -> {
            updateRiwayatStok();
        });

        grid.add(labelRiwayatStok, 0, 13);
        grid.add(tombolRiwayatStok, 1, 13);
        grid.add(textAreaRiwayatStok, 1, 14);

        groupJenisPengguna.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == radioAdmin) {
                labelAdmin.setVisible(true);
                labelPenyewa.setVisible(false);
                labelNamaPenyewa.setVisible(false);
                fieldNamaPenyewa.setVisible(false);
                labelSewaBarang.setVisible(false);
                listViewBarang.setVisible(false);
                labelJumlahSewa.setVisible(false);
                spinnerJumlahSewa.setVisible(false);
                labelAlamat.setVisible(false);
                fieldAlamat.setVisible(false);
                tombolSewaBarang.setVisible(false);
                tombolRiwayatStok.setVisible(true);

                // Menampilkan kolom-kolom Admin
                labelNamaBarang.setVisible(true);
                fieldNamaBarang.setVisible(true);
                labelJumlahBarang.setVisible(true);
                spinnerJumlahBarang.setVisible(true);
                labelHargaBarang.setVisible(true);
                fieldHargaBarang.setVisible(true);
                labelDiskonBarang.setVisible(true);
                fieldDiskonBarang.setVisible(true);
                tombolTambahBarang.setVisible(true);
            } else if (newValue == radioPenyewa) {
                labelAdmin.setVisible(false);
                labelPenyewa.setVisible(true);
                labelNamaBarang.setVisible(false);
                fieldNamaBarang.setVisible(false);
                labelJumlahBarang.setVisible(false);
                spinnerJumlahBarang.setVisible(false);
                labelHargaBarang.setVisible(false);
                fieldHargaBarang.setVisible(false);
                labelDiskonBarang.setVisible(false);
                fieldDiskonBarang.setVisible(false);
                tombolTambahBarang.setVisible(false);
                tombolRiwayatStok.setVisible(false);

                // Menampilkan kolom-kolom Penyewa
                labelNamaPenyewa.setVisible(true);
                fieldNamaPenyewa.setVisible(true);
                labelSewaBarang.setVisible(true);
                listViewBarang.setVisible(true);
                labelJumlahSewa.setVisible(true);
                spinnerJumlahSewa.setVisible(true);
                labelAlamat.setVisible(true);
                fieldAlamat.setVisible(true);
                tombolSewaBarang.setVisible(true);
            }
        });

         // Membuat scene dan menampilkan stage
        Scene scene = new Scene(grid, 400, 650);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

     // Metode untuk memperbarui data pada ListView
    private void updateListView() {
        listViewBarang.getItems().setAll(inventaris.keySet());
    }

     // Metode untuk menginisialisasi data inventaris
    private void inisialisasiInventaris() {
        inventaris.put("Tenda", 10);
        inventaris.put("Sleeping Bag", 20);
        inventaris.put("Kompor Camping", 5);
        inventaris.put("Lentera", 15);
    }

    // Metode untuk menginisialisasi data harga barang
    private void inisialisasiHargaBarang() {
        hargaBarang.put("Tenda", 50);
        hargaBarang.put("Sleeping Bag", 20);
        hargaBarang.put("Kompor Camping", 30);
        hargaBarang.put("Lentera", 10);
    }

    // Metode untuk menginisialisasi data diskon barang
    private void inisialisasiDiskonBarang() {
        diskonBarang.put("Tenda", 5);
        diskonBarang.put("Sleeping Bag", 0);
        diskonBarang.put("Kompor Camping", 10);
        diskonBarang.put("Lentera", 8);
    }

    // Metode untuk menginisialisasi riwayat stok
    private void inisialisasiRiwayatStok() {
        for (Map.Entry<String, Integer> entry : inventaris.entrySet()) {
            String namaBarang = entry.getKey();
            int stokAwal = entry.getValue();
            riwayatStok.put(namaBarang, stokAwal);
        }
    }

    // Metode untuk mencetak struk penyewaan
    private void cetakStruk() {
        System.out.println("\n===== Struk Penyewaan =====");
        System.out.println("Nama Penyewa      : " + namaPenyewa);
        System.out.println("Alamat Pengiriman : " + alamatPenyewa);

        int totalHarga = 0;

        for (Map.Entry<String, Integer> entry : barangSewaPenyewa.entrySet()) {
            String namaBarang = entry.getKey();
            int jumlah = entry.getValue();
            int harga = hargaBarang.get(namaBarang);
            int diskon = diskonBarang.get(namaBarang);

            System.out.println("\nBarang            : " + namaBarang);
            System.out.println("Jumlah            : " + jumlah);
            System.out.println("Harga per Barang  : $" + harga);
            System.out.println("Diskon            : " + diskon + "%");

            int totalSebelumDiskon = jumlah * harga;
            double jumlahDiskon = totalSebelumDiskon * (diskon / 100.0);
            int totalSetelahDiskon = (int) (totalSebelumDiskon - jumlahDiskon);

            System.out.println("Total Harga       : $" + totalSebelumDiskon);
            System.out.println("Total Diskon      : $" + jumlahDiskon);
            System.out.println("Total Bayar       : $" + totalSetelahDiskon);

            totalHarga += totalSetelahDiskon;
        }

        System.out.println("\nTotal Harga Keseluruhan: $" + totalHarga);
        System.out.println("===========================\n");
    }

     // Metode untuk memperbarui riwayat stok
    private void updateRiwayatStok() {
        StringBuilder riwayat = new StringBuilder();
        for (Map.Entry<String, Integer> entry : riwayatStok.entrySet()) {
            riwayat.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        textAreaRiwayatStok.setText(riwayat.toString());
    }
}
