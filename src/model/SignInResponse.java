/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author WitherDragon
 */
public class SignInResponse {
    private String localId;
    private String email;
    private String displayName;
    private String idToken;

    public SignInResponse() {}
    

    public String getLocalId() {
        return localId;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIdToken() {
        return idToken;
    }


    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    
}
