package net.mysite.SocialMedia;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Random;

public class RandomNumberGenerator {
    public static void main(String[] args) {
        // Create a new workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Create a new sheet
        XSSFSheet sheet = workbook.createSheet("Random Numbers");
        Random random = new Random();
        // Generate and write random numbers to Excel sheet
//        for (int i = 0; i < 10000; i++) {
//            Row row = sheet.createRow(i);
//            Cell cell1 = row.createCell(0);
//            cell1.setCellValue(random.nextInt(1000) + 1);
//            Cell cell2 = row.createCell(1);
//            cell2.setCellValue(random.nextInt(1000) + 1);
//        }
        for(int i = 0; i < 10000; i++){
            Row row = sheet.createRow(i);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(i+1);
        }

        // Write the workbook to a file
        try {
            FileOutputStream outputStream = new FileOutputStream("D://users/friendId.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
