/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author WitherDragon
 */
public class AccountSession 
{
    private Account loggedInAccount;
    private boolean isLoggedIn;

    public AccountSession() {
        this.loggedInAccount = null;
        this.isLoggedIn = false;
    }

    public void setLoggedInAccount(Account loggedInAccount) {
        this.loggedInAccount = loggedInAccount;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void logout() {
        loggedInAccount = null;
        isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public Account getLoggedInAccount() {
        return loggedInAccount;
    }

}
