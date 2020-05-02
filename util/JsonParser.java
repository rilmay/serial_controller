package util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonParser {

    public static Map<String, String> parse(File file) {
        Map<String, String> result = new HashMap<>();
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader fileReader = new FileReader(file);
            JSONObject object = (JSONObject) jsonParser.parse(fileReader);
            Set<String> set = (Set<String>) object.keySet();
            for (String key : set) {
                String value = object.get(key).toString();
                result.put(key, value);
            }
            fileReader.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
