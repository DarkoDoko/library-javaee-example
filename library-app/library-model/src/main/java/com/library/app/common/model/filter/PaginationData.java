package com.library.app.common.model.filter;

public class PaginationData {
    private final int firstResult;
    private final int maxResults;
    private final String orderField;
    private final OrderMode orderMode;
    
    public enum OrderMode{
        ASCENDING, DESCENDING
    }

    public PaginationData(int firstResult, int maxResults, String orderField, OrderMode orderMode) {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.orderField = orderField;
        this.orderMode = orderMode;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public String getOrderField() {
        return orderField;
    }

    public OrderMode getOrderMode() {
        return orderMode;
    }
    
    public boolean isAscending(){
        return OrderMode.ASCENDING.equals(orderMode);
    }

    @Override
    public String toString() {
        return "PaginationData{" + "firstResult=" + firstResult + ", maxResults=" + maxResults +
                ", orderField=" + orderField + ", orderMode=" + orderMode + '}';
    }
}
