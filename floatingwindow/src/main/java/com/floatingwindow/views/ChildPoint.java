package com.floatingwindow.views;

public class ChildPoint {
	
	private float chlid_x;
	private float child_y;
	private int position;
	private int degress;
	public float getChlid_x() {
		return chlid_x;
	}
	public void setChlid_x(float chlid_x) {
		this.chlid_x = chlid_x;
	}
	public float getChild_y() {
		return child_y;
	}
	public void setChild_y(float child_y) {
		this.child_y = child_y;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getDegress() {
		return degress;
	}
	public void setDegress(int degress) {
		this.degress = degress;
	}
	public ChildPoint(float chlid_x, float child_y, int position, int degress) {
		super();
		this.chlid_x = chlid_x;
		this.child_y = child_y;
		this.position = position;
		this.degress = degress;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(child_y);
		result = prime * result + Float.floatToIntBits(chlid_x);
		result = prime * result + position;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChildPoint other = (ChildPoint) obj;
		if (Float.floatToIntBits(child_y) != Float
				.floatToIntBits(other.child_y))
			return false;
		if (Float.floatToIntBits(chlid_x) != Float
				.floatToIntBits(other.chlid_x))
			return false;
		if (position != other.position)
			return false;
		return true;
	}
	
}
