package com.bocchipet.services;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class AssetManager {

    private Map<String, Image> images;
    private Map<String, Media> sounds;

    public AssetManager() {
        this.images = new HashMap<>();
        this.sounds = new HashMap<>();
    }

    public void loadAssets() {
        System.out.println("Memuat aset...");
        
        // Kunci harus SAMA ama yang di PartTimeJob.java, dll.
        loadImage("partTimeImage", "/images/activities/partTime.png");
        loadImage("bandPracticeImage", "/images/activities/panggung1.png");
        // gambar buat icon, karakter, gitar, dll.
        loadImage("iconSanity", "/images/ui/icon_otak.png");
        loadImage("iconFood", "/images/ui/icon_onigiri.png");
        loadImage("iconMoney", "/images/ui/icon_money.png");
        loadImage("iconSettings", "/images/ui/icon_gear.png");
        loadImage("bocchiIdle1", "/images/character/idle_1.png");
        loadImage("bocchiIdle2", "/images/character/idle_2.png");
        loadImage("bocchiGuitar", "/images/character/guitar.png");
        loadImage("bocchiSleep", "/images/character/sleep.png");
        loadImage("defaultGuitar", "/images/guitars/default_guitar.png");
        loadImage("pinkGuitar", "/images/guitars/pink_guitar.png");
        loadImage("yellowGuitar", "/images/guitars/yellow_guitar.png");
        loadImage("Stress1", "/images/ZeroSanity/Stress1.jpeg");
        loadImage("Stress2", "/images/ZeroSanity/Stress2.jpeg");
        loadImage("Stress3", "/images/ZeroSanity/Stress3.jpeg");
        loadImage("Lapar 1", "/images/ZeroFood/Hungry1.jpeg");
        loadImage("Lapar 2", "/images/ZeroFood/Hungry2.jpeg");
        loadImage("Lapar 3", "/images/ZeroFood/Hungry3.jpeg");

        // SFX
        loadSound("cashRegisterSound", "/sfx/cash_register.mp3");
        loadSound("guitarRiffSound", "/sfx/guitar_riff.mp3");
        loadSound("shopBellSound", "/sfx/bell.mp3");
        loadSound("gearSound", "/sfx/gear.mp3");
        loadSound("guitarLoopSound", "/sfx/guitar_loop.mp3"); // Buat animasi karakter

        System.out.println("Aset berhasil dimuat.");
    }

    private void loadImage(String key, String path) {
        URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null) {
            System.err.println("Gagal ngambil gambar! Aset gak ditemuin di: " + path);
        } else {
            images.put(key, new Image(resourceUrl.toExternalForm()));
        }
    }


    private void loadSound(String key, String path) {
        URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null) {
            System.err.println("Gagal ngambil suara! Aset gak ditemuin di: " + path);
        } else {
            sounds.put(key, new Media(resourceUrl.toExternalForm()));
        }
    }

    // Getters Publik

    public Image getImage(String key) {
        Image img = images.get(key);
        if (img == null) {
            System.err.println("Gagal ngambil gambar. Kunci gak ditemuin: " + key);
        }
        return img;
    }

    public Media getSound(String key) {
        Media sound = sounds.get(key);
        if (sound == null) {
            System.err.println("Gagal ngambil suara. Kunci gak ditemuin: " + key);
        }
        return sound;
    }
}