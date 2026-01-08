package com.bocchipet.controllers;

import com.bocchipet.models.Player;
import com.bocchipet.models.PlayerDataDTO;
import com.bocchipet.models.activities.BandPractice;
import com.bocchipet.models.activities.IActivity;
import com.bocchipet.models.activities.PartTimeJob;
import com.bocchipet.models.shop.Shop;
import com.bocchipet.models.shop.ShopItem;
import com.bocchipet.services.AssetManager;
import com.bocchipet.services.SaveLoadService;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainGameController implements Initializable {

    // Elemen UI
    @FXML private StackPane rootPane;
    @FXML private ProgressBar sanityBar;
    @FXML private ProgressBar foodBar;
    @FXML private Label moneyLabel;
    @FXML private ImageView bocchiCharacter;
    
    // Container posisi Floating Text
    @FXML private StackPane sanityContainer;
    @FXML private StackPane foodContainer;
    
    @FXML private Button partTimeButton;
    @FXML private Button bandButton;
    @FXML private Button shopButton;
    @FXML private Button settingsButton;
    
    @FXML private ImageView animationOverlay;
    
    // Notasi Musik
    @FXML private ImageView noteView1;
    @FXML private ImageView noteView2;
    @FXML private ImageView noteView3;

    @FXML private AnchorPane discoOverlay; 

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
    
    // Game Over UI
    @FXML private ImageView SanityZeroGIF;
    @FXML private ImageView FoodZeroGIF;
    @FXML private VBox gameOverMenu;
    
    // Init service ama model
    private Shop shop; 
    private Player player;
    private AssetManager assetManager;
    private SaveLoadService saveLoadService;
    private InternalUIManager uiManager; 

    // Init audio ama animasi
    private MediaPlayer sfxPlayer;
    private MediaPlayer musicPlayer; 
    private MediaPlayer guitarLoopMusic; 
    private MediaPlayer bgmPlayer; 

    private Timeline characterAnimation;
    private Timeline FoodZero;
    private Timeline discoLightTimeline;
    private Timeline notesAnimation1;
    private Timeline notesAnimation2;
    private Timeline notesAnimation3;
    
    // State kondisinya
    private boolean isSettingsOpen = false;
    private boolean isSavingMode = false; 
    private int selectedSlot = -1;
    private boolean isGameOver = false;
    private Random random = new Random();

    // Palet Warna Notasi
    private final Color[] NOTE_COLORS = { 
        Color.HOTPINK, Color.CYAN, Color.GOLD, Color.RED 
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assetManager = new AssetManager();
        assetManager.loadAssets();
        saveLoadService = new SaveLoadService();

        player = new Player();
        shop = new Shop(); 

        uiManager = new InternalUIManager(sanityBar, foodBar, moneyLabel, bocchiCharacter); 
        uiManager.bindToPlayer(player);

        resetUIState();

        // Music setup
        Media guitarLoopMedia = assetManager.getSound("guitarLoopSound");
        if (guitarLoopMedia != null) {
            guitarLoopMusic = new MediaPlayer(guitarLoopMedia);
            guitarLoopMusic.setCycleCount(MediaPlayer.INDEFINITE);
            guitarLoopMusic.setVolume(0.4);
        }

        Media bgmMedia = assetManager.getSound("bgm");
        if (bgmMedia != null) {
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(0.5); 
            bgmPlayer.play();
        }

        initializeCharacterAnimation();
        characterAnimation.play();
    }

    private void resetUIState() {
        animationOverlay.setVisible(false);
        animationOverlay.setTranslateX(500);
        animationOverlay.setScaleX(1.0);
        animationOverlay.setScaleY(1.0);
        
        stopNoteAnimations();

        settingsMenu.setTranslateY(400); 
        settingsMenu.setVisible(false);
        shopMenu.setVisible(false);
        slotMenu.setVisible(false);
        confirmationMenu.setVisible(false);
        inputNameMenu.setVisible(false);
        SanityZeroGIF.setVisible(false); 
        FoodZeroGIF.setVisible(false);
        gameOverMenu.setVisible(false);
        
        if (discoOverlay != null) {
            discoOverlay.setVisible(false);
            discoOverlay.getChildren().clear();
        }
        animationOverlay.setEffect(null); 
        
        bocchiCharacter.setVisible(true);
        toggleGameControls(false);
        isGameOver = false;
    }

    // Ngehentiin semua nya
    private void stopAllGameActivities() {
        toggleGameControls(true);
        
        if (musicPlayer != null) musicPlayer.stop();
        if (guitarLoopMusic != null) guitarLoopMusic.stop(); 
        if (sfxPlayer != null) sfxPlayer.stop();
        if (bgmPlayer != null) bgmPlayer.stop();
        
        if (discoLightTimeline != null) stopDiscoLights();
        stopNoteAnimations();
        
        animationOverlay.setVisible(false);
        animationOverlay.setEffect(null);
        
        if (characterAnimation != null) characterAnimation.stop();
    }

    // Logika activity

    private void executeActivity(IActivity activity) {
        if (isGameOver) return;
        toggleGameControls(true); 
        
        int oldSanity = player.getSanity();
        int oldFood = player.getFood();

        // Mulai aktivitas dari sini
        activity.perform(player);
        
        // Cek game over biar gak nabrak ama animasi laen
        if (isGameOver) return; 

        // Floating Text disini
        int diffSanity = player.getSanity() - oldSanity;
        int diffFood = player.getFood() - oldFood;
        showFloatingText(sanityContainer, diffSanity);
        showFloatingText(foodContainer, diffFood);
        
        // Nyiapin animasi ama sound effect
        Image animationImage = assetManager.getImage(activity.getAnimationImageKey());
        if (animationImage == null) animationImage = assetManager.getImage("panggung1");
        if (animationImage == null) animationImage = assetManager.getImage("partTimeImage");

        Media soundEffect = assetManager.getSound(activity.getSoundEffectKey());
        if (soundEffect == null && activity instanceof BandPractice) {
             soundEffect = assetManager.getSound("guitarRiffSound");
        }
        
        hideCharacterForActivity();

        if (activity instanceof BandPractice) {
            playDynamicBandAnimation(animationImage, soundEffect);
        } else {
            playStaticSlideAnimation(animationImage, soundEffect);
        }
    }
    
    private void hideCharacterForActivity() {
        if (characterAnimation != null) characterAnimation.pause();
        if (guitarLoopMusic != null) guitarLoopMusic.stop();
        if (bgmPlayer != null) bgmPlayer.pause();
        bocchiCharacter.setVisible(false);
    }

    private void restoreCharacterAfterActivity() {
        if (!isGameOver) {
            bocchiCharacter.setVisible(true);
            if (bgmPlayer != null) {
                bgmPlayer.setVolume(0.5); 
                bgmPlayer.play();
            }
            if (characterAnimation != null) characterAnimation.play();
            toggleGameControls(false);
        }
    }

    private void playStaticSlideAnimation(Image img, Media sound) {
        if (img == null) { restoreCharacterAfterActivity(); return; }
        
        playSound(sound);
        setupSlideIn(img);
        
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), animationOverlay);
        slideIn.setToX(0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), animationOverlay);
        slideOut.setDelay(Duration.millis(1500)); slideOut.setToX(-400);
        SequentialTransition sequence = new SequentialTransition(slideIn, slideOut);
        sequence.setOnFinished(e -> { 
            animationOverlay.setVisible(false); 
            restoreCharacterAfterActivity();
        });
        sequence.play();
    }

    private void playDynamicBandAnimation(Image img, Media sound) {
        if (img == null) { restoreCharacterAfterActivity(); return; }
        
        setupSlideIn(img);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), animationOverlay);
        slideIn.setToX(0);
        
        slideIn.setOnFinished(e -> {
            if (isGameOver) { // Cek lagi kondisinya
                animationOverlay.setVisible(false);
                return;
            }

            if (sound != null) {
                if (sfxPlayer != null) sfxPlayer.stop();
                sfxPlayer = new MediaPlayer(sound);
                
                // Efek Scale ama Outline
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), animationOverlay);
                scaleDown.setToX(0.85); 
                scaleDown.setToY(0.85);
                scaleDown.play();

                // Bagian Deteksi Warna Gitar
                Color themeColor = Color.HOTPINK; 
                String currentGuitar = player.getCurrentGuitarImage();
                if (currentGuitar != null) {
                    if (currentGuitar.contains("pink")) themeColor = null;
                    else if (currentGuitar.contains("yellow")) themeColor = Color.GOLD;
                }

                DropShadow outlineGlow = new DropShadow();
                outlineGlow.setRadius(50);
                outlineGlow.setSpread(0.5);
                outlineGlow.setColor(themeColor != null ? themeColor : Color.RED); 
                
                // Langsung pasang outline (tanpa darken)
                animationOverlay.setEffect(outlineGlow);

                startDiscoLights(outlineGlow, themeColor); 
                startNoteAnimations(themeColor); 

                sfxPlayer.play();
                
                sfxPlayer.setOnEndOfMedia(() -> {
                    stopDiscoLights();
                    stopNoteAnimations(); 
                    
                    if (isGameOver) return;

                    ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), animationOverlay);
                    scaleUp.setToX(1.0);
                    scaleUp.setToY(1.0);
                    scaleUp.setOnFinished(ev -> {
                        animationOverlay.setEffect(null); 
                        slideOutBand(); 
                    });
                    scaleUp.play();
                });
            } else {
                PauseTransition fallback = new PauseTransition(Duration.seconds(2));
                fallback.setOnFinished(ev -> slideOutBand());
                fallback.play();
            }
        });
        slideIn.play();
    }

    private void setupSlideIn(Image img) {
        animationOverlay.setImage(img); 
        animationOverlay.setVisible(true);
        animationOverlay.setTranslateX(400); 
        animationOverlay.setOpacity(1.0);
        animationOverlay.setEffect(null); 
        animationOverlay.setScaleX(1.0);
        animationOverlay.setScaleY(1.0);
    }

    private void slideOutBand() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), animationOverlay);
        slideOut.setToX(-400);
        slideOut.setOnFinished(e -> {
            animationOverlay.setVisible(false);
            restoreCharacterAfterActivity();
        });
        slideOut.play();
    }

    // Bagian efek visual (kacau bat lampu disko & notasi musik)
    private void startDiscoLights(DropShadow outlineEffect, Color themeColor) {
        if (discoOverlay == null) return;
        discoOverlay.getChildren().clear();
        discoOverlay.setVisible(true);

        double w = rootPane.getWidth();
        double h = rootPane.getHeight();
        Rectangle screenRect = new Rectangle(0, 0, w, h);

        Bounds boundsInScene = animationOverlay.localToScene(animationOverlay.getBoundsInLocal());
        Bounds boundsInOverlay = discoOverlay.sceneToLocal(boundsInScene);

        Rectangle holeRect = new Rectangle(
            boundsInOverlay.getMinX(), boundsInOverlay.getMinY(), 
            boundsInOverlay.getWidth(), boundsInOverlay.getHeight()
        );
        
        Shape hollowOverlay = Shape.subtract(screenRect, holeRect);
        hollowOverlay.setFill(Color.TRANSPARENT);
        discoOverlay.getChildren().add(hollowOverlay);

        double opacity = 0.8;
        
        if (themeColor != null) {
            // Mode satu warna
            discoLightTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0), 
                    new KeyValue(hollowOverlay.fillProperty(), deriveColor(themeColor, opacity)),
                    new KeyValue(outlineEffect.colorProperty(), themeColor)),
                new KeyFrame(Duration.seconds(0.5), 
                    new KeyValue(hollowOverlay.fillProperty(), deriveColor(themeColor, 0.4)), 
                    new KeyValue(outlineEffect.colorProperty(), Color.WHITE))
            );
        } else {
            // Mode rainbow
            discoLightTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0), 
                    new KeyValue(hollowOverlay.fillProperty(), Color.rgb(100, 0, 0, opacity)),
                    new KeyValue(outlineEffect.colorProperty(), Color.RED)), 
                new KeyFrame(Duration.seconds(0.3), 
                    new KeyValue(hollowOverlay.fillProperty(), Color.rgb(0, 0, 100, opacity)),
                    new KeyValue(outlineEffect.colorProperty(), Color.BLUE)),
                new KeyFrame(Duration.seconds(0.6), 
                    new KeyValue(hollowOverlay.fillProperty(), Color.rgb(0, 100, 0, opacity)),
                    new KeyValue(outlineEffect.colorProperty(), Color.LIME)),
                new KeyFrame(Duration.seconds(0.9), 
                    new KeyValue(hollowOverlay.fillProperty(), Color.rgb(100, 100, 0, opacity)),
                    new KeyValue(outlineEffect.colorProperty(), Color.YELLOW)),
                new KeyFrame(Duration.seconds(1.2), 
                    new KeyValue(hollowOverlay.fillProperty(), Color.rgb(80, 0, 80, opacity)),
                    new KeyValue(outlineEffect.colorProperty(), Color.MAGENTA))
            );
        }

        discoLightTimeline.setAutoReverse(true);
        discoLightTimeline.setCycleCount(Timeline.INDEFINITE);
        discoLightTimeline.play();
    }
    
    private Color deriveColor(Color c, double opacity) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }

    private void stopDiscoLights() {
        if (discoLightTimeline != null) discoLightTimeline.stop();
        if (discoOverlay != null) {
            discoOverlay.setVisible(false);
            discoOverlay.getChildren().clear();
        }
    }

    private void startNoteAnimations(Color themeColor) {
        notesAnimation1 = createSingleNoteAnimation(noteView1, -120, 0, themeColor); 
        notesAnimation2 = createSingleNoteAnimation(noteView2, 0, 1, themeColor);    
        notesAnimation3 = createSingleNoteAnimation(noteView3, 120, 3, themeColor);  
        
        notesAnimation1.play();
        notesAnimation2.play();
        notesAnimation3.play();
    }

    private Timeline createSingleNoteAnimation(ImageView noteView, double baseX, int startColorIndex, Color themeColor) {
        final int[] colorIdx = { startColorIndex };

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.8), e -> {
            String noteKey = "note" + (random.nextInt(3) + 1);
            Image img = assetManager.getImage(noteKey);
            if (img != null) noteView.setImage(img);
            
            Color currentColor;
            if (themeColor != null) {
                currentColor = themeColor; 
            } else {
                currentColor = NOTE_COLORS[colorIdx[0] % NOTE_COLORS.length]; 
                colorIdx[0]++; 
            }

            ColorAdjust makeWhite = new ColorAdjust();
            makeWhite.setBrightness(1.0);
            InnerShadow colorize = new InnerShadow();
            colorize.setColor(currentColor);
            colorize.setChoke(1.0);
            colorize.setRadius(50);
            colorize.setInput(makeWhite);
            noteView.setEffect(colorize);

            double randomX = baseX + (random.nextDouble() * 60 - 30);
            double randomY = (random.nextDouble() * 100 - 50);
            noteView.setTranslateX(randomX);
            noteView.setTranslateY(randomY);
            
            noteView.setVisible(true);
            noteView.setOpacity(1.0);
            noteView.setScaleX(0.5); 
            noteView.setScaleY(0.5);

            TranslateTransition moveUp = new TranslateTransition(Duration.millis(700), noteView);
            moveUp.setByY(-80); 
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(700), noteView);
            scaleUp.setToX(1.2); scaleUp.setToY(1.2);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(700), noteView);
            fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0);

            ParallelTransition anim = new ParallelTransition(moveUp, scaleUp, fadeOut);
            anim.play();
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setDelay(Duration.millis(random.nextInt(500))); 
        return timeline;
    }

    private void stopNoteAnimations() {
        if (notesAnimation1 != null) notesAnimation1.stop();
        if (notesAnimation2 != null) notesAnimation2.stop();
        if (notesAnimation3 != null) notesAnimation3.stop();
        noteView1.setVisible(false);
        noteView2.setVisible(false);
        noteView3.setVisible(false);
    }

    private void showFloatingText(StackPane targetContainer, int value) {
        if (value == 0) return; 
        String text = (value > 0 ? "+" : "") + value;
        Color color = (value > 0) ? Color.LIMEGREEN : Color.RED; 
        Label floatLabel = new Label(text);
        floatLabel.setTextFill(color);
        floatLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        DropShadow ds = new DropShadow();
        ds.setRadius(2.0);
        ds.setColor(Color.BLACK);
        floatLabel.setEffect(ds);

        rootPane.getChildren().add(floatLabel);
        StackPane.setAlignment(floatLabel, Pos.TOP_LEFT);

        Bounds bounds = targetContainer.localToScene(targetContainer.getBoundsInLocal());
        Bounds rootBounds = rootPane.sceneToLocal(bounds);

        floatLabel.setTranslateX(rootBounds.getMinX() + (rootBounds.getWidth() / 2) - 15); 
        floatLabel.setTranslateY(rootBounds.getMaxY() + 5); 

        TranslateTransition moveDown = new TranslateTransition(Duration.millis(1000), floatLabel);
        moveDown.setByY(20); 
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), floatLabel);
        fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0);
        ParallelTransition anim = new ParallelTransition(moveDown, fadeOut);
        anim.setOnFinished(e -> rootPane.getChildren().remove(floatLabel)); 
        anim.play();
    }
    
    private void playDelayedSound(String soundKey, double delaySeconds) {
        PauseTransition pause = new PauseTransition(Duration.seconds(delaySeconds));
        pause.setOnFinished(e -> {
            Media sound = assetManager.getSound(soundKey);
            playSound(sound); 
        });
        pause.play();
    }

    private void playSound(Media sound) {
        if (sound == null) return;
        if (sfxPlayer != null) sfxPlayer.stop();
        sfxPlayer = new MediaPlayer(sound); sfxPlayer.play();
    }

    // --- GAME OVER ---
    public void triggerSanityZero() {
        if (isGameOver) return;
        isGameOver = true;
        stopAllGameActivities(); 

        SanityZeroGIF.setVisible(true);
        SanityZeroGIF.setImage(assetManager.getImage("stressGIF"));

        playSound(assetManager.getSound("gameOverStress"));
        startGameOverDelay();
    }

    public void triggerFoodZero() {
        if (isGameOver) return;
        isGameOver = true;
        stopAllGameActivities(); 
        FoodZeroGIF.setVisible(true);
        if (FoodZero == null) initializeFoodZero();
        FoodZero.playFromStart();

        playSound(assetManager.getSound("gameOverHunger"));
        startGameOverDelay();
    }
    private void initializeFoodZero(){
        FoodZero = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> FoodZeroGIF.setImage(assetManager.getImage("Lapar 1"))),
            new KeyFrame(Duration.seconds(0.5), e -> FoodZeroGIF.setImage(assetManager.getImage("Lapar 2"))),
            new KeyFrame(Duration.seconds(1.0), e -> FoodZeroGIF.setImage(assetManager.getImage("Lapar 3"))),
            new KeyFrame(Duration.seconds(1.5))
        ); FoodZero.setCycleCount(Timeline.INDEFINITE);
    }
    private void startGameOverDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
        delay.setOnFinished(e -> gameOverMenu.setVisible(true));
        delay.play();
    }

    @FXML private void onRestartClick() {
        player.sanityProperty().set(100);
        player.foodProperty().set(100);
        player.moneyProperty().set(1000);
        player.setName("Bocchi");        
        if (FoodZero != null) FoodZero.stop();
        
        stopAllGameActivities();
        resetUIState();
        if (bgmPlayer != null) { bgmPlayer.setVolume(0.5); bgmPlayer.play(); } 
        characterAnimation.play();
    }
    @FXML private void onLoadGameOverClick() { isSavingMode = false; openSlotMenu(); }

    // --- MENUS (Save, Load, Settings) ---
    @FXML private void onPartTimeClick() { executeActivity(new PartTimeJob()); }
    @FXML private void onBandClick() { int variant = random.nextInt(3) + 1; executeActivity(new BandPractice(variant)); }
    @FXML private void onSettingsClick() { if(isGameOver) return; playSound(assetManager.getSound("gearSound")); toggleSettingsMenu(); }
    @FXML private void onSaveMenuClick() { isSavingMode = true; openSlotMenu(); }
    @FXML private void onLoadMenuClick() { isSavingMode = false; openSlotMenu(); }
    private void openSlotMenu() { slotMenu.setVisible(true); slotMenuTitle.setText(isSavingMode ? "SAVE GAME" : "LOAD GAME"); updateSlotButton(slot1Button, "save_slot_1"); updateSlotButton(slot2Button, "save_slot_2"); updateSlotButton(slot3Button, "save_slot_3"); }
    private void updateSlotButton(Button btn, String slotName) { PlayerDataDTO data = saveLoadService.getSlotPreview(slotName); if (data != null) { String name = (data.getPlayerName() != null) ? data.getPlayerName() : "Bocchi"; btn.setText(String.format("%s\nMoney: ¥ %,d", name, data.getMoney())); } else { btn.setText("Empty Slot"); } }
    @FXML private void onSlot1Click() { handleSlotSelection(1); }
    @FXML private void onSlot2Click() { handleSlotSelection(2); }
    @FXML private void onSlot3Click() { handleSlotSelection(3); }
    private void handleSlotSelection(int slot) { selectedSlot = slot; String slotFileName = "save_slot_" + slot; boolean isSlotEmpty = (saveLoadService.getSlotPreview(slotFileName) == null); if (isSavingMode) { if (isSlotEmpty) openNameInput(); else { confirmationMenu.setVisible(true); confirmationText.setText("Overwrite Slot " + slot + "?"); } } else { if (isSlotEmpty) return; confirmationMenu.setVisible(true); confirmationText.setText("Load Slot " + slot + "?"); } }
    private void openNameInput() { inputNameMenu.setVisible(true); nameInput.clear(); nameInput.requestFocus(); }
    @FXML private void onConfirmYes() { if (isSavingMode) { saveLoadService.saveGame(player, "save_slot_" + selectedSlot, player.getName()); } else { this.player = saveLoadService.loadGame("save_slot_" + selectedSlot); uiManager.bindToPlayer(player); if(isGameOver) { stopAllGameActivities(); resetUIState(); if (bgmPlayer != null) { bgmPlayer.setVolume(0.5); bgmPlayer.play(); } characterAnimation.play(); } } closeAllMenus(); }
    @FXML private void onNameConfirm() { String inputName = nameInput.getText(); if (inputName.trim().isEmpty()) inputName = "Bocchi"; player.setName(inputName); saveLoadService.saveGame(player, "save_slot_" + selectedSlot, inputName); closeAllMenus(); }
    @FXML private void onNameCancel() { inputNameMenu.setVisible(false); }
    @FXML private void onConfirmNo() { confirmationMenu.setVisible(false); }
    @FXML private void onSlotBackClick() { slotMenu.setVisible(false); }
    private void closeAllMenus() { inputNameMenu.setVisible(false); confirmationMenu.setVisible(false); slotMenu.setVisible(false); if(!isGameOver) closeSettings(); else slotMenu.setVisible(false); }
    private void toggleSettingsMenu() { if (isSettingsOpen) closeSettings(); else openSettings(); }
    private void openSettings() { settingsMenu.setVisible(true); TranslateTransition slide = new TranslateTransition(Duration.millis(300), settingsMenu); slide.setToY(0); slide.play(); isSettingsOpen = true; toggleGameControls(true); }
    private void closeSettings() { TranslateTransition slide = new TranslateTransition(Duration.millis(300), settingsMenu); slide.setToY(400); slide.setOnFinished(e -> settingsMenu.setVisible(false)); slide.play(); isSettingsOpen = false; toggleGameControls(false); }
    private void toggleGameControls(boolean disable) { partTimeButton.setDisable(disable); bandButton.setDisable(disable); shopButton.setDisable(disable); }
    @FXML private void onShopClick() { if(isGameOver) return; playSound(assetManager.getSound("shopBellSound")); shopMenu.setVisible(true); toggleGameControls(true); }
    @FXML private void onShopBackClick() { playSound(assetManager.getSound("shopBellSound")); shopMenu.setVisible(false); toggleGameControls(false); }
    @FXML private void onBuyOnigiri() { buyItem(0); }
    @FXML private void onBuyBento() { buyItem(1); }
    @FXML private void onBuyEnergyDrink() { buyItem(2); }
    @FXML private void onBuyPill() { buyItem(3); }
    @FXML private void onBuyPinkGuitar() { buyItem(4); }
    @FXML private void onBuyYellowGuitar() { buyItem(5); }
    private void buyItem(int index) {
        ShopItem item = shop.getAvailableItems().get(index);
        int oldSanity = player.getSanity(); int oldFood = player.getFood();
        if (shop.purchaseItem(player, item)) {
            if (index >= 4) playSound(assetManager.getSound("guitarRiffSound")); else playSound(assetManager.getSound("cashRegisterSound"));
            int diffSanity = player.getSanity() - oldSanity; int diffFood = player.getFood() - oldFood;
            showFloatingText(sanityContainer, diffSanity); showFloatingText(foodContainer, diffFood);
        }
    }
    
    private void initializeCharacterAnimation() {
        ColorAdjust lightsOff = new ColorAdjust(); lightsOff.setBrightness(-0.6); 
        characterAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> { bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1")); bocchiCharacter.setEffect(null); if (guitarLoopMusic != null) guitarLoopMusic.stop(); if (bgmPlayer != null) bgmPlayer.setVolume(0.5); }),
            new KeyFrame(Duration.seconds(2.0), e -> { bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2")); bocchiCharacter.setEffect(null); }),
            new KeyFrame(Duration.seconds(2.2), e -> { bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1")); bocchiCharacter.setEffect(null); }),
            new KeyFrame(Duration.seconds(5), e -> { bocchiCharacter.setImage(assetManager.getImage("bocchiGuitar")); bocchiCharacter.setEffect(lightsOff); if (guitarLoopMusic != null) guitarLoopMusic.play(); if (bgmPlayer != null) bgmPlayer.setVolume(0.1); }),
            new KeyFrame(Duration.seconds(10), e -> { bocchiCharacter.setImage(assetManager.getImage("bocchiSleep")); bocchiCharacter.setEffect(lightsOff); if (guitarLoopMusic != null) guitarLoopMusic.stop(); if (bgmPlayer != null) bgmPlayer.setVolume(0.5); }),
            new KeyFrame(Duration.seconds(15)) 
        );
        characterAnimation.setCycleCount(Timeline.INDEFINITE);
    }
    
    private class InternalUIManager {
        private ProgressBar sanityBar; private ProgressBar foodBar; private Label moneyLabel; private ImageView bocchiCharView;
        private Timeline moneyRollAnimation; private Timeline sanityBlink; private Timeline foodBlink;
        public InternalUIManager(ProgressBar s, ProgressBar f, Label m, ImageView b) {
            this.sanityBar = s; this.foodBar = f; this.moneyLabel = m; this.bocchiCharView = b;
            sanityBlink = createBlinkAnimation(sanityBar); foodBlink = createBlinkAnimation(foodBar);
        }
        private Timeline createBlinkAnimation(ProgressBar bar) {
            Timeline blink = new Timeline(new KeyFrame(Duration.seconds(0.0), e -> bar.setOpacity(1.0)), new KeyFrame(Duration.seconds(0.3), e -> bar.setOpacity(0.4)), new KeyFrame(Duration.seconds(0.6), e -> bar.setOpacity(1.0)));
            blink.setCycleCount(Timeline.INDEFINITE); return blink;
        }
        public void bindToPlayer(Player playerToBind) {
            sanityBar.progressProperty().bind(playerToBind.sanityProperty().divide(100.0));
            foodBar.progressProperty().bind(playerToBind.foodProperty().divide(100.0));
            moneyLabel.setText("¥ " + String.format("%,d", playerToBind.getMoney()));
            playerToBind.moneyProperty().addListener((obs, oldVal, newVal) -> playMoneyRollingAnimation(oldVal.intValue(), newVal.intValue()));
            playerToBind.sanityProperty().addListener((obs, oldValue, newValue) -> { updateBarStyle(sanityBar, newValue.intValue(), sanityBlink); if (newValue.intValue() <= 0 && oldValue.intValue() > 0) MainGameController.this.triggerSanityZero(); });
            playerToBind.foodProperty().addListener((obs, oldValue, newValue) -> { updateBarStyle(foodBar, newValue.intValue(), foodBlink); if (newValue.intValue() <= 0 && oldValue.intValue() > 0) MainGameController.this.triggerFoodZero(); });
            updateBarStyle(sanityBar, playerToBind.getSanity(), sanityBlink); updateBarStyle(foodBar, playerToBind.getFood(), foodBlink);
        }
        private void updateBarStyle(ProgressBar bar, int value, Timeline blinkAnimation) {
            bar.getStyleClass().removeAll("bar-warning", "bar-critical"); blinkAnimation.stop(); bar.setOpacity(1.0); 
            if (value < 20) { bar.getStyleClass().add("bar-critical"); blinkAnimation.play(); } else if (value < 50) { bar.getStyleClass().add("bar-warning"); }
        }
        private void playMoneyRollingAnimation(int startValue, int endValue) {
            if (moneyRollAnimation != null) moneyRollAnimation.stop();
            if (endValue > startValue) moneyLabel.setTextFill(Color.LIMEGREEN); else if (endValue < startValue) moneyLabel.setTextFill(Color.RED); else moneyLabel.setTextFill(Color.WHITE);
            final int[] currentValue = {startValue};
            moneyRollAnimation = new Timeline(new KeyFrame(Duration.millis(30), e -> {
                int diff = endValue - currentValue[0]; int step = diff / 5; if (step == 0) step = (diff > 0) ? 1 : -1;
                currentValue[0] += step; moneyLabel.setText("¥ " + String.format("%,d", currentValue[0]));
                if (currentValue[0] == endValue) { moneyRollAnimation.stop(); moneyLabel.setTextFill(Color.WHITE); }
            }));
            moneyRollAnimation.setCycleCount(Timeline.INDEFINITE); moneyRollAnimation.play();
        }
    } 
}