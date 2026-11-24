
package com.example.inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryReport {

    public static void main(String[] args) {
        String filePath = "data/products.csv";

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {

            List<String[]> rows = lines
                    .skip(1)
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> line.split(","))
                    .collect(Collectors.toList());

            List<Product> products = rows.stream()
                    .filter(row -> row.length == 4)
                    .map(row -> new Product(
                            row[0].trim(),
                            row[1].trim(),
                            Integer.parseInt(row[2].trim()),
                            Double.parseDouble(row[3].trim())))
                    .collect(Collectors.toList());

            Map<String, Double> totalsByCategory = products.stream()
                    .collect(Collectors.groupingBy(
                            Product::getCategory,
                            Collectors.summingDouble(Product::getTotalValue)));

            System.out.println("=== Inventory Report ===");
            System.out.println("Total products: " + products.size());
            System.out.println("\nTotal inventory value by category:");

            totalsByCategory.forEach((category, total) ->
                    System.out.printf("%s: %.2f%n", category, total));

            double grandTotal = products.stream()
                    .mapToDouble(Product::getTotalValue)
                    .sum();

            System.out.printf("%nGrand total inventory value: %.2f%n",
                    grandTotal);

        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number in the CSV file: "
                    + e.getMessage());
        }
    }
}
