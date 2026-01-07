package com.bocchipet.services;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private Map<String, Image> images;
    private Map<String, Media> sounds;

    public AssetManager() {
        this.images = new HashMap<>();
        this.sounds = new HashMap<>();
    }

    public void loadAssets() {
        System.out.println("Memuat aset...");
        
        // Gambar Bocchi
        loadImage("bocchiIdle1", "/images/character/idle_1.png");
        loadImage("bocchiIdle2", "/images/character/idle_2.png");
        loadImage("bocchiGuitar", "/images/character/guitar.png");
        loadImage("bocchiSleep", "/images/character/sleep.png");

        // Gambar Activity
        loadImage("partTimeImage", "/images/activities/partTime.png");
        loadImage("panggung1", "/images/activities/panggung1.png");
        loadImage("panggung2", "/images/activities/panggung2.png");
        loadImage("panggung3", "/images/activities/panggung3.png");

        // Gambar Game Over
        loadImage("stressGIF", "/images/character/stress.gif");

        loadImage("Lapar 1", "/images/character/lapar_1.png");
        loadImage("Lapar 2", "/images/character/lapar_2.png");
        loadImage("Lapar 3", "/images/character/lapar_3.png");

        // Gambar EfeK (Notasi Musik)
        loadImage("note1", "/images/ui/note_1.png");
        loadImage("note2", "/images/ui/note_2.png");
        loadImage("note3", "/images/ui/note_3.png");

        // Gambar UI ama Ikon
        loadImage("iconSanity", "/images/ui/icon_otak.png");
        loadImage("iconFood", "/images/ui/icon_onigiri.png");
        loadImage("iconMoney", "/images/ui/icon_money.png");
        loadImage("iconSettings", "/images/ui/icon_gear.png");
        loadImage("iconBand", "/images/ui/icon_band.png");
        loadImage("iconPartTime", "/images/ui/icon_part_time.png");
        loadImage("iconShop", "/images/ui/icon_shop.png");

        // Gambar Shop Items
        loadImage("defaultGuitar", "/images/guitars/default_guitar.png");
        loadImage("pinkGuitar", "/images/guitars/pink_guitar.png");
        loadImage("yellowGuitar", "/images/guitars/yellow_guitar.png");
        loadImage("iconOnigiri", "/images/ui/icon_onigiri.png");
        loadImage("iconBento", "/images/ui/Bento_icon.png");
        loadImage("iconEnergy", "/images/ui/energy_drink.png"); 
        loadImage("iconPill", "/images/ui/pill.png");           


        // Suara Efek
        loadSound("cashRegisterSound", "/sfx/cash_register.mp3");
        loadSound("shopBellSound", "/sfx/bell.mp3");
        loadSound("gearSound", "/sfx/gear.mp3");
        loadSound("guitarLoopSound", "/sfx/guitar_loop.mp3");
        loadSound("gameOverStress", "/sfx/gameover_stress.mp3");
        loadSound("gameOverHunger", "/sfx/gameover_hunger.mp3");

        
        // Background Music Utama
        loadSound("bgm", "/sfx/bgm.mp3");

        // Musik Band
        loadSound("music1", "/sfx/music1.mp3");
        loadSound("music2", "/sfx/music2.mp3");
        loadSound("music3", "/sfx/music3.mp3");
        loadSound("guitarRiffSound", "/sfx/guitar_riff.mp3"); //Pas beli gitar

        System.out.println("Aset berhasil dimuat.");
    }

    private void loadImage(String key, String path) {
        URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null) {
            System.err.println("WARNING: Gagal load gambar [" + key + "] di path: " + path);
        } else {
            images.put(key, new Image(resourceUrl.toExternalForm()));
        }
    }

    private void loadSound(String key, String path) {
        URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null) {
            System.err.println("WARNING: Gagal load suara [" + key + "] di path: " + path);
        } else {
            sounds.put(key, new Media(resourceUrl.toExternalForm()));
        }
    }

    public Image getImage(String key) { return images.get(key); }
    public Media getSound(String key) { return sounds.get(key); }
}