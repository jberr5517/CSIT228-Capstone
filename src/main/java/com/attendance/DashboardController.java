package com.attendance;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private Label lblWelcome, lblDate, lblRoleBadge;
    @FXML private Label lblTotal, lblPresent, lblAbsent, lblRate;
    @FXML private Label lblStatusBar, lblFormError;
    @FXML private TableView<Student>           tableView;
    @FXML private TableColumn<Student, String> colId, colName, colSection, colStatus, colTime;
    @FXML private TextField tfSearch, tfNewId, tfNewName, tfNewSection, tfSearchStudent;
    @FXML private HBox adminToolbar, studentToolbar;
    @FXML private VBox addFormPanel, readOnlyPanel;
    @FXML private TableView<HistoryRecord>           historyTable;
    @FXML private TableColumn<HistoryRecord, String> hColDate, hColName, hColSection,
            hColStatus, hColTime;
    @FXML private DatePicker       dpHistoryFrom, dpHistoryTo;
    @FXML private ComboBox<String> cbHistoryStatus;
    @FXML private TextField        tfHistorySearch;

    private final ObservableList<Student>       allStudents = FXCollections.observableArrayList();
    private final ObservableList<HistoryRecord> allHistory  = FXCollections.observableArrayList();
    private FilteredList<Student>       filtered;
    private FilteredList<HistoryRecord> filteredHistory;
    private String currentRole      = "admin";
    private String loggedInUsername = "";

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupHistoryTable();
        setupSearch();
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));

        cbHistoryStatus.setItems(FXCollections.observableArrayList(
                "All", "Present", "Absent", "Leave"));
        cbHistoryStatus.getSelectionModel().selectFirst();
        dpHistoryFrom.setValue(LocalDate.now().minusDays(30));
        dpHistoryTo.setValue(LocalDate.now());
    }

    public void initData(String username) {
        loggedInUsername = username;
        currentRole = UserStore.getRole(username);

        String cap = username.substring(0, 1).toUpperCase() + username.substring(1);
        lblWelcome.setText("Welcome, " + cap + "!");

        boolean student = isStudentRole();
        adminToolbar.setVisible(!student);    adminToolbar.setManaged(!student);
        studentToolbar.setVisible(student);   studentToolbar.setManaged(student);
        addFormPanel.setVisible(!student);    addFormPanel.setManaged(!student);
        readOnlyPanel.setVisible(student);    readOnlyPanel.setManaged(student);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hColName.setVisible(true);
        hColSection.setVisible(true);

        lblRoleBadge.setText(student ? "STUDENT" : "ADMIN");
        lblRoleBadge.setStyle(student
                ? "-fx-background-color: #E6F1FB; -fx-text-fill: #0C447C;" +
                "-fx-font-size: 11px; -fx-padding: 3 10;" +
                "-fx-border-radius: 10; -fx-background-radius: 10;"
                : "-fx-background-color: #EAF3DE; -fx-text-fill: #27500A;" +
                "-fx-font-size: 11px; -fx-padding: 3 10;" +
                "-fx-border-radius: 10; -fx-background-radius: 10;");

        loadStudentsFromDB();
        loadAttendanceFromDB();
        loadHistoryFromDB();
        updateStats();
    }

    private boolean isStudentRole() {
        return "student".equalsIgnoreCase(currentRole);
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSection.setCellValueFactory(new PropertyValueFactory<>("section"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> statusCell());

        filtered = new FilteredList<>(allStudents, s -> true);
        tableView.setItems(filtered);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setPlaceholder(new Label("No students found."));
    }

    private void setupHistoryTable() {
        hColDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        hColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        hColSection.setCellValueFactory(new PropertyValueFactory<>("section"));
        hColTime.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        hColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        hColStatus.setCellFactory(col -> statusCell());

        filteredHistory = new FilteredList<>(allHistory, r -> true);
        historyTable.setItems(filteredHistory);
        historyTable.setPlaceholder(new Label("No history records found."));
    }

    private <T> TableCell<T, String> statusCell() {
        return new TableCell<>() {
            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setStyle(""); return; }
                setText(status);
                String base = "-fx-font-weight: bold; -fx-alignment: CENTER;";
                switch (status) {
                    case "Present" -> setStyle(base + "-fx-text-fill: #27500A;");
                    case "Absent"  -> setStyle(base + "-fx-text-fill: #A32D2D;");
                    case "Leave"   -> setStyle(base + "-fx-text-fill: #854F0B;");
                    default        -> setStyle(base + "-fx-text-fill: #888780;");
                }
            }
        };
    }

    private void setupSearch() {
        tfSearch.textProperty().addListener((obs, old, val) -> {
            String q = val.trim().toLowerCase();
            filtered.setPredicate(s ->
                    q.isEmpty()
                            || s.getStudentId().toLowerCase().contains(q)
                            || s.getName().toLowerCase().contains(q)
                            || s.getSection().toLowerCase().contains(q));
            updateStats();
        });
    }

    @FXML private void onStudentSearch() {
        String q = tfSearchStudent.getText().trim().toLowerCase();
        filtered.setPredicate(s ->
                q.isEmpty()
                        || s.getStudentId().toLowerCase().contains(q)
                        || s.getName().toLowerCase().contains(q)
                        || s.getSection().toLowerCase().contains(q));
        updateStats();
    }

    @FXML
    private void applyHistoryFilter() {
        LocalDate from   = dpHistoryFrom.getValue();
        LocalDate to     = dpHistoryTo.getValue();
        String    status = cbHistoryStatus.getValue();
        String    search = tfHistorySearch == null ? ""
                : tfHistorySearch.getText().trim().toLowerCase();

        filteredHistory.setPredicate(r -> {
            try {
                LocalDate rDate = LocalDate.parse(r.getRawDate());
                if (from != null && rDate.isBefore(from)) return false;
                if (to   != null && rDate.isAfter(to))   return false;
            } catch (Exception ignored) {}

            if (status != null && !status.equals("All")
                    && !r.getStatus().equalsIgnoreCase(status)) return false;

            if (!search.isEmpty()
                    && !r.getName().toLowerCase().contains(search)
                    && !r.getSection().toLowerCase().contains(search)) return false;

            return true;
        });
    }

    private void loadStudentsFromDB() {
        allStudents.clear();
        String sql = "SELECT student_id, full_name, section FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                allStudents.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("section")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Failed to load students from database.");
        }
    }

    private void loadAttendanceFromDB() {
        String today = LocalDate.now().toString();
        String sql   = "SELECT student_id, status, time_in FROM attendance WHERE date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String sid    = rs.getString("student_id");
                String status = rs.getString("status");
                String timeIn = rs.getString("time_in");
                allStudents.stream()
                        .filter(s -> s.getStudentId().equals(sid))
                        .findFirst()
                        .ifPresent(s -> {
                            s.setStatus(status);
                            s.setTimeIn(timeIn != null ? timeIn : "");
                        });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadHistoryFromDB() {
        allHistory.clear();
        String sql =
                "SELECT a.date, s.full_name, s.section, a.status, a.time_in " +
                        "FROM attendance a " +
                        "JOIN students s ON s.student_id = a.student_id " +
                        "ORDER BY a.date DESC, s.full_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String rawDate = rs.getString("date");
                String display;
                try { display = LocalDate.parse(rawDate).format(DATE_FMT); }
                catch (Exception e) { display = rawDate; }
                allHistory.add(new HistoryRecord(
                        rawDate, display,
                        rs.getString("full_name"),
                        rs.getString("section"),
                        rs.getString("status"),
                        rs.getString("time_in") != null ? rs.getString("time_in") : "—"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Failed to load history.");
        }
    }

    private void refreshHistory() {
        loadHistoryFromDB();
        applyHistoryFilter();
    }

    @FXML private void markPresent() { mark("Present"); }
    @FXML private void markAbsent()  { mark("Absent");  }
    @FXML private void markLeave()   { mark("Leave");   }

    private void mark(String status) {
        if (isStudentRole()) {
            setStatus("Student accounts are view only.");
            return;
        }
        var selected = tableView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) { setStatus("Select at least one student first."); return; }
        String time  = "Present".equals(status) ? LocalTime.now().format(TIME_FMT) : "—";
        String today = LocalDate.now().toString();
        for (Student s : selected) {
            s.setStatus(status);
            s.setTimeIn(time);
            saveAttendanceToDB(s.getStudentId(), status, time, today);
        }
        tableView.refresh();
        updateStats();
        refreshHistory();
        setStatus(selected.size() + " student(s) marked as " + status + ".");
    }

    private void saveAttendanceToDB(String studentId, String status,
                                    String timeIn, String date) {
        String sql =
                "INSERT INTO attendance (student_id, status, time_in, date) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE status=VALUES(status), time_in=VALUES(time_in)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId); ps.setString(2, status);
            ps.setString(3, timeIn);   ps.setString(4, date);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }


    @FXML private void addStudent() {
        if (isStudentRole()) {
            setStatus("Student accounts are view only.");
            return;
        }
        lblFormError.setText("");
        String id      = tfNewId.getText().trim();
        String name    = tfNewName.getText().trim();
        String section = tfNewSection.getText().trim();

        if (id.isBlank() || name.isBlank() || section.isBlank()) {
            lblFormError.setText("All fields required."); return;
        }

        String sql = "INSERT INTO students (student_id, full_name, section) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.setString(2, name); ps.setString(3, section);
            ps.executeUpdate();
            allStudents.add(new Student(id, name, section));
            tfNewId.clear(); tfNewName.clear(); tfNewSection.clear();
            updateStats();
            setStatus("Student \"" + name + "\" added to section " + section + ".");
        } catch (SQLIntegrityConstraintViolationException e) {
            lblFormError.setText("Student ID already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            lblFormError.setText("Database error.");
        }
    }

    @FXML private void removeSelected() {
        if (isStudentRole()) {
            setStatus("Student accounts are view only.");
            return;
        }
        var selected = tableView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) { setStatus("Select a student to remove."); return; }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove " + selected.size() + " student(s)?", ButtonType.YES, ButtonType.NO);
        dlg.setHeaderText(null);
        dlg.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                for (Student s : new ArrayList<>(selected)) {
                    deleteStudentFromDB(s.getStudentId());
                    allStudents.remove(s);
                }
                updateStats();
                refreshHistory();
                setStatus("Removed.");
            }
        });
    }

    private void deleteStudentFromDB(String studentId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM attendance WHERE student_id = ?")) {
                ps.setString(1, studentId); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM students WHERE student_id = ?")) {
                ps.setString(1, studentId); ps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void resetAll() {
        if (isStudentRole()) {
            setStatus("Student accounts are view only.");
            return;
        }
        allStudents.forEach(s -> { s.setStatus("—"); s.setTimeIn(""); });
        tableView.refresh();
        updateStats();
        setStatus("All statuses reset.");
    }

    private void updateStats() {
        long total   = filtered.size();
        long present = filtered.stream().filter(s -> "Present".equals(s.getStatus())).count();
        long absent  = filtered.stream().filter(s -> "Absent".equals(s.getStatus())).count();
        double rate  = total == 0 ? 0 : (present * 100.0 / total);
        lblTotal.setText(String.valueOf(total));
        lblPresent.setText(String.valueOf(present));
        lblAbsent.setText(String.valueOf(absent));
        lblRate.setText(String.format("%.0f%%", rate));
    }

    @FXML private void logout() {
        DatabaseConnection.close();
        try { Main.showLogin(); } catch (Exception e) { e.printStackTrace(); }
    }

    private void setStatus(String msg) { lblStatusBar.setText(msg); }

    
    public static class Student {
        private final SimpleStringProperty studentId;
        private final SimpleStringProperty name;
        private final SimpleStringProperty section;
        private final SimpleStringProperty status;
        private final SimpleStringProperty timeIn;

        public Student(String id, String name, String section) {
            this.studentId = new SimpleStringProperty(id);
            this.name      = new SimpleStringProperty(name);
            this.section   = new SimpleStringProperty(section);
            this.status    = new SimpleStringProperty("—");
            this.timeIn    = new SimpleStringProperty("");
        }

        public String getStudentId() { return studentId.get(); }
        public String getName()      { return name.get(); }
        public String getSection()   { return section.get(); }
        public String getStatus()    { return status.get(); }
        public String getTimeIn()    { return timeIn.get(); }
        public void setStatus(String v) { status.set(v); }
        public void setTimeIn(String v) { timeIn.set(v); }
    }

    
    public static class HistoryRecord {
        private final SimpleStringProperty rawDate;
        private final SimpleStringProperty date;
        private final SimpleStringProperty name;
        private final SimpleStringProperty section;
        private final SimpleStringProperty status;
        private final SimpleStringProperty timeIn;

        public HistoryRecord(String rawDate, String date,
                             String name, String section,
                             String status, String timeIn) {
            this.rawDate = new SimpleStringProperty(rawDate);
            this.date    = new SimpleStringProperty(date);
            this.name    = new SimpleStringProperty(name);
            this.section = new SimpleStringProperty(section);
            this.status  = new SimpleStringProperty(status);
            this.timeIn  = new SimpleStringProperty(timeIn);
        }

        public String getRawDate()  { return rawDate.get(); }
        public String getDate()     { return date.get(); }
        public String getName()     { return name.get(); }
        public String getSection()  { return section.get(); }
        public String getStatus()   { return status.get(); }
        public String getTimeIn()   { return timeIn.get(); }
    }
}
