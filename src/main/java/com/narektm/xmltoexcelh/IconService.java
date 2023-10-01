package com.narektm.xmltoexcelh;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;

public class IconService {
    private static final Image MAIN_ICON;

    static {
        try (InputStream resourceAsStream = XmlToExcelApplication.class.getResourceAsStream("/icons/icon.png")) {
            assert resourceAsStream != null;
            MAIN_ICON = new Image(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("Can't initialize the main icon.", e);
        }
    }

    public static Image getMainIcon() {
        return MAIN_ICON;
    }
}
