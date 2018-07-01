package UI;

import Logical.Compress;
import Logical.CompressDir;
import Logical.Decompress;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;  import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class Main extends Application {

    private StackPane stackPane;
    private static int task = 0;
    private Label taskLabel = new Label();
    private Label pathLabel = new Label();
    private Label ratioLabel = new Label();
    private Label timeLabel = new Label();
    private String filePathStr = "Selected files or directory : ";
    private String comRatioStr = "Compression Ratio : ";
    private String timeStr = "Time-consuming : ";
    private String taskStr = "Number of tasks: ";
    private String timeConsuming = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void init(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(Main.class.getResource("Css/Main.css").toString());
        stackPane = new StackPane();
        BorderPane mainPane = getMainPane(primaryStage);
        primaryStage.getIcons().add(new Image(Main.class.getResource("Icon/Icon.png").toString()));
        ImageView bg_image = new ImageView(Main.class.getResource("Images/bg_light.jpg").toString());
        bg_image.setId("bgImage");
//        bg_image.setFitHeight(500);
        bg_image.setFitWidth(900);
        Scene scene = new Scene(stackPane, 800, 500);
        stackPane.getChildren().addAll(bg_image, mainPane);
        Path path = new Path();
        path.getElements().add(new MoveTo(420, 250));
        path.getElements().add(new LineTo(470, 250));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(8000));
        pathTransition.setPath(path);
        pathTransition.setNode(bg_image);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
        primaryStage.setTitle("Huffman");
        primaryStage.setScene(scene);
    }

    private BorderPane getMainPane(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();
        HBox topPane = getTopPane();
        VBox middlePane = getMiddlePane();
        HBox bottomPane = getBottomPane(stage);

        mainPane.setTop(topPane);
        mainPane.setCenter(middlePane);
        mainPane.setBottom(bottomPane);
        return mainPane;
    }

    private HBox getTopPane() {
        HBox topPane = new HBox();
        topPane.setMinWidth(600);
        topPane.setMinHeight(20);
        topPane.setAlignment(Pos.TOP_CENTER);
        topPane.setPadding(new Insets(80, 0, 10, 0));
        ImageView logo_img = new ImageView(new Image(Main.class.getResource("Images/hi.png").toString()));
        logo_img.setFitHeight(100);
        logo_img.setFitWidth(100);
        logo_img.setId("logoImg");
        Label app_name = new Label("     Huffman\n By Jon Zhang");
        app_name.setContentDisplay(ContentDisplay.RIGHT);
        app_name.setId("appName");
        topPane.getChildren().addAll(logo_img, app_name);
        return topPane;
    }

    private VBox getMiddlePane() {
        VBox middlePane = new VBox();
        middlePane.setMinWidth(800);
        middlePane.setPadding(new Insets(0, 0, 20, 80));
        middlePane.setAlignment(Pos.BOTTOM_LEFT);
        taskLabel.setText(taskStr + task);
        middlePane.getChildren().addAll(taskLabel, pathLabel, timeLabel, ratioLabel);
        return middlePane;
    }

    private HBox getBottomPane(Stage stage) throws Exception {
        HBox bottomPane = new HBox();
        bottomPane.setMaxSize(820, 80);
        bottomPane.setMinSize(820, 80);
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);
        bottomPane.setPadding(new Insets(5));
        bottomPane.setId("bottomPane");

        Label compress_label = new Label("Compress");
        Label comSin_label = new Label("Compress Single File");
        Label comDir_label = new Label("Compress Directory");
        Label decompress_label = new Label("Decompress");
        Label start_label = new Label("Start");
        Label exit_label = new Label("Exit");
        comSin_label.setId("singleLabel");

        Label[] labels = {compress_label, comSin_label, comDir_label, decompress_label};
        Label[] labels1 = {compress_label, comSin_label, comDir_label, decompress_label, start_label, exit_label};
        compress_label.setMinSize(180, 70);
        decompress_label.setMinSize(180, 70);
        start_label.setMinSize(180, 70);
        exit_label.setMinSize(180, 70);

        addClass("label", labels1);
        start_label.getStyleClass().add("labelDisabled");

        VBox branchPane = new VBox();
        branchPane.setAlignment(Pos.CENTER);
        branchPane.setId("branchPane");
        branchPane.setMaxSize(400, 180);
        branchPane.setMinSize(400, 180);
        Label titleLabel = new Label("Select the type of compression : ");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setId("branchLabel");
        titleLabel.setMaxSize(400, 30);
        titleLabel.setMinSize(400, 30);
        comSin_label.setMaxSize(400, 70);
        comSin_label.setMinSize(400, 70);
        comDir_label.setMaxSize(400, 70);
        comDir_label.setMinSize(400, 70);
        branchPane.getChildren().addAll(titleLabel, comSin_label, comDir_label);


        compress_label.setOnMouseClicked(event -> {
            if (stackPane.getChildren().contains(branchPane)) {
                stackPane.getChildren().remove(branchPane);
            } else
                stackPane.getChildren().add(branchPane);
        });
        comSin_label.setOnMouseClicked(event -> {
            stackPane.getChildren().remove(branchPane);
            if (compress_label.getStyleClass().contains("labelDisabled")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Cannot compressFile file now!");
                alert.showAndWait();
                return;
            }
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            start_label.getStyleClass().remove("labelDisabled");
            if (file != null) {
                timeLabel.setText(timeStr);
                ratioLabel.setText(comRatioStr);
                pathLabel.setText(filePathStr + file.getAbsolutePath());
                start_label.setOnMouseClicked(event1 -> {
                    long startTime = System.currentTimeMillis();
                    start_label.getStyleClass().add("labelDisabled");
                    timeLabel.setText(timeStr + " Calculating……");
                    String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf(".")) + ".hfm";
                    if (file.length() != 0) {
                        new Thread(() -> {
                            Platform.runLater(() -> taskLabel.setText(taskStr + (++task)));
                            try {
                                File dstFile = new File(path);
                                if (!dstFile.exists()) {
                                    dstFile.createNewFile();
                                }
                                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dstFile));
                                new Compress(file, outputStream).compressFile();
                                outputStream.close();

                                long endTime = System.currentTimeMillis();
                                long time = endTime - startTime;
                                timeConsuming = time + "ms";

                                long oldSize = file.length();
                                long newSize = new File(path).length();
                                System.out.println(newSize + ":" + oldSize);
                                String ratioStr = ((double) newSize / oldSize) * 100 + "%";

                                Platform.runLater(() -> updateState(--task, file.getAbsolutePath(), timeConsuming, comRatioStr + ratioStr));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();


                    } else {
                        //空文件
                        taskLabel.setText(taskStr + (++task));
                        File output = new File(path);
                        try {
                            if (!output.exists())
                                output.createNewFile();
                            BufferedOutputStream outputStream = null;
                            outputStream = new BufferedOutputStream(new FileOutputStream(output));
                            outputStream.write(0);
                            outputStream.write(0);
                            outputStream.close();
                            long endTime = System.currentTimeMillis();
                            timeConsuming = (endTime - startTime) + "ms";
                            Platform.runLater(() -> updateState(--task, filePathStr + file.getAbsolutePath(), timeConsuming, ""));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });

            }
        });
        comDir_label.setOnMouseClicked(event -> {
            stackPane.getChildren().remove(branchPane);
            if (comDir_label.getStyleClass().contains("labelDisabled")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot compressFile directory now!");
                alert.showAndWait();
                return;
            }
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(stage);
            start_label.getStyleClass().remove("labelDisabled");
            if (file != null) {
                timeLabel.setText(timeStr);
                ratioLabel.setText(comRatioStr);
                pathLabel.setText(filePathStr + file.getAbsolutePath());
                start_label.setOnMouseClicked(event1 -> {
                    long startTime = System.currentTimeMillis();
                    start_label.getStyleClass().add("labelDisabled");
                    timeLabel.setText(timeStr + " Calculating……");
//                    addClass("labelDisabled", labels);
                    String path = file.getAbsolutePath() + ".hfm";
                    new Thread(() -> {
                        Platform.runLater(() -> taskLabel.setText(taskStr + (++task)));
                        try {
                            File dstFile = new File(path);
                            if (!dstFile.exists())
                                dstFile.createNewFile();
                            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dstFile));
                            long oldSize = new CompressDir(file, outputStream).compressDir();
                            outputStream.close();

                            long endTime = System.currentTimeMillis();
                            long time = endTime - startTime;
                            timeConsuming = time + "ms";

                            long newSize = new File(path).length();
                            System.out.println(newSize + ":" + oldSize);
                            String ratioStr = ((double) newSize / oldSize) * 100 + "%";

                            Platform.runLater(() -> updateState(--task, file.getAbsolutePath(), timeConsuming, comRatioStr + ratioStr));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
            }
        });
        decompress_label.setOnMouseClicked(event -> {
            if (decompress_label.getStyleClass().contains("labelDisabled")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Cannot decompress file now!");
                alert.showAndWait();
                return;
            }
            if (stackPane.getChildren().contains(branchPane))
                stackPane.getChildren().remove(branchPane);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Resource File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Huffman Files(*.hfm)", "*.hfm"));
            File file = fileChooser.showOpenDialog(stage);

            start_label.getStyleClass().remove("labelDisabled");
            if (file != null) {
                timeLabel.setText(timeStr);
                ratioLabel.setText("");
                pathLabel.setText(filePathStr + file.getAbsolutePath());
                start_label.setOnMouseClicked(event1 -> {
                    long startTime = System.currentTimeMillis();
                    start_label.getStyleClass().add("labelDisabled");
                    timeLabel.setText(timeStr + " Calculating……");
                    new Thread(() -> {
                        Platform.runLater(() -> taskLabel.setText(taskStr + (++task)));
                        try {
                            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                            new Decompress(file, inputStream).decompressFile();
                            inputStream.close();

                            long endTime = System.currentTimeMillis();
                            long time = endTime - startTime;
                            timeConsuming = time + "ms";

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> updateState(--task, file.getAbsolutePath(), timeConsuming, ""));
                    }).start();
                });

            }
        });
        exit_label.setOnMouseClicked(event -> {
            stage.close();
            System.exit(0);
        });

        bottomPane.getChildren().addAll(compress_label, decompress_label, start_label, exit_label);
        return bottomPane;
    }

    private void updateState(int task, String path, String timeConsuming, String ratio) {
        taskLabel.setText(taskStr + task);
        pathLabel.setText(filePathStr + path);
        timeLabel.setText(timeStr + timeConsuming);
        ratioLabel.setText(ratio);
    }

    private void addClass(String className, Label... labels) {
        for (int i = 0; i < labels.length; i++) {
            labels[i].getStyleClass().add(className);
        }
    }

    private void removeClass(String className, Label... labels) {
        for (int i = 0; i < labels.length; i++) {
            labels[i].getStyleClass().remove(className);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
