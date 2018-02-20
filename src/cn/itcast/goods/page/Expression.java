package cn.itcast.goods.page;

public class Expression {

	private String name;//名称
	private String operator;//逻辑运算符
	private String value;//值
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/*
	 * 有参构造器
	 */
	public Expression(String name, String operator, String value) {
		super();
		this.name = name;
		this.operator = operator;
		this.value = value;
	}
	/**
	 * 无参构造器
	 */
	public Expression() {
		
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
