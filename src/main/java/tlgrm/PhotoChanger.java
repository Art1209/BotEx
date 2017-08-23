package tlgrm;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class PhotoChanger {
    Random rand = new Random();
    File sourceImageFile;
    File watermarkImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\1-2-telephone-download-png.png");
    File destImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\result.jpg");
    BufferedImage sourceImage;

    public void addImageWatermark() {
        try {
//            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

            // initializes necessary graphic properties
            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
            g2d.setComposite(alphaChannel);

            // calculates the coordinate where the image is painted
            int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth()) / 2;
            int topLeftY = (sourceImage.getHeight() - watermarkImage.getHeight()) / 2;

            AffineTransform at = new AffineTransform();
            at.translate(watermarkImage.getWidth() / 2, watermarkImage.getHeight() / 2);

            // 3. do the actual rotation
            at.rotate(rand.nextGaussian() * Math.PI/10);

            // 2. just a scale because this image is big
            at.scale(rand.nextGaussian()*0.05+0.3, rand.nextGaussian()*0.05+0.3);

            // 1. translate the object so that you rotate it around the
            //    center (easier :))
            at.translate(-watermarkImage.getWidth()/2, -watermarkImage.getHeight()/2);

            for (int x = 0; x<watermarkImage.getWidth();x++){
                for (int y = 0; y<watermarkImage.getHeight();y++){
                    watermarkImage.setRGB(x,y,watermarkImage.getRGB(x,y)|
                            ((50 + rand.nextInt(50) << 16)
                                    | (10+ rand.nextInt(20) << 8)
                                    | (90+ rand.nextInt(50) << 0))
                    );
                }
            }

            g2d.drawImage(watermarkImage,at,null);

            ImageIO.write(sourceImage, "png", destImageFile);
            g2d.dispose();

            System.out.println("The image watermark is added to the image.");

        } catch (IOException ex) {ex.printStackTrace();}
    }

    public PhotoChanger (String imgUrl) {

        try {
            sourceImage = ImageIO.read(new URL(imgUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (sourcePicture!=null&&sourcePicture.canRead()){
//            sourceImageFile = sourcePicture;
//        } else sourceImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\Penguinthemum.jpg");
    }
}
