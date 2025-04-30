package hu.ksh.idgs.worklist.dto;

import java.time.OffsetDateTime;

public class MajaDataCollection {

    private String id;
    private String code;
    private Integer year;
    private String period;
    private String description;
    private Integer maxFailures;
    private OffsetDateTime censusStartDate;
    private OffsetDateTime censusEndDate;
    private Boolean active;

    public MajaDataCollection() {
    }

    public MajaDataCollection(String id, String code, Integer year, String period, String description, Integer maxFailures, OffsetDateTime censusStartDate, OffsetDateTime censusEndDate, Boolean active) {
        this.id = id;
        this.code = code;
        this.year = year;
        this.period = period;
        this.description = description;
        this.maxFailures = maxFailures;
        this.censusStartDate = censusStartDate;
        this.censusEndDate = censusEndDate;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxFailures() {
        return maxFailures;
    }

    public void setMaxFailures(Integer maxFailures) {
        this.maxFailures = maxFailures;
    }

    public OffsetDateTime getCensusStartDate() {
        return censusStartDate;
    }

    public void setCensusStartDate(OffsetDateTime censusStartDate) {
        this.censusStartDate = censusStartDate;
    }

    public OffsetDateTime getCensusEndDate() {
        return censusEndDate;
    }

    public void setCensusEndDate(OffsetDateTime censusEndDate) {
        this.censusEndDate = censusEndDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
