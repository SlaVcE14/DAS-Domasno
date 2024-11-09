package mk.ukim.finki;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //https://www.mse.mk/mk/stats/symbolhistory/stb?FromDate=30.7.2024&ToDate=31.10.2024
    public static final String URL = "https://www.mse.mk/mk/stats/symbolhistory/";
    public static final String FILE_NAME = "data.json";

    List<Issuer> issuers = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Main();
    }

    public Main() throws Exception {
        //Read current base
        issuers = ReadData();

        int total = 0;

        for (Issuer i : issuers){
            total += i.getStockDataList().size();
            System.out.println("(" + i.getStockDataList().size() + ") " + i.getCode());

        }
        System.out.println("Total = " + total);

        System.out.println("------------------------------");

        //Get all Issuers
        List<String> issuersList = getAllIssuers();
        System.out.println("Issuers: " + issuersList);
        System.out.println("------------------------------");


        long start = System.currentTimeMillis();


        int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(cores); // Adjust thread pool size as needed

        for (String code : issuersList) {
            executor.submit(() -> {
                try {
                    Issuer issuer = getIssuer(code);

                    if (issuer.getStockDataList().isEmpty()) {
                        // Get data 10 years back
                        System.out.println("Getting 10 years data for: " + code + "...");
                        issuer.stockDataList.addAll(getLastTenYears(code));
                    } else {
                        // Get new data if needed
                        String date = issuer.getStockDataList().getFirst().getDate();
                        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);

                        int year = localDateTime.getYear();
                        int month = localDateTime.getMonthValue();
                        int day = localDateTime.getDayOfMonth();

                        LocalDateTime fromDate = LocalDateTime.parse(date + " 00:00:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")).plusDays(1);

                        int fromYear = fromDate.getYear();
                        int fromMonth = fromDate.getMonthValue();
                        int fromDay = fromDate.getDayOfMonth();

                        if (!String.format("%02d.%02d.%d", day, month, year).equals(date) && (month == fromMonth && day > fromDay)) {
                            List<StockData> data = getData(code, String.format("FromDate=%d.%d.%d&ToDate=%d.%d.%d", fromDay, fromMonth, fromYear, day, month, year));
                            issuer.getStockDataList().addAll(0, data); // Ensure no duplicates
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Handle exceptions for each task
                }
            });
        }
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(600, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        String gsonStr = new Gson().newBuilder().setPrettyPrinting().create().toJson(issuers);

        SaveData(gsonStr);

        long end = System.currentTimeMillis();

        double time = (end - start)/1000d;

        System.out.println("Time taken: " + ((time > 60?((int)(time/60) + "min " + (int)(time % 60) + "s"):time + "s")));

    }

    private synchronized Issuer getIssuer(String code) {
        return issuers.stream().filter(i -> i.getCode().equals(code)).findFirst().orElseGet(() -> {
            Issuer issuer = new Issuer(code);
            issuers.add(issuer);
            return issuer;
        });
    }

    private List<StockData> getLastTenYears(String code) throws Exception {

        List<StockData> data = new ArrayList<>();

        LocalDateTime toDate = LocalDateTime.now().minusDays(1);

        for (int i = 0; i < 10; i++){
            LocalDateTime fromDate = toDate.minusYears(1).plusDays(1);

            List<StockData> dataYear = getData(code, String.format(
                    "FromDate=%d.%d.%d&ToDate=%d.%d.%d",
                    fromDate.getDayOfMonth(),
                    fromDate.getMonthValue(),
                    fromDate.getYear(),
                    toDate.getDayOfMonth(),
                    toDate.getMonthValue(),
                    toDate.getYear()
            ));
            data.addAll(dataYear);
            toDate = toDate.minusYears(1);
        }

        return data;
    }

    private void SaveData(String gsonStr) {
        try {
            File file = new File("database");
            if (!file.exists())
                file.mkdir();

            BufferedWriter out = new BufferedWriter(new FileWriter("database/" + FILE_NAME));
            out.write(gsonStr);
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Issuer> ReadData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("database/" + FILE_NAME));
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);

            return new Gson().fromJson(builder.toString(),new TypeToken<List<Issuer>>(){}.getType());

        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }


    private List<StockData> getData(String code, String dateRange) throws Exception {

        System.out.println("Getting data for " + code + " (" + dateRange + ")");

        List<StockData> dataList = new ArrayList<>();

        String body = makeRequest(code + "?" + dateRange);

        try {
            String tableHtml = body.substring(body.indexOf("<table"),body.indexOf("</table>")-8);

            Document doc = Jsoup.parse(tableHtml);
            Element table = doc.getElementById("resultsTable");
            Elements rows = table.select("tbody tr");

            for (Element row : rows) {
                Elements cols = row.select("td");
                StockData data = new StockData();

                data.setDate(cols.get(0).text());
                data.setLastTransactionPrice(cols.get(1).text());
                data.setMaxPrice(cols.get(2).text());
                data.setMinPrice(cols.get(3).text());
                data.setAveragePrice(cols.get(4).text());
                data.setPercentageChange(cols.get(5).text());
                data.setQuantity(cols.get(6).text());
                data.setTurnoverBest(cols.get(7).text());
                data.setTotalTurnover(cols.get(8).text());

                dataList.add(data);
            }

        }catch (Exception e){
            System.out.println("NO DATA FOUND!!!");
        }

        return dataList;
    }

    private static String makeRequest(String params) throws Exception {
        String url = URL + params;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        return body;
/*
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL + params))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.err.println("Failed to fetch HTML, HTTP response code: " + response.statusCode());
            return "";
        }
*/
    }

    private List<String> getAllIssuers() throws Exception {
        String res = makeRequest("get");
        String options = res.substring(res.indexOf("name=\"Code\">")+ 12,res.indexOf("</select>"));

        List<String> optionsList = new ArrayList<>();

        Pattern pattern = Pattern.compile("<option value=\"([^\"]+)\">[^<]+</option>");
        Matcher matcher = pattern.matcher(options);

        while (matcher.find()) {
            String s = matcher.group(1);

            if (!s.matches(".*\\d.*"))
                optionsList.add(s);
        }

        return optionsList;
    }

}