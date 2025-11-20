package com.bocchipet.controllers;

//Astaga naga, apa-apaan ini semua
import com.bocchipet.models.Player;
import com.bocchipet.models.activities.BandPractice;
import com.bocchipet.models.activities.IActivity;
import com.bocchipet.models.activities.PartTimeJob;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainGameController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private ProgressBar sanityBar;
    @FXML private ProgressBar foodBar;
    @FXML private Label moneyLabel;
    @FXML private ImageView bocchiCharacter;
    @FXML private ImageView guitarOnPlayer;
    @FXML private Button partTimeButton;
    @FXML private Button bandButton;
    @FXML private Button shopButton;
    @FXML private Button settingsButton;
    @FXML private ImageView animationOverlay;
    @FXML private VBox settingsMenu;
    @FXML private Button saveButton;
    @FXML private Button loadButton;

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

        uiManager = new UIManager(sanityBar, foodBar, moneyLabel, bocchiCharacter, guitarOnPlayer);
        uiManager.bindToPlayer(player);

        animationOverlay.setVisible(false);
        animationOverlay.setTranslateX(500);
        settingsMenu.setVisible(false);
        settingsMenu.setTranslateY(200);

        initializeCharacterAnimation();
        characterAnimation.play();

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Tombol Save (Anonymous) diklik!");
                saveLoadService.saveGame(player, "save_slot_1");
                toggleSettingsMenu();
            }
        });
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
    private void onShopClick() {
        playSound(assetManager.getSound("shopBellSound"));
        System.out.println("Tombol Shop diklik!");
    }

    @FXML
    private void onSettingsClick() {
        playSound(assetManager.getSound("gearSound"));
        toggleSettingsMenu();
    }

    @FXML
    private void onLoadClick() {
        System.out.println("Tombol Load diklik!");
        this.player = saveLoadService.loadGame("save_slot_1");
        uiManager.bindToPlayer(player);
        toggleSettingsMenu();
    }

    private void executeActivity(IActivity activity) {
        activity.perform(player);

        Image animationImage = assetManager.getImage(activity.getAnimationImageKey());
        Media soundEffect = assetManager.getSound(activity.getSoundEffectKey());

        playSlideAnimation(animationImage);
        playSound(soundEffect);
    }

    private void playSound(Media sound) {
        if (sound == null) return;
        if (sfxPlayer != null) {
            sfxPlayer.stop();
        }
        sfxPlayer = new MediaPlayer(sound);
        sfxPlayer.play();
    }

    private void playSlideAnimation(Image img) {
        if (img == null) return;

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
        sequence.setOnFinished(e -> animationOverlay.setVisible(false));
        sequence.play();
    }

    private void toggleSettingsMenu() {
        settingsMenu.setVisible(true);
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), settingsMenu);

        if (settingsMenu.getTranslateY() == 0) {
            slide.setToY(200);
            slide.setOnFinished(e -> settingsMenu.setVisible(false));
        } else {
            slide.setToY(0);
        }
        slide.play();
    }

    private void initializeCharacterAnimation() {
        characterAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
                new KeyFrame(Duration.seconds(0.5), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))),
                new KeyFrame(Duration.seconds(1), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
                new KeyFrame(Duration.seconds(1.5), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))),
                new KeyFrame(Duration.seconds(2), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
                new KeyFrame(Duration.seconds(2.5), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))),
                new KeyFrame(Duration.seconds(3), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
                new KeyFrame(Duration.seconds(3.5), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))),
                new KeyFrame(Duration.seconds(4), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle1"))),
                new KeyFrame(Duration.seconds(4.5), e -> bocchiCharacter.setImage(assetManager.getImage("bocchiIdle2"))),
                
                new KeyFrame(Duration.seconds(5), e -> {
                    bocchiCharacter.setImage(assetManager.getImage("bocchiGuitar"));
                    if (musicPlayer != null) musicPlayer.stop();
                    musicPlayer = new MediaPlayer(assetManager.getSound("guitarLoopSound"));
                    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    musicPlayer.play();
                }),
                
                new KeyFrame(Duration.seconds(10), e -> {
                    bocchiCharacter.setImage(assetManager.getImage("bocchiSleep"));
                    if (musicPlayer != null) musicPlayer.stop();
                }),

                new KeyFrame(Duration.seconds(15)) 
        );
        characterAnimation.setCycleCount(Timeline.INDEFINITE);
    }

    private class UIManager {
        
        private ProgressBar sanityBar;
        private ProgressBar foodBar;
        private Label moneyLabel;
        private ImageView bocchiCharView;
        private ImageView guitarView;

        public UIManager(ProgressBar sBar, ProgressBar fBar, Label mLabel, ImageView bChar, ImageView gView) {
            this.sanityBar = sBar;
            this.foodBar = fBar;
            this.moneyLabel = mLabel;
            this.bocchiCharView = bChar;
            this.guitarView = gView;
        }

        public void bindToPlayer(Player playerToBind) {
            sanityBar.progressProperty().bind(playerToBind.sanityProperty().divide(100.0));
            foodBar.progressProperty().bind(playerToBind.foodProperty().divide(100.0));

            moneyLabel.textProperty().bind(playerToBind.moneyProperty().asString("¥ %,d"));

            guitarView.setImage(assetManager.getImage(playerToBind.getCurrentGuitarImage()));
            
            playerToBind.currentGuitarImageProperty().addListener(
                (obs, oldImagePath, newImagePath) -> {
                    Image newGuitarImg = assetManager.getImage(newImagePath);
                    if (newGuitarImg != null) {
                        guitarView.setImage(newGuitarImg);
                    } else {
                        guitarView.setImage(assetManager.getImage("defaultGuitar"));
                    }
                }
            );
        }
    }
}