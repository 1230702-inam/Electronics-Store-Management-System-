package utils;

import javafx.scene.image.Image;

public class CategoryImages {

    private static final int[][] WARRANTY_MONTHS = {
        {1, 12}, // Headphones
        {2, 24}, // Laptops
        {3, 12}, // Phones
        {4, 12}, // Keyboards
        {5, 24}, // Screens
        {6, 6}   // Chargers
    };

    public static int getWarrantyMonths(int categoryId) {
        for (int[] entry : WARRANTY_MONTHS) {
            if (entry[0] == categoryId) return entry[1];
        }
        return 12; 
    }

    public static Image loadImageByPath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return null;

        try {
            String fullPath = "/resources/images/" + relativePath;
            return new Image(CategoryImages.class.getResourceAsStream(fullPath));
        } catch (Exception e) {
            return null;
        }
    }
}
