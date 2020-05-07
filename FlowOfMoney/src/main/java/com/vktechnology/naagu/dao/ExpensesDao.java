
package com.vktechnology.naagu.dao;

import com.vktechnology.naagu.models.Expenses;
import com.vktechnology.naagu.models.Holder;






import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PostAuthorize;

/**
 * Created by HOME on 11/22/2015.
 */
public interface ExpensesDao {
	
    public String showExpenses();

    public List<Expenses> listOfExpenses(String user);

    public String saveExpenses(Expenses expenses);

    public String deleteExpensesRow(String id, String amount, String source, String user);

    public List<String> holderMoney_ListOnly(String user);

    public List<Expenses> listOFCurrentDay(String user);

    public List<Expenses> listOfDate(String user, String date);

    public List<Expenses> listOfTwoDate(String user, String fromdate, String todate);

    public String availablityCheck(String user, String sourcce, BigDecimal amount);

    public String generateExpensesExcel(XSSFWorkbook workbook, String fromDate, String toDate) throws FileNotFoundException;

    public List<Expenses> listExpensesTwoDates(String user, String fromDate, String toDate);

    public List<String> listByOnlineType(String onlineType);
    
    public String dateConvertionFromJSPToDB(String strDate);
    
    public String dateConvertionFromDBToJSP(String strDate);
}
