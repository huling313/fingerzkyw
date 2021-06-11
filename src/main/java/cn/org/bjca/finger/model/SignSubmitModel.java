package cn.org.bjca.finger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @版权所有： 北京数字医信科技有限公司 (C) 2019
 * @类描述:
 * @版本: V1.0.0
 * @作者 huling
 * @创建时间 2020-09-02 19:04
 */
public class SignSubmitModel implements Parcelable {

    public SignSubmitModel() {

    }

    private String pdfSignId;
    private String userIdNumber;
    private String userName;
    private String userPhoto;
    private boolean isSelf;
    private String signFile;
    private String fingerprintSignFile;
    private String handwrittenSignFile;
    private String keyword;
    private String afterFile;
    private String beforeFile;
    private int readStatus;
    private String keywordId;
    private String suggestion;//勾选项
    private String signType;//1 普通签名 2 勾选签名
    private int signStatus;

    public SignSubmitModel(String pdfSignId, String userIdNumber, String userName, String keyword, String afterFile, String keywordId) {
        this.pdfSignId = pdfSignId;
        this.userIdNumber = userIdNumber;
        this.userName = userName;
        this.keyword = keyword;
        this.afterFile = afterFile;
        this.keywordId = keywordId;
    }

    public SignSubmitModel(String pdfSignId, String userIdNumber, String userName, String afterFile, int readStatus, String suggestion, String signType, int signStatus) {
        this.pdfSignId = pdfSignId;
        this.userIdNumber = userIdNumber;
        this.userName = userName;
        this.afterFile = afterFile;
        this.readStatus = readStatus;
        this.suggestion = suggestion;
        this.signType = signType;
        this.signStatus = signStatus;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pdfSignId);
        dest.writeString(userIdNumber);
        dest.writeString(userName);
        dest.writeString(userPhoto);
        dest.writeString(signFile);
        dest.writeString(fingerprintSignFile);
        dest.writeString(handwrittenSignFile);
        dest.writeString(keyword);
        dest.writeString(afterFile);
        dest.writeString(beforeFile);
        dest.writeByte((byte) (isSelf ? 1 : 0));
        dest.writeInt((readStatus));
//        dest.writeList(keywords);
        dest.writeString(keywordId);
        dest.writeString(suggestion);
        dest.writeString(signType);
        dest.writeInt(signStatus);
    }

    protected SignSubmitModel(Parcel in) {
        pdfSignId = in.readString();
        userIdNumber = in.readString();
        userName = in.readString();
        userPhoto = in.readString();
        signFile = in.readString();
        fingerprintSignFile = in.readString();
        handwrittenSignFile = in.readString();
        keyword = in.readString();
        afterFile = in.readString();
        beforeFile = in.readString();
        isSelf = in.readByte() != 0;
        readStatus = in.readInt();//和writeToParcel函数顺序保持一致


        // 读取list集合时,一定要先判断是否为Null.如果不判断,会出现null指针
//        if (keywords == null) {
//            keywords = new ArrayList<>();
//        }
//        in.readList(keywords, SignKeyWordModel.class.getClassLoader());
        keywordId = in.readString();
        suggestion = in.readString();
        signType = in.readString();
        signStatus = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SignSubmitModel> CREATOR = new Creator<SignSubmitModel>() {
        @Override
        public SignSubmitModel createFromParcel(Parcel in) {
            return new SignSubmitModel(in);
        }

        @Override
        public SignSubmitModel[] newArray(int size) {
            return new SignSubmitModel[size];
        }
    };

    public String getPdfSignId() {
        return pdfSignId;
    }

    public void setPdfSignId(String pdfSignId) {
        this.pdfSignId = pdfSignId;
    }

    public String getUserIdNumber() {
        return userIdNumber;
    }

    public void setUserIdNumber(String userIdNumber) {
        this.userIdNumber = userIdNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        if (userPhoto == null) {
            return "";
        }
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getSignFile() {
        return signFile;
    }

    public void setSignFile(String signFile) {
        this.signFile = signFile;
    }

    public String getFingerprintSignFile() {
        return fingerprintSignFile;
    }

    public void setFingerprintSignFile(String fingerprintSignFile) {
        this.fingerprintSignFile = fingerprintSignFile;
    }

    public String getHandwrittenSignFile() {
        return handwrittenSignFile;
    }

    public void setHandwrittenSignFile(String handwrittenSignFile) {
        this.handwrittenSignFile = handwrittenSignFile;
    }

    public String getKeyword() {
        if (keyword == null) {
            return "";
        }
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getAfterFile() {
        return afterFile;
    }

    public void setAfterFile(String afterFile) {
        this.afterFile = afterFile;
    }

    public String getBeforeFile() {
        return beforeFile;
    }

    public void setBeforeFile(String beforeFile) {
        this.beforeFile = beforeFile;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public String getKeywordId() {
        if (keywordId == null) {
            return "";
        }
        return keywordId;
    }

    public void setKeywordId(String keywordId) {
        this.keywordId = keywordId;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public int getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(int signStatus) {
        this.signStatus = signStatus;
    }

    @Override
    public String toString() {
        return "SignSubmitModel{" +
                "pdfSignId='" + pdfSignId + '\'' +
                ", userIdNumber='" + userIdNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", isSelf=" + isSelf +
                ", signFile='" + signFile + '\'' +
                ", fingerprintSignFile='" + fingerprintSignFile + '\'' +
                ", handwrittenSignFile='" + handwrittenSignFile + '\'' +
                ", keyword='" + keyword + '\'' +
                ", afterFile='" + afterFile + '\'' +
                ", beforeFile='" + beforeFile + '\'' +
                ", readStatus=" + readStatus +
                ", keywordId='" + keywordId + '\'' +
                ", suggestion='" + suggestion + '\'' +
                ", signType='" + signType + '\'' +
                ", signStatus='" + signStatus + '\'' +
                '}';
    }
}
