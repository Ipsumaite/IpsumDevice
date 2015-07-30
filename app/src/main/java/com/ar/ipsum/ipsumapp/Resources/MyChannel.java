package com.ar.ipsum.ipsumapp.Resources;

/**
 * Created by QSR on 02-06-2015.
 */
public class MyChannel {
    private boolean active;
    private String description;
    private String accountid;
    private String name;
    private String id;
    private boolean premium;
    private boolean visible;

    public MyChannel(){
        active=true;
        description="";
        accountid= "";
        name="";
        id="";
        premium=false;
        visible=true;
    }

    public MyChannel(boolean active, String description, String accountid, String name, String id, boolean premium, boolean visible) {
        this.active = active;
        this.description = description;
        this.accountid = accountid;
        this.name = name;
        this.id = id;
        this.premium = premium;
        this.visible = visible;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

