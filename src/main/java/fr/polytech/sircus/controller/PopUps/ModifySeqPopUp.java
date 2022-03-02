package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.MetaSequenceController;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

/**
 * Controleur permettant la gestion de modification d'une sequence
 */
public class ModifySeqPopUp {
    //******************************************************************************************************************
    // Composants UI
    //******************************************************************************************************************

    /**
     * Objet du bouton d'ajout de fichier : permet la sélection d'un fichier pour un media
     */
    private final FileChooser fileChooserMedia;
    /**
     * Bouton sauvegardant les modifications de la sequence
     */
    @FXML
    private Button saveAddMediaSeq;
    /**
     * Bouton d'annulation, fermant la pop-up de modification de la séquence
     */
    @FXML
    private Button cancelAddMediaSeq;
    /**
     * Bouton d'ajout d'un media à la sequence
     */
    @FXML
    private Button addMediaToSeq;
    /**
     * Objet du bouton d'ajout de fichier : permet la sélection d'un fichier pour une inter-stimulation
     */
    private FileChooser fileChooserInterstim;

    /**
     * Champ de texte du nom de la séquence à modifier
     */
    @FXML
    private TextField titleSequenceLabel;

    /**
     * Tableau des médias
     */
    @FXML
    private TableView<Media> mediaTable;

    /**
     * Tableau des médias : colonne pour le vérrouillage
     */
    @FXML
    private TableColumn<Media, String> mediaTableColumnVerrouillage;

    /**
     * Tableau des médias : colonne du nom
     */
    @FXML
    private TableColumn<Media, String> mediaTableColumnName;

    /**
     * Tableau des médias : colonne de la durée
     */
    @FXML
    private TableColumn<Media, String> mediaTableColumnDuration;

    /**
     * Tableau des médias : colonne des options (boutons de modifications)
     */
    @FXML
    private TableColumn<Media, String> mediaTableColumnOption;
    //******************************************************************************************************************

    //******************************************************************************************************************
    // Gestionnaires sequences
    //******************************************************************************************************************

    /**
     * Sequence a modifier
     */
    private Sequence sequence = null;

    /**
     * Pop up de modification
     */
    private Stage popUpStage = null;

    /**
     * Objet listener crée dans MetaSequenceController
     */
    private MetaSequenceController.ModificationSequenceListener listener;

    //******************************************************************************************************************
    /**
     * Liste des médias ET des médias d'inter-stimulation
     */
    private List<Media> listMediaPlusInterstim = null;
    /**
     * Objet temporaire servant au stockage d'un média (utile lors de la création par copie d'un média)
     */
    private Media tempMedia = null;

    /**
     * Constructeur du contrôleur de la pop-up de modification d'une séquence et de ses composantes
     *
     * @param owner      fenêtre principale
     * @param listMedias liste des médias contenus dans la séquence
     * @param sequence   la séquence à modifier
     * @param listener   l'event listener de modification de la séquence provenant de MetaSequenceController
     */
    public ModifySeqPopUp(Window owner, ObservableList<Media> listMedias, Sequence sequence,
                          MetaSequenceController.ModificationSequenceListener listener, FileChooser fileChooserMedia,
                          FileChooser fileChooserInterstim) {

        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/modify_seq_popup.fxml"));
        fxmlLoader.setController(this);

        this.fileChooserMedia = fileChooserMedia;
        this.fileChooserInterstim = fileChooserInterstim;

        try {
            this.sequence = sequence;
            this.listener = listener;
            this.listMediaPlusInterstim = new ArrayList<>();
            consructMediaInterstimList();

            this.fileChooserInterstim = new FileChooser();
            this.fileChooserInterstim.setTitle("Open file (interstim)");
            this.fileChooserInterstim.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            Scene dialogScene = new Scene(fxmlLoader.load(), 1000, 500);
            Stage dialog = new Stage();

            this.popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setMinHeight(250); //220 (+30 hauteur de l'entête de la fenêtre sur windows)
            dialog.setMinWidth(585); //575 (+10 largeur de la fenêtre sur windows)
            dialog.setTitle("Modifier la séquence : " + this.sequence.getName());

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructeur de la liste des médias ET inter-stimulations
     */
    private void consructMediaInterstimList() {

        this.listMediaPlusInterstim.clear();

        for (int i = 0; i < this.sequence.getListMedias().size(); i++) {
            this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i));

            if (this.sequence.getListMedias().get(i).getInterStim() != null) {
                this.listMediaPlusInterstim.add(this.sequence.getListMedias().get(i).getInterStim());
            }
        }
    }

    /**
     * Méthode d'initialisation du controleur et de ses attributs puis ajoute des fonctionnalités à chaque composant
     */
    @FXML
    private void initialize() {

        this.titleSequenceLabel.setText(this.sequence.getName());
        this.mediaTableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        this.mediaTableColumnDuration.setCellValueFactory(cellData -> {
            String formattedDuration = cellData.getValue().getDuration().toString()
                    .replace("PT", "")
                    .replace("M", "m")
                    .replace("S", "s");
            return new SimpleStringProperty(formattedDuration);
        });

        Callback<TableColumn<Media, String>, TableCell<Media, String>> cellFactoryOption = new Callback<>() {
            @Override
            public TableCell<Media, String> call(final TableColumn<Media, String> param) {
                return new TableCell<>() {
                    final Button tableViewOptionButton = new Button("");
                    final Button tableViewDeleteButton = new Button("");
                    final Button tableViewAddButton = new Button("");
                    final HBox hBox = new HBox(tableViewAddButton, tableViewOptionButton, tableViewDeleteButton);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Boolean interstim = Boolean.FALSE;
                            if (tempMedia != null) {
                                if (getTableRow().getItem() == tempMedia.getInterStim()) {
                                    interstim = Boolean.TRUE;
                                }
                            }
                            tempMedia = getTableRow().getItem();

                            final FontIcon addIcon = new FontIcon("fa-plus");
                            final FontIcon cogIcon = new FontIcon("fa-cog");
                            final FontIcon delIcon = new FontIcon("fa-trash");

                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);

                            tableViewAddButton.setGraphic(addIcon);
                            tableViewOptionButton.setGraphic(cogIcon);
                            tableViewDeleteButton.setGraphic(delIcon);

                            tableViewOptionButton.setOnMouseClicked(event ->
                            {
                                Media media = getTableView().getItems().get(getIndex());
                                modifyMediaInSeq(media);
                            });


                            if (interstim) {
                                tableViewAddButton.setDisable(true);

                                tableViewDeleteButton.setOnAction(event ->
                                {
                                    for (Media media : listMediaPlusInterstim) {
                                        if (media.getInterStim() == getTableView().getItems().get(getIndex())) {
                                            media.setInterStim(null);

                                        }
                                    }

                                    consructMediaInterstimList();
                                    mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                    mediaTable.refresh();
                                });

                                getTableRow().setStyle("-fx-background-color : #e6f2ff");
                            } else {
                                tableViewDeleteButton.setOnAction(event ->
                                {
                                    sequence.removeMedia(getTableView().getItems().get(getIndex()));
                                    consructMediaInterstimList();
                                    mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                    mediaTable.refresh();
                                });

                                if (getTableView().getItems().get(getIndex()).getInterStim() == null) {
                                    tableViewAddButton.setOnMouseClicked(event ->
                                    {
                                        try {
                                            File newInterstim = fileChooserInterstim.showOpenDialog(popUpStage);
                                            Path path = Paths.get(newInterstim.getPath());
                                            OutputStream os = new FileOutputStream("medias/" + newInterstim.getName());
                                            Files.copy(path, os);

                                            Media newMedia = new Media(
                                                    newInterstim.getName(),
                                                    newInterstim.getName(),
                                                    Duration.ofSeconds(1),
                                                    TypeMedia.PICTURE,
                                                    null
                                            );

                                            getTableView().getItems().get(getIndex()).setInterStim(newMedia);

                                            consructMediaInterstimList();
                                            mediaTable.setItems(FXCollections.observableList(listMediaPlusInterstim));
                                            mediaTable.refresh();
                                        } catch (Exception e) {
                                            System.out.print("Aucun fichier selectionné.");
                                        }
                                    });
                                } else {
                                    tableViewAddButton.setDisable(true);
                                }

                                getTableRow().setStyle("-fx-background-color : #b3d9ff");
                            }

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        Callback<TableColumn<Media, String>, TableCell<Media, String>> cellFactoryVerr = new Callback<>() {
            @Override
            public TableCell<Media, String> call(final TableColumn<Media, String> param) {
                return new TableCell<>() {
                    final CheckBox tableViewVerrCheckBox = new CheckBox("");
                    final HBox hBox = new HBox(tableViewVerrCheckBox);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20);
                            Media media = getTableView().getItems().get(getIndex());

                            tableViewVerrCheckBox.setSelected(media.getLock());

                            tableViewVerrCheckBox.setOnAction(event ->
                                    media.setLock(tableViewVerrCheckBox.isSelected()));

                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };

        this.mediaTableColumnOption.setCellFactory(cellFactoryOption);
        this.mediaTableColumnVerrouillage.setCellFactory(cellFactoryVerr);

        this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));

        this.cancelAddMediaSeq.setOnMouseClicked(mouseEvent -> cancelAddSeq());
        this.saveAddMediaSeq.setOnMouseClicked(mouseEvent -> saveMediasToSeq());
        this.addMediaToSeq.setOnMouseClicked(mouseEvent -> addMediaToSeq());
        this.titleSequenceLabel.setOnKeyPressed(mouseEvent -> {
            KeyCode keyCode = mouseEvent.getCode();
            if (keyCode.equals(KeyCode.ENTER)) {
                modifySequenceName();
            }
        });
    }

    //******************************************************************************************************************
    //   ###    ###   #   #   ####  #####  ####   #   #   ###   #####   ###   ####    ####
    //  #   #  #   #  ##  #  #        #    #   #  #   #  #   #    #    #   #  #   #  #
    //  #      #   #  # # #   ###     #    ####   #   #  #        #    #   #  ####    ###
    //  #   #  #   #  #  ##      #    #    #   #  #   #  #   #    #    #   #  #   #      #
    //   ###    ###   #   #  ####     #    #   #   ###    ###     #     ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Méthode modifiant le nom de la séquence
     */
    private void modifySequenceName() {
        if (!Objects.equals(this.sequence.getName(), this.titleSequenceLabel.getText())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Etes-vous sûr de vouloir renommer la séquence en " + this.titleSequenceLabel.getText() + " ?",
                    ButtonType.YES,
                    ButtonType.NO);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                this.sequence.setName(this.titleSequenceLabel.getText());
                this.popUpStage.setTitle("Modifier la séquence : " + this.sequence.getName());
                this.listener.onModified(this.sequence);
            }
        }
    }

    //******************************************************************************************************************
    //      #  #####  #   #         #####  #   #  #   #   ###   #####  #   ###   #   #   ####
    //      #  #       # #          #      #   #  ##  #  #   #    #    #  #   #  ##  #  #
    //      #  ###      #           ###    #   #  # # #  #        #    #  #   #  # # #   ###
    //  #   #  #       # #          #      #   #  #  ##  #   #    #    #  #   #  #  ##      #
    //   ###   #      #   #         #       ###   #   #   ###     #    #   ###   #   #  ####
    //******************************************************************************************************************

    /**
     * Méthode d'ajoute d'un media à la séquence
     */
    @FXML
    private void addMediaToSeq() {
        SequenceModificationListener listener = sequence -> {
            consructMediaInterstimList();
            this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
            this.mediaTable.refresh();
        };

        new AddMediaPopUp(
                this.saveAddMediaSeq.getScene().getWindow(),
                FXCollections.observableList(this.sequence.getListMedias()),
                this.sequence,
                listener,
                fileChooserMedia,
                fileChooserInterstim
        );
    }

    /**
     * Méthode de modification d'un média dans la séquence
     *
     * @param media média à modifier
     */
    @FXML
    private void modifyMediaInSeq(Media media) {
        SequenceModificationListener listener1 = sequence -> {
            consructMediaInterstimList();
            this.mediaTable.setItems(FXCollections.observableList(this.listMediaPlusInterstim));
            this.mediaTable.refresh();
        };

        MediaModificationListener listener2 = temp -> this.mediaTable.refresh();

        new ModifyMediaPopUp(
                this.saveAddMediaSeq.getScene().getWindow(),
                this.sequence,
                media,
                listener1,
                listener2
        );
    }

    /**
     * Méthode de fermeture de la pop-up de modification de la séquence
     */
    @FXML
    private void cancelAddSeq() {
        this.popUpStage.close();
    }

    /**
     * Méthode de sauvegarde des modifications de la séquence et fermeture la pop-up
     */
    @FXML
    private void saveMediasToSeq() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Etes-vous sûr de vouloir enregistrer les modifications de " + this.sequence.getName() + " ?",
                ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            this.sequence.setName(this.titleSequenceLabel.getText());
            this.sequence.setDuration(sequence.getDuration());
            this.listener.onModified(this.sequence);
            this.popUpStage.close();
        }
    }

    /**
     * Event listener de modification d'une séquence
     */
    public interface SequenceModificationListener extends EventListener {
        void onModified(Sequence sequence);
    }

    /**
     * Event listener de modification d'un média
     */
    public interface MediaModificationListener extends EventListener {
        void onModified(Media media);
    }
}
