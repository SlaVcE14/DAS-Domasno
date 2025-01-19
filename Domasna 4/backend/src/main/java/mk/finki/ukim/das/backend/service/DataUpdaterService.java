package mk.finki.ukim.das.backend.service;

import mk.finki.ukim.das.backend.repository.IssuerRepository;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

/**
 * This service is for updating the database
 * */
@Service
public class DataUpdaterService {

    private final String SCRAPER_FILE;
    public final IssuerRepository repository;

    public DataUpdaterService(IssuerRepository repository, Environment environment) {
        this.repository = repository;
        SCRAPER_FILE = environment.getProperty("DATASCRAPER_JAR_PATH", "../DataScraper/datascraper.jar");
    }

    /**
     * In every 24h, this functions runs and executes the datascraper.jar file for scraping and saving new data to the database
     * */
    @Scheduled(fixedRate = 86400000)
    public void updateData(){
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", SCRAPER_FILE);

        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            Executors.newSingleThreadExecutor().execute(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    reader.lines().forEach(line -> {
                        output.append(line).append("\n");
                        System.out.println(line);
                    });
                } catch (Exception e) {
                    output.append("Error reading process output: ").append(e.getMessage());
                }
            });

            process.waitFor();
            repository.updateRepository();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
