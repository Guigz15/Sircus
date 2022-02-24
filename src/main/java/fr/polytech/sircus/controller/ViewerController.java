package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Contrôleur permettant la gestion de la fenêtre du lecteur
 */
public class ViewerController {
    //******************************************************************************************************************
    // Composants UI
    //******************************************************************************************************************
    /**
     * Le controller MetaSequenceController qui a créé ce controller
     */
    private final MetaSequenceController metaSequenceController;
    /**
     * La métaséquence communiquée au viewer
     */
    private final MetaSequence playingMetaSequence;
    /**
     * La liste contenant les temps de début des médias de la méta-séquence actuelle, ainsi que le temps de fin
     */
    ArrayList<Integer> listeDebutMedia;
    //******************************************************************************************************************
    /**
     * L'objet qui gère la taille et la position des vidéos et contient le mediaPlayer
     */
    @FXML
    private MediaView mediaView;
    /**
     * L'objet affichant les images
     */
    @FXML
    private ImageView imageView;
    /**
     * L'objet jouant les vidéos
     */
    private MediaPlayer mediaPlayer;
    /**
     * Stage du viewer
     */
    private Stage viewerStage = null;
    /**
     * La timeline permettant la lecture des médias avec gestion du temps de chacun
     */
    private Timeline timeline = null;
    /**
     * Booléen indiquant si la méta-séquence a déjà été démarrée une fois ou pas
     */
    private boolean metaSequenceStarted;

    private ViewerController viewerController;
    //******************************************************************************************************************
    //******************************************************************************************************************
    //   ###    ###   #   #   ####  #####  ####   #   #   ###   #####   ###   ####    ####
    //  #   #  #   #  ##  #  #        #    #   #  #   #  #   #    #    #   #  #   #  #
    //  #      #   #  # # #   ###     #    ####   #   #  #        #    #   #  ####    ###
    //  #   #  #   #  #  ##      #    #    #   #  #   #  #   #    #    #   #  #   #      #
    //   ###    ###   #   #  ####     #    #   #   ###    ###     #     ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Constructeur du controller
     *
     * @param owner Fenetre principale
     */
    public ViewerController(Window owner, MetaSequence metaSequence, MetaSequenceController metaSequenceController) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/viewer.fxml")));
        fxmlLoader.setController(this);

        try {
            Scene dialogScene = new Scene(fxmlLoader.load(), 1600, 900);
            Stage dialog = new Stage();

            viewerStage = dialog;

            dialog.initModality(Modality.NONE);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setTitle("Viewer");
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playingMetaSequence = metaSequence;
        metaSequenceStarted = false;
        this.metaSequenceController = metaSequenceController;
        listeDebutMedia = new ArrayList<Integer>();
        closingManager();
    }

    //******************************************************************************************************************
    //      #  #####  #   #         #####  #   #  #   #   ###   #####  #   ###   #   #   ####
    //      #  #       # #          #      #   #  ##  #  #   #    #    #  #   #  ##  #  #
    //      #  ###      #           ###    #   #  # # #  #        #    #  #   #  # # #   ###
    //  #   #  #       # #          #      #   #  #  ##  #   #    #    #  #   #  #  ##      #
    //   ###   #      #   #         #       ###   #   #   ###     #    #   ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Initialise le controller et ses attributs
     */
    @FXML
    private void initialize() {
        // mediaView = new MediaView();
        // Ne pas décommenter la ligne suivante, cela remplace l'ImageView déjà mise placée dans la vue
        // imageView = new ImageView();
    }

    /**
     * Retourne l'attribut timeline du controller
     *
     * @return timeline l'attribut timeline du controller
     */
    @FXML
    public Timeline getTimeline() {
        return timeline;
    }

    /**
     * Affiche le Media donné en paramètre
     *
     * @param media Media que l'on veut afficher
     */
    @FXML
    private void showMedia(Media media) {
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);
    }

    /**
     * Affiche le Media dont le nom est donné en paramètre
     *
     * @param name nom du Media que l'on veut afficher
     */
    @FXML
    private void showMediaFromName(String name) {
        File mediaFile = new File("medias/" + name);
        try {
            Media media = new Media(mediaFile.toURI().toURL().toString());
            showMedia(media);
        }
        // Si l'URL est malformée, on le signale
        catch (MalformedURLException error) {
            System.out.println("URL malformée, le chemin vers la vidéo est incorrect.");
        }

    }

    /**
     * Retire la vidéo affichée
     */
    @FXML
    private void removeMedia() {
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().pause();
            mediaView.setMediaPlayer(null);
        }
    }

    /**
     * Affiche l'image donnée en paramètre
     *
     * @param image l'image que l'on veut afficher
     */
    @FXML
    private void showImage(Image image) {
        imageView.setImage(image);
        imageView.setCache(true);
    }

    /**
     * Affiche l'image dont le nom est donné en paramètre
     *
     * @param name nom de l'image que l'on veut afficher
     */
    @FXML
    private void showImageFromName(String name) {
        // On essaye de créer un InputStream avec le chemin du fichier
        try {
            InputStream is = new FileInputStream("medias/" + name);
            Image image = new Image(is);
            showImage(image);
        }
        // Si le chemin n'est pas trouvé on le signale
        catch (FileNotFoundException error) {
            System.out.println("Le fichier n'existe pas.");
        }
    }

    /**
     * Retire l'image affichée actuellement
     */
    @FXML
    private void removeImage() {
        imageView.setImage(null);
    }

    /**
     * Commence la lecture de la méta-séquence donnée en paramètre
     *
     * @param metaSequence la meta-séquence à lancer
     */
    @FXML
    private void startMetaSequence(MetaSequence metaSequence) {
        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        // Compteur permettant de compter la durée totale des médias parcourus
        int cptDuree = 0;
        // System.out.println("CptDurée : " + cptDuree);

        for (Sequence sequence : metaSequence.getSequencesList()) {
            // Pour chaque Média de la séquence
            for (fr.polytech.sircus.model.Media media : sequence.getListMedias()) {
                // Si le média est une image
                if (media.getType() == TypeMedia.PICTURE) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(cptDuree),
                            new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    removeMedia();
                                    showImageFromName(media.getName());
                                    // System.out.println("Image donnée.");
                                }
                            }));
                    // On ajoute dans la liste des départs de médias la seconde à laquelle l'image démarre
                    listeDebutMedia.add(cptDuree);

                    // On ajoute au compteur de durée la durée du média actuellement parcouru
                    cptDuree += media.getDuration().getSeconds();
                }
                // Si le média est une vidéo
                else if (media.getType() == TypeMedia.VIDEO) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(cptDuree),
                            new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    removeImage();
                                    showMediaFromName(media.getName());
                                    // System.out.println("Vidéo donnée.");
                                }
                            }));
                    // On ajoute dans la liste des départs de médias la seconde à laquelle la vidéo démarre
                    listeDebutMedia.add(cptDuree);
                    // On ajoute au compteur de durée la durée du média actuellement parcouru
                    cptDuree += media.getDuration().getSeconds();
                }
                // Si le média donné a une interstim
                if (media.getInterStim() != null) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(cptDuree),
                            new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    removeMedia();
                                    showImageFromName(media.getInterStim().getName());
                                    // System.out.println("Image donnée.");
                                }
                            }));
                    // On ajoute dans la liste des départs de médias quand l'interstimulation démarre
                    // On pourrait commenter cette ligne si l'on ne souhaite pas passer par les interstimulations avec
                    // les boutons permettant de passer un média ou de revenir au média précédent
                    listeDebutMedia.add(cptDuree);
                    // On ajoute au compteur de durée de l'interstimulation
                    cptDuree += media.getInterStim().getDuration().getSeconds();
                }
            }
        }

        // On ajoute un évènement qui retire l'image ou la vidéo à la fin de la lecture
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(cptDuree),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        removeMedia();
                        removeImage();
                    }
                }));
        // On ajoute dans la liste des départs de médias la fin de la lecture
        // Cela permet notamment au bouton passant un média de déclencher la fin de la lecture de la méta-séquence
        // si nous sommes en train de lire le dernier média
        listeDebutMedia.add(cptDuree);

        // On lance le chronomètre de la timeline pour que les évènements que l'on vient de créer se déclenchent
        timeline.play();
    }

    /**
     * Met en pause la lecture
     */
    @FXML
    public void pauseViewer() {
        if (timeline != null) {
            timeline.pause();
            if (mediaView.getMediaPlayer() != null) {
                mediaPlayer.pause();
            }
        }
    }

    /**
     * Démarre la lecture de 0 si la méta-séquence n'a jamais été lancée. Sinon, relance la lecture des médias.
     */
    @FXML
    public void playViewer() {
        if (metaSequenceStarted) {
            timeline.play();
            if (mediaView.getMediaPlayer() != null) {
                mediaPlayer.play();
            }
        } else {
            metaSequenceStarted = true;
            startMetaSequence(playingMetaSequence);
        }
    }

    /**
     * Affiche le média suivant
     */
    public void nextMedia() {
        if (listeDebutMedia.size() > 0) {
            // On définit une date de départ tampon, à la première date de départ de la liste des débuts (probablement 0)
            Integer dateDebutTrouvee = listeDebutMedia.get(0);

            // Pour chaque date de départ de média
            for (int i = 0; i < listeDebutMedia.size(); i++) {
                // On vérifie si la date de départ est supérieure à la date actuelle de la timeline
                if (listeDebutMedia.get(i) > timeline.getCurrentTime().toSeconds()) {
                    // On a trouvé le média
                    dateDebutTrouvee = listeDebutMedia.get(i);
                    break;
                }
            }
            // Duration de javafx.util.Duration prend en paramètre de constructeur des ms
            // On multiplie par 1000 les secondes
            timeline.jumpTo(new Duration(dateDebutTrouvee * 1000));
        }
    }

    /**
     * Affiche le média précédent
     */
    public void prevMedia() {
        if (listeDebutMedia.size() > 0) {
            // On définit une date de départ tampon, à la première date de départ de la liste des débuts (probablement 0)
            Integer dateDebutTrouvee = listeDebutMedia.get(0);

            // Pour chaque date de départ de média
            int i = 0;
            // Cette boucle permet de trouver l'index du premier média qui sera lancé après le média en cours
            while (i < listeDebutMedia.size() && listeDebutMedia.get(i) < timeline.getCurrentTime().toSeconds()) {
                i++;
            }

            // Si i est supérieur à 0, le média précédent est i-1, c'est l'index correspondant au médial actuel.
            // Le média à l'index i correspond au prochain média
            // Donc on veut retirer deux à i pour trouver le média précédent
            if (i > 1) {
                i -= 2;
                dateDebutTrouvee = listeDebutMedia.get(i);
            }

            // Duration de javafx.util.Duration prend en paramètre de constructeur des ms
            // On multiplie par 1000 les secondes
            timeline.jumpTo(new Duration(dateDebutTrouvee * 1000));
        }
    }

    /**
     * Quitte le viewer
     */
    @FXML
    private void quitViewer() {
        viewerStage.close();
    }

    /**
     * Le handle de cette méthode est appelé quand l'utilisateur ferme la fenêtre du viewer.
     * Cela appelle la méthode appropriée du MetaSequence controller afin de pouvoir remettre par défaut
     * certains attributs et pouvoir relancer le viewer correctement.
     */
    private void closingManager() {
        viewerStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                metaSequenceController.viewerClosed();
            }
        });
    }
}
