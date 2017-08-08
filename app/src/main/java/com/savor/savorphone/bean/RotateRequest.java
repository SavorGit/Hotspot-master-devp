package com.savor.savorphone.bean;

import java.io.Serializable;

public class RotateRequest implements Serializable {
	private String projectId;

	@Override
	public String toString() {
		return "RotateRequest{" +
				"projectId='" + projectId + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RotateRequest that = (RotateRequest) o;

		return projectId != null ? projectId.equals(that.projectId) : that.projectId == null;

	}

	@Override
	public int hashCode() {
		return projectId != null ? projectId.hashCode() : 0;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
