package com.oselan.commons.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
@JsonPropertyOrder(value = {"id" } ,alphabetic = true)
public class SimpleIdDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2980696528252368033L;
	protected Long id;

}
