package com.yuanjy.logdb.pojo;

public class Logdb {
    private String module;
    private Integer uid;
    private String apiName;
    private String requestMethod;
    private String paramJson;
    private String response;
    private Integer status;
    private Integer errCode;
    private String errMessage;
    private String userIp;
    private String serverIp;
    private Integer endTime;
    private Integer spendTime;
    private String system;
    private String brand;
    private String model;
    private String version;
    private String appVersion;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String toString() {
        return "Logdb{" +
                "module='" + module + '\'' +
                ", uid=" + uid +
                ", apiName='" + apiName + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", paramJson='" + paramJson + '\'' +
                ", response='" + response + '\'' +
                ", status=" + status +
                ", errCode=" + errCode +
                ", errMessage='" + errMessage + '\'' +
                ", userIp='" + userIp + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", endTime=" + endTime +
                ", spendTime=" + spendTime +
                ", system='" + system + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }
}
