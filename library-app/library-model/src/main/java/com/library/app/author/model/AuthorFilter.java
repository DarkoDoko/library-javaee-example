package com.library.app.author.model;

import com.library.app.pagination.filter.GenericFilter;

public class AuthorFilter extends GenericFilter{
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AuthorFilter{" + "name=" + name + '}';
    }
     
}
