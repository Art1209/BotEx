package BotEx.statPicture;

import BotEx.tlgrm.HttpExecuter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Drawer {

    private Random rand = new Random();
    double WMTransp = 0.3; //прорачность маски фона для watermark

    private File sourceImageFile;
    private File watermarkImageFile;
    private File destImageFile;

    private BufferedImage watermarkImage;
    private BufferedImage sourceImage;

    private String imgUrl;

    public void addImageWatermark(int x, int y, int scale) {
        AlphaComposite alphaChannelWM = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        AlphaComposite alphaChannelSI = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

        Graphics2D g2dWM = (Graphics2D) sourceImage.getGraphics();
        g2dWM.setComposite(alphaChannelWM);

        Graphics2D g2dSI = (Graphics2D) sourceImage.getGraphics();
        g2dSI.setComposite(alphaChannelSI);

        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(rand.nextGaussian() * Math.PI/15);
        at.scale(rand.nextGaussian()*0.05+((0.3*scale)/15), rand.nextGaussian()*0.05+((0.3*scale)/15));

//        int mask1 = bluePenMask(); //create random pencil color for black watermark
//        maskPerformer(watermarkImage,mask1); //apply pencil color to watermark
        int mask2 = maskCounter(sourceImage,WMTransp); // how much the page background differ from white
//            maskCounter(watermarkImage);
//            maskApper(sourceImage,mask2, false);
        maskApper(watermarkImage, mask2); // adds same darkness as mask to original picture

        g2dSI.drawImage(watermarkImage,at,null);

        g2dWM.dispose();
        g2dSI.dispose();
    }

    public int bluePenMask() {
//        return(50<<16)|(10<< 8)|(90<< 0);
        return(50+rand.nextInt(50)<<16)|(10+rand.nextInt(20)<< 8)|(90+ rand.nextInt(50));
    }

    public void getResultToFile(File file){
        try {
            destImageFile = file;
            ImageIO.write(sourceImage, "png", destImageFile);
        } catch (IOException ex) {ex.printStackTrace();}
    }

    private int maskCounter(BufferedImage image, double WMTransp){
        int resultMask =0;
        int tMask =0;
        int rMask =0;
        int gMask =0;
        int bMask =0;
        int [] checkpoints = new int [400];
        int stepx = image.getWidth()/20;
        int stepy = image.getHeight()/20;
        for (int x = 0; x<20;x++){
            for (int y = 0; y<20;y++){
                checkpoints[20*x+y]=image.getRGB(stepx*x,stepy*y);
            }
        }
        for (int i:checkpoints){
            tMask+=i>>>24;
            rMask+=(i<<8)>>>24;
            gMask+=(i<<16)>>>24;
            bMask+=(i<<24)>>>24;
        }
        tMask=(int)Math.round((tMask/400)*WMTransp);
        rMask=(rMask/400);
        gMask=(gMask/400);
        bMask=(bMask/400);
        resultMask = (tMask<<24|rMask<< 16)|(gMask << 8)| bMask;
        return resultMask;
    }

    private void maskPerformer(BufferedImage image, int mask){
        for (int x = 0; x<image.getWidth();x++){
            for (int y = 0; y<image.getHeight();y++){
                image.setRGB(x,y,image.getRGB(x,y)|mask);
            }
        }
    }

    private void maskApper(BufferedImage image, int mask){maskApper(image,mask, false);}

    private void maskApper(BufferedImage image, int mask, boolean reverse){
        Graphics2D imageG = (Graphics2D) image.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        imageG.setComposite(alphaChannel);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D tempG2D = (Graphics2D) tempImage.getGraphics();
        AlphaComposite tempAlphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        tempG2D.setComposite(tempAlphaChannel);

        tempG2D.drawImage(image, null, 0, 0);
        for (int x = 0; x<tempImage.getWidth();x++){
            for (int y = 0; y<tempImage.getHeight();y++){
                if (tempImage.getRGB(x,y)>>24==0){
                    tempImage.setRGB(x,y,0);
                }else{
                    tempImage.setRGB(x,y,mask);
                }
            }
        }
        imageG.drawImage(tempImage, null, 0, 0);
    }

    private void initImages(){ // for local debug purposes
        try {
            if (sourceImage==null)sourceImage = ImageIO.read(sourceImageFile);
            if (watermarkImage==null) watermarkImage = ImageIO.read(getClass().getResourceAsStream(watermarkImageFile.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Drawer() {
        //call initImages() after file setters are done
    }

    public Drawer(String sourceImgUrl, String wmImgUrl) {
        HttpExecuter httpExecuter = HttpExecuter.getHttpExecuter();
        this.imgUrl = sourceImgUrl;
        try {
            sourceImage = ImageIO.read(httpExecuter.getStreamForFileUrl(sourceImgUrl));
            watermarkImage = ImageIO.read(httpExecuter.getStreamForFileUrl(wmImgUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Drawer(File sourceImageFile, File watermarkImageFile, File destImageFile) {
        this.sourceImageFile = sourceImageFile;
        this.watermarkImageFile = watermarkImageFile;
        this.destImageFile = destImageFile;
        initImages();
    }

    public Drawer setSourceImageFile(File sourceImageFile) {
        this.sourceImageFile = sourceImageFile;
        return this;
    }

    public Drawer setWatermarkImageFile(File watermarkImageFile) {
        this.watermarkImageFile = watermarkImageFile;
        return this;
    }

    public Drawer setDestImageFile(File destImageFile) {
        this.destImageFile = destImageFile;
        return this;
    }

    public File getSourceImageFile() {
        return sourceImageFile;
    }

    public File getWatermarkImageFile() {
        return watermarkImageFile;
    }

    public File getDestImageFile() {
        return destImageFile;
    }
}
