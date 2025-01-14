import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {

    public static String propertyFilePath;

    private static Properties properties;

    public static Properties gerProperties() throws Exception {
        FileReader input = null;
        if (properties == null || properties.isEmpty()) {
            try {
                properties = new Properties();
                input = new FileReader(propertyFilePath);
                properties.load(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public static String gerProperty(String propertyName) throws Exception {
        return gerProperties().getProperty(propertyName);
    }

    public static void updateProperty(String propertyName, Object value) throws Exception {
        Properties properties = gerProperties();

        properties.put(propertyName, value);
    }
}
