package utils;

import models.Product;
import java.util.LinkedHashMap;
import java.util.Map;

public class CartManager {

    private static LinkedHashMap<Product, Integer> cart = new LinkedHashMap<>();
    public static void addToCart(Product p, int qty) {
        if (cart.containsKey(p)) {
            cart.put(p, cart.get(p) + qty);
        } else {
            cart.put(p, qty);
        }
    }

    public static void removeFromCart(Product p) {
        cart.remove(p);
    }

    public static void updateQuantity(Product p, int qty) {
        if (qty <= 0) {
            cart.remove(p);
        } else {
            cart.put(p, qty);
        }
    }

    public static LinkedHashMap<Product, Integer> getCart() {
        return cart;
    }

    public static double getTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public static int getItemCount() {
        int count = 0;
        for (int qty : cart.values()) count += qty;
        return count;
    }

    public static void clearCart() {
        cart.clear();
    }
}
