package com.bocchipet.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.bocchipet.models.Player;
import com.bocchipet.models.PlayerDataDTO;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MainGameController implements Initializable {

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
    
    @FXML private VBox slotMenu;
    @FXML private Label slotMenuTitle;
    @FXML private Button slot1Button;
    @FXML private Button slot2Button;
    @FXML private Button slot3Button;

    @FXML private VBox inputNameMenu;
    @FXML private TextField nameInput;

    @FXML private VBox confirmationMenu;
    @FXML private Label confirmationText;

    @FXML private AnchorPane shopMenu;
    
    @FXML private ImageView SanityZeroGIF;
    @FXML private ImageView FoodZeroGIF;
    
    private Shop shop; 
    private Player player;
    private AssetManager assetManager;
    private SaveLoadService saveLoadService;
    
    private InternalUIManager uiManager; 

    private MediaPlayer sfxPlayer;
    private MediaPlayer musicPlayer; 
    private MediaPlayer guitarLoopMusic; 
    private Timeline characterAnimation;
    private Timeline SanityZero;
    private Timeline FoodZero;
    

    private boolean isSettingsOpen = false;
    private boolean isSavingMode = false; 
    private int selectedSlot = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assetManager = new AssetManager();
        assetManager.loadAssets();
        saveLoadService = new SaveLoadService();

        player = new Player();
        shop = new Shop(); 

        uiManager = new InternalUIManager(sanityBar, foodBar, moneyLabel, bocchiCharacter); 
        uiManager.bindToPlayer(player);

        animationOverlay.setVisible(false);
        animationOverlay.setTranslateX(500);
        settingsMenu.setTranslateY(400); 
        settingsMenu.setVisible(false);
        shopMenu.setVisible(false);
        slotMenu.setVisible(false);
        confirmationMenu.setVisible(false);
        inputNameMenu.setVisible(false);
        SanityZeroGIF.setVisible(false); 
        FoodZeroGIF.setVisible(false);

        // PERBAIKAN: INISIALISASI MUSIC PLAYER HANYA SEKALI
        Media guitarLoopMedia = assetManager.getSound("guitarLoopSound");
        if (guitarLoopMedia != null) {
            guitarLoopMusic = new MediaPlayer(guitarLoopMedia);
            guitarLoopMusic.setCycleCount(MediaPlayer.INDEFINITE);
        }

        initializeCharacterAnimation();
        characterAnimation.play();
    }

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
    private void onSaveMenuClick() { 
        isSavingMode = true; 
        openSlotMenu(); 
    }
    
    @FXML 
    private void onLoadMenuClick() { 
        isSavingMode = false; 
        openSlotMenu(); 
    }

    private void openSlotMenu() {
        slotMenu.setVisible(true);
        slotMenuTitle.setText(isSavingMode ? "SAVE GAME" : "LOAD GAME");
        updateSlotButton(slot1Button, "save_slot_1");
        updateSlotButton(slot2Button, "save_slot_2");
        updateSlotButton(slot3Button, "save_slot_3");
    }

    private void updateSlotButton(Button btn, String slotName) {
        PlayerDataDTO data = saveLoadService.getSlotPreview(slotName);
        
        if (data != null) {
            String name = (data.getPlayerName() != null && !data.getPlayerName().isEmpty()) ? data.getPlayerName() : "Bocchi";
            btn.setText(String.format("%s\nMoney: ¥ %,d", name, data.getMoney()));
        } else {
            btn.setText("Empty Slot");
        }
    }

    @FXML private void onSlot1Click() { handleSlotSelection(1); }
    @FXML private void onSlot2Click() { handleSlotSelection(2); }
    @FXML private void onSlot3Click() { handleSlotSelection(3); }

    private void handleSlotSelection(int slot) {
        selectedSlot = slot;
        String slotFileName = "save_slot_" + slot;
        boolean isSlotEmpty = (saveLoadService.getSlotPreview(slotFileName) == null);

        if (isSavingMode) {
            if (isSlotEmpty) {
                openNameInput();
            } else {
                confirmationMenu.setVisible(true);
                confirmationText.setText("Overwrite Slot " + slot + "?");
            }
        } else {
            if (isSlotEmpty) {
                System.out.println("Cannot load empty slot!");
                return;
            }
            confirmationMenu.setVisible(true);
            confirmationText.setText("Load Slot " + slot + "?");
        }
    }

    private void openNameInput() {
        inputNameMenu.setVisible(true);
        nameInput.clear();
        nameInput.requestFocus();
    }

    @FXML
    private void onConfirmYes() {
        if (isSavingMode) {
            saveLoadService.saveGame(player, "save_slot_" + selectedSlot, player.getName());
            System.out.println("Game Overwritten!");
        } else {
            this.player = saveLoadService.loadGame("save_slot_" + selectedSlot);
            uiManager.bindToPlayer(player);
            System.out.println("Game Loaded!");
        }
        closeAllMenus();
    }

    @FXML
    private void onNameConfirm() {
        String inputName = nameInput.getText();
        if (inputName.trim().isEmpty()) inputName = "Bocchi"; 

        player.setName(inputName);
        saveLoadService.saveGame(player, "save_slot_" + selectedSlot, inputName);
        System.out.println("Game Saved (New Slot)!");
        
        closeAllMenus();
    }

    @FXML private void onNameCancel() { inputNameMenu.setVisible(false); }
    @FXML private void onConfirmNo() { confirmationMenu.setVisible(false); }
    @FXML private void onSlotBackClick() { slotMenu.setVisible(false); }

    private void closeAllMenus() {
        inputNameMenu.setVisible(false);
        confirmationMenu.setVisible(false);
        slotMenu.setVisible(false);
        closeSettings();
    }

    private void toggleSettingsMenu() { 
        if (isSettingsOpen) closeSettings(); else openSettings(); 
    }
    
    private void openSettings() {
        settingsMenu.setVisible(true);
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), settingsMenu);
        slide.setToY(0); 
        slide.play(); 
        isSettingsOpen = true; 
        toggleGameControls(true); 
    }
    
    private void closeSettings() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), settingsMenu);
        slide.setToY(400); 
        slide.setOnFinished(e -> settingsMenu.setVisible(false));
        slide.play(); 
        isSettingsOpen = false; 
        toggleGameControls(false);
    }
    
    private void toggleGameControls(boolean disable) {
        partTimeButton.setDisable(disable); 
        bandButton.setDisable(disable); 
        shopButton.setDisable(disable);
    }

    private void executeActivity(IActivity activity) {
        // Cek dulu apakah game sudah berakhir
        if (player.getSanity() <= 0) return; 

        toggleGameControls(true); 
        activity.perform(player);
        Image animationImage = assetManager.getImage(activity.getAnimationImageKey());
        Media soundEffect = assetManager.getSound(activity.getSoundEffectKey());
        playSlideAnimation(animationImage); 
        playSound(soundEffect);
    }
    
    private void playSound(Media sound) {
        if (sound == null) return;
        if (sfxPlayer != null) sfxPlayer.stop();
        sfxPlayer = new MediaPlayer(sound); 
        sfxPlayer.play();
    }
    
    private void playSlideAnimation(Image img) {
        if (img == null) { 
            toggleGameControls(false); 
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
            toggleGameControls(false); 
        });
        sequence.play();
    }
    
    @FXML 
    private void onShopClick() { 
        playSound(assetManager.getSound("shopBellSound")); 
        shopMenu.setVisible(true); 
        toggleGameControls(true); 
    }
    
    @FXML 
    private void onShopBackClick() { 
        playSound(assetManager.getSound("shopBellSound")); 
        shopMenu.setVisible(false); 
        toggleGameControls(false); 
    }
    
    @FXML private void onBuyOnigiri() { buyItem(0); }
    @FXML private void onBuyBento() { buyItem(1); }
    @FXML private void onBuyPinkGuitar() { buyItem(2); }
    @FXML private void onBuyYellowGuitar() { buyItem(3); }
    
    private void buyItem(int index) {
        ShopItem item = shop.getAvailableItems().get(index);
        if (shop.purchaseItem(player, item)) {
            if (index >= 2) playSound(assetManager.getSound("guitarRiffSound"));
            else playSound(assetManager.getSound("cashRegisterSound"));
        }
    }
    
    private void initializeCharacterAnimation() {
        characterAnimation = new Timeline(
            // KeyFrame 0: Idle 1 (Stop Loop Music)
            new KeyFrame(Duration.seconds(0), e -> {
                bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"));
                if (guitarLoopMusic != null) guitarLoopMusic.stop(); 
            }),
            
            // KeyFrame 2.0: Idle 2
            new KeyFrame(Duration.seconds(2.0), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))),
            
            // KeyFrame 2.2: Idle 1
            new KeyFrame(Duration.seconds(2.2), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
            
            // KeyFrame 5.0: Guitar (Start Loop Music)
            new KeyFrame(Duration.seconds(5), e -> {
                bocchiCharacter.setImage(assetManager.getImage("bocchiGuitar"));
                if (guitarLoopMusic != null) guitarLoopMusic.play();
            }),
            
            // KeyFrame 10.0: Sleep (Stop Loop Music)
            new KeyFrame(Duration.seconds(10), e -> {
                bocchiCharacter.setImage(assetManager.getImage("bocchiSleep"));
                if (guitarLoopMusic != null) guitarLoopMusic.stop();
            }),
            new KeyFrame(Duration.seconds(15)) 
        );
        characterAnimation.setCycleCount(Timeline.INDEFINITE);
    }

    
    public void triggerSanityZero() {
        if (SanityZeroGIF != null && SanityZeroGIF.isVisible() == false){
            
            System.out.println("DEBUG: Memulai urutan Sanity Zero."); 

            // Logika Penghentian Game
            toggleGameControls(true);
            characterAnimation.stop();
            if (musicPlayer != null) musicPlayer.stop();
            if (guitarLoopMusic != null) guitarLoopMusic.stop(); 

            SanityZeroGIF.setVisible(true);
            
            if (SanityZero == null){
                initializeSanityZero();
                System.out.println("DEBUG: Inisialisasi SanityZero Timeline.");
            }
            
            SanityZero.playFromStart(); 
            System.out.println("DEBUG: Sanity Zero GIF diputar.");

            System.out.println("Bocchi is mentally kejang-kejang (GIF).");
        } else {
            System.out.println("DEBUG: Sanity Zero sudah terlihat, tidak dipicu ulang.");
        }
    }

    private void initializeSanityZero(){
        SanityZero = new Timeline(
            new KeyFrame(Duration.seconds(0), 
                        e -> SanityZeroGIF.setImage(assetManager.getImage("Stress1"))),
            
            new KeyFrame(Duration.seconds(0.5), 
                        e -> SanityZeroGIF.setImage(assetManager.getImage("Stress2"))),
            
            new KeyFrame(Duration.seconds(1.0), 
                        e -> SanityZeroGIF.setImage(assetManager.getImage("Stress3"))),
            
            new KeyFrame(Duration.seconds(3.0))
        );

        SanityZero.setCycleCount(1);
        
        SanityZero.setOnFinished(e -> {
            SanityZeroGIF.setVisible(false);
            toggleGameControls(false); 
            characterAnimation.play();
        });
    }

    public void triggerFoodZero() {
        // Cek hanya jika GIF tidak terlihat
        if (FoodZeroGIF != null && FoodZeroGIF.isVisible() == false){
            
            System.out.println("DEBUG: Memulai urutan Food Zero (Lapar)."); 

            toggleGameControls(true); // Nonaktifkan kontrol game
            characterAnimation.stop();
            if (musicPlayer != null) musicPlayer.stop();
            if (guitarLoopMusic != null) guitarLoopMusic.stop();

            FoodZeroGIF.setVisible(true);
            
            if (FoodZero == null){
                initializeFoodZero();
                System.out.println("DEBUG: Inisialisasi FoodZero Timeline.");
            }
            
            FoodZero.playFromStart(); 
            System.out.println("DEBUG: Food Zero GIF diputar.");
        }
    }

    private void initializeFoodZero(){
        FoodZero = new Timeline(
            new KeyFrame(Duration.seconds(0), 
                        e -> FoodZeroGIF.setImage(assetManager.getImage("Lapar 1"))),
            
            new KeyFrame(Duration.seconds(0.5), 
                        e -> FoodZeroGIF.setImage(assetManager.getImage("Lapar 2"))),
            
            new KeyFrame(Duration.seconds(1.0), 
                        e -> FoodZeroGIF.setImage(assetManager.getImage("Lapar 3"))),
            
            new KeyFrame(Duration.seconds(3.0))
        );

        FoodZero.setCycleCount(1);
        
        FoodZero.setOnFinished(e -> {
            FoodZeroGIF.setVisible(false);
            toggleGameControls(false); 
            characterAnimation.play();
        });
    }

    
    private class InternalUIManager {
        private ProgressBar sanityBar; 
        private ProgressBar foodBar; 
        private Label moneyLabel; 
        private ImageView bocchiCharView;
        
        public InternalUIManager(ProgressBar s, ProgressBar f, Label m, ImageView b) {
            this.sanityBar = s; 
            this.foodBar = f; 
            this.moneyLabel = m; 
            this.bocchiCharView = b;
        }
        
        public void bindToPlayer(Player playerToBind) {
            sanityBar.progressProperty().bind(playerToBind.sanityProperty().divide(100.0));
            foodBar.progressProperty().bind(playerToBind.foodProperty().divide(100.0));
            moneyLabel.textProperty().bind(playerToBind.moneyProperty().asString("¥ %,d"));

            playerToBind.sanityProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue.intValue() <= 0 && oldValue.intValue() > 0) { 
                    System.out.println("DEBUG: Pemicu Listener Sanity Zero Aktif!"); 
                    MainGameController.this.triggerSanityZero(); 
                }
            });

            playerToBind.foodProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue.intValue() <= 0 && oldValue.intValue() > 0) { 
                    System.out.println("DEBUG: Pemicu Listener Food Zero Aktif!"); 
                    MainGameController.this.triggerFoodZero(); 
                }
            });
        }
        
    } 
}