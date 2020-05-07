package com.vktechnology.naagu.controller;

import java.math.*;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.google.gson.Gson;
import com.vktechnology.naagu.dao.CreditDao;
import com.vktechnology.naagu.models.Credit;
import com.vktechnology.naagu.models.CreditClearRecord;
import com.vktechnology.naagu.models.CreditRecord;
import com.vktechnology.naagu.models.Debit;
import com.vktechnology.naagu.models.Expenses;
import com.vktechnology.naagu.service.CreditService;
import com.vktechnology.naagu.service.DebitService;
import java.util.logging.Logger;

@Controller
public class CreditController extends MultiActionController {

    private static final Logger log = Logger.getLogger(CreditController.class.getName());    
    
    @Autowired
    private CreditService creditService;

    @Autowired
    private DebitService debitService;

    @RequestMapping(value = "/credit", method = RequestMethod.GET)
    public String credit() {
        return creditService.showCredit();
    }

    @RequestMapping(value = "/creditClearDynamic", method = RequestMethod.GET)
    public String creditClearDynamic() {
        return "redirect:/htmlpages/creditClearDynamic.html";
    }

    @RequestMapping(value = "/creditList", method = RequestMethod.GET)
    public @ResponseBody
    String creditList(HttpServletRequest request) {

        List<Credit> listCredit = creditService.listOfCredit(getPrincipal());        
        Gson gson = new Gson();
        String creditListJS = gson.toJson(listCredit);        
        return creditListJS;
    }

    @RequestMapping(value = "/addCredit", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String addCredit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Credit credit = new Credit();
        credit.setCredit_Date(request.getParameter("Credit_Date"));
        credit.setCredit_Name(request.getParameter("creditname"));
        //credit.setCredit_Amount(Float.parseFloat(request.getParameter("Credit_Amount")));
        credit.setCredit_Amount(new BigDecimal(request.getParameter("Credit_Amount")));
        credit.setCredit_Description(request.getParameter("Credit_Description"));
        credit.setUser(getPrincipal());
        String Message = creditService.saveCredit(credit);
        System.out.println("save credt success--" + Message);

        if (Message.indexOf("Success") > -1) {
            creditService.saveCreditRecord(credit);
            System.out.println("-----save Credit Record-------");
        }

        Gson gson = new Gson();
        String forT12 = gson.toJson(Message);
        return forT12;
    }

    @RequestMapping(value = "/crediterNamesList", method = RequestMethod.GET)
    public @ResponseBody
    String crediterNamesList(HttpServletResponse response, HttpServletRequest request) throws Exception {        
        List<String> onlysource = creditService.crediterList(getPrincipal());

        Gson gson = new Gson();
        String js = gson.toJson(onlysource);
        // return new ModelAndView("redirect:/htmlpages/source.html", "command", new Source());
        return js;
    }

    @RequestMapping(value = "/clearLentAmount", method = RequestMethod.GET)
    public @ResponseBody
    String clearLentAmount(HttpServletResponse response, HttpServletRequest request,
            @RequestParam("sourceCC") String Credit_name,
            @RequestParam("amountCC") String Credit_amount,
            @RequestParam("debitName") String Debit_name,
            @RequestParam("debitAmount") String Debit_amount,
            @RequestParam("curr_date") String Curr_date,
            @RequestParam("des_Cle") String Des_Cle) throws Exception {
        String Message = "";
        BigDecimal Zero = new BigDecimal("0");        
        //float C_amount = Float.parseFloat(Credit_amount);
        BigDecimal C_amount = new BigDecimal(Credit_amount);
        //float D_amount = Float.parseFloat(Debit_amount);
        BigDecimal D_amount = new BigDecimal(Debit_amount);
        //float remaing_Amount = 0.0f;
        BigDecimal remaing_Amount = new BigDecimal("0");
        //float diff = D_amount - C_amount;
        BigDecimal diff = D_amount.subtract(C_amount);
        System.out.println("diffff : " + diff);
        //if(C_amount == 0 || D_amount == 0){
        if (C_amount.compareTo(Zero) == 0 || D_amount.compareTo(Zero) == 0) {
            Message = "NoNeedAction";
        } else {
            Message = creditService.clearLentAmountS(Debit_name, Credit_name, diff, remaing_Amount, getPrincipal());
        }
        if (Message.indexOf("CreditMore") > -1 || Message.indexOf("DebitMore") > -1 || Message.indexOf("EquallMore") > -1) {
            CreditClearRecord record = new CreditClearRecord();
            record.setCreditClear_Date(Curr_date);
            record.setCreditClear_name(Credit_name);
            record.setCreditClear_Amount(C_amount);
            record.setCreditClear_Source(Debit_name);
            record.setCreditClear_Des(Des_Cle);
            record.setCreditClear_User(getPrincipal());
            creditService.saveCreditClearRecord(record);            
        }
        Gson gson = new Gson();
        String jsMessage = gson.toJson(Message);        
        return jsMessage;
    }

    @RequestMapping(value = "/creditRecord", method = RequestMethod.GET)
    public @ResponseBody
    String creditRecord(HttpServletRequest request) {        
        List<CreditRecord> record = creditService.recordOfCredit(getPrincipal());        
        Gson gson = new Gson();
        String js = gson.toJson(record);        
        return js;
    }

    @RequestMapping(value = "/ClearRecordForCredit", method = RequestMethod.GET)
    public @ResponseBody
    String CreditClearRecord(HttpServletRequest request) {        
        List<CreditClearRecord> record145 = creditService.recordOfClearRecord(getPrincipal());        
        Gson gson = new Gson();
        String js123 = gson.toJson(record145);        
        return js123;
    }

    @RequestMapping(value = "/creditClear", method = RequestMethod.GET)
    public String creditClear() {        
        return "redirect:/htmlpages/creditClear.html";
    }

    @RequestMapping(value = "/creditClearFirst", method = RequestMethod.GET)
    public @ResponseBody
    String creditClearFirst(HttpServletResponse response, HttpServletRequest request) throws Exception {        
        List<Debit> CreditFullList = debitService.listOfDebit(getPrincipal());
        Gson gson = new Gson();
        String jsFull = gson.toJson(CreditFullList);

        return jsFull;
    }

    @RequestMapping(value = "/saveCredtClearDynamic", method = RequestMethod.GET)
    public @ResponseBody
    String saveCredtClearDynamic(HttpServletResponse response, HttpServletRequest request,
            @RequestParam("Credit_ClearDate") String Credit_ClearDate,
            @RequestParam("CreditClear_name") String CreditClear_name,
            @RequestParam("Credit_ClearAmount") BigDecimal Credit_ClearAmount,
            @RequestParam("Credit_ClearSource") String Credit_ClearSource,
            @RequestParam("Credit_ClearDes") String Credit_ClearDes) throws Exception {
        String Message = "";        
        if (CreditClear_name.equalsIgnoreCase(Credit_ClearSource)) {
            Message = "Credit Clear Name and source name should not be same";
        } else {
            Message = creditService.clearLentAmountDynamic(CreditClear_name, Credit_ClearSource, Credit_ClearAmount, getPrincipal());
            if (Message.indexOf("SuccessHolder") > -1 || Message.indexOf("SuccessCredit") > -1) {
                CreditClearRecord creditClearRecord = new CreditClearRecord();
                creditClearRecord.setCreditClear_Date(Credit_ClearDate);
                creditClearRecord.setCreditClear_name(CreditClear_name);
                creditClearRecord.setCreditClear_Amount(Credit_ClearAmount);
                creditClearRecord.setCreditClear_Source(Credit_ClearSource);
                creditClearRecord.setCreditClear_Des(Credit_ClearDes);
                creditClearRecord.setCreditClear_User(getPrincipal());
                creditService.saveCreditClearRecord(creditClearRecord);                
            }
        }
        Gson gson = new Gson();
        String jsMessage = gson.toJson(Message);       
        return jsMessage;
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
