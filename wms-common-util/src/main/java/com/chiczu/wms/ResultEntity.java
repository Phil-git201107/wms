package com.chiczu.wms;

/**
 * 將Ajax請求返回結果,統一整個project的返回類型
 * 日後也可用於分布式架構各個module間調用時返回的類型
 * @author Phil
 * @param <T>
 */
public class ResultEntity<T> {

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	//用來封裝當前請求結果是成功or失敗
	private String result;
	
	//請求失敗時,返回的錯誤訊息
	private String message;
	
	//要返回的資料
	private T data;
	/*
	 * 請求處理成功,且不需要返回資料時使用的方法
	 * <Type>ResultEntity<Type> : 第1個<Type>:聲明這個方法是泛型;第2個<Type>:聲明這個方法使用泛型
	 * 一般是增刪改的操作
	 */
	public static <Type>ResultEntity<Type> successWithoutData() {
		return new ResultEntity<Type>(SUCCESS,null,null);
	}
	/*
	 * 請求處理成功,且需要返回資料時使用的方法
	 * data:是要返回的資料
	 * 一般是查詢操作
	 */
	public static <Type>ResultEntity<Type> successWithData(Type data) {
		return new ResultEntity<Type>(SUCCESS,null,data);
	}
	/*
	 * 請求處理失敗後使用的方法
	 * msg:是錯誤訊息
	 */
	public static <Type>ResultEntity<Type> failed(String message) {
		return new ResultEntity<Type>(FAILED,message,null);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ResultEntity [result=" + result + ", msg=" + message + ", data=" + data + "]";
	}

	public ResultEntity(String result, String message, T data) {
		this.result = result;
		this.message = message;
		this.data = data;
	}

	public ResultEntity() {
	}
	
	
}
