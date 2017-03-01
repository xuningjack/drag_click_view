package com.example.android_pra.ui.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 订阅的vo类
 * @author Jack
 */
public class SubScribeBean implements Serializable, Parcelable {
	
	private static final long serialVersionUID = 1L;
	/**板块id*/
	@Expose
	@SerializedName("tagid")
	public String fid;
	/**板块名称*/
	@Expose
	@SerializedName("tagname")
	public String tag;
	public boolean isSelected;


	public static final Creator<SubScribeBean> CREATOR = new Creator<SubScribeBean>() {
		public SubScribeBean createFromParcel(Parcel in) {
			SubScribeBean bean = new SubScribeBean();
			bean.fid = in.readString();
			bean.tag = in.readString();
			return bean;
		}

		public SubScribeBean[] newArray(int size) {
			return new SubScribeBean[size];
		}
	};


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.fid);
		dest.writeString(this.tag);
	}

	@Override
	public boolean equals(Object o) {
		return fid.equals(((SubScribeBean) o).fid);
	}

	public SubScribeBean() {
		super();
	}

	public SubScribeBean(String fid, String tag, boolean isSelected) {
		super();
		this.fid = fid;
		this.tag = tag;
		this.isSelected = isSelected;
	}
}