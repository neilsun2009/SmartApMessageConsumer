package smartAP.MessageConsumer_2.entity;

public class ColumnDefind {
	/**
	 * column dataType
	 */
	private String dataType;

	/**
	 * column dataLength
	 */
	private String dataLength;

	/**
	 * column nullable
	 */
	private String nullable;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataLength() {
		return dataLength;
	}

	public void setDataLength(String dataLength) {
		this.dataLength = dataLength;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
}
