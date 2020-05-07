package com.vktechnology.naagu.controller;


import com.google.gson.Gson;
import com.vktechnology.naagu.dao.ExpensesDao;
import com.vktechnology.naagu.models.Expenses;
import com.vktechnology.naagu.service.DebitService;
import com.vktechnology.naagu.service.ExpensesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by HOME on 11/22/2015.
 */
@Controller
public class ExpensesController extends MultiActionController{

    private static final Logger log = Logger.getLogger(ExpensesController.class.getName());
        
    @Autowired
    private ExpensesService expensesService;
    
    @Autowired
    private DebitService debitService;
    
    @RequestMapping(value = "/expenses", method = RequestMethod.GET)
    public String expenses() {
        System.out.println("-------------++expenses++------------");
        return expensesService.showExpenses();
    }

    @RequestMapping(value = "/expensesMulty", method = RequestMethod.GET)
    public String expensesMulty() {
        System.out.println("-------------++expensesMulty++------------");
        return "redirect:/htmlpages/ExpensesMulty.html";
    }
    
    
    @RequestMapping(value = "/expensesList", method = RequestMethod.GET)
    public ModelAndView expensesList(HttpServletRequest request) {
        ModelAndView model = new ModelAndView("expensesResult");        
        return model;
    }

    @RequestMapping(value = "/holderMoneyList", method = RequestMethod.GET)
    public @ResponseBody String holderMoneyList(HttpServletResponse response, HttpServletRequest request) throws Exception{
        System.out.println("-------------++ajax expenses call++------------");
        List<String> onlysource = expensesService.holderMoney_ListOnly(getPrincipal());

        Gson gson = new Gson();
        String js = gson.toJson(onlysource);
        System.out.println("---only holderNameListOnly"+js);
        return js;
    }
    
    @RequestMapping(value = "/ListByOnlineType", method = RequestMethod.GET)
    public @ResponseBody String listByOnlineType(@RequestParam("OnlineType") String OnlineType,  HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println("-- ListByOnlineType --"+OnlineType+"--");      
        List<String> onlineonly = expensesService.listByOnlineType(OnlineType);
        Gson gson = new Gson();
        String js = gson.toJson(onlineonly);
        System.out.println("---only onlie kist"+js);
        return js;
    }

   @RequestMapping(value = "/deleteExpenses", method = RequestMethod.GET)
    public @ResponseBody String deleteExpenses(@RequestParam("dle_Id") String Dle_Id, @RequestParam("dle_amount") String Dle_amount, @RequestParam("dle_source") String Dle_source, HttpServletRequest request, HttpServletResponse response) throws Exception{
        
       
        System.out.println("--delete expense--"+Dle_Id+"--"+Dle_amount+"--"+Dle_source);
      
        String delMessage = expensesService.deleteExpensesRow(Dle_Id, Dle_amount, Dle_source, getPrincipal());         
        Gson gson = new Gson();
        String deleteMessage = gson.toJson(delMessage);
        return deleteMessage;
    }

    @RequestMapping(value = "/jsonExpenses", method = RequestMethod.GET,  produces="application/json")
    // @ResponseBody
    public ModelAndView jsonOb(){
        System.out.println("--json expense--");

        
        ModelAndView model = new ModelAndView("redirect:/htmlpages/jsonOB.html");

        JsonObject result = Json.createObjectBuilder().add("name", "raaju").build();


        System.out.print(result);

        model.addObject("jsonList", result);
        return model;
    }
    
    
    @RequestMapping(value = "/expensesCurrentDay", method = RequestMethod.GET)
    public ModelAndView expensesCurrentDay(HttpServletRequest request) {
        System.out.println("-------------++ expensesCurrentDay++------------");
        System.out.println("CREATE -- create list------:-"+getPrincipal());
        List<Expenses> listCD = expensesService.listOFCurrentDay(getPrincipal());
        System.out.println("create list current day: "+listCD);
        ModelAndView model = new ModelAndView("expensesResult");
        model.addObject("ExpensesList", listCD);        
        return model;    
        }
    
    @RequestMapping(value = "/dateByList", method = RequestMethod.GET,		produces="application/json")
    public @ResponseBody String dateByList(@RequestParam("date1") String date1, HttpServletRequest request) {
        String dbDate = expensesService.dateConvertionFromJSPToDB(date1);
        List<Expenses> listDate = expensesService.listOfDate(getPrincipal(), dbDate);        
        
        for (Expenses ax : listDate) {           
            String sd = ax.getDate();
            String jsp = expensesService.dateConvertionFromDBToJSP(sd);            
            ax.setDate(jsp);            
        }       
        Gson gson = new Gson();
        String jsforDate = gson.toJson(listDate);        
        return jsforDate;      
    }
    
    @RequestMapping(value = "/betweenDates", method = RequestMethod.GET)
    public @ResponseBody String bwtweenDates(HttpServletRequest request, @RequestParam("date1") String fromdate, @RequestParam("date2") String todate) {
        fromdate = expensesService.dateConvertionFromJSPToDB(fromdate);
        todate = expensesService.dateConvertionFromJSPToDB(todate);       
                
        List<Expenses> listBTD = expensesService.listOfTwoDate(getPrincipal(), fromdate, todate);        
        for (Expenses ax : listBTD) {
            String sd = ax.getDate();
            String jsp = expensesService.dateConvertionFromDBToJSP(sd);
            ax.setDate(jsp);
        }
        Gson gson = new Gson();
        String forTwoDate = gson.toJson(listBTD);
        return forTwoDate;   
        }
    
    @RequestMapping(value = "/allDataOfExpence", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody String allDataOfExpence(@RequestParam("date1") String date1, HttpServletRequest request){
        HttpSession session = request.getSession();               
        List<Expenses> listUsUs = expensesService.listOfExpenses1(getPrincipal());        
        for (Expenses ax : listUsUs) {
            String sd = ax.getDate();
            String jsp = expensesService.dateConvertionFromDBToJSP(sd);            
            ax.setDate(jsp);
        }
        Gson gson = new Gson();
        String forAllDate = gson.toJson(listUsUs);
        return forAllDate;
    }
    
    @RequestMapping(value = "/amountAvailability", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody String amountAvailability(HttpServletRequest request, @RequestParam("source") String Asource, @RequestParam("amount") String Aamount) {
        System.out.println(Asource+"-------------++ amountAvailability++------------"+Aamount);
        //float amount = Float.parseFloat(Aamount);
        BigDecimal amount = new BigDecimal(Aamount);
        System.out.println("CREATE -- create list------:-"+getPrincipal());
        String available = expensesService.availablityCheck(getPrincipal(), Asource, amount);
        Gson gson = new Gson();
        String forTwoDate = gson.toJson(available);
        System.out.println("message: "+forTwoDate);
        return forTwoDate;   
        }
    
    @RequestMapping(value="/addExpenses", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody String  addExpensesd(HttpServletRequest request, HttpServletResponse response) throws Exception{	
    	String ddd = request.getParameter("Select");    	
    	Expenses expenses = new Expenses();    	
        String fomDate = request.getParameter("Date");        
        String dateDb= expensesService.dateConvertionFromJSPToDB(fomDate);        
        expenses.setDate(dateDb);
    	expenses.setUnit_Type(request.getParameter("unitType"));
    	expenses.setUnit_Name(request.getParameter("unitName"));
    	expenses.setUnit_Tag(request.getParameter("unitNameTag"));    	
    	expenses.setUnit_price(new BigDecimal(request.getParameter("Unit_price")));
    	expenses.setQuantity(request.getParameter("Quantity"));    	
    	expenses.setAmount(new BigDecimal(request.getParameter("Amount")));
    	expenses.setSource_Name(request.getParameter("Select"));
    	expenses.setSold_company(request.getParameter("Sold_company"));
    	expenses.setArea(request.getParameter("Area"));
    	expenses.setWhome_to(request.getParameter("Whome_to"));
    	expenses.setDescription(request.getParameter("Description"));
        expenses.setTax(request.getParameter("Tax"));
        expenses.setAmount_type(request.getParameter("AmountType"));
    	
        expenses.setUser(getPrincipal());                 // add user name to expenses object
        String Message = expensesService.saveExpenses(expenses);         
        Gson gson = new Gson();
        String forT12 = gson.toJson(Message);
         
        return forT12;
	}
    
	private String getPrincipal(){
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
 
        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }
}
