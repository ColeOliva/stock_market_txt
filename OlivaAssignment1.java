import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* This program takes in a text file with stock informaiton on each line.
 * The program reads the file line by line and processes the stock information.
 * It detects stock splits based on the closing and opening prices of the stocks. */
public class OlivaAssignment1 {

    private static final double SPLIT_2_TO_1_THRESHOLD = 0.20;
    private static final double SPLIT_3_TO_1_THRESHOLD = 0.30;
    private static final double SPLIT_3_TO_2_THRESHOLD = 0.15;
    private static int numOfSplits = 0;

    public static void main(String[] args) {
        List<Stock> stockList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line;
            int lineNum = 0;
            String[] values;

            while ((line = br.readLine()) != null) {
                values = line.split("\t");

                // Print "Processing" and stock header only for the first line of a new stock
                if (lineNum == 0) {
                    System.out.println("Processing " + values[0]);
                    System.out.println("====================================");
                }

                // Detect a new stock and process splits for the previous one
                if (lineNum > 0 && !values[0].equals(stockList.get(lineNum - 1).getTickerSymbol())) {
                    detectStockSplits(stockList);
                    stockList.clear();
                    System.out.println();
                    System.out.println("Processing " + values[0]);
                    System.out.println("====================================");
                    lineNum = 0; // reset the line number for the new stock
                    numOfSplits = 0; // reset the split counter
                }

                addStock(stockList, values);
                lineNum++;
            }

            // Detect and process splits for the final stock
            detectStockSplits(stockList);
            stockList.clear();

        } catch (IOException e) {
            System.err.println("Error reading the stock file: " + e.getMessage());
        }
    }

    private static void detectStockSplits(List<Stock> stockList) {
        for (int i = 1; i < stockList.size(); i++) {
            Stock previousStock = stockList.get(i - 1);
            Stock currentStock = stockList.get(i);

            // Doing the math
            double ratio = currentStock.getClosingPrice() / previousStock.getOpeningPrice();

            if (isSplit(ratio, 1.5, SPLIT_3_TO_2_THRESHOLD)) {
                System.out.println(
                        "3:2 split on " + currentStock.getTickerSymbol() + " on " + currentStock.getDate() + ": "
                                + currentStock.getClosingPrice() + " --> " + previousStock.getOpeningPrice());
                numOfSplits++;
            } else if (isSplit(ratio, 3.0, SPLIT_3_TO_1_THRESHOLD)) {
                System.out.println(
                        "3:1 split on " + currentStock.getTickerSymbol() + " on " + currentStock.getDate() + ": "
                                + currentStock.getClosingPrice() + " --> " + previousStock.getOpeningPrice());
                numOfSplits++;
            } else if (isSplit(ratio, 2.0, SPLIT_2_TO_1_THRESHOLD)) {
                System.out.println(
                        "2:1 split on " + currentStock.getTickerSymbol() + " on " + currentStock.getDate() + ": "
                                + currentStock.getClosingPrice() + " --> " + previousStock.getOpeningPrice());
                numOfSplits++;
            }
        }
        System.out.println("Total number of splits = " + numOfSplits);
    }

    // Check if a split condition is met
    private static boolean isSplit(double ratio, double expectedRatio, double threshold) {
        return Math.abs(ratio - expectedRatio) < threshold;
    }

    // Add a stock to the list
    private static void addStock(List<Stock> stockList, String[] values) {
        String tickerSymbol = values[0];
        String date = values[1];
        double openingPrice = Double.parseDouble(values[2]);
        double highPrice = Double.parseDouble(values[3]);
        double lowPrice = Double.parseDouble(values[4]);
        double closingPrice = Double.parseDouble(values[5]);
        long volume = Long.parseLong(values[6]);
        double adjustedClosingPrice = Double.parseDouble(values[7]);

        Stock stock = new Stock(tickerSymbol, date, openingPrice, highPrice, lowPrice, closingPrice, volume,
                adjustedClosingPrice);
        stockList.add(stock);
    }

    // Stock class representing stock data
    public static class Stock {
        private final String tickerSymbol;
        private final String date;
        private final double openingPrice;
        private final double highPrice;
        private final double lowPrice;
        private final double closingPrice;
        private final long volume;
        private final double adjustedClosingPrice;

        public Stock(String tickerSymbol, String date, double openingPrice, double highPrice, double lowPrice,
                double closingPrice, long volume, double adjustedClosingPrice) {
            this.tickerSymbol = tickerSymbol;
            this.date = date;
            this.openingPrice = openingPrice;
            this.highPrice = highPrice;
            this.lowPrice = lowPrice;
            this.closingPrice = closingPrice;
            this.volume = volume;
            this.adjustedClosingPrice = adjustedClosingPrice;
        }

        public String getTickerSymbol() {
            return tickerSymbol;
        }

        public String getDate() {
            return date;
        }

        public double getOpeningPrice() {
            return openingPrice;
        }

        public double getClosingPrice() {
            return closingPrice;
        }
    }
}
