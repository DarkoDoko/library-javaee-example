package com.library.app.user.model.filter;

import com.library.app.pagination.filter.GenericFilter;
import com.library.app.user.model.User;

public class UserFilter extends GenericFilter{
    private String name;
    private User.UserType userType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User.UserType getUserType() {
        return userType;
    }

    public void setUserType(User.UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserFilter{" + "name=" + name + ", userType=" + userType + " toString()=" + super.toString() + '}';
    }
}
