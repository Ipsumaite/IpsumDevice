package com.ar.ipsum.ipsumapp.Resources;

/**
 * Created by QSR on 02-03-2015.
 */
public class Subscription {
    private String id;
    private int ContractTerm;
    private String ContractNumber;
    private String CreatedDate;
    private String Description;
    private String EndDate;
    private String StartDate;
    private String Status;

    public Subscription(){
        this.id="";
        this.ContractTerm=0;
        this.ContractNumber="";
        this.CreatedDate="";
        this.Description="";
        this.EndDate="";
        this.StartDate="";
        this.Status="";
    }

    public Subscription(String id,int ContractTerm,String ContractNumber,String CreatedDate,String Description,String EndDate,String StartDate,String Status){
        this.id=id;
        this.ContractTerm=ContractTerm;
        this.ContractNumber=ContractNumber;
        this.CreatedDate=CreatedDate;
        this.Description=Description;
        this.EndDate=EndDate;
        this.StartDate=StartDate;
        this.Status=Status;
    }
}
