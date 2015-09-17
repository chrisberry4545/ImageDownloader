/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package websiteimagedownloader;

import java.net.URL;
import java.net.*;
import java.io.*;
import javax.swing.text.html.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.AttributeSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


/**
 *
 * @author Chris
 */
public class ExtractAllImages {
    public static int imageNumber = 1;
    public static String imageClass = "article-image";
    public static String imageHeight = "370";
    
    public static void extractAll() throws Exception {
        String webUrl = "http://magic.wizards.com/en/articles/archive/card-image-gallery/magicorigins";
        //String webUrl = "http://magic.wizards.com/en/articles/archive/card-image-gallery/dragonsoftarkir";
        Document doc = Jsoup.connect(webUrl).get();
        System.out.println(doc.toString());
       // System.out.println(doc.html());
        Elements elements = doc.getElementsByAttributeValue("w", "265");
//        Elements elements = doc.getElementsByClass("full-page");
        
        for (Element e : elements) {
            Element imgElement = e;//.child(0);
            String imgSrc = imgElement.attr("src");
            String imgName = getAppropriateImageName(e.nextElementSibling().nextElementSibling());
            
            if (imgSrc != null &&
                    (imgSrc.endsWith(".jpg") || (imgSrc.endsWith(".png")) || (imgSrc.endsWith(".jpeg")) || (imgSrc.endsWith(".bmp")) || (imgSrc.endsWith(".ico")))) {
                try {
                    downloadImage(webUrl, imgSrc, imgName);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                imageNumber++;
            }
        }
        System.out.println("Total images downloaded: " + (imageNumber - 1));
    }
    
    public static void magicSpoilerExtractor() throws Exception {
        String originalWebUrl =  "http://www.magicspoiler.com/dragons-tarkir-spoiler/";
        boolean failed = false;
        int pageNumber = 1;
        while (!failed) {
            String webUrl = "";
            if (pageNumber == 1) {
                webUrl = originalWebUrl;
            } else {
                webUrl = originalWebUrl + "/page/" + pageNumber + "/";
            }
            System.out.println("Getting data from: " + webUrl + "...");
            try
            {   
                Document doc = Jsoup.connect(webUrl).get();
                Elements elements = doc.getElementsByClass("spoiler-set-card");
                
                if (elements.isEmpty()) {
                    failed = true;
                }
                for (Element e : elements) {
                    Element aElement = e.child(0);
                    Element imgElement = aElement.child(0);
                    String imgSrc = imgElement.attr("src");
                    String imgName = getAppropriateImageNameMagicSpoiler(e);

                    if (imgSrc != null &&
                            (imgSrc.endsWith(".jpg") || (imgSrc.endsWith(".png")) || (imgSrc.endsWith(".jpeg")) || (imgSrc.endsWith(".bmp")) || (imgSrc.endsWith(".ico")))) {
                        try {
                            downloadImage(webUrl, imgSrc, imgName);
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        imageNumber++;
                    }
                }
                pageNumber++;
            } catch (Exception e) {
                System.err.println(e.toString());
                failed = true;
            }
        }
        
        System.out.println("Total images downloaded: " + (imageNumber - 1));
    }
    
    private static String getAppropriateImageName(Element el) {
        String imgName = el.html();
        return removeInvalidChars(imgName);
    }
    
    private static String getAppropriateImageNameMagicSpoiler(Element div) {
        Element header = div.child(1).child(0);
        return removeInvalidChars(header.html());
    }
    
    private static String removeInvalidChars(String str) {
        str = replaceAE(str);
        str = stripApstrophes(str);
        str =  stripCommas(str);
        str = stripSpaces(str);
        return str;
    }
    
    private static String replaceAE(String str) {
        if (str.contains("&AElig;")) {
            str = str.replace("&AElig;", "AE");
        }
        return str;
    }
    
    private static String stripCommas(String str) {
        if (str.contains(",")) {
            str = str.replace(",", "--");
        }
        return str;
    }
    
    private static String stripApstrophes(String str) {
        if (str.contains("'")) {
            str = str.replace("'", "");
        }
        if (str.contains("’")) {
            str = str.replace("’", "");
        }
        return str;
    }
    
    private static String stripSpaces(String str) {
        if (str.contains(" ")) {
            str = str.replace(" ", "");
        }
        return str;
    }
    
    private static void downloadImage(String url, String imgSrc, String imageName) throws IOException {
        BufferedImage image = null;
        try {
            if (!(imgSrc.startsWith("http"))) {
                url = url + imgSrc;
            } else {
                url = imgSrc;
            }
            imgSrc = imgSrc.substring(imgSrc.lastIndexOf("/") + 1);
            String imageFormat = null;
            imageFormat = imgSrc.substring(imgSrc.lastIndexOf(".") + 1);
            String imgPath = null;
            imgPath = "C:/Users/Chris/Documents/NetBeansProjects/Java-WebsiteRead/" + imageName + ".jpg";
            URL imageUrl = new URL(url);
            image = ImageIO.read(imageUrl);
            if (image != null) {
                File file = new File(imgPath);
                ImageIO.write(image, imageFormat, file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
