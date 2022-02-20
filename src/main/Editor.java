package main;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;

public class Editor {
    private final double THRESHOLD  = 0.4;
    private double[][] matrix = null;

    public void changeFilter(double[][] matrix) {
        this.matrix = matrix;
    }

    public Image transform_bw(Image image) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        for (int x = 0; x < writableImage.getWidth(); ++x)
            for (int y = 0; y < writableImage.getHeight(); ++y) {
                Color color = pixelReader.getColor(x, y);
                double value = (color.getRed() + color.getBlue() + color.getGreen()) / 3;
                pixelWriter.setColor(x, y, new Color(value, value, value, 1));
            }
        return writableImage;
    }

    public Image clustering(Image image) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();
        for(int i = 0; i < image.getWidth(); ++i)
            visited.add(new ArrayList<>(Collections.nCopies((int) image.getHeight(), false)));
        for(int x = 0; x < image.getWidth(); ++x)
            for(int y = 0; y < image.getHeight(); ++y) {
                if(!visited.get(x).get(y)) {
                    Color color = pixelReader.getColor(x, y);
                    double[] sum = {color.getRed(), color.getGreen(), color.getBlue()};
                    int no = 1;
                    Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
                    ArrayList<Pair<Integer, Integer>> pixels = new ArrayList<>();
                    pixels.add(new Pair<>(x, y));
                    queue.add(new Pair<>(x, y));
                    int x_acc, y_acc;
                    while(!queue.isEmpty()) {
                        Pair<Integer, Integer> pos = queue.poll();
                        x_acc = pos.getKey(); y_acc = pos.getValue();
                        for(int i = 0; i < 4; ++i) {
                            if(x_acc + dx[i] < 0 || x_acc + dx[i] >= image.getWidth() ||
                                y_acc + dy[i] < 0 || y_acc + dy[i] >= image.getHeight())
                                continue;
                            if (!visited.get(x_acc + dx[i]).get(y_acc + dy[i]) &&
                                    computeDif(color, pixelReader.getColor(x_acc + dx[i], y_acc + dy[i]))) {
                                queue.add(new Pair<>(x_acc + dx[i], y_acc + dy[i]));
                                no++;
                                pixels.add(new Pair<>(x_acc + dx[i], y_acc + dy[i]));
                                Color newColor = pixelReader.getColor(x_acc + dx[i], y_acc + dy[i]);
                                sum[0] += newColor.getRed();
                                sum[1] += newColor.getGreen();
                                sum[2] += newColor.getBlue();
                                visited.get(x_acc + dx[i]).set(y_acc + dy[i], true);
                            }
                        }
                    }
                    sum[0] /= no; sum[1] /= no; sum[2] /= no;
                    for (Pair<Integer, Integer> pixel : pixels)
                        pixelWriter.setColor(pixel.getKey(), pixel.getValue(),
                                new Color(sum[0], sum[1], sum[2], 1));
                }
            }
        return writableImage;
    }

    private boolean computeDif(Color color, Color color1) {
        return Math.abs(color.getRed() - color1.getRed()) +
                Math.abs(color.getGreen() - color1.getGreen()) +
                Math.abs(color.getBlue() - color1.getBlue()) <= THRESHOLD;
    }

    public Image applyEffect(Image image) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        int[] dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        for (int x = 0; x < image.getWidth(); ++x)
            for (int y = 0; y < image.getHeight(); ++y) {
                double red = 0, blue = 0, green = 0;
                for (int i = 0; i < 9; ++i) {
                    if(x + dx[i] < 0 || x + dx[i] >= image.getWidth() ||
                            y + dy[i] < 0 || y + dy[i] >= image.getHeight())
                        continue;
                    Color color = pixelReader.getColor(x + dx[i], y + dy[i]);
                    red += color.getRed() * matrix[1 + dx[i]][1 + dy[i]];
                    blue += color.getBlue() * matrix[1 + dx[i]][1 + dy[i]];
                    green += color.getGreen() * matrix[1 + dx[i]][1 + dy[i]];
                }
                Color resultColor = new Color(adjustValue(red), adjustValue(green),
                        adjustValue(blue), 1);
                pixelWriter.setColor(x, y, resultColor);
            }
        return writableImage;
    }

    private double adjustValue(double value) {
        if(value > 1)
            return 1;
        if(value < 0)
            return 0;
        return value;
    }

    public Image sepia(Image image) {
        WritableImage writableImage = new WritableImage((int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();

        for (int x = 0; x < image.getWidth(); ++x)
            for (int y = 0; y < image.getHeight(); ++y) {
                Color color = pixelReader.getColor(x, y);
                double red = adjustValue(color.getRed() * 0.39 + color.getGreen() * 0.77 + color.getBlue() * 0.19);
                double green = adjustValue(color.getRed() * 0.35 + color.getGreen() * 0.68 + color.getBlue() * 0.17);
                double blue = adjustValue(color.getRed() * 0.27 + color.getGreen() * 0.54 + color.getBlue() * 0.13);
                pixelWriter.setColor(x, y,
                        new Color(red, green, blue, 1));
            }

        return writableImage;
    }
}
