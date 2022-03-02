package fr.polytech.sircus.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
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

    /**
     * List of results
     */
    public ObservableList<Result> list = FXCollections.observableArrayList(
            initResults());

    /**
     * Results table
     */
    @FXML private TableView<Result> resultTable;

    /**
     * MetaSequence name
     */
    @FXML private TableColumn<Result, String> metaSequenceName;

    /**
     * Duration
     */
    @FXML private TableColumn<Result, Duration> duration;

    /**
     * List of sequences
     */
    @FXML private TableColumn<Result, List<Sequence>> listSequences;


    /**
     * Initializes list of results
     */
    private List<Result> initResults() {
        List<MetaSequence> metaSequences = new ArrayList<MetaSequence>();
        metaSequences.add(SircusApplication.dataSircus.getMetaSequencesList().get(0));
        List<Result> results = new ArrayList<Result>();
        for (MetaSequence metaSequence : metaSequences) {
            Result result = new Result();
            result.setMetaSequenceName(metaSequence.getName());
            result.setDuration(metaSequence.getDuration());
            result.setSequencesList(metaSequence.getSequencesList());
            results.add(result);
        }
        return results;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        metaSequenceName.setCellValueFactory(new PropertyValueFactory<Result, String>("metaSequenceName"));
        duration.setCellValueFactory(new PropertyValueFactory<Result, Duration>("duration"));
        listSequences.setCellValueFactory(new PropertyValueFactory<Result, List<Sequence>>("sequencesList"));
        resultTable.setItems(list);
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
            for (Result result : list) {
                String nomMetaSequence = result.getMetaSequenceName();
                table_cell = new PdfPCell(new Phrase(nomMetaSequence));
                my_report_table.addCell(table_cell);
                Duration duration = result.getDuration();
                table_cell = new PdfPCell(new Phrase(String.valueOf(duration)));
                my_report_table.addCell(table_cell);
                List<Sequence> listSequences = result.getSequencesList();
                table_cell = new PdfPCell(new Phrase(String.valueOf((listSequences))));
                my_report_table.addCell(table_cell);

                /* Attach report table to PDF */
                my_pdf_report.add(my_report_table);

            }
            my_pdf_report.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}