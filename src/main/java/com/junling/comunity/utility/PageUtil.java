package com.junling.comunity.utility;

public class PageUtil {

    private int currentPage = 1;
    private int totalRows;
    private int rowsPerPage = 10;
    private String path;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage < 1) {
            this.currentPage = 1;
            return;
        }
        this.currentPage = currentPage;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {

        if (rowsPerPage < 1 || rowsPerPage > 10) {
            this.rowsPerPage = 10;
            return;
        }
        this.rowsPerPage = rowsPerPage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset(){
       return (currentPage-1) * rowsPerPage;
    }

    public int getTotalPages(){
        if (totalRows%rowsPerPage == 0) {
            return totalRows/rowsPerPage;
        }

        return totalRows/rowsPerPage + 1;
    }

    public int getFromPage() {

        return (currentPage - 2) > 0 ? (currentPage-2) : 1;
    }

    public int getToPage(){
        return (currentPage + 2) < getTotalPages() ? (currentPage + 2) : getTotalPages();
    }
}
