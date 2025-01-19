package mk.finki.ukim.das.backend.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import mk.finki.ukim.das.backend.model.Issuer;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    private static final String FILE_NAME = "data.json";
    private final String DATABASE_LOCATION;

    @Getter
    private static FileSystem instance;

    private FileSystem(Environment environment) {
        DATABASE_LOCATION = environment.getProperty("DATABASE_PATH","../database");
    }

    public static void init(Environment environment){
        if (instance == null) {
            instance = new FileSystem(environment);
        }
    }

    public List<Issuer> ReadData(){

        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATABASE_LOCATION + "/" + FILE_NAME));
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);
            reader.close();
            return new Gson().fromJson(builder.toString(),new TypeToken<List<Issuer>>(){}.getType());

        }
        catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
