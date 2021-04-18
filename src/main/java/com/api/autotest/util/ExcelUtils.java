package com.api.autotest.util;

import com.api.autotest.pojo.WriteBackData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * poi批量读取数据
 */
public class ExcelUtils {

    public static String casePath="src/main/resources/case_v9.xls";

    public static Map<String, Integer> rowIdentifierMappingRowNum = new HashMap<>();
    public static Map<String, Integer> cellNameMappingCellNum = new HashMap<>();
    public static List<WriteBackData> writeBackDatas = new ArrayList<>();

    static {
        /**
         * 用例表：
         * 1、获取caseId以及对应的行索引
         * 2、获取cellName以及它对应的列索引
         */
        loadRownumAndCellnumMapping(ExcelUtils.casePath, "用例");
    }

    /**
     * 不连续行号和列
     *
     * @param path
     * @param sheet
     * @param rows
     * @param cells
     * @return
     */
    public static Object[][] getDataByArray(String path, String sheet, int[] rows, int[] cells) {

        //1、获取WorkBookd对象
        Object[][] datas = null;
        try {
            Workbook workbook = WorkbookFactory.create(new File(path));
            //2、获取sheet
            Sheet sheetName = workbook.getSheet(sheet);
            //定义一个Object的二维数组对象
            datas = new Object[rows.length][cells.length];

            //循环遍历每一行，行索引从0开始，所以不能用“=”
            for (int i = 0; i < rows.length; i++) {
                //3、获取行，rows[i] - 1得到行的索引
                Row row = sheetName.getRow(rows[i] - 1);
                //循环遍历每一列
                for (int j = 0; j < cells.length; j++) {
                    //4、获取列，cells[j] - 1得到列的索引
                    Cell cell = row.getCell(cells[j] - 1);
                    //将列处理成字符串类型
                    cell.setCellType(CellType.STRING);
                    String value = cell.getStringCellValue();
                    //将每个单元格数据添加到二维数组中
                    datas[i][j] = value;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        return datas;
    }

    /**
     * 连续行和连续列
     *
     * @param path
     * @param sheet
     * @param startRow  传开始行号
     * @param endRow
     * @param startCell
     * @param endCell
     * @return
     */
    public static Object[][] getData2(String path, String sheet, int startRow, int endRow, int startCell, int endCell) {

        //1、获取WorkBookd对象
        Object[][] datas = null;
        try {
            Workbook workbook = WorkbookFactory.create(new File(path));
            //2、获取sheet
            Sheet sheetName = workbook.getSheet(sheet);
            //3、获取行
            datas = new Object[endRow - startRow + 1][endCell - startCell + 1];
            for (int i = startRow; i <= endRow; i++) {
                Row row = sheetName.getRow(i - 1);
                for (int j = startCell; j <= endCell; j++) {
                    Cell cell = row.getCell(j - 1);
                    //将列处理成字符串类型
                    cell.setCellType(CellType.STRING);
                    String value = cell.getStringCellValue();
                    datas[i - startRow][j - startCell] = value;
                }
            }

            //4、获取列
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        return datas;
    }

    /**
     * 指定excel表单的数据，封装对象
     *
     * @param excelPath excel的相对路径
     * @param sheetName excel表单名
     */
    public static <T> List<T> load(String excelPath, String sheetName, Class<T> clazz) {

        List<T>  list = new ArrayList<>();
        //创建WorkBook对象
        try {
            Workbook workbook = WorkbookFactory.create(new File(excelPath));
            Sheet sheet = workbook.getSheet(sheetName);
            //获取第一行
            Row titleRow = sheet.getRow(0);
            //获取最后一个列
            int lastCellNum = titleRow.getLastCellNum();
            //数据数组
            String[] fields = new String[lastCellNum];
            //循环处理第一行的title
            for (int i = 0; i < lastCellNum; i++) {
                //获取列索来引获取对应的列
                Cell cell = titleRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                //设置列的类型为字符串
                cell.setCellType(CellType.STRING);
                //获取列值
                String title = cell.getStringCellValue();
                //截取英文名称（除汉字描述之外）
                if (title.indexOf("(") != -1) {
                    title = title.substring(0, title.indexOf("("));
                }

                fields[i] = title;
            }
            System.out.println("");
            int lastRowIndex = sheet.getLastRowNum();

            //循环遍历标题以外的所有内容，将所有内容保存到用例对象中
            //循环处理每一个数据行
            for (int i = 1; i <= lastRowIndex; i++) {
                //反射获取Case对象
                T obj = clazz.newInstance();
                //获取me一行
                Row dataRow = sheet.getRow(i);
                //如果行为null或者为空就跳过
                if (dataRow == null || isEmptyRow(dataRow)) {
                    continue;
                }
                //遍历每一列，将每一列的内容添加到Case的对中
                for (int j = 0; j < lastCellNum; j++) {
                    //Row.MissingCellPolicy.CREATE_NULL_AS_BLANK枚举类策略
                    Cell cell = dataRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    String value = cell.getStringCellValue();
                    //反射获取方法名
                    String methodName = "set" + fields[j];
                    //获取（set标题）方法
                    Method method = clazz.getMethod(methodName, String.class);
                    //通过反射调用并执行（set标题）方法将每行的title对应的数据添加到Case对象中
                    //调用执行方法时，无参只穿Class
                    method.invoke(obj, value);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 判断行是否空
     *
     * @param dataRow
     * @return
     */
    private static boolean isEmptyRow(Row dataRow) {
        int lastCellNum = dataRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = dataRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            if (value != null || value.trim().length() > 0) {
                //不为空返回fasle
                return false;
            }
        }
        //为空返回true
        return true;
    }

    /**
     * 回写接口响应报文
     *
     * @param filePath
     * @param sheetName
     * @param caseId
     * @param cellName
     * @param result
     */
    public static void writeBackData(String filePath, String sheetName, String rowIdentifier, String cellName, String result) {
        System.out.println("读写excel");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        //sheet.getRow(rowNum)-->row
        //row.getCell(cellNum)-->row
        //cell.setStringValue(result)

        try {
            inputStream = new FileInputStream(new File(filePath));

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet(sheetName);
            //获取caseid对应的行索引
            int rowNum = rowIdentifierMappingRowNum.get(rowIdentifier);
            //获取索引对于对应的行
            Row row = sheet.getRow(rowNum);
            //获取列对应的列索引
            int cellNum = cellNameMappingCellNum.get(cellName);
            //获取列号对应的列
            Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(result);
            outputStream = new FileOutputStream(new File(filePath));
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取caseId以及对应的行索引
     * 获取cellName以及它对应的列索引
     *
     * @param filePath
     * @param sheetName
     */
    public static void loadRownumAndCellnumMapping(String filePath, String sheetName) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheet(sheetName);
            //获取第一行（标题行）
            Row titleRow = sheet.getRow(0);
            if (titleRow != null && !isEmptyRow(titleRow)) {
                int lastCellNum = titleRow.getLastCellNum();
                //获取第一行的最后一列索引
                for (int i = 0; i < lastCellNum; i++) {
                    Cell cell = titleRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    //获取第一行（title行）的数据
                    String title = cell.getStringCellValue();
                    if (title.indexOf("(") != -1) {
                        title = title.substring(0, title.indexOf("("));
                    }
                    //获取列索引
                    int column = cell.getAddress().getColumn();
                    cellNameMappingCellNum.put(title, column);

                }
            }
            //从第二行开始，获取所有的数据行
            //获取最后一行的行号
            int lastRowNum = sheet.getLastRowNum();
            //循环获取第一列每行的数据
            for (int j = 1; j <= lastRowNum; j++) {
                Row dataRow = sheet.getRow(j);
                Cell firstCellOfRow = dataRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                firstCellOfRow.setCellType(CellType.STRING);
                //获取caseId
                String rowIdentifier = firstCellOfRow.getStringCellValue();
                //获取行索引
                int rowNum = dataRow.getRowNum();
                rowIdentifierMappingRowNum.put(rowIdentifier, rowNum);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 批量回写数据
     *
     * @param excelPath
     */
    public static void batchWriteBackDate(String excelPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(new File(excelPath));
            Workbook workbook = WorkbookFactory.create(inputStream);

            for (WriteBackData writeBackData : writeBackDatas) {
                String sheetName = writeBackData.getSheetName();
                Sheet sheet = workbook.getSheet(sheetName);
                String rowIdentifier = writeBackData.getRowIdentifier();
                int rowNum = rowIdentifierMappingRowNum.get(rowIdentifier);
                Row row = sheet.getRow(rowNum);
                String cellName = writeBackData.getCellName();
                int cellNum = cellNameMappingCellNum.get(cellName);
                Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellType(CellType.STRING);
                String result = writeBackData.getResult();
                cell.setCellValue(result);
            }
            outputStream = new FileOutputStream(new File(excelPath));
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    public static void main(String[] args) {
//        String caasePath = "src/main/resources/case_v8.xls";
//        String sheetName = "接口信息";
//        int[] rows = {2, 3};
//        int[] cells = {1, 4};
//        String url = "";
//        Object[][] datas = ExcelUtils.getDataByArray(caasePath, sheetName, rows, cells);
//        for (Object[] objects : datas) {
//            String apiidFromRest = objects[0].toString();
//            if (apiidFromRest.equals("1")) {
//                url = objects[1].toString();
//
//                break;
//            }
//        }
//        System.out.println(url);
//    }
}
