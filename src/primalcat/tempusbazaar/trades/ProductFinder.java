package primalcat.tempusbazaar.trades;

import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductFinder {
    public static int limit = 5;

    public static void setLimit(int limit) {
        ProductFinder.limit = limit;
    }

    private static List<Product> getAllProductsFromCategories(List<Category> categories) {
        return categories.stream()  // Создает поток из списка категорий
                .flatMap(category -> category.getProducts().stream())  // Для каждой категории создает поток продуктов и объединяет их в один поток
                .collect(Collectors.toList());  // Собирает все продукты в один список
    }

    public static List<Product> findTopLowestPricedProducts(List<Category> categories) {
        List<Product> products = getAllProductsFromCategories(categories).stream()
                .filter(product -> product.getPriceRatio() != Double.MAX_VALUE)
                .sorted(Comparator.comparingDouble(Product::getPriceRatio))
                .limit(limit)
//                .peek(product -> logProductState(product, "Before increasePrice"))
                .peek(Product::increasePrice)
//                .peek(product -> logProductState(product, "After increasePrice"))
                .collect(Collectors.toList());
        return products;
    }

    public static List<Product> findTopLowestAmountProducts(List<Category> categories) {
        List<Product> products = getAllProductsFromCategories(categories).stream()
                .filter(product -> product.getPriceRatio() != Double.MAX_VALUE)
                .sorted(Comparator.comparingDouble(Product::getCountRatio))
                .limit(limit)
//                .peek(product -> logProductState(product, "Before increaseCount"))
                .peek(Product::increaseCount)
//                .peek(product -> logProductState(product, "After increaseCount"))
                .collect(Collectors.toList());
        return products;
    }

    public static List<Product> findTopHighestPricedProducts(List<Category> categories) {
        List<Product> products = getAllProductsFromCategories(categories).stream()
                .filter(product -> product.getPriceRatio() != Double.MAX_VALUE)
                .sorted(Comparator.comparingDouble(Product::getPriceRatio).reversed())
                .limit(limit)
//                .peek(product -> logProductState(product, "Before decreasePrice"))
                .peek(Product::decreasePrice)
//                .peek(product -> logProductState(product, "After decreasePrice"))
                .collect(Collectors.toList());
        return products;
    }

    public static List<Product> findTopHighestAmountProducts(List<Category> categories) {
        List<Product> products = getAllProductsFromCategories(categories).stream()
                .filter(product -> product.getPriceRatio() != Double.MAX_VALUE)
                .sorted(Comparator.comparingDouble(Product::getCountRatio).reversed())
                .limit(limit)
//                .peek(product -> logProductState(product, "Before decreaseCount"))
                .peek(Product::decreaseCount)
//                .peek(product -> logProductState(product, "After decreaseCount"))
                .collect(Collectors.toList());
        return products;
    }

//    private static void logProductState(Product product, String message) {
//        System.out.println(message + " - Product: " + product.getItem().getType() +
//                ", Count: " + product.getCount().getCurrent() +
//                ", Price: " + product.getPrice().getCurrent() +
//                ", Ratio Count: " + product.getCountRatio() +
//                ", Ratio Price: " + product.getPriceRatio()
//        );
//    }



}
