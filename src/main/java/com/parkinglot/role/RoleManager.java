package com.parkinglot.role;

import com.parkinglot.account.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public enum RoleManager {
    INSTANCE;
    private static final String ROLE_MANAGER_USER_NAME = "rolemanager";
    private static final String ROLE_MANAGER_USER_PASSWORD = "rolemanager123";
    private final Role ROLE_MANAGER_USER_ROLE = SystemRole.ADMIN;
    private Account roleManagerUser = new Account(ROLE_MANAGER_USER_NAME, ROLE_MANAGER_USER_PASSWORD);
    private Map<String, Role> userRoleMap = new HashMap<>();
    private Map<String, String> userPasswordMap = new HashMap<>();
    RoleManager(){
        assignRoleToUser(ROLE_MANAGER_USER_NAME, ROLE_MANAGER_USER_PASSWORD, ROLE_MANAGER_USER_ROLE);
    }
    public static RoleManager getInstance(){
        return INSTANCE;
    }
    private boolean assignRoleToUser(String username, String userPassword, Role role){
        if(username == null ||
                userPassword == null ||
                role == null){
            return false;
        }
        userRoleMap.put(username, role);
        userPasswordMap.put(username, userPassword);
        return true;
    }
    public boolean assignRoleToUser(Account user, Role role, String rolePassKey){
        if(user == null || role == null || !role.isPassKeyMatch(rolePassKey)){
            return false;
        }
        return assignRoleToUser(user.getUsername(),
                user.getPassword(roleManagerUser).orElseGet(() -> null),
                role);
    }
    private boolean isUserPasswordMatch(String username, String providedPassword){
        if(!userPasswordMap.containsKey(username) || providedPassword == null) return false;
        return Objects.equals(userPasswordMap.get(username), providedPassword);
    }
    public Optional<Role> getUserRole(Account user){
        if(user == roleManagerUser) return Optional.of(ROLE_MANAGER_USER_ROLE);
        String username = user.getUsername();
        String userPassword = user.getPassword(roleManagerUser).orElseGet(()->null);
        if(!isUserPasswordMatch(username, userPassword) ||
                !userRoleMap.containsKey(user.getUsername())){
            return Optional.empty();
        }
        return Optional.of(userRoleMap.get(username));
    }
}
