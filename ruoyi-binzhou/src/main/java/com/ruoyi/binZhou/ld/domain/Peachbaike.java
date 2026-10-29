package com.ruoyi.binZhou.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 peachbaike
 * 
 * @author ruoyi
 * @date 2026-04-18
 */
public class Peachbaike extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String varietyName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String description;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String plant;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String mature;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String picture;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String character;

    /** $column.columnComment */
    private Long id;

    public void setVarietyName(String varietyName) 
    {
        this.varietyName = varietyName;
    }

    public String getVarietyName() 
    {
        return varietyName;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setPlant(String plant) 
    {
        this.plant = plant;
    }

    public String getPlant() 
    {
        return plant;
    }

    public void setMature(String mature) 
    {
        this.mature = mature;
    }

    public String getMature() 
    {
        return mature;
    }

    public void setPicture(String picture) 
    {
        this.picture = picture;
    }

    public String getPicture() 
    {
        return picture;
    }

    public void setCharacter(String character) 
    {
        this.character = character;
    }

    public String getCharacter() 
    {
        return character;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("varietyName", getVarietyName())
            .append("description", getDescription())
            .append("plant", getPlant())
            .append("mature", getMature())
            .append("picture", getPicture())
            .append("character", getCharacter())
            .append("id", getId())
            .toString();
    }
}
