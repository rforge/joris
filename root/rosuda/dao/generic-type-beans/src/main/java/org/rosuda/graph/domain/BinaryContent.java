package org.rosuda.graph.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="BDATA")
public class BinaryContent {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "DATA")
	private byte[] data;
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setData(final byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public InputStream getDataFromStream() {
		if (data == null)
			return new ByteArrayInputStream(new byte[0]);
		return new ByteArrayInputStream(data);
	}

	/**
	 * does consume the stream but neither close nor reset the source
	 * @param source
	 * @throws IOException
	 */
	public void setDataFromStream(final InputStream source) throws IOException {
		final byte[] buffer = new byte[1024];
		final ByteArrayOutputStream byteContainer = new ByteArrayOutputStream(
				source.available());
		int readBytes;
		while ((readBytes = source.read(buffer)) > 0) {
			byteContainer.write(buffer, 0, readBytes);
		}
		this.data = byteContainer.toByteArray();
		byteContainer.close();
	}
}
