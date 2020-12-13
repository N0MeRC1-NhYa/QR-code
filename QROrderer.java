import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class QROrderer {

    private static final int SIZE = 64;
    private int[][] resultImg;
    private List<QRPart> parts;
    private static final String DIR = "C:\\Users\\Tim\\Desktop\\QR-code\\Best images\\";

    public QROrderer() throws IOException {
        parts = new ArrayList<>();
        resultImg = new int[SIZE * 4][SIZE * 4];
        getImages();
        for (int i = 0; i < parts.size(); i++) {
            setCornerPos(parts.get(i));
        }
        for (int i = 0; i < parts.size(); i++) {
            System.out.println();
            setBorderPos(parts.get(i));
        }
        for (int i = 0; i < parts.size(); i++) {
            setCenterPos(parts.get(i));
        }
    }

    public void show() throws IOException {
       /* for (int i = 0; i < resultImg.length; i++) {
            for (int j = 0; j < resultImg[i].length; j++) {
                System.out.print(resultImg[i][j] + " ");
            }
            System.out.println();
        }*/
        BufferedImage img = new BufferedImage(SIZE * 4, SIZE * 4, 3);

        for (int x = 0; x < SIZE * 4; x++) {
            for (int y = 0; y < SIZE * 4; y++) {
                int rgb = resultImg[x][y] << 16 | resultImg[x][y] << 8 | resultImg[x][y];
                img.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(img, "png", new File(DIR + "CurrResult.png"));
    }

    private void getImages() throws IOException {
        for (int img = 1; img < 17; img++) {
            File file = new File(DIR + img + ".png");
            BufferedImage image = ImageIO.read(file);
            int[][] color = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    color[j][i] = image.getRGB(i, j);
                }
            }
           /* for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    System.out.print(color[i][j] + " ");
                }
                System.out.println();
            }*/
            System.out.println(img);

            parts.add(findPlace(color));
        }
    }

    private QRPart findPlace(int[][] color) {
        int leftCheck = 0;
        int rightCheck = 0;
        int topCheck = 0;
        int bottomCheck = 0;
        QRPart qr = new QRPart(color);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < 17; j++) {
                if (color[i][j] == 0 || color[i][j] == -1) {
                    leftCheck++;
                }
                if (color[j][i] == 0 || color[j][i] == -1) {
                    topCheck++;
                }
            }
            for (int j = SIZE - 17; j < SIZE; j++) {
                if (color[i][j] == 0 || color[i][j] == -1) {
                    rightCheck++;
                }
                if (color[j][i] == 0 || color[j][i] == -1) {
                    bottomCheck++;
                }
            }
        }
        if (leftCheck == 1088) {
            qr.setLeft();
        }

        if (rightCheck == 1088) {
            qr.setRight();
        }

        if (topCheck == 1088) {
            qr.setTop();
        }

        if (bottomCheck == 1088) {
            qr.setBottom();
        }

        System.out.println(leftCheck + " " + rightCheck + " " + topCheck + " " + bottomCheck);

        return qr;
    }

    private void setCornerPos(QRPart qr) {
        if (qr.LeftBottomCorner()) {
            for (int i = 3 * SIZE; i < 4 * SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    resultImg[i][j] = qr.getColor(i - 3 * SIZE, j);
                }

            }
        }
        if (qr.RightBottomCorner()) {
            for (int i = 3 * SIZE; i < 4 * SIZE; i++) {
                for (int j = 3 * SIZE; j < 4 * SIZE; j++) {
                    resultImg[i][j] = qr.getColor(i - 3 * SIZE, j - 3 * SIZE);
                }

            }

        }
        if (qr.LeftTopCorner()) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    resultImg[i][j] = qr.getColor(i, j);
                }

            }

        }
        if (qr.RightTopCorner()) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 3 * SIZE; j < 4 * SIZE; j++) {
                    resultImg[i][j] = qr.getColor(i, j - 3 * SIZE);
                }

            }
        }

    }

    private void setBorderPos(QRPart qr) {
        int startPoint;
        int Sum = 0;
        if (qr.Right()) {
            for (int i = 0; i < SIZE; i++) {
                if (qr.getColor(0, i) == 0 || qr.getColor(0, i) == -1) {
                    Sum++;
                }
            }
            if (Sum == SIZE) {
                startPoint = SIZE;
            } else {
                startPoint = 2 * SIZE;
            }
            for (int i = startPoint; i < startPoint + SIZE; i++) {
                for (int j = 3 * SIZE; j < 4 * SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - startPoint, j - 3 * SIZE);
                    if (qr.getColor(i - startPoint, j - 3 * SIZE) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }
        if (qr.Left()) {
            for (int i = 0; i < SIZE; i++) {
                if (qr.getColor(0, i) == 0 || qr.getColor(0, i) == -1) {
                    Sum++;
                }
            }
            if (Sum == SIZE) {
                startPoint = SIZE;
            } else {
                startPoint = 2 * SIZE;
            }
            for (int i = startPoint; i < startPoint + SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - startPoint, j);
                    if (qr.getColor(i - startPoint, j) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }
        if (qr.Top()) {

            for (int i = 0; i < SIZE; i++) {
                if (qr.getColor(i, 0) == 0 || qr.getColor(i, 0) == -1) {
                    Sum++;
                }
            }
            if (Sum == SIZE) {
                startPoint = SIZE;
            } else {
                startPoint = 2 * SIZE;
            }
            for (int i = 0; i < SIZE; i++) {
                for (int j = startPoint; j < startPoint + SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i, j - startPoint);
                    if (qr.getColor(i, j - startPoint) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }
        if (qr.Bottom()) {
            for (int i = 0; i < SIZE; i++) {
                if (qr.getColor(i, 0) == 0 || qr.getColor(i, 0) == -1) {
                    Sum++;
                }
            }
            if (Sum == SIZE) {
                startPoint = SIZE;
            } else {
                startPoint = 2 * SIZE;
            }
            for (int i = 3 * SIZE; i < 4 * SIZE; i++) {
                for (int j = startPoint; j < startPoint + SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - 3 * SIZE, j - startPoint);
                    if (qr.getColor(i - 3 * SIZE, j - startPoint) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }
    }

    private void setCenterPos(QRPart qr) {
        if (qr.center()) {
            int[] left = new int[SIZE];
            int[] right = new int[SIZE];
            int[] top = new int[SIZE];
            int[] bottom = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                right[i] = qr.getColor(i, SIZE - 1);
                if (qr.getColor(i, SIZE - 1) == 0) {
                    right[i] = -1;
                }
                top[i] = qr.getColor(0, i);
                if (qr.getColor(0, i) == 0) {
                    top[i] = -1;
                }
                left[i] = qr.getColor(i, 0); //bottom
                if (qr.getColor(i, 0) == 0) {
                    left[i] = -1;
                }
                bottom[i] = qr.getColor(SIZE - 1, i);
                if (qr.getColor( SIZE - 1, i) == 0) {
                    bottom[i] = -1;
                }
                /*System.out.println("left - " + left[i] + " top - " + top[i] + " right - " + right[i] + " bottom - " + bottom[i] );*/
            }
            checkLeftTopCorner(qr, left, top);
            checkRightTopCorner(qr, right, top);
            checkLeftBottomCorner(qr, left, bottom);
            checkRightBottomCorner(qr, right, bottom);

        }
    }

    private void checkLeftTopCorner(QRPart qr, int[] left, int[] top) {
        int CheckSum = 0;
        for (int i = SIZE; i < 2 * SIZE; i++) {
            /*System.out.println(resultImg[SIZE - 1][i] + " - " + left[i - SIZE]);*/
            if (resultImg[SIZE - 1][i] == left[i - SIZE]) {
                CheckSum++;
            }

        }
        System.out.println("\n");
        for (int i = SIZE; i < 2 * SIZE; i++) {
            /*System.out.println(resultImg[i][SIZE - 1] + " - " + top[i - SIZE]);*/
            if (resultImg[i][SIZE - 1] == top[i - SIZE]) {
                CheckSum++;
            }
        }
        //System.out.println(CheckSum);
        if (CheckSum == 2 * SIZE) {
            for (int i = SIZE; i < 2 * SIZE; i++) {
                for (int j = SIZE; j < 2 * SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - SIZE, j - SIZE);
                    if (qr.getColor(i - SIZE, j - SIZE) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }
    }

    private void checkRightTopCorner(QRPart qr, int[] right, int[] top) {
        int CheckSum = 0;
        for (int i = 2 * SIZE; i < 3 * SIZE; i++) {
            //System.out.println(resultImg[i][SIZE - 1] + " - " + top[i - 2 * SIZE]);
            if (resultImg[i][SIZE - 1] == top[i - 2 * SIZE]) {
                CheckSum++;
            }
            //System.out.println(CheckSum);
        }
       // System.out.println("\n");
        for (int i = SIZE; i < 2 * SIZE; i++) {
            System.out.println(resultImg[3 * SIZE][i] + " - " + right[i - SIZE]);
            if (resultImg[3 * SIZE][i] == right[i - SIZE]) {
                CheckSum++;
            }
        }
       // System.out.println(CheckSum);
        if (CheckSum == 2 * SIZE) {
            for (int i = SIZE; i < 2 * SIZE; i++) {
                for (int j = 2 * SIZE; j < 3 * SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - SIZE, j - 2 * SIZE);
                    if (qr.getColor(i - SIZE, j - 2 * SIZE) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }

    }

    private void checkLeftBottomCorner(QRPart qr, int[] left, int[] bottom) {
        int CheckSum = 0;
        for (int i = 2 * SIZE; i < 3 * SIZE; i++) {
           /* System.out.println(resultImg[SIZE - 1][i] + " - " + left[i - 2* SIZE]);*/
            if (resultImg[ SIZE - 1][i] == left[i - 2 * SIZE]) {
                CheckSum++;
            }

        }
        System.out.println("\n");
        for (int i = SIZE; i < 2 * SIZE; i++) {
           // System.out.println(resultImg[i][ 3 * SIZE] + " - " + bottom[i - SIZE]);
            if (resultImg[i][3 * SIZE] == bottom[i - SIZE]) {
                CheckSum++;
            }
        }
       // System.out.println(CheckSum);
        if (CheckSum == 2 * SIZE) {
            for (int i = 2 * SIZE; i < 3 * SIZE; i++) {
                for (int j = SIZE; j < 2 * SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - 2 * SIZE, j - SIZE);
                    if (qr.getColor(i - 2 * SIZE, j - SIZE) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }

    }

    private void checkRightBottomCorner(QRPart qr, int[] right, int[] bottom) {
        int CheckSum = 0;
        for (int i = 2 * SIZE; i < 3 * SIZE; i++) {
             System.out.println(resultImg[i][ 3 * SIZE] + " - " + bottom[i - 2 * SIZE]);
            if (resultImg[i][3 * SIZE] == bottom[i - 2 * SIZE]) {
                CheckSum++;
            }
        }
        System.out.println("\n");
        /*for (int i = 2 * SIZE; i < 3 * SIZE; i++) {
            System.out.println(resultImg[3 * SIZE][i] + " - " + right[i - 2 * SIZE]);
            if (resultImg[3 * SIZE][i] == right[i - 2 *  SIZE]) {
                CheckSum++;
            }
        }*/
        System.out.println(CheckSum);
        if (CheckSum ==  SIZE) {
            for (int i = 2 * SIZE; i < 3 * SIZE; i++) {
                for (int j = 2 * SIZE; j < 3 * SIZE; j++) {
                    resultImg[j][i] = qr.getColor(i - 2 *SIZE, j - 2 * SIZE);
                    if (qr.getColor(i - 2 * SIZE, j - 2 * SIZE) == 0) {
                        resultImg[j][i] = -1;
                    }
                }
            }
        }

    }
}

class QRPart {

    private int[][] color;
    private boolean top;
    private boolean bottom;
    private boolean left;
    private boolean right;
    private boolean center;

    public QRPart(int[][] color) {
        this.color = color;
        center = true;
    }

    public QRPart(int[][] color, boolean top, boolean bottom, boolean left, boolean right) {
        this.bottom = bottom;
        this.color = color;
        this.top = top;
        this.left = left;
        this.right = right;
    }

    public int getColor(int x, int y) {
        return color[x][y];
    }

    public boolean LeftTopCorner() {
        if (top && left) {
            top = false;
            center = false;
            left = false;
            return true;
        }
        return false;
    }

    public boolean RightTopCorner() {
        if (top && right) {
            top = false;
            right = false;
            center = false;
            return true;
        }
        return false;

    }

    public boolean LeftBottomCorner() {
        if (left && bottom) {
            left = false;
            bottom = false;
            center = false;
            return true;
        }
        return false;
    }

    public boolean RightBottomCorner() {
        if (bottom && right) {
            bottom = false;
            right = false;
            center = false;
            return true;
        }
        return false;
    }

    public boolean Right() {
        return right;
    }

    public boolean Left() {
        return left;
    }

    public boolean Top() {
        return top;
    }

    public boolean Bottom() {
        return bottom;
    }

    public boolean center() {
        return center;
    }

    public void setRight() {
        center = false;
        right = true;
    }

    public void setLeft() {
        center = false;
        left = true;
    }

    public void setTop() {
        center = false;
        top = true;
    }

    public void setBottom() {
        center = false;
        bottom = true;
    }

}

class Main {
    public static void main(String[] args) throws IOException {
        QROrderer qr = new QROrderer();
        qr.show();
    }
}
