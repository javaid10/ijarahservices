package com.ijarah.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CustomErrors {
	 public static Map<String, ArrayList<String>> getAssignedError() {
	        Map<String, ArrayList<String>> assignIds = new HashMap<String, ArrayList<String>>();


	        assignIds.put("ERR_60000", new ArrayList<String>());
	        assignIds.get("ERR_60000").add("Success");
	        assignIds.get("ERR_60000").add("");
	        
	        
	        
	        assignIds.put("ERR_66001", new ArrayList<String>());
	        assignIds.get("ERR_66001").add("Mobile Number you have provided is already registered");
	        assignIds.get("ERR_66001").add("");

	        assignIds.put("ERR_66013", new ArrayList<String>());
	        assignIds.get("ERR_66013").add("Mobile Number is not registered with National ID");
	        assignIds.get("ERR_66013").add("");
	        
	        assignIds.put("ERR_66015", new ArrayList<String>());
	        assignIds.get("ERR_66015").add("The ID Is Not Found at NIC");
	        assignIds.get("ERR_66015").add("");
	        
	        assignIds.put("ERR_66016", new ArrayList<String>());
	        assignIds.get("ERR_66016").add("The Iqama Number Is Not Found at NIC");
	        assignIds.get("ERR_66016").add("");
	        
	        assignIds.put("ERR_66017", new ArrayList<String>());
	        assignIds.get("ERR_66017").add("Not Authorized");
	        assignIds.get("ERR_66017").add("");
	        
	        assignIds.put("ERR_66018", new ArrayList<String>());
	        assignIds.get("ERR_66018").add("The National ID format is not valid");
	        assignIds.get("ERR_66018").add("");
	        
	        assignIds.put("ERR_66019", new ArrayList<String>());
	        assignIds.get("ERR_66019").add("Date Of Birth does not match NIC records");
	        assignIds.get("ERR_66019").add("");
	        
	        assignIds.put("ERR_66020", new ArrayList<String>());
	        assignIds.get("ERR_66020").add("The Iqama Number format is not valid");
	        assignIds.get("ERR_66020").add("");
	        
	        assignIds.put("ERR_66022", new ArrayList<String>());
	        assignIds.get("ERR_66022").add("Customer Life status is not Alive, Your Registration Process has Canceled");
	        assignIds.get("ERR_66022").add("");
	        
	        assignIds.put("ERR_66032", new ArrayList<String>());
	        assignIds.get("ERR_66032").add("National ID/Iqama is required");
	        assignIds.get("ERR_66032").add("");
	        
	        assignIds.put("ERR_66007", new ArrayList<String>());
	        assignIds.get("ERR_66007").add("Password is required");
	        assignIds.get("ERR_66007").add("");
	        
	        assignIds.put("ERR_66008", new ArrayList<String>());
	        assignIds.get("ERR_66008").add("Your Account has been locked");
	        assignIds.get("ERR_66008").add("");

	        assignIds.put("ERR_66009", new ArrayList<String>());
	        assignIds.get("ERR_66009").add("Missing IBAN number");
	        assignIds.get("ERR_66009").add("");
	        
	        assignIds.put("ERR_66010", new ArrayList<String>());
	        assignIds.get("ERR_66010").add("Please upload iban file");
	        assignIds.get("ERR_66010").add("");

	    
	        assignIds.put("ERR_60101", new ArrayList<String>());
	        assignIds.get("ERR_60101").add("Something went wrong Due to technical difficulties, we are unable to process your application");
	        assignIds.get("ERR_60101").add("عذراً, حدث خطأ ما نواجه حاليا مشاكل تقنية، نعمل على حلها في اسرع وقت  ");

	    
	        assignIds.put("ERR_60102", new ArrayList<String>());
	        assignIds.get("ERR_60102").add("Something went wrong Please make sure the entered details are correct and try again later");
	        assignIds.get("ERR_60102").add(" عذراً, حدث خطأ ماً يرجى التأكد من البيانات المدخلة ومعاودة المحاولة لاحقا  ");


	    
	        assignIds.put("ERR_60103", new ArrayList<String>());
	        assignIds.get("ERR_60103").add("Sorry We apologize for not accepting your application number: , Due to not fulfilling the Age Requirement");
	        assignIds.get("ERR_60103").add("عذراً لعدم قبول طلبك رقم: بسبب أن العمر لايتوافق مع سياسة التمويل ");


	        assignIds.put("ERR_60104", new ArrayList<String>());
	        assignIds.get("ERR_60104").add("Sorry We apologize for not accepting your application number: , Due to high financial obligations ");
	        assignIds.get("ERR_60104").add("عذراًلعدم قبول طلبك رقم: لوجود التزامات عالية في سجلك الإئتماني");



	        assignIds.put("ERR_60105", new ArrayList<String>());
	        assignIds.get("ERR_60105").add("Sorry We apologize for not accepting your application number: , Due to not fulfilling the Credit Policy Requirement");
	        assignIds.get("ERR_60105").add("عذراًلعدم قبول طلبك رقم: لعدم إستيفاء متطلبات التمويل");


	        assignIds.put("ERR_60106", new ArrayList<String>());
	        assignIds.get("ERR_60106").add("Sorry Your last application was declined. You can apply again on : ");
	        assignIds.get("ERR_60106").add("عذرا لديك طلب مرفوض سابقا. يمكنك اعادة التقديم في");


	        assignIds.put("ERR_60107", new ArrayList<String>());
	        assignIds.get("ERR_60107").add("Sorry You have reached the maximum allowed attempts. You can apply again on : ");
	        assignIds.get("ERR_60107").add("عذراًتم تجاوز عدد المحاولات المسموحة. يمكنك اعادة التقديم في");


	        return assignIds;
	    }


}
