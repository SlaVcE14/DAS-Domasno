package mk.ukim.finki;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final String URL = "https://www.mse.mk/mk/stats/symbolhistory/";
    public static final String URL_CODES = "https://www.mse.mk/en/stats/current-schedule";
    public static final String FILE_NAME = "data.json";

    List<Issuer> issuers = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Main();
    }

    public Main() throws Exception {
        //Read current database
        issuers = ReadData();

        int total = 0;

        for (Issuer i : issuers){
            total += i.getStockDataList().size();
            System.out.println("(" + i.getStockDataList().size() + ") " + i.getCode());

        }
        System.out.println("Total = " + total);
        System.out.println("Total Issuers = " + issuers.size());

        System.out.println("------------------------------");

        //Get all Issuers
        List<String> issuersList = getAllIssuers();
        System.out.println("Issuers: " + issuersList);
        System.out.println(issuersList.size());
        System.out.println("------------------------------");
        for (String code : issuersList){
            Issuer i = new Issuer(code);
            if(!issuers.contains(i))
                issuers.add(i);
        }

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
                        String date = issuer.getStockDataList().get(0).getDate();
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
                            issuer.getStockDataList().addAll(0, data);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(3600, TimeUnit.SECONDS)) {
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
            File file = new File("../database");
            if (!file.exists())
                file.mkdir();

            BufferedWriter out = new BufferedWriter(new FileWriter("../database/" + FILE_NAME));
            out.write(gsonStr);
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Issuer> ReadData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../database/" + FILE_NAME));
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

        String body = makeRequestForData(code + "?" + dateRange);

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

    private static String makeRequestForData(String params) throws Exception {
        return makeRequest(URL + params);
    }

    private static String makeRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private List<String> getAllIssuers() throws Exception {
        String res = makeRequestForData("get");
        List<String> optionsList = new ArrayList<>();

        Document document = Jsoup.parse(res);
        Elements options = document.select("select option");

        for (Element element : options){
            String code = element.text();
            if (!code.matches(".*\\d.*"))
                optionsList.add(code);
        }


        if (optionsList.isEmpty()){
            optionsList = getAllIssuersFromOtherSource();
        }

        return optionsList;
    }

    private List<String> getAllIssuersFromOtherSource() throws Exception {

        String res = makeRequest(URL_CODES);

        Document document = Jsoup.parse(res);

        Elements tables = document.select("table");

        List<String> codes = new ArrayList<>();

        for (Element table : tables) {
            Elements rows = table.select("tbody tr");
            for (Element row : rows) {
                Elements cols = row.select("td");

                String code = cols.getFirst().text();
                if (!code.matches(".*\\d.*"))
                    codes.add(code);
            }
        }

        Collections.sort(codes);

        return codes;
    }

}