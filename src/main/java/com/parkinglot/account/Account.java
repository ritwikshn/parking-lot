package com.parkinglot.account;

import com.parkinglot.privilege.GetUserPasswordSystemPrivilege;
import com.parkinglot.privilege.GetUserPasswordSystemPrivilege.IGetUserPasswordPrivilegeFulfiller;

import java.util.Optional;

public class Account implements IGetUserPasswordPrivilegeFulfiller {
    private String username;
    private String password;
    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    @Override
    public Optional<String> getPassword(Account requester) {
        if(!requesterHasPrivilege(new GetUserPasswordSystemPrivilege(), requester)){
            return Optional.empty();
        }
        return Optional.of(password);
    }
}
