package com.vktechnology.naagu.dao;

import com.vktechnology.naagu.models.AllTransRecord;
import com.vktechnology.naagu.models.Credit;
import com.vktechnology.naagu.models.CreditClearRecord;
import com.vktechnology.naagu.models.CreditRecord;
import com.vktechnology.naagu.models.DebitClearRecord;
import com.vktechnology.naagu.models.DebitRecord;
import com.vktechnology.naagu.models.Expenses;
import com.vktechnology.naagu.models.Holder;
import com.vktechnology.naagu.models.HolderRecord;
import com.vktechnology.naagu.models.Source;
import com.vktechnology.naagu.models.SourceRecord;
import com.vktechnology.naagu.service.CreditService;
import com.vktechnology.naagu.service.DebitService;
import com.vktechnology.naagu.service.ExpensesService;
import com.vktechnology.naagu.service.HolderService;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tiles.template.GetAsStringModel;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * Created by HOME on 12/20/2015.
 */
public class HolderDaoImpl implements  HolderDao {

	private static final Logger logger = Logger.getLogger(HolderDaoImpl.class);
	
    @Autowired
    private SessionFactory sessionFactory;
    
    private String holderName = "notavailable";
    private String holderBankName ="notavailable";
    private String holderSourcename = "notavailable";
    private String sourceTypeFD = "";
    //private BigDecimal amount = 0 ;
    BigDecimal amount = new BigDecimal(0);
    //private float H_Amount = 0.0f;
    BigDecimal H_Amount = new BigDecimal(0);
    //private float Source_Amount = 0.0f;
    BigDecimal Source_Amount = new BigDecimal(0);
    //private float debit_amount = 0.0f;
    BigDecimal debit_amount = new BigDecimal(0);
    //private float Holder_Amount = 0.0f;
    BigDecimal Holder_Amount = new BigDecimal(0);
    
    @Autowired
    private CreditService creditService;
    
    @Autowired
    private ExpensesService expensesService;
    
    @Autowired
    private HolderService holderService;
    
    @Autowired
    private DebitService debitService;
    
    public HolderDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<Holder> BankList(String user) {

        //List<Holder> listLogin = (List<Holder>) sessionFactory.getCurrentSession().createQuery("from Holder").list();
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(Holder.class);
        cr.add(Restrictions.eq("holder_User", user)).addOrder(Order.asc("holder_Id"));
        List<Holder> listLogin = cr.list();
        return listLogin;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<AllTransRecord> allTransList(String user) {

    	Criteria cr = sessionFactory.getCurrentSession().createCriteria(HolderRecord.class);
        cr.add(Restrictions.eq("holder_User", user)).addOrder(Order.asc("holder_Id"));
        List<HolderRecord> list = cr.list();
        ArrayList<AllTransRecord> li = new ArrayList<AllTransRecord>();
        
        ArrayList<HolderRecord> hr = new ArrayList<HolderRecord>();    
       
        
        for(HolderRecord lis: list){
        	System.out.println(lis.getHolder_Name());       
        	
        	 AllTransRecord All = new AllTransRecord();
        	 All.setAll_Id(lis.getHolder_Id());
        	 All.setAll_Date(lis.getHolder_Date());
        	 All.setAll_Perticuler(lis.getHolder_Name());        	 
        	 All.setAll_Deposite(lis.getHolder_Withdraw());
        	 All.setAll_DBalance(lis.getHolder_Balance());
        	 All.setAll_SourceName(lis.getHolder_SourceName());
        	 All.setAll_SourceBal(lis.getHolder_SourceBalance());
        	 All.setAll_Description(lis.getHolder_Description());
        	 li.add(All);
        }
        
        Criteria crite = sessionFactory.getCurrentSession().createCriteria(SourceRecord.class);
        crite.add(Restrictions.eq("sr_User", user)).addOrder( Order.asc("sr_Id"));
        List<SourceRecord> source_record = crite.list();
        for(SourceRecord sou: source_record){
        	AllTransRecord All = new AllTransRecord();
        	All.setAll_Id(sou.getSr_Id());
       	 	All.setAll_Date(sou.getSr_Date());
       	 	All.setAll_Perticuler(sou.getSr_Name().concat(" (Sou)"));        	 
       	 	All.setAll_Deposite(sou.getSr_Deposits());
       	 	All.setAll_DBalance(sou.getSr_Balance());
       	 	All.setAll_SourceName("---");
       	 	All.setAll_SourceBal(new BigDecimal("0.00"));
       	 	All.setAll_Description(sou.getSr_Description());
       	 	li.add(All);
        }
        
        List<DebitRecord> Debi_record = debitService.recordOfDebit(user);
        for(DebitRecord dbr: Debi_record){
        	AllTransRecord All = new AllTransRecord();
        	All.setAll_Id(dbr.getId());
       	 	All.setAll_Date(dbr.getDr_Date());
       	 	All.setAll_Perticuler(dbr.getDr_Name().concat(" (Dbr)"));        	 
       	 	All.setAll_Deposite(dbr.getDr_Withdraw());
       	 	All.setAll_DBalance(dbr.getDr_Balance());
       	 	All.setAll_SourceName(dbr.getDr_SourceName());
       	 	All.setAll_SourceBal(dbr.getDr_SourceBalance());
       	 	All.setAll_Description(dbr.getDr_Description());
       	 	li.add(All);
        }
        
        List<DebitClearRecord> DebCleRec = debitService.debitClearRecord(user);
        for(DebitClearRecord DCR: DebCleRec){
        	AllTransRecord All = new AllTransRecord();
        	All.setAll_Id(DCR.getDebitClear_Id());
       	 	All.setAll_Date(DCR.getDebitClear_Date());
       	 	All.setAll_Perticuler(DCR.getDebitClear_name().concat(" (Dbcl)"));        	 
       	 	All.setAll_Deposite(DCR.getDebitClear_Amount());
       	 	All.setAll_DBalance(DCR.getDebitClear_balance());
       	 	All.setAll_SourceName(DCR.getDebitClear_Source());
       	 	All.setAll_SourceBal(DCR.getDebitClear_SourceBal());
       	 	All.setAll_Description(DCR.getDebitClear_Des());
       	 	li.add(All);
        }
        
        List<Expenses> ExpeRec = expensesService.listOfExpenses1(user);
        for(Expenses exp: ExpeRec){
        	AllTransRecord All = new AllTransRecord();
        	All.setAll_Id(exp.getId());
       	 	All.setAll_Date(exp.getDate());
       	 	All.setAll_Perticuler(exp.getUnit_Name().concat(" (Exp)"));        	 
       	 	All.setAll_Deposite(exp.getAmount());
       	 	All.setAll_DBalance(new BigDecimal("0.00"));
       	 	All.setAll_SourceName(exp.getSource_Name());
       	 	All.setAll_SourceBal(exp.getSource_bal());
       	 	All.setAll_Description(exp.getDescription());
       	 	li.add(All);
        }
        
        List<CreditRecord> CreRec = creditService.recordOfCredit(user);
        for(CreditRecord Cre: CreRec){
        	AllTransRecord All = new AllTransRecord();
        	All.setAll_Id(Cre.getCr_Id());
       	 	All.setAll_Date(Cre.getCr_Date());
       	 	All.setAll_Perticuler(Cre.getCr_Name().concat(" (Cre)"));        	 
       	 	All.setAll_Deposite(Cre.getCr_Withdraw());
       	 	All.setAll_DBalance(Cre.getCr_A_Balance());
       	 	All.setAll_SourceName("---->");
       	 	All.setAll_SourceBal(Cre.getCr_C_Balance());
       	 	All.setAll_Description(Cre.getCr_Description());
       	 	li.add(All);
        }
        
        List<CreditClearRecord> CreClRec = creditService.recordOfClearRecord(user);
        for(CreditClearRecord CreCl: CreClRec){
        	AllTransRecord All = new AllTransRecord();
        	All.setAll_Id(CreCl.getCreditClear_Id());
       	 	All.setAll_Date(CreCl.getCreditClear_Date());
       	 	All.setAll_Perticuler(CreCl.getCreditClear_name().concat(" (CrCl)"));        	 
       	 	All.setAll_Deposite(CreCl.getCreditClear_Amount());
       	 	All.setAll_DBalance(CreCl.getCreditClear_balance());
       	 	All.setAll_SourceName(CreCl.getCreditClear_Source());
       	 	All.setAll_SourceBal(CreCl.getCreditClear_SourceBal());
       	 	All.setAll_Description(CreCl.getCreditClear_Des());
       	 	li.add(All);
        }
        return li;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<HolderRecord> HolderList(String user) {

        //List<HolderList> list = (List<HolderList>) sessionFactory.getCurrentSession().createQuery("from HolderList").list();
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(HolderRecord.class);
        cr.add(Restrictions.eq("holder_User", user)).addOrder(Order.asc("holder_Id"));
        List<HolderRecord> list = cr.list();
        return list;

    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<HolderRecord> HolderListForExl(String user, String fromDate, String toDate) {
    	Criteria cr = sessionFactory.getCurrentSession().createCriteria(HolderRecord.class);
        cr.add(Restrictions.eq("holder_User", user)).addOrder(Order.asc("holder_Id"));
        cr.add(Restrictions.between("holder_Date", fromDate, toDate));
        List<HolderRecord> list = cr.list();
        return list;
    }

    @Override
    @Transactional
    public String saveBank(Holder holder) {
        String Message = "";
        String user = holder.getHolder_User();
        List<Holder> result = holderService.existOrNot(holder);
        for (Holder sxxx : result) {
            amount = sxxx.getHolder_Amount();
            holderName = sxxx.getHolder_Name();
        }

        List<String> resultOnlyBank = holderService.existOrNotBank(holder.getHolder_User());
        for (String sss : resultOnlyBank) {
            if (holder.getHolder_Source().equalsIgnoreCase(sss)) {
                holderBankName = sss;
            }
        }

        List<String> resultSourcenames = holderService.existOrNotSource(getPrincipal());
        for (String S_Name : resultSourcenames) {
            if (holder.getHolder_Source().equalsIgnoreCase(S_Name)) {
                holderSourcename = S_Name;
            }
        }

        BigDecimal One = new BigDecimal(0);

        if (One.compareTo(holder.getHolder_Amount()) == 1) {
            Message = "Error";
        } else if (expensesService.availablityCheck(user, holder.getHolder_Source(), holder.getHolder_Amount()).equalsIgnoreCase("Error")) {
            Message = "ErrorAmountLess";
        } else {

            if (holder.getHolder_Source().indexOf("_Credit") > -1) {
                creditService.deductCredit(holder.getHolder_Source(), holder.getHolder_User(), holder.getHolder_Amount());
                holderService.AmountAddToHolder(holder.getHolder_Name(), holder.getHolder_Amount(), holder.getHolder_User());
                Message = "SuccessCreditToHolder";
            } else if (holder.getHolder_Source().equalsIgnoreCase(holderBankName)) {
                System.out.println("we r in bank to pocket, bank to bank ;" + holder.getHolder_Name() + ",," + holderBankName);
                holderService.AmountAddToHolder(holder.getHolder_Name(), holder.getHolder_Amount(), holder.getHolder_User());
                holderService.deductHolderMoney(holder.getHolder_Source(), holder.getHolder_Amount(), user);
                Message = "BankToBankPocket";
            } else if (holder.getHolder_Source().equalsIgnoreCase(holderSourcename)) {
                holderService.AmountAddToHolder(holder.getHolder_Name(), holder.getHolder_Amount(), getPrincipal());
                holderService.deductSource(holder.getHolder_Source(), holder.getHolder_Amount(), getPrincipal());
                Message = "SuccessSourceToHolder";

            } else {
                if ("notavailable".equals(holderName)) {
                    logger.info("-- create new holder and save---");
                    sessionFactory.getCurrentSession().save(holder);
                    Message = "SuccessHolderNewEntry";
                }
            }
        }
        holderName = "notavailable";
        holderBankName = "notavailable";
        holderSourcename = "notavailable";
        sourceTypeFD = " ";
        return Message;
    }

    @Override
    @Transactional
    public void saveBankList(Holder holder) {
        HolderRecord holderList = new HolderRecord();
        holderList.setHolder_Date(holder.getHolder_Date());
        holderList.setHolder_Name(holder.getHolder_Name());
        holderList.setHolder_Withdraw(holder.getHolder_Amount());
        holderList.setHolder_Balance(holderService.holderBalance(getPrincipal(), holder.getHolder_Name()));
        holderList.setHolder_SourceName(holder.getHolder_Source());
        holderList.setHolder_SourceBalance(debitService.debitRecordPurpose(holder.getHolder_Source(), getPrincipal()));
        holderList.setHolder_Description(holder.getHolder_Description());
        holderList.setHolder_User(getPrincipal());
        sessionFactory.getCurrentSession().save(holderList);

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void saveDebitPerson(Holder holder, String name, String type) {
        String hqldebit = "from Holder where holder_Type= :holdertype and holder_Name= :holdername and holder_User= :userNName";
        Query query3 = sessionFactory.getCurrentSession().createQuery(hqldebit);
        query3.setParameter("holdertype", holder.getHolder_Type());
        query3.setParameter("holdername", holder.getHolder_Name());
        query3.setParameter("userNName", holder.getHolder_User());
        List<Holder> sourceresult = query3.list();
        for (Holder sss : sourceresult) {
            debit_amount = sss.getHolder_Amount();
        }

        if ("notavailable".equals(name) && "notavailable".equals(type)) {

            sessionFactory.getCurrentSession().save(holder);
        } else {
            logger.info("-- update entry holder save---");
            BigDecimal addDebit = debit_amount.add(holder.getHolder_Amount());

            Query debitUpdate = sessionFactory.getCurrentSession().createQuery("update Holder set holder_Amount=:holderAmount where holder_Name=:holderName and holder_type= :holdertype and holder_User= :uuser");
            debitUpdate.setBigDecimal("holderAmount", addDebit);
            debitUpdate.setString("holderName", holder.getHolder_Name());
            debitUpdate.setString("holdertype", holder.getHolder_Type());
            debitUpdate.setString("uuser", holder.getHolder_User());
            debitUpdate.executeUpdate();
        }
        debit_amount = new BigDecimal(0);
        name = "";
        type = "";
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void deductSource(String Sname, BigDecimal Samount, String user) {
        logger.info("----deduct source--");
        // deduction from source
        String hqlsource1 = "from Source where sourceName= :sourcename and user= :username";
        Query query4 = sessionFactory.getCurrentSession().createQuery(hqlsource1);
        query4.setParameter("sourcename", Sname);
        query4.setParameter("username", user);
        List<Source> sourceresult = query4.list();
        for (Source sss : sourceresult) {
            Source_Amount = sss.getSourceAmount();
        }

        BigDecimal deduct = Source_Amount.subtract(Samount);
        Query sourceUpdate = sessionFactory.getCurrentSession().createQuery("update Source set sourceAmount=:sourAmount where sourceName=:source_Name and user= :user_name");
        sourceUpdate.setBigDecimal("sourAmount", deduct);
        sourceUpdate.setString("source_Name", Sname);
        sourceUpdate.setString("user_name", user);
        sourceUpdate.executeUpdate();
        Source_Amount = new BigDecimal(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void deductHolderMoney(String Sname, BigDecimal Samount, String user) {
        logger.info("----deduct holder--");
        // deduction from source
        String hqlsource1 = "from Holder where holder_Name= :holdername and holder_User= :ussname";
        Query query4 = sessionFactory.getCurrentSession().createQuery(hqlsource1);
        query4.setParameter("holdername", Sname);
        query4.setParameter("ussname", user);
        List<Holder> sourceresult = query4.list();
        for (Holder sss : sourceresult) {
            Holder_Amount = sss.getHolder_Amount();
        }
        // write if condition for source list empty or etc

        BigDecimal deductHol = Holder_Amount.subtract(Samount);
        Query holderUpdate = sessionFactory.getCurrentSession().createQuery("update Holder set holder_Amount=:sourAmount where holder_Name=:source_Name and holder_User= :userr_Name");
        holderUpdate.setBigDecimal("sourAmount", deductHol);
        holderUpdate.setString("source_Name", Sname);
        holderUpdate.setString("userr_Name", user);
        holderUpdate.executeUpdate();
        Holder_Amount = new BigDecimal(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<Holder> existOrNot(Holder holder) {
        sourceTypeFD = holder.getHolder_Source();
        String hql = "from Holder where holder_Type= :username and holder_Name= :password and holder_User= :holduser";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", holder.getHolder_Type());
        query.setParameter("password", holder.getHolder_Name());
        query.setParameter("holduser", holder.getHolder_User());
        List<Holder> result = query.list();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public List<String> existOrNotBank(String user) {
        List<String> listThree = sessionFactory.getCurrentSession().createQuery("select holder_Name FROM Holder where holder_User ='" + user + "' ").list();
        List<String> list_bank = new ArrayList<String>();
        list_bank.addAll(listThree);
        return list_bank;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public List<String> existOrNotSource(String user) {
        List<String> listSource = sessionFactory.getCurrentSession().createQuery("select sourceName FROM Source where user ='" + user + "' ").list();
        List<String> list_source = new ArrayList<String>();
        list_source.addAll(listSource);
        return list_source;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public void AmountAddToHolder(String Hname, BigDecimal Hamount, String user) {
        String hqlsource1 = "from Holder where  holder_Name=:holdername and holder_User=:holderuser";
        Query query4 = sessionFactory.getCurrentSession().createQuery(hqlsource1);
        query4.setParameter("holdername", Hname);
        query4.setParameter("holderuser", user);
        List<Holder> Hresult = query4.list();

        for (Holder sss : Hresult) {
            H_Amount = sss.getHolder_Amount();
        }

        BigDecimal updateHamount = H_Amount.add(Hamount);
        Query holderUpdate = sessionFactory.getCurrentSession().createQuery("update Holder set holder_Amount=:HAmount where holder_Name=:HName  and holder_User= :username");
        //holderUpdate.setFloat("HAmount", updateHamount);
        holderUpdate.setBigDecimal("HAmount", updateHamount);
        holderUpdate.setString("HName", Hname);
        holderUpdate.setString("username", user);
        holderUpdate.executeUpdate();
        logger.info("-- successfully add to holder ---");
        H_Amount = new BigDecimal(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<String> loadHolderList(String user, String holdertype) {

        List<String> load12 = sessionFactory.getCurrentSession().createQuery("select holder_Name FROM Holder where holder_Type = '" + holdertype + "' "
                + "and holder_User ='" + user + "' ").list();
        List<String> list_bank1 = new ArrayList<String>();
        list_bank1.addAll(load12);
        return list_bank1;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<String> loadHolderComplteList(String user) {
        List<String> load13 = sessionFactory.getCurrentSession().createQuery("select holder_Name FROM Holder where holder_User ='" + user + "' ").list();
        List<String> list_bank2 = new ArrayList<String>();
        list_bank2.addAll(load13);
        return list_bank2;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<String> availability_Holder_Name(String user, String holdertype, String holdername) {

        List<String> load13 = sessionFactory.getCurrentSession().createQuery("select holder_Name FROM Holder where holder_Type = '" + holdertype + "' and holder_Name ='" + holdername + "' and holder_User ='" + user + "' ").list();
        return load13;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public String availablityCheckForHolder(String user, String source, BigDecimal amount) {
        String message = "";
        if (source.indexOf("Credit") > -1) {
            Criteria cr = sessionFactory.getCurrentSession().createCriteria(Credit.class);
            Criterion query1 = Restrictions.eq("user", user);
            Criterion query2 = Restrictions.eq("credit_Name", source);
            LogicalExpression andExp = Restrictions.and(query1, query2);
            cr.add(andExp);
            List<Credit> betweenDates = cr.list();
            for (Credit ss : betweenDates) {
                //if(amount > ss.getCredit_Amount() ){
                if (amount.compareTo(ss.getCredit_Amount()) == 1) {
                    message = "Error";
                } else {
                    message = "Success";
                }
            }
        } else {

            String SourceName = "";
            List<String> Source12 = sessionFactory.getCurrentSession().createQuery("select holder_Name FROM Holder where holder_User "
                    + "='" + user + "' and holder_Name = '" + source + "' ").list();
            for (String aa : Source12) {
                SourceName = aa;
            }

            if (SourceName.isEmpty()) {
                logger.info("----check from source----");
                Criteria cr = sessionFactory.getCurrentSession().createCriteria(Source.class);
                Criterion query5 = Restrictions.eq("user", user);
                Criterion query6 = Restrictions.eq("sourceName", source);
                LogicalExpression andExp3 = Restrictions.and(query5, query6);
                cr.add(andExp3);
                List<Source> checkSource = cr.list();
                for (Source sss : checkSource) {
                    if (amount.compareTo(sss.getSourceAmount()) == 1) {
                        message = "Error";
                    } else {
                        message = "Success";
                    }
                }
            } else {
                logger.info("----check from holder---");
                Criteria cr = sessionFactory.getCurrentSession().createCriteria(Holder.class);
                Criterion query3 = Restrictions.eq("holder_User", user);
                Criterion query4 = Restrictions.eq("holder_Name", source);
                LogicalExpression andExp2 = Restrictions.and(query3, query4);
                cr.add(andExp2);
                List<Holder> checkHolder = cr.list();
                for (Holder sss : checkHolder) {
                    if (amount.compareTo(sss.getHolder_Amount()) == 1) {
                        message = "Error";
                    } else {
                        message = "Success";
                    }
                }
            }
        }
        return message;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public BigDecimal holderBalance(String user12, String Source12) {
        BigDecimal sourceCurBal = new BigDecimal(0);
        // getting holder balance
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(Holder.class);
        Criterion userS = Restrictions.eq("holder_User", user12);
        Criterion dateS = Restrictions.eq("holder_Name", Source12);
        LogicalExpression andExp = Restrictions.and(userS, dateS);
        cr.add(andExp);
        List<Holder> SourceBal = cr.list();
        for (Holder hold : SourceBal) {
            sourceCurBal = hold.getHolder_Amount();
        }
        return sourceCurBal;
    }

    @Override
    @Transactional
    public String holderRecordExcel(XSSFWorkbook workbook) throws FileNotFoundException {
        logger.info("-------------++download holder Excel++------------");
        String SuccessMes = "none";
        List<Holder> listHR = holderService.BankList(getPrincipal());

        XSSFSheet sheet = workbook.createSheet("sheet 1");
        sheet.setDefaultColumnWidth(20);

        // create style for header cells
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);

        // create header row
        XSSFRow header = sheet.createRow(0);

        header.createCell(0).setCellValue("Holder_Id");
        header.getCell(0).setCellStyle(style);

        header.createCell(1).setCellValue("Holder_Date");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("Holder_Type");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("Holder_Name");
        header.getCell(3).setCellStyle(style);

        header.createCell(4).setCellValue("Holder_Amount");
        header.getCell(4).setCellStyle(style);

        header.createCell(5).setCellValue("Holder_Description");
        header.getCell(5).setCellStyle(style);

        header.createCell(6).setCellValue("Holder_Source");
        header.getCell(6).setCellStyle(style);
        // create data rows
        int rowCount = 1;
        DecimalFormat dfff = new DecimalFormat("#.00");
        for (Holder CRBook : listHR) {
            XSSFRow aRow = sheet.createRow(rowCount++);
            System.out.println(CRBook.getHolder_Name());
            aRow.createCell(0).setCellValue(CRBook.getHolder_Id());
            aRow.createCell(1).setCellValue(CRBook.getHolder_Date());
            aRow.createCell(2).setCellValue(CRBook.getHolder_Type());
            aRow.createCell(3).setCellValue(CRBook.getHolder_Name());
            aRow.createCell(4).setCellValue(dfff.format(CRBook.getHolder_Amount()));
            aRow.createCell(5).setCellValue(CRBook.getHolder_Description());
            aRow.createCell(6).setCellValue(CRBook.getHolder_Source());
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Save file");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().toString();
            Calendar cle = Calendar.getInstance();
            int dd = cle.get(Calendar.DATE);
            int mm = cle.get(Calendar.MONTH);
            ++mm;
            int year = cle.get(Calendar.YEAR);
            String curDate = "[" + mm + "-" + dd + "-" + year + "].xlsx";

            String FilePath = filename.concat("\\Holder").concat(curDate);
            //Write the workbook in file system
            FileOutputStream out = null;

            out = new FileOutputStream(new File(FilePath));

            try {
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SuccessMes = e.toString();
            }
            SuccessMes = "Success";
        } else {
            System.out.println("No Selection ");
            SuccessMes = "FileNotSelect";
            // write here apple else to whole
        }

        logger.info("Writesheet.xlsx written successfully");
        return SuccessMes;
    }

    @Override
    @Transactional
    public String holderReRecordExcel(XSSFWorkbook workbook, String fromDate, String toDate) throws FileNotFoundException {

        logger.info("-------------++download holder record++------------");
        String SuccessMes = "none";
        List<HolderRecord> listHRR = holderService.HolderListForExl(getPrincipal(), fromDate, toDate);

        XSSFSheet sheet = workbook.createSheet("sheet 1");
        sheet.setDefaultColumnWidth(20);

        // create style for header cells
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);

        // create header row
        XSSFRow header = sheet.createRow(0);

        header.createCell(0).setCellValue("Holder_Id");
        header.getCell(0).setCellStyle(style);

        header.createCell(1).setCellValue("Holder_Date");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("Holder_Name");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("Holder_Withdraw");
        header.getCell(3).setCellStyle(style);

        header.createCell(4).setCellValue("Holder_Balance");
        header.getCell(4).setCellStyle(style);

        header.createCell(5).setCellValue("Holder_SourceName");
        header.getCell(5).setCellStyle(style);

        header.createCell(6).setCellValue("Holder_SourceBalance");
        header.getCell(6).setCellStyle(style);

        header.createCell(7).setCellValue("Holder_Description");
        header.getCell(7).setCellStyle(style);
        // create data rows
        int rowCount = 1;
        DecimalFormat dff = new DecimalFormat("#.00");
        for (HolderRecord CRBook : listHRR) {
            XSSFRow aaRow = sheet.createRow(rowCount++);
            System.out.println(CRBook.getHolder_Name());
            aaRow.createCell(0).setCellValue(CRBook.getHolder_Id());
            aaRow.createCell(1).setCellValue(CRBook.getHolder_Date());
            aaRow.createCell(2).setCellValue(CRBook.getHolder_Name());
            aaRow.createCell(3).setCellValue(dff.format(CRBook.getHolder_Withdraw()));
            aaRow.createCell(4).setCellValue(dff.format(CRBook.getHolder_Balance()));
            aaRow.createCell(5).setCellValue(CRBook.getHolder_SourceName());
            aaRow.createCell(6).setCellValue(dff.format(CRBook.getHolder_SourceBalance()));
            aaRow.createCell(7).setCellValue(CRBook.getHolder_Description());
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Save file");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().toString();
            String from_Date = fromDate.replaceAll("/", "-");
            String to_Date = toDate.replaceAll("/", "-");
            String fnnn = "[" + from_Date + "," + to_Date + "].xlsx";

            String FilePath = filename.concat("\\HolderRecord").concat(fnnn);
            //Write the workbook in file system
            FileOutputStream out = null;

            out = new FileOutputStream(new File(FilePath));

            try {
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SuccessMes = e.toString();
            }
            SuccessMes = "Success";
        } else {
            System.out.println("No Selection ");
            SuccessMes = "FileNotSelect";
            // write here apple else to whole
        }

        logger.info("Writesheet.xlsx written successfully");
        return SuccessMes;
    }

    private String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

}