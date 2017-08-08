package com.savor.savorphone.bean;

import java.io.Serializable;

public class CategoryItemVo implements Serializable {

	private static final long serialVersionUID = -1;

	private int id;
	private String imageURL;
	private String name;
	private String englishName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	@Override
	public String toString() {
		return "CategoryItemVo{" +
				"id=" + id +
				", imageURL='" + imageURL + '\'' +
				", name='" + name + '\'' +
				", englishName='" + englishName + '\'' +
				'}';
	}
}
