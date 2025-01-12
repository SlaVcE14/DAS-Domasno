package mk.finki.ukim.das.backend.service;

import mk.finki.ukim.das.backend.repository.IssuerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

@Service
public class DataUpdaterService {

    public final IssuerRepository repository;

    public DataUpdaterService(IssuerRepository repository) {
        this.repository = repository;
    }


    @Scheduled(fixedRate = 86400000)
    public void updateData(){
        String updaterJarPath = "../DataScraper/datascraper.jar";
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", updaterJarPath);

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
