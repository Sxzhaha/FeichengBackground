package com.ruoyi.binZhou.ld.domain;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName RuoYi-kangDun
 * @ClassName MenuItemDTO
 * @Author 刘邓
 * @Date 2025-05-23 9:56
 */
@Data
public class MenuItemDTO {
	private String label;
	private boolean expanded;
	private List<MenuItemDTO> children;

	public MenuItemDTO(String label) {
		this.label = label;
		this.expanded = false;
	}
}
