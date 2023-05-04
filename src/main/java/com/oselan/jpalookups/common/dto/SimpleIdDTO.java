package com.oselan.jpalookups.common.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
/***
 * A simple Id wrapper used as based class for ID DTOS 
 * @author Ahmad Hamid
 *
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor 
public class SimpleIdDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2980696528252368033L;
	protected Long id;

}
