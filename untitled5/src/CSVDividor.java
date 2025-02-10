import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVDividor {
    public static void splitCsv(String inputFile, String outputPrefix, int linesPerFile, String outputFormat) {
        List<String> lines;

        try {
            // 讀取 CSV 文件
            lines = Files.readAllLines(Paths.get(inputFile));

            if (lines.isEmpty()) {
                System.out.println("CSV 文件為空，無需分割。");
                return;
            }

            // 獲取標題
            String header = lines.get(0);
            List<String> dataLines = lines.subList(1, lines.size());

            // 分割數據
            int partNumber = 1;
            for (int i = 0; i < dataLines.size(); i += linesPerFile) {
                List<String> chunk = new ArrayList<>();
                chunk.add(header); // 添加標題
                chunk.addAll(dataLines.subList(i, Math.min(i + linesPerFile, dataLines.size())));

                // 輸出文件
                String outputFileName = outputPrefix + "-" + partNumber + "." + outputFormat;
                Files.write(Paths.get(outputFileName), chunk);
                System.out.println("生成文件：" + outputFileName);
                partNumber++;
            }

        } catch (IOException e) {
            System.err.println("讀取或寫入文件時發生錯誤：" + e.getMessage());
        }
    }

    public static void main(String[] args) {

        try {

            String sPropertyFile = args[0];

            if (sPropertyFile == null || sPropertyFile.isEmpty() == true) {
                System.err.println(
                        "ERROR : Property File Name is not given. Please provide info -Dproperty.file=<Path of property file>");
                System.exit(-1);
            }


            PropertyLoader.propertyFilePath = sPropertyFile;

            System.out.println(sPropertyFile);

            String inputFile = PropertyLoader.gerProperty("original_CSV_Path"); // 原始 CSV 路徑
            String outputPrefix = PropertyLoader.gerProperty("outputPrefix"); // 輸出的文件前綴
            String outputFormat = PropertyLoader.gerProperty("outputFormat"); // 或者 "txt"
            String strlinesPerFile = PropertyLoader.gerProperty("linesPerFile"); // 每個檔案的數據行數（不含標題）
            int linesPerFile = Integer.parseInt(strlinesPerFile);

            splitCsv(inputFile, outputPrefix, linesPerFile, outputFormat);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
