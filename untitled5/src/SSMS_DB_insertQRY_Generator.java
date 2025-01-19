import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class SSMS_DB_insertQRY_Generator {

    public static void main(String[] args) throws Exception {

        try {
            String sPropertyFile = args[0];

            if (sPropertyFile == null || sPropertyFile.isEmpty()) {
                System.err.println(
                        "ERROR : Property File Name is not given. Please provide info -Dproperty.file=<Path of property file>");
                System.exit(-1);
            }

            PropertyLoader.propertyFilePath = sPropertyFile;

            System.out.println(sPropertyFile);

            String excelFilePath = PropertyLoader.gerProperty("excelFile_Path"); // 替換為你的 Excel 文件路徑
            String kthrINF_sheetName = PropertyLoader.gerProperty("kthrINF_sheetName");
            String kthrINF_HIS_sheetName = PropertyLoader.gerProperty("kthrINF_HIS_sheetName");
            String outputKTHR_INF_txtFile_Path = PropertyLoader.gerProperty("outputKTHR_INF_txtFile_Path");
            String outputKTHR_INF_HIS_txtFile_path = PropertyLoader.gerProperty("outputKTHR_INF_HIS_txtFile_path");
            String strINF_sheet_last_row = PropertyLoader.gerProperty("INF_sheet_last_row");
            int intINF_sheet_last_row = Integer.parseInt(strINF_sheet_last_row);
            String strINF_HIS_sheet_last_row = PropertyLoader.gerProperty("INF_HIS_sheet_last_row");
            int intINF_HIS_sheet_last_row = Integer.parseInt(strINF_HIS_sheet_last_row);
            String strINF_last_Col_Num = PropertyLoader.gerProperty("INF_last_Col_Num");
            int intINF_last_Col_Num = Integer.parseInt(strINF_last_Col_Num);
            String strINF_HIS_last_Col_Num = PropertyLoader.gerProperty("INF_HIS_last_Col_Num");
            int intINF_HIS_last_Col_Num = Integer.parseInt(strINF_HIS_last_Col_Num);
            String KTHR_INF_insertQuery_Prefix = PropertyLoader.gerProperty("KTHR_INF_insertQuery_Prefix");
            String KTHR_INF_HIS_insertQuery_Prefix = PropertyLoader.gerProperty("KTHR_INF_HIS_insertQuery_Prefix");


            insertQueryGenerator(excelFilePath, kthrINF_sheetName, outputKTHR_INF_txtFile_Path, intINF_sheet_last_row, intINF_last_Col_Num, KTHR_INF_insertQuery_Prefix);
            insertQueryGenerator(excelFilePath, kthrINF_HIS_sheetName, outputKTHR_INF_HIS_txtFile_path, intINF_HIS_sheet_last_row, intINF_HIS_last_Col_Num, KTHR_INF_HIS_insertQuery_Prefix);

            replaceNULL_txt(outputKTHR_INF_txtFile_Path, outputKTHR_INF_HIS_txtFile_path);


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private static void insertQueryGenerator(String excelFilePath, String INF_sheetName, String output_txtFile_Path, int lastRow, int lastColNum, String insert_query_prefix) {

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis);
             BufferedWriter writer = new BufferedWriter(new FileWriter(output_txtFile_Path))) {

            Sheet sheet = workbook.getSheet(INF_sheetName);
            if (sheet == null) {
                System.out.println("Sheet " + INF_sheetName + " not found in the Excel file.");
                return;
            }

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) { // 從第二行開始
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                StringBuilder sqlBuilder = new StringBuilder(insert_query_prefix);

                for (int colIndex = 0; colIndex <= lastColNum; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    if (colIndex > 0) sqlBuilder.append("','");
                    if (cell == null) {
                        sqlBuilder.append("");
                    } else {
                        String cellValue = cell.getStringCellValue();

                        sqlBuilder.append(cellValue);
                    }


                }
                sqlBuilder.append("');");

                // 寫入到文件中
                writer.write(sqlBuilder.toString());
                writer.newLine();
            }

            System.out.println("SQL statements have been written to: " + output_txtFile_Path);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void replaceNULL_txt(String infPath, String inf_hisPath) {

        List<String> filePaths = List.of(
                infPath,
                inf_hisPath
        );

        // 要替換的字串
        String target = "'NULL'";
        String replacement = "NULL";

        for (String filePath : filePaths) {
            replaceTextInFile(filePath, target, replacement);
        }


    }

    public static void replaceTextInFile(String filePath, String target, String replacement) {
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found or is not a valid file: " + filePath);
            return;
        }

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.replace(target, replacement)).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content.toString());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }

        System.out.println("Replaced text in file: " + filePath);
    }
}
