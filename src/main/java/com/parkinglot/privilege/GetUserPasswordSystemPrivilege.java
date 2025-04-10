package com.parkinglot.privilege;

import com.parkinglot.account.Account;

import java.util.Optional;

public final class GetUserPasswordSystemPrivilege extends SystemPrivilege {
    @Override
    public Boolean visitAdmin() {
        return true;
    }

    @Override
    public Boolean visitParkingAgent() {
        return false;
    }

    @Override
    public Boolean visitGuest() {
        return false;
    }

    public interface IGetUserPasswordPrivilegeFulfiller extends PrivilegeFulfiller{
        Optional<String> getPassword(Account requester);
    }
}
