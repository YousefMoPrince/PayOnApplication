package com.megabyte.payonapplication;

public class APIurl {
    public static final String BASE_URL = "http://10.78.220.109:9090/";
    public static final String LOGIN_URL = BASE_URL + "api/LogReg/Login";
    public static final String REGISTER_URL = BASE_URL + "api/LogReg/register";
    public static final String ADMIN_LOGIN_URL = BASE_URL + "api/admins/Adlogin";
    public static final String ACCOUNT_REGISTER_URL = BASE_URL + "api/accmanager/accountreg";
    public static final String FIND_ACCOUNT = BASE_URL + "api/accmanager/findacc?accountNumber=";
    public static final String CREATE_WALLET = BASE_URL + "api/wallets/createwall";
    public static final String GET_WALLET = BASE_URL + "api/wallets/wallet?userId=";
    public static final String Deposit = BASE_URL + "api/transactions/deposit";
    public static final String Withdraw = BASE_URL + "api/transactions/withdraw";
    public static final String Transfer = BASE_URL + "api/transactions/transfer";
    public static final String WITHDRAW_STATUS = BASE_URL + "api/transactions/{transactionId}/withdrawstatus";
    public static final String TRANSFER_STATUS = BASE_URL + "api/transactions/{transactionId}/transferstatus";






}
