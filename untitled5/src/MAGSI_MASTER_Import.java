import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MAGSI_MASTER_Import {

    public static void main(String[] args) {

        try {
//            System.out.println("\nMAGSI_import_generator started.... \n" + new Date());
            String sPropertyFile = args[0];

            if (sPropertyFile == null || sPropertyFile.isEmpty() == true) {
                System.err.println(
                        "ERROR : Property File Name is not given. Please provide info -Dproperty.file=<Path of property file>");
                System.exit(-1);
            }

            PropertyLoader.propertyFilePath = sPropertyFile;

            System.out.println(sPropertyFile);

            String inputFilePath = PropertyLoader.gerProperty("original_CSV_Path"); // 原始 CSV 路徑
            String outputFilePathtemp = PropertyLoader.gerProperty("Output_CSV_Path"); // 輸出 CSV 路徑

            String formattedDate = new SimpleDateFormat("yyyyMMdd").format(new Date());


            String outputFilePath = outputFilePathtemp + formattedDate + ".csv"; // 輸出 CSV 路徑

            String newCSV_Header = PropertyLoader.gerProperty("newCSV_Header");
            String KUNI_index = PropertyLoader.gerProperty("KUNI_index");
            int intKUNI_index = Integer.parseInt(KUNI_index);
            String insertDT = PropertyLoader.gerProperty("insertDT");
            String insertBy = PropertyLoader.gerProperty("insertBy");
            String delete_flg = PropertyLoader.gerProperty("delete_flg");


            // 讀取 CSV 檔案
            List<String[]> rows = readCsv(inputFilePath);

            rows.remove(0); //Remove header

            // 檢查 MAC_Serial 是否有重複值
            Set<String> duplicateCheck = new HashSet<>();
            List<String[]> filteredRows = new ArrayList<>();
            Set<String> duplicates = new HashSet<>();

            for (String[] row : rows) {
                String macSerial = row[0];
                if (!duplicateCheck.add(macSerial)) {
                    duplicates.add(macSerial);
                } else {
                    filteredRows.add(row);
                }
            }

            if (!duplicates.isEmpty()) {
                System.out.println("重複的 MAC_Serial 值: " + String.join(", ", duplicates));
            }

            // 檢查是否包含 TYAGS007 和 TYAGS008
            boolean found007 = false, found008 = false;
            for (String[] row : filteredRows) {
                if ("TYAGS007".equals(row[0])) {
                    System.out.println("TYAGS007 found");
                    found007 = true;
                }
                if ("TYAGS008".equals(row[0])) {
                    System.out.println("TYAGS008 found");
                    found008 = true;
                }
            }
            if (!found007 || !found008) {
                System.out.println("TYAGS007,TYAGS008 is not in original CSV file.");
            }

            // 生成新的 CSV 檔案內容
            List<String[]> newRows = new ArrayList<>();
            newRows.add(new String[]{newCSV_Header});

            for (String[] row : filteredRows) {
                String macSerial = row[0];
                String kuni = row[intKUNI_index];
                newRows.add(new String[]{macSerial, "1", kuni, insertDT, insertBy, delete_flg});
            }

            // 添加手動新增的行
            newRows.add(new String[]{"TYAGS007", "1", "JKN", insertDT, insertBy, delete_flg});
            newRows.add(new String[]{"TYAGS008", "1", "JKN", insertDT, insertBy, delete_flg});

            // 檢查新的檔案是否有重複的 mac_serial_id
            duplicateCheck.clear();
            duplicates.clear();
            for (int i = 1; i < newRows.size(); i++) { // 跳過 header
                String macSerial = newRows.get(i)[0];
                if (!duplicateCheck.add(macSerial)) {
                    duplicates.add(macSerial);
                }
            }
            if (!duplicates.isEmpty()) {
                System.out.println("新檔案中重複的 MAC_Serial 值: " + String.join(", ", duplicates));
            }

            // 寫入新的 CSV 檔案
            writeCsv(outputFilePath, newRows);
            System.out.println("新 CSV 檔案已生成: " + outputFilePath);


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    // 讀取 CSV
    private static List<String[]> readCsv(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .map(line -> line.split(","))
                .collect(Collectors.toList());
    }

    // 寫入 CSV
    private static void writeCsv(String filePath, List<String[]> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }


}
