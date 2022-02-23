package fr.polytech.sircus.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Resultat;
import fr.polytech.sircus.model.Sequence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResultatController implements Initializable {

    public ObservableList<Resultat> list = FXCollections.observableArrayList(
            initResultat());
    @FXML
    private TableView<Resultat> tableResultat;
    @FXML
    private TableColumn<Resultat, String> nomMetaSequence;
    @FXML
    private TableColumn<Resultat, Duration> duration;
    @FXML
    private TableColumn<Resultat, List<Sequence>> listSequences;

    public List<Resultat> initializeResultats(List<MetaSequence> metaSequences) {
        List<Resultat> resultats = new ArrayList<Resultat>();
        for (MetaSequence metaSequence : metaSequences) {
            Resultat resultat = new Resultat();
            resultat.setNomMetaSequence(metaSequence.getName());
            resultat.setDuration(metaSequence.getDuration());
            resultat.setListSequences(metaSequence.getListSequences());
        }
        return resultats;
    }

    private List<Resultat> initResultat() {
        List<MetaSequence> metaSequences = new ArrayList<MetaSequence>();
        metaSequences.add(SircusApplication.dataSircus.getMetaSequencesList().get(0));
        List<Resultat> resultats = new ArrayList<Resultat>();
        for (MetaSequence metaSequence : metaSequences) {
            Resultat resultat = new Resultat();
            resultat.setNomMetaSequence(metaSequence.getName());
            resultat.setDuration(metaSequence.getDuration());
            resultat.setListSequences(metaSequence.getListSequences());
            resultats.add(resultat);
        }
        return resultats;


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nomMetaSequence.setCellValueFactory(new PropertyValueFactory<Resultat, String>("nomMetaSequence"));
        duration.setCellValueFactory(new PropertyValueFactory<Resultat, Duration>("duration"));
        listSequences.setCellValueFactory(new PropertyValueFactory<Resultat, List<Sequence>>("listSequences"));
        tableResultat.setItems(list);

    }


    @FXML
    private void export(ActionEvent event) throws FileNotFoundException {
        try {
            Document my_pdf_report = new Document();
            PdfWriter.getInstance(my_pdf_report, new FileOutputStream("rapport_projet_circus.pdf"));
            my_pdf_report.open();
            //we have 3 columns in our table
            PdfPTable my_report_table = new PdfPTable(3);
            //create a cell object
            PdfPCell table_cell;
            table_cell = new PdfPCell(new Phrase("MetaSequence"));
            my_report_table.addCell(table_cell);
            table_cell = new PdfPCell(new Phrase("Durée"));
            my_report_table.addCell(table_cell);
            table_cell = new PdfPCell(new Phrase("List des séquences"));
            my_report_table.addCell(table_cell);
            for (Resultat resultat : list) {
                String nomMetaSequence = resultat.getNomMetaSequence();
                table_cell = new PdfPCell(new Phrase(nomMetaSequence));
                my_report_table.addCell(table_cell);
                Duration duration = resultat.getDuration();
                table_cell = new PdfPCell(new Phrase(String.valueOf(duration)));
                my_report_table.addCell(table_cell);
                List<Sequence> listSequences = resultat.getListSequences();
                table_cell = new PdfPCell(new Phrase(String.valueOf((listSequences))));
                my_report_table.addCell(table_cell);

                /* Attach report table to PDF */
                my_pdf_report.add(my_report_table);

            }
            my_pdf_report.close();
        } catch (DocumentException | FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
/*
            @FXML
    private void download(ActionEvent event) {
        event.consume();
        //writableImage nodeshot = tableResultat.snapshot(new SnapshotParameters(), null);
        File file = new File("chart.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(tableResultat.snapshot(new SnapshotParameters(), null), null), "png", file);
        } catch (IOException e) {
        }
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        PDImageXObject pdimage;
        PDPageContentStream content;
        try {
            pdimage = PDImageXObject.createFromFile("chart.png", doc);
            content = new PDPageContentStream(doc, page);
            content.drawImage(pdimage, 10, 10);
            content.close();
            doc.addPage(page);
            doc.save("pdf_file.pdf");
            doc.close();
            file.delete();
        } catch (IOException ex) {
                //Logger.getLogger(NodeToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}