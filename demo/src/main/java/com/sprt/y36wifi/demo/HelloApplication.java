package com.sprt.y36wifi.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.Properties;

public class HelloApplication extends Application {

    private static final String CONFIG_FILE = "config.properties";
    private TextField inputFileField;
    private TextField outputFileField;
    private Properties properties;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("删除时间格式");

        // 加载配置文件
        properties = loadProperties();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Input file selection
        Label inputLabel = new Label("选择执行的文件:");
        grid.add(inputLabel, 0, 0);

        inputFileField = new TextField();
        inputFileField.setEditable(false);
        grid.add(inputFileField, 1, 0);

        Button inputBrowseButton = new Button("选择文件夹");
        inputBrowseButton.setOnAction(e -> browseFile());
        grid.add(inputBrowseButton, 2, 0);

        // Output file name input
        Label outputLabel = new Label("输出文件名:");
        grid.add(outputLabel, 0, 1);

        outputFileField = new TextField();
        grid.add(outputFileField, 1, 1);

        // Execute Y36 button
        Button executeY36Button = new Button("执行Y36");
        executeY36Button.setOnAction(e -> executeTimestampRemovalY36());
        grid.add(executeY36Button, 1, 2);

        // Execute Y33 button
        Button executeY33Button = new Button("执行Y33");
        executeY33Button.setOnAction(e -> executeTimestampRemovalY33());
        grid.add(executeY33Button, 2, 2);

        // 新增：过滤 Y33Server 按钮
        Button filterY33ServerButton = new Button("过滤Y33Server");
        filterY33ServerButton.setOnAction(e -> filterY33Server());
        grid.add(filterY33ServerButton, 1, 3);

        Scene scene = new Scene(grid, 600, 250);
        primaryStage.setScene(scene);

        // Set the close request event handler
        primaryStage.setOnCloseRequest(e -> {
            saveProperties();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // 从配置文件加载路径和文件名
        inputFileField.setText(properties.getProperty("inputFilePath", ""));
        outputFileField.setText(properties.getProperty("outputFileName", ""));
    }

    private void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择执行的文件");

        // 如果输入框中有路径，则设置文件选择器的初始目录
        String inputFilePath = inputFileField.getText();
        if (!inputFilePath.isEmpty()) {
            File initialDirectory = new File(inputFilePath).getParentFile();
            if (initialDirectory.exists()) {
                fileChooser.setInitialDirectory(initialDirectory);
            }
        }

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            inputFileField.setText(file.getAbsolutePath());
        }
    }

    private void executeTimestampRemovalY36() {
        String inputFilePath = inputFileField.getText();
        String outputFileName = outputFileField.getText();

        if (inputFilePath.isEmpty() || outputFileName.isEmpty()) {
            System.out.println("请先选择执行的文件并输入输出文件名。");
            return;
        }

        // 获取输入文件的目录
        File inputFile = new File(inputFilePath);
        String outputFilePath = inputFile.getParent() + File.separator + outputFileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String updatedContent = removeTimestampsY36(content.toString());
            writer.write(updatedContent);

            System.out.println("Y36 时间戳删除成功。");
            System.out.println("输出文件位置: " + outputFilePath);

            // 打开包含输出文件的目录
            openDirectory(new File(outputFilePath).getParent());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件处理时发生错误。");
        }
    }

    private void executeTimestampRemovalY33() {
        String inputFilePath = inputFileField.getText();
        String outputFileName = outputFileField.getText();

        if (inputFilePath.isEmpty() || outputFileName.isEmpty()) {
            System.out.println("请先选择执行的文件并输入输出文件名。");
            return;
        }

        // 获取输入文件的目录
        File inputFile = new File(inputFilePath);
        String outputFilePath = inputFile.getParent() + File.separator + outputFileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String updatedContent = removeTimestampsY33(content.toString());
            writer.write(updatedContent);

            System.out.println("Y33 时间戳删除成功。");
            System.out.println("输出文件位置: " + outputFilePath);

            // 打开包含输出文件的目录
            openDirectory(new File(outputFilePath).getParent());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件处理时发生错误。");
        }
    }

    // 新增：过滤 Y33Server 标签的数据
    private void filterY33Server() {
        String inputFilePath = inputFileField.getText();
        String outputFileName = outputFileField.getText();

        if (inputFilePath.isEmpty() || outputFileName.isEmpty()) {
            System.out.println("请先选择执行的文件并输入输出文件名。");
            return;
        }

        // 获取输入文件的目录
        File inputFile = new File(inputFilePath);
        String outputFilePath = inputFile.getParent() + File.separator + outputFileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Y33Server")) {
                    writer.write(line + "\n");
                }
            }

            System.out.println("Y33Server 过滤成功。");
            System.out.println("输出文件位置: " + outputFilePath);

            // 打开包含输出文件的目录
            openDirectory(new File(outputFilePath).getParent());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件处理时发生错误。");
        }
    }

    private String removeTimestampsY36(String input) {
        // 删除各种时间戳和日志格式
        String timestampPattern1 = "\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\]";
        String timestampPattern2 = "\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\]";
        String timestampPattern3 = "(?s)\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} [A-Z]/\\w+\\s+\\(\\s*\\d+\\):.*?\\[\\d+\\]";
        String timestampPattern4 = "(?s)\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} [A-Z]/\\w+\\s+\\(\\s*\\d+\\):.*?Origi:com\\.printer\\.sdk\\.Core\\[\\d+\\].*";
        String timestampPattern5 = "(?s)\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} [A-Z]/\\w+\\s+\\(\\s*\\d+\\):.*? - .*";
        String timestampPattern6 = "(?s)\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} D/\\w+\\s+\\(\\s*\\d+\\):.*? - .*";
        String timestampPattern7 = "(?s)\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} I/\\w+\\s+\\(\\s*\\d+\\):.*? - .*";

        return input
                .replaceAll(timestampPattern1, "")
                .replaceAll(timestampPattern2, "")
                .replaceAll(timestampPattern3, "")
                .replaceAll(timestampPattern4, "")
                .replaceAll(timestampPattern5, "")
                .replaceAll(timestampPattern6, "")
                .replaceAll(timestampPattern7, "");
    }

    private String removeTimestampsY33(String input) {
        // 删除各种时间戳和日志格式
        String timestampPattern = "\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*?\\[\\d+\\]";
        return input.replaceAll(timestampPattern, "");
    }

    private void openDirectory(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("无法加载配置文件，使用默认设置。");
        }
        return props;
    }

    private void saveProperties() {
        properties.setProperty("inputFilePath", inputFileField.getText());
        properties.setProperty("outputFileName", outputFileField.getText());
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            System.out.println("无法保存配置文件。");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
