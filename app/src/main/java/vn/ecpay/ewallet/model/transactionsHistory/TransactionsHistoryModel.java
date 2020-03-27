package vn.ecpay.ewallet.model.transactionsHistory;

import java.io.Serializable;

public class TransactionsHistoryModel implements Serializable {
    public boolean isSection;
    private String senderName;
    private String senderAccountId;
    private String receiverName;
    private String receiverAccountId;
    private String transactionType;
    private String transactionContent;
    private String transactionStatus;
    private String transactionAmount;
    private String transactionDate;
    private String cashLogType;
    private String receiverPhone;
    private String senderPhone;
    private String transactionSignature;
    private String cashEnc;

    public TransactionsHistoryModel(String senderName, String senderAccountId, String receiverName, String receiverAccountId, String transactionType, String transactionContent, String transactionStatus, String transactionAmount, String transactionDate, String cashLogType, String receiverPhone, String senderPhone, String transactionSignature, String cashEnc) {
        this.senderName = senderName;
        this.senderAccountId = senderAccountId;
        this.receiverName = receiverName;
        this.receiverAccountId = receiverAccountId;
        this.transactionType = transactionType;
        this.transactionContent = transactionContent;
        this.transactionStatus = transactionStatus;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
        this.cashLogType = cashLogType;
        this.receiverPhone = receiverPhone;
        this.senderPhone = senderPhone;
        this.transactionSignature = transactionSignature;
        this.cashEnc = cashEnc;
    }

    public TransactionsHistoryModel(boolean isSection, String fullName) {
        this.isSection = isSection;
        this.transactionDate = fullName;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionContent() {
        return transactionContent;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    // Setter Methods

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderAccountId(String senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverAccountId(String receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setTransactionContent(String transactionContent) {
        this.transactionContent = transactionContent;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCashLogType() {
        return cashLogType;
    }

    public void setCashLogType(String cashLogType) {
        this.cashLogType = cashLogType;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(String transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    public String getCashEnc() {
        return cashEnc;
    }

    public void setCashEnc(String cashEnc) {
        this.cashEnc = cashEnc;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }
}
