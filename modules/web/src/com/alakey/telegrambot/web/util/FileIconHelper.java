package com.alakey.telegrambot.web.util;

import java.util.HashMap;

public class FileIconHelper {
    private static final HashMap<String, String> ICONS;

    static {
        HashMap<String, String> icons = new HashMap<>();
        String formatString = "com/alakey/telegrambot/web/screens/images/mime/%s";
        icons.put(".odt", String.format(formatString, "doc.svg"));
        icons.put(".doc", String.format(formatString, "doc.svg"));
        icons.put(".docx", String.format(formatString, "doc.svg"));
        icons.put(".document", String.format(formatString, "doc.svg"));
        icons.put(".ods", String.format(formatString, "xls.svg"));
        icons.put(".xls", String.format(formatString, "xls.svg"));
        icons.put(".xlsx", String.format(formatString, "xls.svg"));
        icons.put(".spreadsheet", String.format(formatString, "xls.svg"));
        icons.put(".unknown", String.format(formatString, "unknown.svg"));
        icons.put(".pdf", String.format(formatString, "pdf.svg"));
        icons.put(".zip", String.format(formatString, "zip.svg"));
        icons.put(".txt", String.format(formatString, "txt.svg"));
        icons.put(".jpeg", String.format(formatString, "jpg.svg"));
        icons.put(".png", String.format(formatString, "png.svg"));
        icons.put(".3ds", String.format(formatString, "3ds.svg"));
        icons.put(".aac", String.format(formatString, "aac.svg"));
        icons.put(".ai", String.format(formatString, "ai.svg"));
        icons.put(".avi", String.format(formatString, "avi.svg"));
        icons.put(".bmp", String.format(formatString, "bmp.svg"));
        icons.put(".cad", String.format(formatString, "cad.svg"));
        icons.put(".cdr", String.format(formatString, "cdr.svg"));
        icons.put(".css", String.format(formatString, "css.svg"));
        icons.put(".dat", String.format(formatString, "dat.svg"));
        icons.put(".dll", String.format(formatString, "dll.svg"));
        icons.put(".dmg", String.format(formatString, "dmg.svg"));
        icons.put(".eps", String.format(formatString, "eps.svg"));
        icons.put(".fla", String.format(formatString, "fla.svg"));
        icons.put(".flv", String.format(formatString, "flv.svg"));
        icons.put(".gif", String.format(formatString, "gif.svg"));
        icons.put(".html", String.format(formatString, "html.svg"));
        icons.put(".indd", String.format(formatString, "indd.svg"));
        icons.put(".iso", String.format(formatString, "iso.svg"));
        icons.put(".jpg", String.format(formatString, "jpg.svg"));
        icons.put(".js", String.format(formatString, "js.svg"));
        icons.put(".midi", String.format(formatString, "midi.svg"));
        icons.put(".mov", String.format(formatString, "mov.svg"));
        icons.put(".mp3", String.format(formatString, "mp3.svg"));
        icons.put(".mpg", String.format(formatString, "mpg.svg"));
        icons.put(".php", String.format(formatString, "php.svg"));
        icons.put(".ppt", String.format(formatString, "ppt.svg"));
        icons.put(".ps", String.format(formatString, "ps.svg"));
        icons.put(".psd", String.format(formatString, "psd.svg"));
        icons.put(".raw", String.format(formatString, "raw.svg"));
        icons.put(".sql", String.format(formatString, "sql.svg"));
        icons.put(".svg", String.format(formatString, "svg.svg"));
        icons.put(".tif", String.format(formatString, "tif.svg"));
        icons.put(".wmv", String.format(formatString, "wmv.svg"));
        icons.put(".xml", String.format(formatString, "xml.svg"));
        ICONS = icons;
    }

    public static String getIcon(String fileName) {
        for (String key : ICONS.keySet()) {
            if (fileName.contains(key))
                return ICONS.get(key);
        }
        return ICONS.get(".unknown");
    }
}
