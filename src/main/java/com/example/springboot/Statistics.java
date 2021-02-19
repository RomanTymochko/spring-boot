package com.example.springboot;

import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class Statistics {
    final static String DOWNLOAD_LINK = "https://docs.google.com/uc?export=download&id=188s0_sYkvJNgHYUXLsZpUb7CZgG_tGq6";
    final static String TRAIN_PATH = "D:/WSB/Network/Application/train";
    final static String EXTRACT_PATH = "D:/WSB/Network/Application/";

    private int epoch = 0;
    private double correct = 0;
    private double errors = 0;

    public Statistics() {
    }

    public Statistics(int epoch, double correct, double errors) {
        this.epoch = epoch;
        this.correct = correct;
        this.errors = errors;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public double getCorrect() {
        return correct;
    }

    public void setCorrect(double correct) {
        this.correct = correct;
    }

    public double getErrors() {
        return errors;
    }

    public void setErrors(double errors) {
        this.errors = errors;
    }

    public void downloadFile(String url, String name){
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(name)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("File downloaded successfully!");
        } catch (IOException e) {
            // handle exception
            System.out.println("Error while downloading!");
        }
    }

    public void unzip(File source, String out) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                File file = new File(out, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                        int bufferSize = Math.toIntExact(entry.getSize());
                        byte[] buffer = new byte[bufferSize > 0 ? bufferSize : 1];
                        int location;

                        while ((location = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, location);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
            System.out.println("Successfully unzipped!");
        }
    }

    public JSONObject handWrittenNumbersRecognition() throws IOException {
		JSONObject jsonObject = new JSONObject();
        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
        NeuralNetwork nn = new NeuralNetwork( 0.001, sigmoid, dsigmoid, 784, 512, 128, 32, 10);

//        Images for training
        int samples = 60000;
        BufferedImage[] images = new BufferedImage[samples];
        int[] digits = new int[samples];
//        File[] imagesFiles = new File("D:/train").listFiles();
        File[] imagesFiles = new File(TRAIN_PATH).listFiles();
        for (int i = 0; i < samples; i++) {
            images[i] = ImageIO.read(imagesFiles[i]);
            digits[i] = Integer.parseInt(imagesFiles[i].getName().charAt(10) + "");
        }

        double[][] inputs = new double[samples][784];
        for (int i = 0; i < samples; i++) {
            for (int x = 0; x < 28; x++) {
                for (int y = 0; y < 28; y++) {
                    inputs[i][x + y * 28] = (images[i].getRGB(x, y) & 0xff) / 255.0;
                }
            }
        }

//        Learning iterations
        int epochs = 10;
        for (int i = 1; i < epochs; i++) {
            int correct = 0;
            double correctSum = 0;
            double errorSum = 0;
            int batchSize = 100;

            for (int j = 0; j < batchSize; j++) {
                int imgIndex = (int)(Math.random() * samples);
                double[] targets = new double[10];
                int digit = digits[imgIndex];
                targets[digit] = 1;

                double[] outputs = nn.feedForward(inputs[imgIndex]);
                int maxDigit = 0;
                double maxDigitWeight = -1;
                for (int k = 0; k < 10; k++) {
                    if(outputs[k] > maxDigitWeight) {
                        maxDigitWeight = outputs[k];
                        maxDigit = k;
                    }
                }

                if(digit == maxDigit){
                    correct++;
                }

                for (int k = 0; k < 10; k++) {
                    errorSum += (targets[k] - outputs[k]) * (targets[k] - outputs[k]);
                }
                nn.backpropagation(targets);
            }
            System.out.println("Epoch " + i + "; correct(pc): " + correct +"; correctSum: "+correctSum+"; errorSum: " + errorSum);
            Statistics statistics = new Statistics(i, correct, errorSum);
			jsonObject.put("StatsObject"+i, statistics);
        }

        DrawingForm f = new DrawingForm(nn);
        new Thread(f).start();
        System.out.println(jsonObject.toString());
        return jsonObject;
    }
}
