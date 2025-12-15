package com.sofirv.waterlillies;

import android.content.Context;
import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParser;

public class LevelLoader {

    public static Level loadLevelFromXml(Context context, int xmlResourceId) {
        try {
            XmlResourceParser parser = context.getResources().getXml(xmlResourceId);

            int eventType = parser.getEventType();
            Level level = null;
            String layoutText = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equals("info")) {
                            int number = parser.getAttributeIntValue(null, "number", 1);
                            int rows = parser.getAttributeIntValue(null, "rows", 9);
                            int cols = parser.getAttributeIntValue(null, "cols", 9);
                            int startRow = parser.getAttributeIntValue(null, "startRow", 0);
                            int startCol = parser.getAttributeIntValue(null, "startCol", 0);
                            int breadRow = parser.getAttributeIntValue(null, "breadRow", 0);
                            int breadCol = parser.getAttributeIntValue(null, "breadCol", 0);

                            level = new Level(number, rows, cols);
                            level.setDuckStartRow(startRow);
                            level.setDuckStartCol(startCol);
                            level.setBreadRow(breadRow);
                            level.setBreadCol(breadCol);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (parser.getText() != null) {
                            layoutText += parser.getText().trim();
                        }
                        break;

                    case XmlPullParser. END_TAG:
                        if (tagName.equals("layout") && level != null) {
                            parseLayout(level, layoutText);
                        }
                        break;
                }

                eventType = parser.next();
            }

            parser.close();
            return level;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void parseLayout(Level level, String layoutText) {
        // Eliminar espacios extra y saltos de línea múltiples
        layoutText = layoutText.replaceAll("\\s+", " ").trim();

        String[] rows = layoutText.split(" ");
        int[][] layout = new int[level.getRows()][level.getCols()];

        for (int r = 0; r < level.getRows() && r < rows.length; r++) {
            String[] cols = rows[r].split(",");
            for (int c = 0; c < level.getCols() && c < cols.length; c++) {
                try {
                    layout[r][c] = Integer.parseInt(cols[c]. trim());
                } catch (NumberFormatException e) {
                    layout[r][c] = 0;
                }
            }
        }

        level.setLayout(layout);
    }
}