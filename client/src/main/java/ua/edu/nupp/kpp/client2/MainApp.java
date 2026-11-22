package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpSportsClient api = new HttpSportsClient();

    private final TableView<Facility> facilities = new TableView<>();
    private final TableView<AppUser> users = new TableView<>();
    private final TableView<TimeSlot> timeSlots = new TableView<>();
    private final TableView<Booking> bookings = new TableView<>();
    private final Label analyticsLabel = new Label("Аналітика: —");

    private ImportTimeSlotsTask currentImportTask;

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Об’єкти", facilityPane()),
                new Tab("Користувачі", userPane()),
                new Tab("Слоти", timeSlotPane()),
                new Tab("Бронювання", bookingPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        BorderPane root = new BorderPane(tabs);
        root.setBottom(statusBar());
        BorderPane.setMargin(root.getBottom(), new Insets(6, 10, 6, 10));

        stage.setScene(new Scene(root, 1120, 720));
        stage.setTitle("Бронювання спортивних об’єктів — варіант 14");
        stage.show();
        refreshAll();
    }

    private HBox statusBar() {
        HBox bar = new HBox(16, analyticsLabel);
        bar.setPadding(new Insets(4, 0, 0, 0));
        return bar;
    }

    private void updateAnalytics() {
        try {
            List<Booking> list = api.getBookings();
            if (list.isEmpty()) {
                analyticsLabel.setText("Аналітика: немає бронювань");
                return;
            }
            double avg = list.stream().mapToDouble(Booking::getIntegralIndex).average().orElse(0);
            long created = list.stream().filter(b -> b.getState() == BookingState.CREATED).count();
            long confirmed = list.stream().filter(b -> b.getState() == BookingState.CONFIRMED).count();
            long active = list.stream().filter(b -> b.getState() == BookingState.ACTIVE).count();
            long completed = list.stream().filter(b -> b.getState() == BookingState.COMPLETED).count();
            analyticsLabel.setText(String.format(
                    "Аналітика: всього %d · середній I = %.1f · CREATED=%d CONFIRMED=%d ACTIVE=%d COMPLETED=%d",
                    list.size(), avg, created, confirmed, active, completed));
        } catch (Exception e) {
            analyticsLabel.setText("Аналітика: недоступна");
        }
    }

    // ========== Facility ==========
    private Pane facilityPane() {
        facilities.getColumns().setAll(
                col("ID", "id"), col("Код", "code"),
                col("Назва", "name"), col("Адреса", "address")
        );
        TextField code = new TextField(), name = new TextField(), address = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addFacility(new Facility(null, code.getText(), name.getText(), address.getText()));
            refreshFacilities();
        }));
        update.setOnAction(e -> safe(() -> {
            Facility f = selected(facilities);
            f.setCode(code.getText());
            f.setName(name.getText());
            f.setAddress(address.getText());
            api.updateFacility(f);
            refreshFacilities();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteFacility(selected(facilities).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshFacilities());

        facilities.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                code.setText(s.getCode());
                name.setText(s.getName());
                address.setText(s.getAddress());
            }
        });

        return layout(facilities, new VBox(8,
                new Label("Код"), code,
                new Label("Назва"), name,
                new Label("Адреса"), address,
                new HBox(8, add, update, delete, refresh)
        ));
    }

    // ========== User ==========
    private Pane userPane() {
        users.getColumns().setAll(
                col("ID", "id"), col("ПІБ", "fullName"),
                col("Email", "email"), col("Телефон", "phone")
        );
        TextField fullName = new TextField(), email = new TextField(), phone = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addUser(new AppUser(null, fullName.getText(), email.getText(), phone.getText()));
            refreshUsers();
        }));
        update.setOnAction(e -> safe(() -> {
            AppUser u = selected(users);
            u.setFullName(fullName.getText());
            u.setEmail(email.getText());
            u.setPhone(phone.getText());
            api.updateUser(u);
            refreshUsers();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteUser(selected(users).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshUsers());

        users.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                fullName.setText(s.getFullName());
                email.setText(s.getEmail());
                phone.setText(s.getPhone());
            }
        });

        return layout(users, new VBox(8,
                new Label("ПІБ"), fullName,
                new Label("Email"), email,
                new Label("Телефон"), phone,
                new HBox(8, add, update, delete, refresh)
        ));
    }

    // ========== TimeSlot ==========
    private Pane timeSlotPane() {
        timeSlots.getColumns().setAll(
                col("ID", "id"), col("Facility", "facilityId"),
                col("Дата", "slotDate"), col("Початок", "startTime"),
                col("Кінець", "endTime"), col("Ціна", "price")
        );
        TextField facilityId = new TextField(), date = new TextField(),
                start = new TextField(), end = new TextField(), price = new TextField();
        ProgressBar progress = new ProgressBar(0);
        progress.setPrefWidth(220);
        Label status = new Label("CSV не запущено");
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити"),
                importCsv = new Button("Імпортувати CSV"),
                cancelImport = new Button("Скасувати");

        add.setOnAction(e -> safe(() -> {
            api.addTimeSlot(readTimeSlot(null, facilityId, date, start, end, price));
            refreshTimeSlots();
        }));
        update.setOnAction(e -> safe(() -> {
            TimeSlot t = selected(timeSlots);
            api.updateTimeSlot(readTimeSlot(t.getId(), facilityId, date, start, end, price));
            refreshTimeSlots();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteTimeSlot(selected(timeSlots).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshTimeSlots());

        timeSlots.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                facilityId.setText("" + s.getFacilityId());
                date.setText(s.getSlotDate());
                start.setText(s.getStartTime());
                end.setText(s.getEndTime());
                price.setText("" + s.getPrice());
            }
        });

        importCsv.setOnAction(e -> {
            if (currentImportTask != null && currentImportTask.isRunning()) {
                showError(new IllegalStateException("Імпорт уже виконується"));
                return;
            }
            ImportTimeSlotsTask task = new ImportTimeSlotsTask(
                    Path.of("sample-data", "time_slots.csv"), api);
            currentImportTask = task;
            progress.progressProperty().bind(task.progressProperty());
            status.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                status.setText("Готово. Імпортовано: " + task.getValue());
                currentImportTask = null;
                refreshTimeSlots();
            });
            task.setOnCancelled(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                status.setText("Скасовано");
                currentImportTask = null;
                refreshTimeSlots();
            });
            task.setOnFailed(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                currentImportTask = null;
                showError(task.getException());
            });
            new Thread(task, "csv-import").start();
        });

        cancelImport.setOnAction(e -> {
            if (currentImportTask != null && currentImportTask.isRunning()) {
                currentImportTask.cancel();
            }
        });

        VBox form = new VBox(6,
                new Label("ID об’єкта"), facilityId,
                new Label("Дата (YYYY-MM-DD)"), date,
                new Label("Початок (HH:MM)"), start,
                new Label("Кінець (HH:MM)"), end,
                new Label("Ціна"), price,
                new HBox(8, add, update, delete, refresh),
                new HBox(8, importCsv, cancelImport),
                progress, status
        );
        return layout(timeSlots, form);
    }

    // ========== Booking ==========
    private Pane bookingPane() {
        bookings.getColumns().setAll(
                col("ID", "id"), col("User", "userId"), col("Slot", "timeSlotId"),
                col("Стан", "state"), col("F1", "f1"), col("F2", "f2"),
                col("F3", "f3"), col("I", "integralIndex")
        );
        TextField userId = new TextField(), slotId = new TextField(),
                f1 = new TextField(), f2 = new TextField(), f3 = new TextField();
        ComboBox<BookingState> state = new ComboBox<>(FXCollections.observableArrayList(BookingState.values()));
        Button add = new Button("Створити"), recalc = new Button("Перерахувати"),
                change = new Button("Змінити стан"), delete = new Button("Видалити"),
                refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.createBooking(
                    Long.parseLong(userId.getText()),
                    Long.parseLong(slotId.getText()),
                    Double.parseDouble(f1.getText()),
                    Double.parseDouble(f2.getText()),
                    Double.parseDouble(f3.getText())
            );
            refreshBookings();
        }));
        recalc.setOnAction(e -> safe(() -> {
            Booking b = selected(bookings);
            api.recalculateBooking(b.getId(),
                    Double.parseDouble(f1.getText()),
                    Double.parseDouble(f2.getText()),
                    Double.parseDouble(f3.getText()));
            refreshBookings();
        }));
        change.setOnAction(e -> safe(() -> {
            api.changeBookingState(selected(bookings).getId(), state.getValue());
            refreshBookings();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteBooking(selected(bookings).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshBookings());

        bookings.getSelectionModel().selectedItemProperty().addListener((o, a, v) -> {
            if (v != null) {
                userId.setText("" + v.getUserId());
                slotId.setText("" + v.getTimeSlotId());
                f1.setText("" + v.getF1());
                f2.setText("" + v.getF2());
                f3.setText("" + v.getF3());
                state.setValue(v.getState());
            }
        });

        Label hint = new Label("F1 — учасники (0–100), F2 — тривалість (0–100), F3 — обладнання (0–100)\n"
                + "Переходи: CREATED → CONFIRMED → ACTIVE → COMPLETED");
        hint.setWrapText(true);
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        return layout(bookings, new VBox(8,
                new Label("ID користувача"), userId,
                new Label("ID слота"), slotId,
                new Label("F1"), f1,
                new Label("F2"), f2,
                new Label("F3"), f3,
                new Label("Новий стан"), state,
                new HBox(8, add, recalc, change, delete, refresh),
                hint
        ));
    }

    // ========== helpers ==========
    private TimeSlot readTimeSlot(Long id, TextField facilityId, TextField date,
                                  TextField start, TextField end, TextField price) {
        return new TimeSlot(id,
                Long.parseLong(facilityId.getText()),
                date.getText(),
                start.getText(),
                end.getText(),
                Double.parseDouble(price.getText()));
    }

    private void refreshAll() {
        refreshFacilities();
        refreshUsers();
        refreshTimeSlots();
        refreshBookings();
    }

    private void refreshFacilities() {
        safe(() -> facilities.setItems(FXCollections.observableArrayList(api.getFacilities())));
    }

    private void refreshUsers() {
        safe(() -> users.setItems(FXCollections.observableArrayList(api.getUsers())));
    }

    private void refreshTimeSlots() {
        safe(() -> timeSlots.setItems(FXCollections.observableArrayList(api.getTimeSlots())));
    }

    private void refreshBookings() {
        safe(() -> {
            bookings.setItems(FXCollections.observableArrayList(api.getBookings()));
            updateAnalytics();
        });
    }

    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();
        if (v == null) throw new IllegalArgumentException("Виберіть запис");
        return v;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        String msg = e == null ? "Помилка" : e.getMessage();
        if (msg != null && msg.contains("HTTP")) {
            // витягуємо повідомлення сервера, якщо є
        }
        Alert d = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        d.setHeaderText("Помилка");
        d.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.62);
        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property));
        return c;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
