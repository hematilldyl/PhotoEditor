/*
Author: Dylan Hematillake
Version: v1.9 
Function: The PhotoEditor presents a GUI that takes an uploaded
GREYSCALE image and performs various transformations on it. These
transformations are called from the transformer.
 */

import becker.xtras.imageTransformation.ITransformations;
import becker.xtras.imageTransformation.ImageTransformerGUI;

public class PhotoEditor extends Object {

    public static void main( String args[] ) {

        ITransformations trans = new Transformer();
        ImageTransformerGUI theGUI = new ImageTransformerGUI( trans );

    }

}