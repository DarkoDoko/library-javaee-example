package com.library.app.pagination.filter;

public class GenericFilter {
    
    private PaginationData paginationData;

    public GenericFilter() {
    }

    public GenericFilter(PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public PaginationData getPaginationData() {
        return paginationData;
    }

    public void setPaginationData(PaginationData paginationData) {
        this.paginationData = paginationData;
    }
    
    public boolean hasPaginationData(){
        return paginationData != null;
    }
    
    public boolean hasOrderField(){
        return this.hasPaginationData() && this.getPaginationData().getOrderField() != null;
    }

    @Override
    public String toString() {
        return "GenericFilter{" + "paginationData=" + paginationData + '}';
    }
    
}
