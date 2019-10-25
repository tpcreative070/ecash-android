package vn.ecpay.ewallet.model.transactionsHistory;

public class TransactionsHistoryModel {
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

    public TransactionsHistoryModel(String senderName,
                                    String senderAccountId,
                                    String receiverName,
                                    String receiverAccountId,
                                    String transactionType,
                                    String transactionStatus,
                                    String transactionAmount,
                                    String transactionDate) {
        this.senderAccountId = senderAccountId;
        this.senderName = senderName;
        this.receiverAccountId = receiverAccountId;
        this.receiverName = receiverName;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
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
}
