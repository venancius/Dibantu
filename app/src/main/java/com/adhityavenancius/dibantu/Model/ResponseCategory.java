package com.adhityavenancius.dibantu.Model;

import java.util.List;

public class ResponseCategory{
	private List<AllcategoryItem> allcategory;
	private String error;
	private String message;

	public void setAllcategory(List<AllcategoryItem> allcategory){
		this.allcategory = allcategory;
	}

	public List<AllcategoryItem> getAllcategory(){
		return allcategory;
	}

	public void setError(String error){
		this.error = error;
	}

	public String getError(){
		return error;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"ResponseCategory{" + 
			"allcategory = '" + allcategory + '\'' + 
			",error = '" + error + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}