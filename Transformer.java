/*
Author: Dylan Hematillake
Version: v1.9 
Function: The transformer contains a set of actions that 
a user may use to transform an uploaded GREYSCALE image.
The transformer works with the PhotoEditor which, when run,
presents a GUI that the user may use. The set of actions are
shown as buttons on the PhotoEditor or within the transformer class
in the following code.
 */


import becker.xtras.imageTransformation.ITransformations;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class Transformer extends Object implements ITransformations {

    public static final int MIN_NUM_TRANS = 11;
    public static final String DARKEN = "Darken";
    public static final String BRIGHTEN = "Brighten";
    public static final String INVERT = "Invert";
    public static final String FLIPX = "Flip X";
    public static final String FLIPY = "Flip Y";
    public static final String ROTATE = "Rotate";
    public static final String SCALE50 = "Scale 50%";
    public static final String MIRROR = "Mirror";
    public static final String BLUR = "Blur";
    public static final String UNDO = "Undo";
    public static final String RESET = "Reset";

    private String[] transformations = new String[MIN_NUM_TRANS];

    private int[][] pictureOriginal;
    private int[][] picture;

    ArrayList<int[][]> array = new ArrayList<int[][]>();

    /**
     * Construct a Transformer object by setting the possible transformations
     * available.
     */
    public Transformer() {
        super();
        this.transformations[0] = DARKEN;
        this.transformations[1] = BRIGHTEN;
        this.transformations[2] = INVERT;
        this.transformations[3] = FLIPX;
        this.transformations[4] = FLIPY;
        this.transformations[5] = ROTATE;
        this.transformations[6] = SCALE50;
        this.transformations[7] = MIRROR;
        this.transformations[8] = BLUR;
        this.transformations[9] = UNDO;
        this.transformations[10] = RESET;
    }

    /**
     * Construct a Transformer object by setting the possible transformations
     * available and initializing the state of the image.
     *
     * @param originalPic A 2-D array representing a grey scale image. The array
     * contains values from 0 - 255.
     */
    public Transformer(int[][] originalPic) {
        this();
        this.setPixels(originalPic);
    }

    /**
     * Get the image that was transformed.
     *
     * @return The pixels representing the image.
     */
    public int[][] getPixels() {
        return this.picture;
    }

    /**
     * Set the image to be transformed to a new set of pixels.
     *
     * @param newPix The new image to be used for subsequent transformations.
     */
    public void setPixels(int[][] newPix) {
        this.pictureOriginal = newPix;
        this.picture = this.copyArray(newPix);
    }

    /**
     * A array filled with the names of the transformations implemented by this
     * class.
     *
     * @return The array of transformation names.
     */
    public String[] getTransformationNames() {
        return transformations;
    }

    public static void display(int[][] twoDArray) {
        for (int row = 0; row < twoDArray.length; row++) {
            for (int col = 0; col < twoDArray[row].length; col++) {
                if (twoDArray[row][col] == 0) {
                    System.out.print(" .");
                } else {
                    System.out.print(" O");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Perform the transformation indicated.
     *
     * @param transformationName The name of the transformation to perform. Must
     * be one of the transformation names returned by {@link #getTransformationNames
     * getTransformationNames}.
     */
    public void performTransformation(String transformationName) {

        boolean altered = true;

        int[][] backup = this.copyArray(this.picture);

        if (DARKEN.equals(transformationName)) {
            this.picture = changeIntensity(-2, this.picture);
        } else if (BRIGHTEN.equals(transformationName)) {
            this.picture = changeIntensity(2, this.picture);
        } else if (INVERT.equals(transformationName)) {
            this.picture = invert(this.picture);
        } else if (FLIPX.equals(transformationName)) {
            this.picture = flipX(this.picture);
        } else if (FLIPY.equals(transformationName)) {
            this.picture = flipY(this.picture);
        } else if (ROTATE.equals(transformationName)) {
            this.picture = rotate(this.picture);
        } else if (MIRROR.equals(transformationName)) {
            this.picture = mirror(this.picture);
        } else if (SCALE50.equals(transformationName)) {
            this.picture = scale50(this.picture);
        } else if (BLUR.equals(transformationName)) {
            this.picture = blur(this.picture);
        } else if (RESET.equals(transformationName)) {
            this.picture = this.copyArray(this.pictureOriginal);
        } else if (UNDO.equals(transformationName)) {
            this.picture = this.undo();
        } else {
            altered = false;
            throw new Error("Invalid transformation requested.");
        }

        if (altered && !UNDO.equals(transformationName)) {
            array.add(backup);
        }

    }

    private int[][] copyArray(int[][] a) {
        int[][] b = new int[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                b[i][j] = a[i][j];
            }
        }
        return b;
    }

    private int[][] undo() {

        int[][] image;
        if (array.size() == 1) {
            image = this.copyArray(array.get(0));
        } else {
            image = array.remove(array.size() - 1);
        }
        return image;
    }

    private int[][] changeIntensity(double percent, int[][] sourcePixels) {

        for (int pass = 0; pass < sourcePixels.length; pass++) {
            for (int i = 0; i < sourcePixels[pass].length; i++) {
                if (percent > 0) {
                    // BRIGHTENING
                    if (sourcePixels[pass][i] < 245 && sourcePixels[pass][i] > 0) {
                        sourcePixels[pass][i] = sourcePixels[pass][i] + 10;
                    }
                } else if (percent < 0) {
                    // DARKENING
                    if (sourcePixels[pass][i] < 255 && sourcePixels[pass][i] > 10) {
                        sourcePixels[pass][i] = sourcePixels[pass][i] - 10;
                    }
                }
            }
        }
        return sourcePixels;
    }

    private int[][] invert(int[][] sourcePixels) {

        // TAKING THE CURRENT VALUE AND SUBTRACTING IT FROM 255
        for (int row = 0; row < sourcePixels.length; row++) {
            for (int col = 0; col < sourcePixels[row].length; col++) {
                sourcePixels[row][col] = 255 - sourcePixels[row][col];
            }
        }
        return sourcePixels;

    }

    private int[][] flipX(int[][] sourcePixels) {

        // CREATING TEMPORARY VALUE
        int temp;

        // SWITCHING TOP WITH BOTTOM
        for (int col = 0; col < sourcePixels[0].length; col++) {
            for (int row = 0; row < sourcePixels.length / 2; row++) {
                temp = sourcePixels[sourcePixels.length - row - 1][col];
                sourcePixels[sourcePixels.length - 1 - row][col] = sourcePixels[row][col];
                sourcePixels[row][col] = temp;
            }

        }
        return sourcePixels;
    }

    private int[][] flipY(int[][] sourcePixels) {

        // CREATING A TEMPORARY VALUE
        int temp;

        // SWITCHING RIGHT WITH LEFT
        for (int row = 0; row < sourcePixels.length; row++) {
            for (int col = 0; col < sourcePixels[row].length / 2; col++) {
                temp = sourcePixels[row][sourcePixels[row].length - col - 1];
                sourcePixels[row][sourcePixels[row].length - col - 1] = sourcePixels[row][col];
                sourcePixels[row][col] = temp;
            }
        }
        return sourcePixels;
    }

    private int[][] rotate(int[][] sourcePixels) {

        // CREATE A SECOND ARRAY
        int[][] sourcePixels2 = new int[sourcePixels[0].length][sourcePixels.length];

        // SWITCH ROW WITH COLUMN
        for (int row = 0; row < sourcePixels.length; row++) {
            for (int col = 0; col < sourcePixels[0].length; col++) {
                sourcePixels2[col][sourcePixels2[0].length - 1 - row] = sourcePixels[row][col];
            }
        }
        return sourcePixels2;
    }

    private int[][] mirror(int[][] sourcePixels) {

        // DECLARING VARIABLES
        int[][] sourcePixels1 = copyArray(sourcePixels);
        int[][] sourcePixels2 = flipY(sourcePixels1);
        int[][] mirroredImage = new int[sourcePixels.length][sourcePixels[0].length * 2];
        int sourceLength = sourcePixels[0].length;

        // ADDING VALUES TO MIRROREDIMAGE
        for (int row = 0; row < sourcePixels.length; row++) {
            for (int col = 0; col < sourcePixels[row].length; col++) {
                mirroredImage[row][col] = sourcePixels[row][col];
                mirroredImage[row][col + sourceLength] = sourcePixels2[row][col];
            }
        }
        return mirroredImage;
    }

    private int[][] scale50(int[][] sourcePixels) {

        if (sourcePixels.length > 1 && sourcePixels[0].length > 1) {
            // CREATE SECOND INT[][]
            int[][] sourcePixels2 = new int[sourcePixels.length / 2][sourcePixels[0].length / 2];

            // ELIMINATE EVERY SECOND ROW / COLUMN
            for (int row = 0; row < sourcePixels.length; row++) {
                if (row % 2 == 1) {
                    for (int col = 0; col < sourcePixels[row].length; col++) {
                        if (col % 2 == 1) {
                            sourcePixels2[row / 2][col / 2] = sourcePixels[row][col];
                        }
                    }
                }
            }
            return sourcePixels2;
        } else {
            System.out.println("ERROR - Cannot get any smaller.");
            return sourcePixels;
        }

    }

    private int[][] blur(int[][] sourcePixels) {

        // DECLARING VARIABLES
        int height = sourcePixels.length;
        int length = sourcePixels[0].length;

        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < length - 1; col++) {
                // MIDDLE
                sourcePixels[row][col] = (sourcePixels[row][col] + sourcePixels[row - 1][col - 1]
                        + sourcePixels[row - 1][col] + sourcePixels[row - 1][col + 1]
                        + sourcePixels[row][col - 1] + sourcePixels[row + 1][col - 1]
                        + sourcePixels[row + 1][col + 1] + sourcePixels[row + 1][col]
                        + sourcePixels[row][col + 1]) / 9;
            }
        }

        // TOP ROW & BOTTOM ROW
        for (int col = 1; col < length - 1; col++) {
            // TOP
            sourcePixels[0][col] = (sourcePixels[0][col]
                    + sourcePixels[0][col + 1]
                    + sourcePixels[0][col - 1]
                    + sourcePixels[1][col]
                    + sourcePixels[1][col - 1]
                    + sourcePixels[1][col + 1]) / 6;

            // BOTTOM
            sourcePixels[height - 1][col] = (sourcePixels[height - 1][col]
                    + sourcePixels[height - 1][col + 1]
                    + sourcePixels[height - 1][col - 1]
                    + sourcePixels[height - 2][col]
                    + sourcePixels[height - 2][col - 1]
                    + sourcePixels[height - 2][col + 1]) / 6;
        }

        for (int row = 1; row < height - 1; row++) {
            // LEFT
            sourcePixels[row][0] = (sourcePixels[row][0]
                    + sourcePixels[row - 1][0]
                    + sourcePixels[row + 1][0]
                    + sourcePixels[row][1]
                    + sourcePixels[row - 1][1]
                    + sourcePixels[row + 1][1]) / 6;

            // RIGHT
            sourcePixels[row][length - 1] = (sourcePixels[row][length - 1]
                    + sourcePixels[row - 1][length - 1]
                    + sourcePixels[row + 1][length - 1]
                    + sourcePixels[row][length - 2]
                    + sourcePixels[row - 1][length - 2]
                    + sourcePixels[row + 1][length - 2]) / 6;
        }

        sourcePixels[0][length - 1] = (sourcePixels[0][length - 1]
                + sourcePixels[0][length - 2]
                + sourcePixels[1][length - 2]
                + sourcePixels[1][length - 1]) / 4;

        sourcePixels[height - 1][length - 1] = (sourcePixels[height - 1][length - 1]
                + sourcePixels[height - 1][length - 2]
                + sourcePixels[height - 2][length - 2]
                + sourcePixels[height - 2][length - 1]) / 4;

        sourcePixels[0][0] = (sourcePixels[0][0]
                + sourcePixels[0][1]
                + sourcePixels[1][0]
                + sourcePixels[1][1]) / 4;

        sourcePixels[height - 1][0] = (sourcePixels[height - 1][0]
                + sourcePixels[height - 1][1]
                + sourcePixels[height - 2][0]
                + sourcePixels[height - 2][1]) / 4;

        return sourcePixels;
    }

    public static void main(String[] args) {

        int[][] myPicture = new int[4][15];

        myPicture[0][0] = 1;
        myPicture[1][1] = 1;
        myPicture[2][2] = 1;
        myPicture[3][3] = 1;
        myPicture[2][4] = 1;
        myPicture[1][5] = 1;
        myPicture[2][6] = 1;
        myPicture[3][7] = 1;
        myPicture[2][8] = 1;
        myPicture[1][9] = 1;
        myPicture[0][10] = 1;

//       Construct the test object
        Transformer test = new Transformer(myPicture);

//       Display Original Image
        System.out.println("Original\n");
        display(myPicture);

//       Test flip on X-axis
        System.out.println("\nFlipped on the X axis.\n");
        test.performTransformation(FLIPX);
        display(test.getPixels());

//       Test flip on Y-axis
        System.out.println("\nFlipped on the Y axis.\n");
        test.performTransformation(FLIPY);
        display(test.getPixels());

//       Test Rotate 90 degrees
        System.out.println("\nRotated 90 degrees.\n");
        test.performTransformation(ROTATE);
        display(test.getPixels());

//       Test Rotate Scale 50%
        System.out.println("\nScaled 50%.\n");
        test.performTransformation(SCALE50);
        display(test.getPixels());

//       Test Mirror Image
        System.out.println("\nMirror image.\n");
        test.performTransformation(MIRROR);
        display(test.getPixels());

//       Test Reset
        System.out.println("\nReset image.\n");
        test.performTransformation(RESET);
        display(test.getPixels());

    }

}