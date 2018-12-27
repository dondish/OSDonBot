package DonBot.utils;

import com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ImageUtils {


    private static BufferedImage getImageFromURL(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
        BufferedImage image = ImageIO.read(conn.getInputStream());
        conn.disconnect();
        return image;
    }

    private static void saveAndSend(BufferedImage image, CommandEvent event) throws IOException {
        File savedimage = new File(System.getProperty("user.dir") + "\\" + event.getGuild().getId() + event.getAuthor().getId() + ".PNG");
        ImageIO.write(image, "PNG", savedimage);
        event.getChannel().sendFile(savedimage).queue((ev) -> savedimage.delete());
    }


    /**
     * A function to put a user's avatar on an image
     *
     * @param event - CommandEvent of the command
     * @param avatarurl - ...
     * @param imageurl - ...
     * @param lx - x of the top left corner
     * @param ly - y of the top left corner
     * @param bx - x of the bottom right corner
     * @param by - y of the bottom left corner
     * @param fillArea - if to fill the area behind the avatar
     * @param fillcolor - what color to fill it with
     */
    public static void putAvatarOnImage(CommandEvent event, String avatarurl, String imageurl, int lx, int ly, int bx, int by, boolean fillArea, Color fillcolor) {
        try {
            BufferedImage image = getImageFromURL(avatarurl + "?size=256");
            BufferedImage bg = getImageFromURL(imageurl);
            Graphics graphics = bg.createGraphics().create();
            if (fillArea) {
                graphics.setColor(fillcolor);
                graphics.fillRect(lx, ly, bx - lx, by - ly);
            }
            graphics.drawImage(image, lx, ly, bx, by, 0, 0, 256, 256, null);
            saveAndSend(bg, event);
        } catch (Exception gulp) {
            gulp.printStackTrace();
        }
    }
    /**
     *  Filters an image to be transparent with the alpha given
     *  then puts the image on the avatar
     *
     * @param event - CommandEvent of the command
     * @param avatarurl - ...
     * @param imageurl - ...
     * @param alpha - transparency (check google for argb)
     */
    public static void filterImageOnAvatar(CommandEvent event, String avatarurl, String imageurl, float alpha) {
        try {
            BufferedImage image = getImageFromURL(avatarurl + "?size=256");
            BufferedImage bg = getImageFromURL(imageurl);
            BufferedImage bgfade = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bgfade.createGraphics();
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            graphics.setComposite(composite);
            graphics.drawImage(bg, 0, 0 ,null);
            Graphics g = image.createGraphics();
            g.drawImage(bgfade, 0, 0, 256, 256, 0, 0, bg.getWidth(), bg.getHeight(), null);
            saveAndSend(image, event);
        } catch (Exception gulp) {
            gulp.printStackTrace();
        }
    }

    public static void blurpleAvatar(CommandEvent event, String avatarurl) {
        try {
            HttpURLConnection conn= (HttpURLConnection) new URL(avatarurl+"?size=256").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
            BufferedImage image = ImageIO.read(conn.getInputStream());
            conn.disconnect();
            WritableRaster raster = image.getRaster();
            int[] pixel = new int[4];
            int level;
            for (int y =0; y<raster.getHeight();y++) {
                for (int x=0; x< raster.getWidth();x++) {
                    raster.getPixel(x, y, pixel);
                    level = (pixel[0]+pixel[1]+pixel[2])/3;
                    if (level<62) {
                        image.setRGB(x, y, (pixel[3]<<24) | (78<<16)|(93<<8)|148);
                    } else if (level<171) {
                        image.setRGB(x, y, (pixel[3]<<24) | (114<<16)|(137<<8)|218);
                    } else {
                        image.setRGB(x, y, (pixel[3]<<24) | (255<<16)|(255<<8)|255);
                    }
                }
            }
            File savedimage = new File(System.getProperty("user.dir")+"\\" + event.getGuild().getId() +event.getAuthor().getId()+".PNG");
            ImageIO.write(image, "PNG", savedimage);
            event.getChannel().sendFile(savedimage).queue((ev)->savedimage.delete());
        } catch (Exception pls) {
            pls.printStackTrace();
        }

    }

    public static void monochrome(CommandEvent event, String avatarurl) {
        try {
            BufferedImage image = getImageFromURL(avatarurl + "?size=256");
            WritableRaster raster = image.getRaster();
            int[] pixel = new int[4];
            int level;
            for (int y =0; y<raster.getHeight();y++) {
                for (int x=0; x< raster.getWidth();x++) {
                    raster.getPixel(x, y, pixel);
                    level = (pixel[0]+pixel[1]+pixel[2])/3;
                    image.setRGB(x, y, (pixel[3]<<24) | (level<<16)|(level<<8)|level);
                }
            }
            saveAndSend(image, event);
        } catch (Exception pls) {
            pls.printStackTrace();
        }
    }
    public static void invert(CommandEvent event, String avatarurl) {
        try {
            BufferedImage image = getImageFromURL(avatarurl + "?size=256");
            WritableRaster raster = image.getRaster();
            int[] pixel = new int[4];
            for (int y =0; y<raster.getHeight();y++) {
                for (int x=0; x< raster.getWidth();x++) {
                    raster.getPixel(x, y, pixel);
                    image.setRGB(x, y, (pixel[3]<<24) | ((255-pixel[0])<<16)|((255-pixel[1])<<8)|(255-pixel[2]));
                }
            }
            saveAndSend(image, event);
        } catch (Exception pls) {
            pls.printStackTrace();
        }
    }

    public static void warp(CommandEvent event, String avatarurl) {
        try {
            Object pixel = null;
            BufferedImage image = getImageFromURL(avatarurl + "?size=256");
            BufferedImage real = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
            real.getGraphics().drawImage(image, 0, 0, null);
            WritableRaster raster = real.getRaster();
            BufferedImage to = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
            WritableRaster tor = to.getRaster();
            double angle;
            int a = 5;
            for (int y =0; y<raster.getHeight();y++) {
                for (int x=0; x< raster.getWidth();x++) {
                    pixel = raster.getDataElements(x, y, pixel);

                    angle = a*(x*x+y*y);
                    // Its new coordinates
                    int X = (int) (Math.cos(angle)*x+Math.sin(angle)*y);
                    int Y = (int) (-Math.sin(angle)*x + Math.cos(angle)*y);
                    System.out.println(X + ", " + y);
                    // Set X,Y,pixel to the new raster
                    tor.setDataElements(X+1, Y+1, pixel);
                }
            }
            saveAndSend(to, event);
        } catch (Exception pls) {
            pls.printStackTrace();
        }
    }

    public static void thanos(CommandEvent event, String avatarurl) {
        try {
            Object[][] pixels = new Object[8][8];
            BufferedImage image = getImageFromURL(avatarurl + "?size=256");
            BufferedImage to = getImageFromURL("https://dondish.ml/images/desert.jpg");
            WritableRaster tor = image.getRaster();
            WritableRaster pls = to.getRaster();
            for (int y = 0; y < tor.getHeight(); y += 8) {
                for (int x = 0; x < tor.getWidth(); x += 8) {
                    for (int plsx = 0; plsx < 8; plsx++) {
                        for (int plsy = 0; plsy < 8; plsy++) {
                            pixels[plsx][plsy] = tor.getDataElements(x + plsx, y + plsy, pixels[plsx][plsy]);
                        }
                    }
                    if (x < 128 || y > 128 || new Random().nextBoolean()) {
                        for (int plsx = 0; plsx < 8; plsx++) {
                            for (int plsy = 0; plsy < 8; plsy++) {
                                pls.setDataElements(x + plsx, 596 + y - plsy, pixels[plsx][7 - plsy]);
                            }
                        }
                        continue;
                    }
                    double nx = (Math.random() * 1070) + 200;
                    int ny = 844 - ((int) Math.abs(nx * 2 * Math.sin(nx)) * 853 / 1280) % 844 + 8;
                    for (int plsx = 0; plsx < 8; plsx++) {
                        for (int plsy = 0; plsy < 8; plsy++) {
                            pls.setDataElements((int) nx + plsx, ny - plsy, pixels[plsx][plsy]);
                        }
                    }
                }
            }
            saveAndSend(to, event);
        } catch (Exception pls) {
            pls.printStackTrace();
        }
    }
}
