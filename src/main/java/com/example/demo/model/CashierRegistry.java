package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cashier_registry")
public class CashierRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer twentyDollarCount;

    private Integer tenDollarCount;

    private Integer fiveDollarCount;

    private Integer twoDollarCount;

    private Integer oneDollarCount;

    public void setZeroBillsForEachDenomination() {
        twentyDollarCount = 0;
        tenDollarCount = 0;
        fiveDollarCount = 0;
        twoDollarCount = 0;
        oneDollarCount = 0;
    }

    public String getState() {
        return String.format("$%d %d %d %d %d %d", getTotal(), getTwentyDollarCount(), getTenDollarCount(),
                getFiveDollarCount(), getTwoDollarCount(), getOneDollarCount());
    }

    public Integer getTotal() {
        return getTwentyDollarTotal() + getTenDollarTotal()
                + getFiveDollarTotal()+ getTwoDollarTotal() + getOneDollarTotal();
    }

    public Integer getTwentyDollarTotal() {
        return twentyDollarCount * 20;
    }

    public Integer getTenDollarTotal() {
        return tenDollarCount * 10;
    }

    public Integer getFiveDollarTotal() {
        return fiveDollarCount * 5;
    }

    public Integer getTwoDollarTotal() {
        return twoDollarCount * 2;
    }

    public Integer getOneDollarTotal() {
        return oneDollarCount * 1;
    }

    public CashierRegistry addTwentyDollarBill(Integer count) {
        twentyDollarCount+= count;
        return this;
    }

    public CashierRegistry addTenDollarBill(Integer count) {
        tenDollarCount+= count;
        return this;
    }

    public CashierRegistry addFiveDollarBill(Integer count) {
        fiveDollarCount+= count;
        return this;
    }

    public CashierRegistry addTwoDollarBill(Integer count) {
        twoDollarCount+= count;
        return this;
    }

    public CashierRegistry addOneDollarBill(Integer count) {
        oneDollarCount+= count;
        return this;
    }
}
