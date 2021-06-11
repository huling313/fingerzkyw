package cn.org.bjca.finger.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @版权所有： 北京数字医信科技有限公司 (C) 2019
 * @类描述:
 * @版本: V1.0.0
 * @作者 huling
 * @创建时间 2020-08-21 10:43
 */
public class SignKeyWordModel implements Parcelable {

    private String keyword;
    private int signStatus;//签署状态，0未签署2已签署
    private boolean isSelect;
    private String keywordId;

    public SignKeyWordModel() {
    }

    public SignKeyWordModel(String keyword, boolean isSelect) {
        this.keyword = keyword;
        this.isSelect = isSelect;
    }

    protected SignKeyWordModel(Parcel source) {
        keyword = source.readString();
        signStatus = source.readInt();
        isSelect = source.readByte() != 0;
        keywordId = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyword);
        dest.writeInt(signStatus);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(keywordId);
    }

    public static final Creator<SignKeyWordModel> CREATOR = new Creator<SignKeyWordModel>() {
        @Override
        public SignKeyWordModel createFromParcel(Parcel in) {
            return new SignKeyWordModel(in);
        }

        @Override
        public SignKeyWordModel[] newArray(int size) {
            return new SignKeyWordModel[size];
        }
    };

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(int signStatus) {
        this.signStatus = signStatus;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(String keywordId) {
        this.keywordId = keywordId;
    }

    @Override
    public String toString() {
        return "SignKeyWordModel{" +
                "keyword='" + keyword + '\'' +
                ", signStatus=" + signStatus +
                ", isSelect=" + isSelect +
                ", keywordId='" + keywordId + '\'' +
                '}';
    }
}
