package mk.finki.ukim.das.stock.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mk.finki.ukim.das.stock.model.Issuer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    public static final String FILE_NAME = "data.json";
    public static final String DATABASE_LOCATION = "../database";

    public static List<Issuer> ReadData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATABASE_LOCATION + "/" + FILE_NAME));
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);

            return new Gson().fromJson(builder.toString(),new TypeToken<List<Issuer>>(){}.getType());

        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
