package BotEx.statPicture;

import BotEx.tlgrm.HttpExecuter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Drawer {

    private Random rand = new Random();
    private File sourceImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\kak-pravilno-oformit-titulnyj-list-doklada-2.jpg");
    private File watermarkImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\1-2-telephone-download-png.png");
    private File destImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\result.jpg");
    private BufferedImage watermarkImage;
    private BufferedImage sourceImage;
    private Graphics2D g2dWM;
    private Graphics2D g2dSI;
    private HttpExecuter httpExecuter = HttpExecuter.getHttpExecuter();

    public String imgUrl;

    public void addImageWatermark(double WMTransp) {
        AlphaComposite alphaChannelWM = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        AlphaComposite alphaChannelSI = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

        g2dWM = (Graphics2D) sourceImage.getGraphics();
        g2dWM.setComposite(alphaChannelWM);

        g2dSI = (Graphics2D) sourceImage.getGraphics();
        g2dSI.setComposite(alphaChannelSI);

        AffineTransform at = new AffineTransform();
        at.translate(sourceImage.getWidth()/2, sourceImage.getHeight()/2);
        at.rotate(rand.nextGaussian() * Math.PI/10);
        at.scale(rand.nextGaussian()*0.05+0.3, rand.nextGaussian()*0.05+0.3);

        int mask1 = bluePenMask(); //create pencil color
        maskPerformer(watermarkImage,mask1);
        int mask2 = maskCounter(sourceImage,WMTransp); // how much he page background differ from white
//            maskCounter(watermarkImage);
//            maskApper(sourceImage,mask2, false);
        maskApper(watermarkImage, sourceImage, mask2, false); // adds same darkness as mask to original picture

        g2dSI.drawImage(watermarkImage,at,null);
        try {
            ImageIO.write(sourceImage, "png", destImageFile);
        } catch (IOException ex) {ex.printStackTrace();}
        g2dWM.dispose();
        g2dSI.dispose();
    }

    public int bluePenMask() {
//        return(50<<16)|(10<< 8)|(90<< 0);
        return(50+rand.nextInt(50)<<16)|(10+rand.nextInt(20)<< 8)|(90+ rand.nextInt(50)<< 0);
    }

    public int maskCounter(BufferedImage image, double WMTransp){
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
        resultMask = (tMask<<24|rMask<< 16)|(gMask << 8)|(bMask << 0);
        return resultMask;
    }

    public void maskPerformer(BufferedImage image, int mask){
        for (int x = 0; x<image.getWidth();x++){
            for (int y = 0; y<image.getHeight();y++){
                image.setRGB(x,y,image.getRGB(x,y)|mask);
            }
        }
    }
    public void maskApper(BufferedImage image, BufferedImage backgroundImage, int mask, boolean reverse){
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

    public Drawer() {
        //call initImages() after file setters are done
    }

    public Drawer(String imgUrl) {
        this.imgUrl = imgUrl;
        try {
            sourceImage = ImageIO.read(new URL(imgUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initImages();
    }

    public Drawer(File sourceImageFile, File watermarkImageFile, File destImageFile) {
        this.sourceImageFile = sourceImageFile;
        this.watermarkImageFile = watermarkImageFile;
        this.destImageFile = destImageFile;
        initImages();
    }

    private void initImages(){
        try {
            if (sourceImage==null)sourceImage = ImageIO.read(sourceImageFile);
            // Костыль todo 
            if (watermarkImage==null) watermarkImage = sourceImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
