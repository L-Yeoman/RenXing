package com.dldzkj.app.renxing.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 各社区
 * 大类选择界面实体类
 */
public class BBSTypeModel implements Parcelable {

    /**
     * Description :
     * BoardName : 爱的色放
     * BoardID : 24
     * Rules :
     * BackImage :
     * Icon :
     */
    private String Description;
    private String BoardName;
    private String BoardID;
    private String Rules;
    private String BackImage;
    private String Icon;

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setBoardName(String BoardName) {
        this.BoardName = BoardName;
    }

    public void setBoardID(String BoardID) {
        this.BoardID = BoardID;
    }

    public void setRules(String Rules) {
        this.Rules = Rules;
    }

    public void setBackImage(String BackImage) {
        this.BackImage = BackImage;
    }

    public void setIcon(String Icon) {
        this.Icon = Icon;
    }

    public String getDescription() {
        return Description;
    }

    public String getBoardName() {
        return BoardName;
    }

    public String getBoardID() {
        return BoardID;
    }

    public String getRules() {
        return Rules;
    }

    public String getBackImage() {
        return BackImage;
    }

    public String getIcon() {
        return Icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Description);
        dest.writeString(this.BoardName);
        dest.writeString(this.BoardID);
        dest.writeString(this.Rules);
        dest.writeString(this.BackImage);
        dest.writeString(this.Icon);
    }

    public BBSTypeModel() {
    }

    protected BBSTypeModel(Parcel in) {
        this.Description = in.readString();
        this.BoardName = in.readString();
        this.BoardID = in.readString();
        this.Rules = in.readString();
        this.BackImage = in.readString();
        this.Icon = in.readString();
    }

    public static final Parcelable.Creator<BBSTypeModel> CREATOR = new Parcelable.Creator<BBSTypeModel>() {
        public BBSTypeModel createFromParcel(Parcel source) {
            return new BBSTypeModel(source);
        }

        public BBSTypeModel[] newArray(int size) {
            return new BBSTypeModel[size];
        }
    };
}
