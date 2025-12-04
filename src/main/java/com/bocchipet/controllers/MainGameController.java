package com.bocchipet.controllers;

import com.bocchipet.models.Player;
import com.bocchipet.models.activities.BandPractice;
import com.bocchipet.models.activities.IActivity;
import com.bocchipet.models.activities.PartTimeJob;
import com.bocchipet.models.shop.Shop;
import com.bocchipet.models.shop.ShopItem;
import com.bocchipet.services.AssetManager;
import com.bocchipet.services.SaveLoadService;

import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainGameController implements Initializable {

    // --- Elemen UI ---
    @FXML private StackPane rootPane;
    @FXML private ProgressBar sanityBar;
    @FXML private ProgressBar foodBar;
    @FXML private Label moneyLabel;
    @FXML private ImageView bocchiCharacter;
    
    @FXML private Button partTimeButton;
    @FXML private Button bandButton;
    @FXML private Button shopButton;
    @FXML private Button settingsButton;
    
    @FXML private ImageView animationOverlay;
    @FXML private VBox settingsMenu;
    @FXML private Button saveButton;
    @FXML private Button loadButton;

    // --- Shop Elements (BARU) ---
    @FXML private AnchorPane shopMenu; 
    private Shop shop; 

    // --- Services & Models ---
    private Player player;
    private AssetManager assetManager;
    private SaveLoadService saveLoadService;
    private UIManager uiManager;

    private MediaPlayer sfxPlayer;
    private MediaPlayer musicPlayer;
    private Timeline characterAnimation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assetManager = new AssetManager();
        assetManager.loadAssets();
        saveLoadService = new SaveLoadService();

        player = new Player();
        shop = new Shop(); // Inisialisasi Toko

        uiManager = new UIManager(sanityBar, foodBar, moneyLabel, bocchiCharacter);
        uiManager.bindToPlayer(player);

        // Reset UI State
        animationOverlay.setVisible(false);
        animationOverlay.setTranslateX(500);
        settingsMenu.setVisible(false);
        settingsMenu.setTranslateY(200);
        shopMenu.setVisible(false); // Sembunyikan toko di awal

        initializeCharacterAnimation();
        characterAnimation.play();

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Saving game...");
                saveLoadService.saveGame(player, "save_slot_1");
                toggleSettingsMenu();
            }
        });
    }

    // --- BUTTON ACTIONS ---

    @FXML
    private void onPartTimeClick() {
        executeActivity(new PartTimeJob());
    }

    @FXML
    private void onBandClick() {
        executeActivity(new BandPractice());
    }

    @FXML
    private void onSettingsClick() {
        playSound(assetManager.getSound("gearSound"));
        toggleSettingsMenu();
    }

    @FXML
    private void onLoadClick() {
        System.out.println("Loading game...");
        this.player = saveLoadService.loadGame("save_slot_1");
        uiManager.bindToPlayer(player);
        toggleSettingsMenu();
    }

    // --- SHOP LOGIC (BARU) ---

    @FXML
    private void onShopClick() {
        playSound(assetManager.getSound("shopBellSound"));
        shopMenu.setVisible(true); // Tampilkan halaman toko
        toggleControls(true); // Matikan tombol
    }

    @FXML
    private void onShopBackClick() {
        playSound(assetManager.getSound("shopBellSound"));
        shopMenu.setVisible(false); // Sembunyikan halaman toko
        toggleControls(false); // Hidupkan kembali tombol
    }

    @FXML
    private void onBuyOnigiri() {
        ShopItem item = shop.getAvailableItems().get(0);
        if (shop.purchaseItem(player, item)) {
            playSound(assetManager.getSound("cashRegisterSound"));
        }
    }

    @FXML
    private void onBuyBento() {
        ShopItem item = shop.getAvailableItems().get(1); // Bento
        if (shop.purchaseItem(player, item)) {
            playSound(assetManager.getSound("cashRegisterSound"));
        }
    }

    @FXML
    private void onBuyPinkGuitar() {
        ShopItem item = shop.getAvailableItems().get(2); // Pink Guitar
        if (shop.purchaseItem(player, item)) {
            playSound(assetManager.getSound("guitarRiffSound"));
        }
    }

    @FXML
    private void onBuyYellowGuitar() {
        ShopItem item = shop.getAvailableItems().get(3); // Yellow Guitar
        if (shop.purchaseItem(player, item)) {
            playSound(assetManager.getSound("guitarRiffSound"));
        }
    }

    // --- HELPER METHODS ---

    private void executeActivity(IActivity activity) {
        // 1. Matikan kontrol agar tidak bisa spam klik (Input Blocking)
        toggleControls(true);

        // 2. Jalankan logika
        activity.perform(player);

        Image animationImage = assetManager.getImage(activity.getAnimationImageKey());
        Media soundEffect = assetManager.getSound(activity.getSoundEffectKey());

        playSlideAnimation(animationImage);
        playSound(soundEffect);
    }

    /**
     * Mematikan atau menghidupkan semua tombol interaksi.
     */
    private void toggleControls(boolean disable) {
        partTimeButton.setDisable(disable);
        bandButton.setDisable(disable);
        shopButton.setDisable(disable);
        settingsButton.setDisable(disable);
    }

    private void playSound(Media sound) {
        if (sound == null) return;
        if (sfxPlayer != null) sfxPlayer.stop();
        sfxPlayer = new MediaPlayer(sound);
        sfxPlayer.play();
    }

    private void playSlideAnimation(Image img) {
        if (img == null) {
            toggleControls(false);
            return;
        }

        animationOverlay.setImage(img);
        animationOverlay.setVisible(true);
        animationOverlay.setTranslateX(400); 
        animationOverlay.setOpacity(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), animationOverlay);
        slideIn.setToX(0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), animationOverlay);
        slideOut.setDelay(Duration.millis(1500)); 
        slideOut.setToX(-400);

        SequentialTransition sequence = new SequentialTransition(slideIn, slideOut);
        sequence.setOnFinished(e -> {
            animationOverlay.setVisible(false);
            // PENTING: Hidupkan kembali tombol setelah animasi selesai
            toggleControls(false);
        });
        sequence.play();
    }

    private void toggleSettingsMenu() {
        settingsMenu.setVisible(true);
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), settingsMenu);

        if (settingsMenu.getTranslateY() == 0) {
            slide.setToY(200);
            slide.setOnFinished(e -> {
                settingsMenu.setVisible(false);
                toggleControls(false); 
            });
            toggleControls(true);
        } else {
            slide.setToY(0);
            toggleControls(true);
        }
        slide.play();
    }

/**
     * Mengatur Timeline untuk animasi karakter.
     * REVISI: Menggunakan timing kedipan mata yang lebih natural.
     */
    private void initializeCharacterAnimation() {
        characterAnimation = new Timeline(
                // === FASE 1: IDLE (0 - 5 Detik) ===
                // Mata Bocchi terbuka (idle_1) sebagian besar waktu
                new KeyFrame(Duration.seconds(0), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
                
                // Kedipan Pertama di detik ke-2 (Cepat: hanya 0.2 detik)
                new KeyFrame(Duration.seconds(2.0), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))), // Tutup
                new KeyFrame(Duration.seconds(2.2), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))), // Buka lagi

                // Kedipan Kedua di detik ke-4 (Cepat)
                new KeyFrame(Duration.seconds(4.0), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))), // Tutup
                new KeyFrame(Duration.seconds(4.2), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))), // Buka lagi
                
                // === FASE 2: MAIN GITAR (5 - 10 Detik) ===
                new KeyFrame(Duration.seconds(5), e -> {
                    bocchiCharacter.setImage(assetManager.getImage("bocchiGuitar"));
                    // Mulai loop suara gitar
                    if (musicPlayer != null) musicPlayer.stop();
                    musicPlayer = new MediaPlayer(assetManager.getSound("guitarLoopSound"));
                    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Putar berulang
                    musicPlayer.play();
                }),
                
                new KeyFrame(Duration.seconds(10), e -> {
                    bocchiCharacter.setImage(assetManager.getImage("bocchiSleep"));
                    if (musicPlayer != null) musicPlayer.stop(); // Hentikan suara gitar
                }),

                new KeyFrame(Duration.seconds(15)) 
        );
        characterAnimation.setCycleCount(Timeline.INDEFINITE); // Ngulangin terus menerus
    }
    
    private class UIManager {
        private ProgressBar sanityBar;
        private ProgressBar foodBar;
        private Label moneyLabel;
        private ImageView bocchiCharView;

        public UIManager(ProgressBar sBar, ProgressBar fBar, Label mLabel, ImageView bChar) {
            this.sanityBar = sBar;
            this.foodBar = fBar;
            this.moneyLabel = mLabel;
            this.bocchiCharView = bChar;
        }

        public void bindToPlayer(Player playerToBind) {
            sanityBar.progressProperty().bind(playerToBind.sanityProperty().divide(100.0));
            foodBar.progressProperty().bind(playerToBind.foodProperty().divide(100.0));
            moneyLabel.textProperty().bind(playerToBind.moneyProperty().asString("¥ %,d"));
        }
    } 
}