package com.library.app.pagination;

import java.util.List;

public class PaginatedData<T> {
    
    private final int numberOfRows;
    private final List<T> rows;

    public PaginatedData(int numberOfRows, List<T> rows) {
        this.numberOfRows = numberOfRows;
        this.rows = rows;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public List<T> getRows() {
        return rows;
    }
    
    public T getRow(int index){
        if(index >= rows.size()){
            return null;
        }
        
        return rows.get(index);
    }

    @Override
    public String toString() {
        return "PaginatedData{" + "numberOfRows=" + numberOfRows + ", rows=" + rows + '}';
    }
    
    
}
