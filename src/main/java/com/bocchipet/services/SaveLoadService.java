package com.bocchipet.services;

import com.bocchipet.models.Player;
import com.bocchipet.models.PlayerDataDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveLoadService {

    private Gson gson;
    // Semua file save bakal disimpen di folder "saves" disebelah file .jar nanti
    private final Path SAVE_PATH = Paths.get("saves");

    public SaveLoadService() {

        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        // Buat cek folder "saves" kalo belum ada
        try {
            if (!Files.exists(SAVE_PATH)) {
                Files.createDirectories(SAVE_PATH);
                System.out.println("Folder 'saves' berhasil dibuat.");
            }
        } catch (IOException e) {
            System.err.println("Gagal buat folder 'saves': " + e.getMessage());
        }
    }

    public void saveGame(Player player, String slotName) {
        PlayerDataDTO data = new PlayerDataDTO();
        data.setSanity(player.getSanity());
        data.setFood(player.getFood());
        data.setMoney(player.getMoney());
        data.setCurrentGuitarImage(player.getCurrentGuitarImage());

        Path filePath = SAVE_PATH.resolve(slotName + ".json");

        try (Writer writer = new FileWriter(filePath.toFile())) {
            gson.toJson(data, writer);
            System.out.println("Game berhasil disimpan ke: " + filePath);
        } catch (IOException e) {
            System.err.println("Gagal nyimoen game: " + e.getMessage());
        }
    }

    public Player loadGame(String slotName) {
        Path filePath = SAVE_PATH.resolve(slotName + ".json");

        if (!Files.exists(filePath)) {
            System.err.println("File save gak ditemuin: " + filePath);
            return new Player();
        }

        try (Reader reader = new FileReader(filePath.toFile())) {
            // 1. Baca JSON dan konversi ke DTO(Data Transfer Object)
            PlayerDataDTO data = gson.fromJson(reader, PlayerDataDTO.class);

            // 2. Buat Player baru biar ke status dari DTO
            Player loadedPlayer = new Player();
            loadedPlayer.addSanity(data.getSanity() - loadedPlayer.getSanity());
            loadedPlayer.addFood(data.getFood() - loadedPlayer.getFood());
            loadedPlayer.addMoney(data.getMoney() - loadedPlayer.getMoney());
            loadedPlayer.setCurrentGuitarImage(data.getCurrentGuitarImage());

            System.out.println("Game berhasil dimuat dari: " + filePath);
            return loadedPlayer;

        } catch (IOException e) {
            System.err.println("Gagal memuat game: " + e.getMessage());
            return new Player();
        }
    }
}