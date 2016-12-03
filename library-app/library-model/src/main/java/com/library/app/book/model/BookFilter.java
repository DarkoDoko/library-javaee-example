package com.library.app.book.model;

import com.library.app.pagination.filter.GenericFilter;

public class BookFilter extends GenericFilter{
    
    private String title;
	private Long categoryId;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public String toString() {
		return "BookFilter [title=" + title + ", categoryId=" + categoryId + ", toString()=" + super.toString() + "]";
	}
    
}
